package com.fh.index.person;



import java.io.*;
import java.security.SecureRandom;
import java.util.Random;

public class GenBcp {

    public static void main(String[] args) throws Exception {
//        String path = "z:/" + Config.FILE_PREFIX + "raw.bcp";
//
//        try(BufferedWriter out = new BufferedWriter(new FileWriter(path))){
//            int sampleCnt = Config.RECORDS;
//            for (int i=0; i<sampleCnt; i++){
//                Person person = new Person();
//                person.generateRandom();
//                String line = person.toLine();
//                out.write(line);
//                out.write('\n');
//            }
//        }

        testread();

    }

    public static void testread() throws Exception{
        File bcpPath = new File("z:/" + Config.FILE_PREFIX + "raw.bcp");

        FileInputStream in = new FileInputStream(bcpPath);

        long start = System.currentTimeMillis();
        try {
            int readed = 0;

            byte[] buf = new byte[1024*8];
            int n = 0;
            while ((n = in.read(buf)) > 0) {
                readed += n;

            }



            System.out.println("elapse=" + (System.currentTimeMillis() - start) + "ms");

        }  finally {
            in.close();
        }
    }


}
