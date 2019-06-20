package com.fh.index.imip;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class QueryTest {

    public static void main(String[] args) throws Exception {
        int THREAD_CNT = 2;
        int TOTOAL_QUERY = 5000000;
        for (int i=0; i<THREAD_CNT; i++){
            final int idx = i;
            final int querys = TOTOAL_QUERY / THREAD_CNT;
            new Thread(){
                @Override
                public void run(){
                    try {
                        new QueryTest().test("-" + idx, querys);
                    }catch (Throwable t){
                        t.printStackTrace();
                    }
                }
            }.start();
        }
        while(true){
            Thread.sleep(10000);
        }
    }

    public void test(String suffix, int maxquerys) throws Exception {

        String indexDir = "z:/" + Config.FILE_PREFIX + "index-raw";
        String termFile = "d:/data/" + Config.FILE_PREFIX + "search-term-shuffled" + suffix + ".bcp";
        Directory directory = FSDirectory.open(Paths.get(indexDir)); //MMapDirectory.open(new File(indexDir));//
        IndexSearcher searcher = getIndexSearcher(directory);
        BufferedReader in = new BufferedReader(new FileReader(termFile));
        String line = null;
        long start = System.currentTimeMillis();
        int searchedTerms = 0;


        while ((line = in.readLine()) != null) {
            String[] cols = StringUtils.split(line, ':');
            if (cols.length != 2) {
                continue;
            }
            String key = cols[0].trim();
            String value = cols[1];

            Query query = LongPoint.newExactQuery(key, Long.parseLong(value));
            TopDocs topDocs = searcher.search(query, 1);
            //System.out.println("实际搜索到的记录数 => " + topDocs.totalHits);
            if (topDocs.totalHits <1) {
                System.out.println("-------------ERR : not searched : " + line);
            }

           //print(line, searcher, topDocs);
            searchedTerms++;
            if ((searchedTerms % 100000) == 0) {
                System.out.println("100000 searched " + searchedTerms);
            }

            if (searchedTerms >= maxquerys) {
                break;
            }
        }
        System.out.println("searched terms=" + searchedTerms + ", elapse=" + (System.currentTimeMillis() - start));


    }

    private void print(String line, IndexSearcher searcher, TopDocs topDocs) throws Exception{
        Document document = null;
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            document = searcher.doc(scoreDoc.doc);
            String result = " mobile:" + document.get("mobile");
            System.out.println(line + ", " + result);
        }
    }



    public IndexSearcher getIndexSearcher(Directory directory) throws Exception{
        IndexReader indexReader = null;
        if(indexReader == null){
            indexReader = DirectoryReader.open(directory);
        }else {
            IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader)indexReader);
            indexReader.close();
            indexReader = newReader;
        }
        return new IndexSearcher(indexReader);
    }

}
