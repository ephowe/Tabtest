package com.example.ephow.tabtest.food;

import android.util.SparseArray;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 1 on 2015/11/15.
 *
 * 点菜系统服务类
 */
public class CmenuService implements BaseCmenu{
    //配置文件路径
    private final String mCfgPath;
    //
    private int mPdaSerial = 0;
    private String mPdaid = null;
    private String mUser = null;
    private String mPass = null;
    //当前购物车中已点菜品
    private Bill mCurrentBill = null;
    private TblResult mCurrentTbl = null;
    //this
    private static CmenuService mSelf = null;
    //serviceObj
    private CmenuSqlite mCmenuSql = null;
    private CmenuFTP mCmenuFtp = null;
    private CmenuWebServices mCmenuWs = null;
    private CmenuJson mCmenuJs = null;
    //帐单数据<台号，帐单>
    private SparseArray<Bill> mBills = null;
    //台位数据<台位状态>
    private SparseArray<TblResult> mTbls = null;

    ////////////////////////////////////////////////
    // 方法区
    //获取CS的实例，单例对象静态方法
    public static CmenuService getInstance() {
        if (mSelf == null ) mSelf = new CmenuService("/booksystem/cmenu.json");
        return mSelf;
    }
    //私有构造,防止被其它类构造
    private CmenuService(String cfg) {
        mCfgPath = SDPATH + cfg;
        iniServices();
        //初始化帐单列表
        mBills = new SparseArray();
    }
    //建立FOOD下的对象
    private void iniServices() {
        mCmenuJs = new CmenuJson(mCfgPath);
        mCmenuWs = new CmenuWebServices(mCmenuJs);
        mCmenuFtp = new CmenuFTP(mCmenuJs);
        mCmenuSql = new CmenuSqlite(mCmenuJs);
    }
    //获取FOOD下的对象
    public CmenuJson getJson() {
        return this.mCmenuJs;
    }
    public RPC_Methods getWebService() {
        return this.mCmenuWs;
    }
    public CmenuSqlite getSqlite() {
        return this.mCmenuSql;
    }
    public CmenuFTP getFtp() {
        return this.mCmenuFtp;
    }
    //关闭cs对象
    public void close() {
        mCmenuSql.close();
        mCmenuSql = null;
        mCmenuJs = null;
        mCmenuWs = null;
        mCmenuFtp = null;
    }
    //pad编号
    public String getPdaid() {
        if (mPdaid == null) mPdaid = mCmenuJs.getValue("login", "padid");
        return mPdaid;
    }
    public void setPdaid(String v) {
        if (v == null) return;
        mCmenuJs.setValue("login", "padid", mPdaid = v);
    }

    public String getUser() {
        if (mUser == null) mUser = mCmenuJs.getValue("login", "user");
        return mUser;
    }

    public int incPdaSerial() {
        return ++mPdaSerial;
    }

