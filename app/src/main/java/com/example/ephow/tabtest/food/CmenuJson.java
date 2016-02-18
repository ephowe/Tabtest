package com.example.ephow.tabtest.food;



import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by 1 on 2015/11/12.
 */
public class CmenuJson {

    final String jsonfile;

    String jsonOri = null;
    String jsonRtn = null;

    public CmenuJson(String file) {
        jsonfile = file;
        jsonRtn = jsonOri = readJsonFile();
    }

    String readJsonFile() {

        File file = new File(jsonfile);
        if ( !file.exists() ) return null;
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) buffer.append(scanner.nextLine());

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            return buffer.toString();
        }
    }

    private void writeJsonFile(String jsonstr) throws Exception {
        /*//创建本地文件夹
        String jsonPath = jsonfile.substring(jsonfile.lastIndexOf("/"));
        File mkFile = new File( jsonPath );
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }*/
        File file = new File( jsonfile );
        if (file.exists()) file.delete();
        byte [] buffer = jsonstr.getBytes();
        OutputStream out = new FileOutputStream(jsonfile, true);
        out.write(buffer);
        out.flush();
        out.close();
    }

    public boolean saveJsonToFile(boolean b) {
        boolean rtn = false;
        String st;
        if (b) st = jsonRtn; else st = jsonOri;
        try {
            writeJsonFile(st);
            rtn = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return rtn;
        }
    }

    public String getValue(String obj, String key) {
        String rtn = null;
        try {
            rtn = new JSONObject(jsonRtn).getJSONObject(obj).getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return rtn;
        }
    }

    public boolean setValue(String obj, String key, String v) {
        boolean rtn = false;
        try {
            JSONObject jsonObj = new JSONObject(jsonRtn);
            jsonObj.getJSONObject(obj).put(key, v);
            jsonRtn = jsonObj.toString();
            rtn = true;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return rtn;
        }
    }

    public boolean setObj(String name, JSONObject v) {
        boolean rtn = false;
        try {
            JSONObject jsonObj = new JSONObject(jsonRtn);
            jsonObj.put(name, v);
            jsonRtn = jsonObj.toString();
            rtn = true;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return rtn;
        }
    }


    private void setObj() throws JSONException {

        //HashMap m = new HashMap<String, String>();
        //JSONObject jsonObj = new JSONObject(m);
        //JSONStringer jsonstr = new JSONStringer();
    }



}
