package com.example.ephow.tabtest.food;


import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1 on 2015/8/18.
 *
 * //数据结构接口
 * 与主类分离
 */

public interface BaseCmenu {
    //SD卡路径
    String SDPATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

    //webservice接口方法
    interface RPC_Methods {
        //执行WS方法
        String invoke_RPC(SoapObject rpc);
    }

    //类型标记
    class ClassType {
        public final String TAG;    //类名标记
        public ClassType(String s) {TAG = s;}
    }
    ///////////////////////////////////////////
    //Food与Attach共有字段
    class CmenuUnion extends ClassType{
        public String Itcode;   //编码
        public String Des;  //名称
        public String Init; //缩写
        public CmenuUnion(String s) {super(s);}
    }
    //基础数据类
    //table food
    //菜单
    class Food extends CmenuUnion {
        public String Price;    //价格
        public String Item;     //内码
        public String Grptyp;   //菜类ID
        public String PicbigId; //小图ID
        public String PicsmallId;   //大图ID
        public String Unit; //单位
        public String Memberprice;  //会员价
        public Food() {super("Food");}
    }
    //table attach
    //附加项
    class Attach extends CmenuUnion {
        public String Amt;  //金额
        public Attach() {super("Attach");}
    }
    //菜类
    class Cls extends ClassType {
        public String Des;  //名称
        public String Grp;  //菜类ID
        private final List<Food> mFoods;    //对应菜类下的所有菜品
        public Cls(List<Food> foods) {
            super("CLS");
            mFoods = foods;
        }
        public List<Food> getFoods() {
            return mFoods;
        }
    }

    /**
    class Cmenu {
        //菜单信息
        private static List<Cls> mClses = null;
        private static List<Attach> mAttachs = null;
        private static List<Food> mFoods = null;
        static List<Cls> getClses() { return mClses;}
        static void setClses(List c) { mClses = c;}
        static List<Food> getFoods() { return mFoods;}
        static void setFoods(List c) { mFoods = c;}
        static List<Attach> getAttachs() { return mAttachs;}
        static void setAttachs(List c) { mAttachs = c;}
        static void free() {mClses = null; mAttachs = null; mFoods = null;}
    }
    */
    //////////////////////////////////////////////
    //webservice传菜数据

    //webservice台位号返回数据结构
    class TblResult extends ClassType{
        public String tbl;  //内码
        public String init; //缩写
        public String des;  //名称
        public String state;    //状态
        public TblResult() {
            super("TblResult");
        }
        //public int toInt() {return Integer.parseInt(tbl);}
    }

    /**
     * 帐单
     * 1.查询台号返回数据结构，帐单
     * 2.点完菜时所需发送的帐单
     * */
    class Bill extends ClassType {
        private final List<DishDetail> mDishes; //帐单所点菜品
        public String id = null;    //账单流水号
        public String total = null; //合计金额
        public String pax = null;   //用餐人数,服务费等
        public String type = null;  //传菜发送类型
        public Bill(List<DishDetail> dds) {
            super("QueResult");
            mDishes = dds != null ? dds : new ArrayList();
            type = "N";
        }
        //使用
        public List<DishDetail> getDishes() {return mDishes;}
        //对外开放
        //加菜如有重复菜品,则增加数量
        public void addDish(DishDetail d) {
            for (int i = mDishes.size(); --i >= 0;) {
                DishDetail dd = mDishes.get(i);
                if (dd.getFood().Item.equals(d.getFood().Item)) {
                    dd.Cnt += d.Cnt;
                    return;
                }
            }
            mDishes.add(d);
        }
        public void addDish(int location, DishDetail d) {
            for (int i = mDishes.size(); --i >= 0;) {
                DishDetail dd = mDishes.get(i);
                if (dd.getFood().Item.equals(d.getFood().Item)) {
                    dd.Cnt += d.Cnt;
                    return;
                }
            }
            mDishes.add(location, d);
        }
        //将所有菜品打包成字符串,供传菜使用
        public String packageDishes() {
            String result = "";
            for(int i = mDishes.size(); --i >= 0;) {
                if (mDishes.get(i).Color != -1) result += mDishes.get(i).toString("|") + "^";
            }
            return result;
        }
        //暂时无用
        int getDishesCnt() {
            int result = 0;
            for (int i = 0; i < mDishes.size(); i++) {
                if (mDishes.get(i).Color != -1) result++; //+= Integer.parseInt(mDishes.get(i).Cnt);
            }
            return result;
        }
    }