    //保存帐单
    //如果以台位为KEY保存帐单信息,每次通过WEBSERIVICE获取信息后保存时,列表中的同台位帐单就被覆盖掉,所以没有意义,待改!!
    //保存台位状态4的帐单还是有意义的
    public void saveBill(int id, Bill b) {
        //if ( mBills == null)  mBills = new SparseArray();
        mBills.put(id, b);
    }
    //获取帐单
    public Bill getBill(int id) {
        return  mBills.get(id);
    }
    //删除帐单
    public void delBill(int id) {
        mBills.delete(id);
    }
    //获取当前已点菜品
    public Bill getCurrentBill() {
        return mCurrentBill != null ? mCurrentBill : (mCurrentBill = new Bill(null));
    }
    //删除当前帐单
    public void delCurrentBill() {
        mCurrentBill = null;
    }
    //获取肖前台位号
    public TblResult getCurrentTbl() {
        return mCurrentTbl;
    }
    //设置肖前台位号
    public void setCurrentTbl(TblResult t) {
        mCurrentTbl = t;
    }
    /**
     * ///////////////////////////////////////////////////////////////////
     * Cmenu
     * 搜索类方法
     * */
    //获取指定名称菜类详情
    public Cls getCls(String s) {
        List<Cls> clses = mCmenuSql.getClses();
        Cls cls = null;
        for (int i = 0; i < clses.size(); i++) {
            boolean ok = false;
            if (commonMethods.isNumeric(s)) {
                if (clses.get(i).Grp.equals(s)) ok = true;
            } else {
                if (clses.get(i).Des.equals(s)) ok = true;
            }
            if (ok) {
                cls = clses.get(i);
                break;
            }
        }
        return cls;
    }
    //获取指定名称附加项详情
    public Attach getAttach(String s) {
        List<Attach> attachs = mCmenuSql.getAttachs();
        Attach attach = null;
        for (int i = 0; i < attachs.size(); i++) {
            boolean ok = false;
            if (commonMethods.isNumeric(s)) {
                if (attachs.get(i).Itcode.equals(s)) ok = true;
            } else if (commonMethods.isLetter(s)) {
                if (attachs.get(i).Init.equals(s)) ok = true;
            } else {
                if (attachs.get(i).Des.equals(s)) ok = true;
            }
            if (ok) {
                attach = attachs.get(i);
                break;
            }
        }
        return attach;
    }
    //获取指定名称菜品详情
    public Food getFood(String s) {
        List<Food> foods = mCmenuSql.getFoods(null, true);
        Food food = null;
        for (int i = 0; i < foods.size(); i++) {
            boolean ok = false;
            if (commonMethods.isNumeric(s)) {
                if (foods.get(i).Itcode.equals(s)) ok = true;
            } else if (commonMethods.isMixing(s)) {
                if (foods.get(i).Init.equals(s)) ok = true;
            } else {
                if (foods.get(i).Des.equals(s)) ok = true;
            }
            if (ok) {
                food = foods.get(i);
                break;
            }
        }
        return food;
    }
    /**
     * 模糊搜索
     * 支持按 编码，缩写，名称 搜索
     * datas只支持food,attach类
     * 返回搜索到的结果的集合
     * */
    private List search(List datas, String s) {
        List result = new ArrayList();
        for (int i = 0; i < datas.size(); i++) {
            CmenuUnion cu = (CmenuUnion)datas.get(i);
            boolean ok = false;
            //instanceof
            if (commonMethods.isNumeric(s)) {
                //搜索的内容必须对应被搜索内容的起点
                // 例如被搜索内容为(123,231,135),搜索关键字为1,那么结果为123,135
                if (cu.Itcode.indexOf(s) == 0) ok = true;
            } else if (commonMethods.isMixing(s)) {
                if (cu.Init.indexOf(s.toUpperCase()) == 0) ok = true;
            } else {
                //搜索的内容可以是被搜索内容的任意位置
                // 例如被搜索内容为(123,231,135,232),搜索关键字为1,那么结果为123,135,231
                if (cu.Des.indexOf(s) >= 0) ok = true;
            }
            if (ok) result.add(datas.get(i));
        }
        return result.size() > 0 ? result : null;
    }
    //模糊搜索菜品
    public List<Food> searchFoods(String s) {
        return search(mCmenuSql.getFoods(null, true), s);
    }
    //模糊搜索附加项
    public List<Attach> searchAttachs(String s) {
        return search(mCmenuSql.getAttachs(), s);
    }


