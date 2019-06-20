package com.fh.index.imip;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat;
import org.apache.lucene.codecs.lucene62.Lucene62Codec;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class GenIndex {

    public static void main(String args[]) throws IOException {
        File bcpPath = new File("d:/data/" + Config.FILE_PREFIX + "raw.bcp");
        File indexPath = new File("z:/" + Config.FILE_PREFIX + "index-raw");


        IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
        config.setOpenMode(OpenMode.CREATE);
        config.setUseCompoundFile(false);
        config.setCodec(new Lucene62Codec(Lucene50StoredFieldsFormat.Mode.BEST_COMPRESSION));
        config.setRAMBufferSizeMB(1024);

        IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath.getAbsolutePath())), config);
        BufferedReader bcpReader = new BufferedReader(new FileReader(bcpPath));
        long start = System.currentTimeMillis();
        try {
            int readed = 0;
            String line = null;
            IMIP IMIP = new IMIP();
            while ((line = bcpReader.readLine()) != null) {
                if (!IMIP.fromLine(line)){
                    System.out.println("parse line fail: " + line);
                    continue;
                }

                writer.addDocument( IMIP.toDocument() );

                readed++;
                if (readed % 100000 == 1) {
                    System.out.println("100000 records has benn inserted, total=" + readed);
                }
            }


//            start = System.currentTimeMillis();
//            System.out.println("start merge...");
//            writer.forceMerge(readed/2000000 + 1);
//            System.out.println("merge finished");
//            System.out.println("elapse=" + (System.currentTimeMillis() - start) + "ms");

        } catch (IOException e) {
            throw new RuntimeException("building index failed. ", e);
        } finally {
            writer.close();
        }
        System.out.println("elapse=" + (System.currentTimeMillis() - start) + "ms");
    }


}
