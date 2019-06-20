package com.fh.index.person;



import java.io.BufferedWriter;
import java.io.FileWriter;

public class GenBcp {

    public static void main(String[] args) throws Exception {
        String path = "d:/data/" + Config.FILE_PREFIX + "raw.bcp";

        try(BufferedWriter out = new BufferedWriter(new FileWriter(path))){
            int sampleCnt = Config.RECORDS;
            for (int i=0; i<sampleCnt; i++){
                Person person = new Person();
                person.generateRandom();
                String line = person.toLine();
                out.write(line);
                out.write('\n');
            }
        }

    }


}