    /**
     * ///////////////////////////////////////////////////////////////////
     * webservice
     *
     * */
    //+method<Msg>
    public String getMsg(String method, String s) {
        if (s == null || method == null) return null;
        if (s.length() < method.length()) return null;
        if (s.toCharArray()[0] != '+') return null;
        String name = s.substring(1, method.length() + 1);
        if ( !name.equals(method) ) return null;
        int start = s.indexOf("<") + 1;
        if (start <= 0) return null; //无<返回NULL
        int end = s.lastIndexOf(">");
        if (end < 0) return null; //无>返回NULL
        return s.substring(start, end);
    }
    /**
     * 查询台位状态
     * +listtable<
     * 1^101^101^1|
     * 2^102^102^1|
     * 3^105^105^1|
     * 4^106^106^1|
     * >
     * */
    public SparseArray<TblResult> getListTable(String s) {
        String msg = getMsg(methodName.LISTTABLE[1], s);
        if (msg == null) return null;
        if (mTbls == null) mTbls = new SparseArray();
        //第一次分割
        List<String> lv1 = commonMethods.getCells("|", msg);
        for (int i =0; i < lv1.size(); i++ ) {
            //第二次分割
            List<String> lv2 = commonMethods.getCells("^", lv1.get(i));
            if ( lv2.size() == 4 ) {
                int key = Integer.parseInt(lv2.get(0));
                TblResult tbl = mTbls.get(key);
                if (tbl == null) {
                    tbl = new TblResult();
                    tbl.tbl   = lv2.get(0);
                    tbl.init  = lv2.get(1);
                    tbl.des   = lv2.get(2);
                    tbl.state = lv2.get(3);
                    mTbls.put(key, tbl);
                } else {
                    tbl.state = lv2.get(3);
                }
            }
        }
        return mTbls;
    }
    /**
     * 查询台号
     * +query<
     * tab:249116;
     * Total$:271.5;
     * String:5人 ;
     * account:
     * 1833240^金牌酸菜鲈鱼^1^58.9^份^ ^ ^#57 ^&
     * 1833241^还想吃的虾米粉丝^1^22.9^份^ ^ ^#57 ^&
     * 1833242^蜂蜜芝士焗南瓜^1^22.9^份^ ^ ^#57 ^&
     * 1833243^二师兄的碳烧肉^1^38.9^份^ ^ ^#57 ^&
     * >
     * */
    public Bill getQuery(String s) {
        //检查并获取有效字符串
        String msg = getMsg(methodName.QUERY[1], s);
        if ( msg == null ) return null;
        //第一次分割
        List<String> lv1 = commonMethods.getCells(";", msg);
        if ( lv1.size() != 4 ) return null;
        //第二次分割
        HashMap<String, Object> hmresult = new HashMap();
        for (int i =0; i < lv1.size(); i++ ) {
            List<String> lv2 = commonMethods.getCells(":", lv1.get(i));
            if ( lv2.size() == 2 ) hmresult.put(lv2.get(0), lv2.get(1));
        }
        //开始生成帐单
        Bill result = new Bill(null);
        result.id = (String)hmresult.get("tab");
        result.total = (String)hmresult.get("Total$");
        result.pax = (String)hmresult.get("String");

        //第三次分割
        List<String> lv3 = commonMethods.getCells("&", (String) hmresult.get("account"));
        /**
         * 第三次分割,分割符为‘#13#11’时需使用的代码
             try {
                 //分割符 #13#11
                 byte[] b= new byte[]{11,13};
                 String split = new String(b,"UTF-8");
                 lv3 = getCells(split, (String)mhresult.get("account"));
             } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
                 return null;
             }
         */
        for (int i =0; i < lv3.size(); i++ ) {
            //第四次分割
            List<String> lv4 = commonMethods.getCells("^", lv3.get(i));
            if (lv4.size() > 7) {
                Food food = getFood(lv4.get(0));
                if (food == null) food = getFood(lv4.get(1));
                if (food != null) {
                    DishDetail dd = new DishDetail(lv4.get(7).substring(lv4.get(7).indexOf("#") + 1), food);
                    dd.Cnt = lv4.get(2);
                    dd.setAttach(getAttach(lv4.get(6)));
                    dd.setAttach(getAttach(lv4.get(7)));
                    result.getDishes().add(dd);
                } else {
                    //check = false;
                    //往数据库里加入该菜,待开发
                    //insert into lv4.get(0) lv4.get(1) lv4.get(3) lv4.get(4)
                }
            }
        }
        return result;
    }

    //获取开台帐单号
    public String getStart(String s) {
        String msg = getMsg(methodName.START[1], s);
        if (msg == null) return null;
        List<String> lv = commonMethods.getCells(":", msg);
        if ( lv.size() != 2 ) return null;
        return lv.get(1);
    }

}
