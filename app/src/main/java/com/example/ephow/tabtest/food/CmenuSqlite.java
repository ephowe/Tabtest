package com.example.ephow.tabtest.food;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1 on 2015/8/18.
 */

public class CmenuSqlite implements BaseCmenu {
    public final String DBPATH;
    public final String DBNAME; //数据库名称

    //数据库对象
    private SQLiteDatabase mDatabase;
    //菜单信息
    private List<Cls> mClses;
    private List<Attach> mAttachs;
    private List<Food> mFoods;

    public CmenuSqlite(CmenuJson cj) {
        //通过JSON获取数据库名与路径
        DBNAME = cj.getValue("sqlite","name");
        DBPATH = SDPATH + cj.getValue("sqlite","relativepath");
    }

    //获取数据库对象
    SQLiteDatabase getDatabase() {
        if (mDatabase != null && mDatabase.isOpen()) return mDatabase;
        if (!(new File(DBPATH + DBNAME)).exists()) return null;
        return mDatabase = SQLiteDatabase.openOrCreateDatabase(DBPATH + DBNAME, null);
    }

    //关闭数据库对象
    synchronized void close() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    //判断有无图片
    boolean havePic(String picId)
    {
        return new File(DBPATH + picId).exists();
    }

    //获取所有菜类信息，并且将菜品信息加入对应的类下
    public List<Cls> getClses() {
        if (mClses != null) return mClses;

        List clses = new ArrayList<>();
        Cursor c = getDatabase().rawQuery("select * from class", null);
        if(c.moveToFirst()) {
            do{
                String grp = c.getString(c.getColumnIndex("GRP"));
                Cls cls = new Cls(getFoods(grp, true));
                cls.Grp = grp;
                cls.Des = c.getString(c.getColumnIndex("DES"));
                if (cls.getFoods().size() > 0) clses.add(cls);
            } while (c.moveToNext());
            c.close();
        }
        return mClses = clses;
    }

    /**
     * 获取菜品信息<如果mFoods为null则从数据库中获取全部菜品信息>
     * GRP为null则获取全部菜品信息，否则获取GRP类的菜品
     * havepic为true只获取有图片的菜品，反之获取无图片菜品
     * */
    public List<Food> getFoods(String grp, boolean havepic) {
        if (mFoods != null && grp == null) return mFoods;
        List foods = new ArrayList<>();
        if (grp == null) {
            //SQLiteDatabase db = getDatabase();
            String sql = "select * from food where ";
            sql += havepic ? "itcode <= ?" : "itcode > ?";
            //选择是否保存有图片的菜品
            Cursor c = getDatabase().rawQuery(sql, new String[]{"612000"});
            if( c.moveToFirst() ) {
                do {
                    Food food = new Food();
                    food.PicsmallId = c.getString(c.getColumnIndex("picSmall"));
                    //如果havepic为true则继续判断该菜品如无对应的图片就不获取，反之直接跳过该句
                    if (!havePic(food.PicsmallId) && havepic) continue;
                    food.Price = c.getString(c.getColumnIndex("PRICE"));
                    food.Item = c.getString(c.getColumnIndex("ITEM"));
                    food.Itcode = c.getString(c.getColumnIndex("ITCODE"));
                    food.Grptyp = c.getString(c.getColumnIndex("GRPTYP"));
                    food.Des = c.getString(c.getColumnIndex("DES"));
                    food.PicbigId = c.getString(c.getColumnIndex("picBig"));
                    food.Unit = c.getString(c.getColumnIndex("UNIT"));
                    food.Init = c.getString(c.getColumnIndex("INIT"));
                    food.Memberprice = c.getString(c.getColumnIndex("MEMBERPRICE"));
                    foods.add(food);
                } while (c.moveToNext());
                c.close();
                mFoods = foods;
            }
        } else {
            mFoods = getFoods(null, true);
            for (int i = 0; i < mFoods.size(); i++) {
                Food food = mFoods.get(i);
                if (food.Grptyp.equals(grp)) foods.add(food);
            }
        }
        return foods;
    }

    //获取所有附加项信息
    public List<Attach> getAttachs() {
        if (mAttachs != null) return mAttachs;
        List attachs = new ArrayList<>();
        Cursor c = getDatabase().rawQuery("select * from attach", null);
        if(c.moveToFirst()) {
            do{
                Attach attach = new Attach();
                attach.Itcode = c.getString(c.getColumnIndex("ITCODE"));
                attach.Des = c.getString(c.getColumnIndex("DES"));
                attach.Init = c.getString(c.getColumnIndex("INIT"));
                attach.Amt = c.getString(c.getColumnIndex("AMT"));
                attachs.add(attach);
            } while (c.moveToNext());
            c.close();
        }
        return mAttachs = attachs;
    }

    public String[] getNavMenu() {
        List<String> lts = new ArrayList<>();
        //获取滑动标签用的菜类名称
        for (int i = 0; i < getClses().size(); i++) {
            lts.add(getClses().get(i).Des);
        }
        return lts.toArray(new String[lts.size()]);
    }

    //caption and cover
    public String getTitle(String title) {
        String s = "";
        Cursor c = getDatabase().rawQuery("select text from ?", new String[]{title});
        if(c.moveToFirst()) {
            s = c.getString(0);
            c.close();
        }
        close();
        return s;
    }

}


