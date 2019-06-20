package com.fh.index.person;

/*
BCP:
IDNO:18                      INDEX
CHNAME  10
NATION  2BYTE
MOBILE 11                   INDEX
VEHLIC  10                  INDEX
VID        (qq,wxid,wx,email)   INDEX
LABELS         2 LABELS     (ZA000119#5;ZA000119#3)
 */

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.StoredField;
import java.util.ArrayList;
import java.util.List;

public class Person {
    public String idno;
    public List<String> vids = new ArrayList<>(8);
    public String chname;
    public String nation;
    public String mobile;
    public String vehlic;
    public String labels;

    public void generateRandom(){
        idno = RandomStringUtils.randomNumeric(18);
        chname = RandomStringUtils.randomAlphabetic(10);
        nation = RandomStringUtils.randomAlphanumeric(2);
        mobile = "18" + RandomStringUtils.randomNumeric(9);
        vehlic = RandomStringUtils.randomAlphanumeric(10);
        for (int i=0; i<5; i++){
            this.vids.add(RandomStringUtils.randomNumeric(16) + "@qq.com");
        }
        StringBuilder sb = new StringBuilder(32);

        int labelCnt = 2;
        for (int i=0; i<labelCnt; i++){
            String label = "ZA00" + RandomStringUtils.randomAlphanumeric(6);
            int cnt = (int)(System.currentTimeMillis() % 10);
            sb.append(label + "#" + cnt);
            if (i != labelCnt - 1){
                sb.append(",");
            }
        }
        this.labels = sb.toString();
    }

    public String toLine(){
        StringBuilder sb = new StringBuilder(256);
        sb.append(idno).append('\t');
        sb.append(chname).append('\t');
        sb.append(nation).append('\t');
        sb.append(mobile).append('\t');
        sb.append(vehlic).append('\t');
        for (String id : this.vids){
            sb.append(id).append(',');
        }
        sb.append('\t');
        sb.append(labels);
        return sb.toString();
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.add(new StringField("idno", idno, Field.Store.YES));
        doc.add(new StringField("mobile", mobile, Field.Store.YES));
        doc.add(new StoredField("vehlic",  vehlic));
        for (String vid : this.vids){
            doc.add(new StringField("vids", vid, Field.Store.YES));
        }

        doc.add(new SortedDocValuesField("chname", new BytesRef(chname.getBytes())));
        doc.add(new StoredField("nation", nation));
        doc.add(new StoredField("labels", labels));
        return doc;
    }

    public boolean fromLine(String line){
        String[] cols = StringUtils.splitPreserveAllTokens(line ,'\t');
        if (cols.length < 7){
            return false;
        }
        this.idno = cols[0];
        this.chname = cols[1];
        this.nation = cols[2];
        this.mobile = cols[3];
        this.vehlic = cols[4];
        String fieldStr = cols[5];
        String[] fields = StringUtils.splitPreserveAllTokens(fieldStr, ',');
        this.vids.clear();
        for (String field : fields){
            if (!field.trim().isEmpty()) {
                this.vids.add(field);
            }
        }

        this.labels = cols[6];
        return true;
    }
}
