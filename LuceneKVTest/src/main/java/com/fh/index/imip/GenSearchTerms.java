package com.fh.index.imip;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GenSearchTerms {

    public static void main(String[] args) throws Exception {
        genSearchTerms();
        shuffle();
    }

    public static void shuffle() throws Exception{
        File inPath = new File("d:/data/" + Config.FILE_PREFIX + "search-term.bcp");
        File outPath = new File("d:/data/" + Config.FILE_PREFIX + "search-term-shuffled.bcp");

        BufferedReader in = new BufferedReader(new FileReader(inPath));
        BufferedWriter out = new BufferedWriter(new FileWriter(outPath));

        int SHUFFLE_BLOCK_SIZE = 1000000;
        List<String> lines = new ArrayList<>(SHUFFLE_BLOCK_SIZE);
        String line = null;
        while ((line = in.readLine()) != null) {
            lines.add(line);
            if (lines.size() >= SHUFFLE_BLOCK_SIZE){
                Collections.shuffle(lines);
                for (String l : lines){
                    out.write(l);
                    out.write('\n');
                }
                lines.clear();
            }
        }
        Collections.shuffle(lines);
        for (String l : lines){
            out.write(l);
            out.write('\n');
        }
        lines.clear();
        in.close();
        out.close();
    }

    public static void genSearchTerms() throws Exception{
        File bcpPath = new File("d:/data/" + Config.FILE_PREFIX + "raw.bcp");
        File termPath = new File("d:/data/" + Config.FILE_PREFIX + "search-term.bcp");

        BufferedReader bcpReader = new BufferedReader(new FileReader(bcpPath));
        BufferedWriter termWriter = new BufferedWriter(new FileWriter(termPath));
        long start = System.currentTimeMillis();
        try {
            int readed = 0;
            String line = null;
            IMIP IMIP = new IMIP();
            Random rand = new SecureRandom();
            while ((line = bcpReader.readLine()) != null) {
                if (!IMIP.fromLine(line)){
                    System.out.println("parse line fail: " + line);
                    continue;
                }

                String term = null;
//                int type = rand.nextInt(2); // idno, mobile, vid
//                if (type == 0){
//                    term = "mac:" + IMIP.mac;
//                }else if (type == 1){
                    term = "imsi:" + IMIP.imsi;
//                }
                termWriter.write(term);
                termWriter.write('\n');

                readed++;
                if (readed % 100000 == 1) {
                    System.out.println("100000 records has benn inserted, total=" + readed);
                }
				if (readed >= 5500000){
					break;
				}
            }



            System.out.println("elapse=" + (System.currentTimeMillis() - start) + "ms");

        }  finally {
            bcpReader.close();
            termWriter.close();
        }
    }
}
