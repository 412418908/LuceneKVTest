package com.fh.index.imip;



import jdk.nashorn.internal.codegen.types.NumericType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IMIP {
    public String imei;
    public String mac;
    public String imsi;
    public String mobile;

    public static Random gRandom;
    static {
        gRandom = new SecureRandom();
    }


    public void generateRandom(){
        imei = "";
        long macLong = 0x100000000000000L + gRandom.nextLong();
        mac = Long.toHexString(macLong);
        if (mac.length() > 12){
            mac = mac.substring(0, 12);
        }
       // mac = RandomStringUtils.randomAlphanumeric(12);
        imsi = RandomStringUtils.randomNumeric(16);
        mobile = "1" + RandomStringUtils.randomNumeric(10);

    }

    public String toLine(){
        StringBuilder sb = new StringBuilder(256);
        sb.append(imei).append('\t');
        sb.append(mac).append('\t');
        sb.append(imsi).append('\t');
        sb.append(mobile);

        return sb.toString();
    }

    public Document toDocument() {
        Document doc = new Document();
//        if (!imei.isEmpty()) {
//            doc.add(new StringField("imei", imei, Field.Store.NO));
//        }
//        if (!mac.isEmpty()) {
//            //doc.add(new StringField("mac", mac, Field.Store.NO));
//            doc.add(new LongPoint("mac", Long.parseLong(mac, 16)));
//        }
        if (!imsi.isEmpty()) {
            //doc.add(new StringField("imsi", imsi, Field.Store.NO));
            doc.add(new LongPoint("imsi", Long.parseLong(imsi)));
        }
        if (!mobile.isEmpty()) {
            //doc.add(new StoredField("mobile", Long.parseLong(mobile)));
        }
        return doc;
    }

    public boolean fromLine(String line){
        String[] cols = StringUtils.splitPreserveAllTokens(line ,'\t');
        if (cols.length < 4){
            return false;
        }
        this.imei = cols[0];
        this.mac = cols[1];
        this.imsi = cols[2];
        this.mobile = cols[3];

        return true;
    }
}
