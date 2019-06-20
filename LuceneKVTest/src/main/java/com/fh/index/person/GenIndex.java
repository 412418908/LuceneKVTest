package com.fh.index.person;

import java.io.*;
import java.nio.file.Paths;

import java.io.IOException;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat;
import org.apache.lucene.codecs.lucene62.Lucene62Codec;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

public class GenIndex {

    public static void main(String args[]) throws IOException {
        File bcpPath = new File("d:/data/" + Config.FILE_PREFIX + "raw.bcp");
        File indexPath = new File("z:/" + Config.FILE_PREFIX + "index-raw");


        IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setUseCompoundFile(false);
        config.setCodec(new Lucene62Codec(Lucene50StoredFieldsFormat.Mode.BEST_COMPRESSION));
        config.setRAMBufferSizeMB(1024);

        IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath.getAbsolutePath())), config);
        BufferedReader bcpReader = new BufferedReader(new FileReader(bcpPath));
        long start = System.currentTimeMillis();
        try {
            int readed = 0;
            String line = null;
            Person person = new Person();
            while ((line = bcpReader.readLine()) != null) {
                if (!person.fromLine(line)){
                    System.out.println("parse line fail: " + line);
                    continue;
                }

                writer.addDocument( person.toDocument() );

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
