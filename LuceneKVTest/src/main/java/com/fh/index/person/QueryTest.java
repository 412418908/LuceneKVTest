package com.fh.index.person;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Arrays;


public class QueryTest {

    public static void main(String[] args) throws Exception {
        new QueryTest().test();
    }

    public void test() throws Exception {

        String indexDir = "z:/" + Config.FILE_PREFIX + "index-raw";
        String termFile = "d:/data/" + Config.FILE_PREFIX + "search-term-shuffled.bcp";
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
            Term term = new Term(cols[0].trim(), cols[1].trim());
            Query query = new TermQuery(term);
            TopDocs topDocs = searcher.search(query, 1);
            //System.out.println("实际搜索到的记录数 => " + topDocs.totalHits);
            if (topDocs.totalHits < 1) {
                System.out.println("-------------ERR : not searched : " + line);
            }

           // print(searcher, topDocs);

            searchedTerms++;
            if ((searchedTerms % 100000) == 0) {
                System.out.println("100000 searched " + searchedTerms);
            }
        }
        System.out.println("searched terms=" + searchedTerms + ", elapse=" + (System.currentTimeMillis() - start));


    }

    private void print(IndexSearcher searcher, TopDocs topDocs) throws Exception{
        Document document = null;
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            document = searcher.doc(scoreDoc.doc);
            String result = "idno:" + document.get("idno") + ", vids:" + Arrays.asList(document.getValues("vids"))
                    + ", mobile:" + document.get("mobile");
            System.out.println(result);
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
