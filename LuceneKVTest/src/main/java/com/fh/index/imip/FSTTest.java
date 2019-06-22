package com.fh.index.imip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Util;

import com.google.common.primitives.Ints;

public class FSTTest {

    private static List<IMIP> readSortedData() throws Exception{
        List<IMIP> items = new ArrayList<>();

        File bcpPath = new File("d:/data/" + Config.FILE_PREFIX + "raw.bcp");
        BufferedReader bcpReader = new BufferedReader(new FileReader(bcpPath));
        long start = System.currentTimeMillis();

        int readed = 0;
        String line = null;

        while ((line = bcpReader.readLine()) != null) {
            IMIP imip = new IMIP();
            if (!imip.fromLine(line)) {
                System.out.println("parse line fail: " + line);
                continue;
            }
            items.add(imip);

            readed++;
            if (readed > 2000000){
                break;
            }
        }

        items.sort(new Comparator<IMIP>() {
            @Override
            public int compare(IMIP o1, IMIP o2) {
                return o1.imsi.compareTo(o2.imsi);
            }
        });
        return items;
    }

    public static void main(String[] args) throws Exception{
        ByteSequenceOutputs outputs = ByteSequenceOutputs.getSingleton();
        Builder<BytesRef> builder = new Builder<BytesRef>(FST.INPUT_TYPE.BYTE4, outputs);
       // final IntsRef scratchIntsRef = new IntsRef();
        IntsRefBuilder scratchIntsRef = new IntsRefBuilder();
        //BytesRef output = new BytesRef(4);
        File bcpPath = new File("d:/data/" + Config.FILE_PREFIX + "raw.bcp");
        BufferedReader bcpReader = new BufferedReader(new FileReader(bcpPath));
        long start = System.currentTimeMillis();

        List<IMIP> items = readSortedData();
        for (IMIP imip : items) {

            // NumericUtils.intToPrefixCodedBytes(key.length(), 0, output);
            String key = imip.imsi;
            String value = imip.mobile;
            try {
                builder.add(Util.toUTF32(key, scratchIntsRef), new BytesRef(value.getBytes("UTF-8")));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        FST<BytesRef> buildFst = builder.finish();

        File fstFile = new File("d:/data", "fst.bin");
        buildFst.save(Paths.get(fstFile.getAbsolutePath()));
    }
}