    //菜品明细
    class DishDetail extends ClassType {
        private final Food mFood;   //菜品信息
        private final Attach[] mAttachs;    //附加项信息
        public final int ATTCNT;    //附加项数量
        private int mIndex; //附加项计数器，用来累计附加项的数量，数量不能超过规定的总量
        public String PackID;   //套餐编号，默认-1
        public String PackCnt;  //套餐数量，默认0
        public String Num;  //只数，默认为0.00，目前意义不明
        public String Cnt;  //菜品数量,默认为1
        public String Oth;  //其它 拼菜（0为不拼，默认为0拼菜显示1）
        public String User; //点菜员号
        public int Color = -1;   //颜色,待传菜品的颜色需区别与已传菜品的颜色
        //此构造用于传菜
        public DishDetail(Food food) {
            this(food, 5);
            PackID = "-1";
            PackCnt = "0";
            Oth = "0";
            Num = "0.00";
            Cnt = "1";
        }
        //此构造用于接收查询台号所返回的菜品数据
        public DishDetail(String user, Food food) {
            this(food, 2);
            User = user;
        }
        //基础构造体
        private DishDetail(Food food, final int athcnt) {
            super("DishDetail");
            mFood = food;
            mAttachs = new Attach[ATTCNT = athcnt];
            mIndex = 0;
        }
        public Food getFood() {
            return mFood;
        }
        //设置，增加附加项，如果超过规定总量返回FALSE
        public boolean setAttach(Attach ath) {
            if (mIndex >= mAttachs.length) return false;
            if (ath == null) return false;
            mAttachs[mIndex++] = ath;
            return true;
        }
        //获取附加项
        public Attach getAttach(int idx) {
            if (mAttachs.length == 0 && idx >= mAttachs.length) return null;
            return mAttachs[idx];
        }
        //对应WEBSERIVICE的输出格式，例如：'-1|0|85003|1.00|位|598.00|0.00|||||||||||0|'
        public String toString(String split) {
            String result = PackID + split + PackCnt + split;
            if (mFood != null) {
                result += mFood.Itcode + split + Cnt + split + mFood.Unit + split + mFood.Price + split;
            } else {
                result += split + Cnt + split + split + split;
            }
            result += Num + split;
            for (Attach attach : mAttachs) {
                if (attach != null ) result += attach.Des + split + attach.Amt + split;
                else result += split + split;
            }
            result += Oth + split;
            return result;
        }
    }

    //台位状态色
    class COLORSTATE {
        private static final int RED = 0xFAFA0000;
        private static final int GREEN = 0xFA00FA00;
        private static final int PURPLE = 0xFA9600FA;
        private static final int YELLOW = 0xFAFAFA00;
        public static int getState(int bk) {
            switch (bk) {
                case 1: bk = RED; break;
                case 2: bk = GREEN;  break;
                case 3: bk = PURPLE;  break;
                case 4: bk = YELLOW;  break;
                default: bk = 0;
            }
            return bk;
        }
    }

    //webservice方法名
    class methodName {
        public static final String LOGIN[] = {"pLogin", "login"};
        public static final String LISTTABLE[] = {"pListTableC", "listtable"};
        public static final String QUERY[] = {"pQueryC", "query"};
        public static final String START[] = {"pStartC", "start"};
        public static final String SENDTAB[] = {"pSendtabC", "sendtab"};
    }

    //通用方法
    class commonMethods {
        //判断是否为数字
        public static boolean isNumeric(String str) {
            for(int i = str.length(); --i >= 0;) {
                int chr = str.charAt(i);
                if(chr < 48 || chr > 57) return false;
            }
            return true;
        }
        //判断是否为字母
        public static boolean isLetter(String str) {
            for(int i = str.length(); --i >= 0;) {
                int chr = str.charAt(i);
                if(!((chr >= 65 && chr <= 90) || (chr >= 97 && chr <= 122))) return false;
            }
            return true;
        }
        //判断是否含有字母或数字
        public static boolean isMixing(String str) {
            for(int i = str.length(); --i >= 0;) {
                int chr = str.charAt(i);
                if(!((chr >= 48 && chr <= 57) || (chr >= 65 && chr <= 90) || (chr >= 97 && chr <= 122))) return false;
            }
            return true;
        }
        /**
         * 将字符段按要求分割,并将分割结果分别储存在LIST中
         * key为分割符
         * s为待分割字符串
         * 结果分组存入list并返回该list
         */
        public static List<String> getCells(String key, String s) {
            List<String> ls = new ArrayList<>();
            if (key != null && s != null ) {    //&& s.contains(key)//字符串包含指定的key值序列
                int i;
                //字符串中没有KEY，退出循环
                while ( (i = s.indexOf(key)) >= 0 ) {
                    //将KEY与KEY中间的字符串加入LIST
                    ls.add( s.substring( 0, i ) );
                    //载去KEY前的字符串与KEY
                    s = s.substring(i + key.length());
                }
                //最后一个KEY后还有内容，则将这些内容加入LIST
                if ( s.length() > 0 ) ls.add(s);
            }
            return ls;
        }
    }



}


