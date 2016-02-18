package com.example.ephow.tabtest.function;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ephow on 2015/10/6.
 */
public class Md5 {

    public static String getMD5(String val) throws NoSuchAlgorithmException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");

        md5.update(val.getBytes());

        byte[] m = md5.digest();    //加密

        return getString(m);
    }

    private static String getString(byte[] b){

        StringBuffer sb = new StringBuffer();

        for(byte by : b) sb.append(by);

        return sb.toString();
    }

}
