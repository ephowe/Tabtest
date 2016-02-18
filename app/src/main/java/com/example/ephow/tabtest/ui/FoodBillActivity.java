package com.example.ephow.tabtest.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.adapter.CommonAdapter;
import com.example.ephow.tabtest.adapter.FoodBillAdapter;
import com.example.ephow.tabtest.adapter.ViewHolder;
import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.food.CmenuService;
import com.example.ephow.tabtest.food.CmenuWebServices;

import org.ksoap2.serialization.SoapObject;

/**
 * Created by ephow on 2015/10/6.
 */
public class FoodBillActivity extends BaseActivity implements BaseCmenu { //AppCompatActivity {
    private final int RESUCCESS = 1024;
    private final int RERELASH = 1026;
    private final int REFAIL = 1028;
    private final int RESENDTAB = 1030;

    private Handler mHandler;
    private FoodBillActivity mFoodBillAct;
    private FoodBillAdapter mFBAdapter;
    private CommonAdapter mFoodAdapter, mAttachAdapter;
    private ListView mLView;
    private Spinner mspFoods, mspAttachs;
    private EditText metSerachFood, metSerachAttach, metDDCnt;
    private Button mbtAddAttach, mbtAddFood, mbtSend;
    private TextView mtvAttachs;

    private Attach[] mAttachs;
    private String mAttachInfo = "";
    private int mIdx = 0;

    private TblResult mTbl;
    private Bill mBill;
    private DishDetail mCurrentDD;
    private int mDDIdx = 0;
    boolean misSend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodbill);

        mFoodBillAct = this;

        resetAttachs();
        iniView();
        iniAdapter();
        iniListeners();

        mTbl = CmenuService.getInstance().getCurrentTbl();
        if (mTbl != null) {

            //刷新UI信息
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg){
                    // call update gui method.
                    switch (msg.what) {
                        case RESUCCESS: {
                            //无法在其它线程中更新adapter,只能通过HANDLE来更新
                            mFoodBillAct.mToolbar.setTitle(mTbl.des + " - 帐单详情 - " + mBill.id);
                        }
                        case RERELASH: {
                            //更新数据源
                            mFBAdapter.upDatas(mBill.getDishes());
                            //等待刷新菜品
                            mFBAdapter.notifyDataSetChanged();
                            break;
                        }
                        case REFAIL: {
                            mFoodBillAct.mToolbar.setTitle(mTbl.des + " - 获取帐单详情失败!");
                            break;
                        }
                        case RESENDTAB: {
                            mFoodBillAct.mToolbar.setTitle(mTbl.des + " - 菜品发送成功!");
                            mtvAttachs.setText(mAttachInfo);
                            break;
                        }
                    }
                }
            };

            //如果台位状态为红色则开启线程通过WEBSERVICE查询该台位的帐单
            if (mTbl.state.equals("1")) {
                startQueryThread();
            } else if (mTbl.state.equals("2") ) {
                //状态2
                mBill = CmenuService.getInstance().getCurrentBill();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //如该台位是红色，则获取该台位帐单
                        SoapObject cmethod = new SoapObject(CmenuWebServices.PACE, methodName.START[0]);
                        cmethod.addProperty("PdaID", CmenuService.getInstance().getPdaid());
                        cmethod.addProperty("User", "5555");
                        cmethod.addProperty("Acct", "");
                        cmethod.addProperty("TblInit", mTbl.init);
                        cmethod.addProperty("Pax", mBill.pax);
                        cmethod.addProperty("Water", CmenuService.getInstance().getUser());
                        String result = CmenuService.getInstance().getWebService().invoke_RPC(cmethod);

                        if (result.equals("调用异常.")) {
                            //提示
                        } else if (result.equals("超时无应答.")) {
                            //提示
                        } else {
                            String ret = CmenuService.getInstance().getStart(result);
                            if (ret.equals("开台错误")) {
                                startQueryThread();
                            } else {
                                mBill.id = ret;
                                //保存未发送帐单
                                CmenuService.getInstance().saveBill(Integer.parseInt(mTbl.tbl), mBill);
                                mHandler.sendEmptyMessage(RESUCCESS);
                            }
                        }
                    }
                }).start();
            } else if (mTbl.state.equals("4")) {
                mBill = CmenuService.getInstance().getBill(Integer.parseInt(mTbl.tbl));
                if (mBill != null)  {
                    mHandler.sendEmptyMessage(RESUCCESS);
                }else {
                    startQueryThread();
                }
            }

            ///////////////////
        }

    }

    @Override
    protected void onDestroy() {
        //关闭窗口时删除当前帐单
        CmenuService.getInstance().delCurrentBill();
        super.onDestroy();
    }

    //重置附加项
    private void resetAttachs() {
        if (mAttachs != null) for (Attach attach: mAttachs) attach = null;
        mAttachs = new Attach[5];
        mAttachInfo = "附加项:";
        mIdx = 0;
    }

    //查询台位线程
    private void startQueryThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //如该台位是红色，则获取该台位帐单
                SoapObject cmethod = new SoapObject(CmenuWebServices.PACE, methodName.QUERY[0]);
                cmethod.addProperty("PdaID", CmenuService.getInstance().getPdaid());
                cmethod.addProperty("User", "5555-8020");
                cmethod.addProperty("TblInit", mTbl.init);
                cmethod.addProperty("iRecNo", "0");
                String result = CmenuService.getInstance().getWebService().invoke_RPC(cmethod);

                if (result.equals("调用异常.")) {
                    //提示
                } else if (result.equals("超时无应答.")) {
                    //提示
                } else {
                    mBill = CmenuService.getInstance().getQuery(result);
                    if (mBill != null) {
                        //如果已开台点菜,则删除旧帐单
                        //mBill.id.equals()
                        CmenuService.getInstance().delBill(Integer.parseInt(mTbl.tbl));
                        mHandler.sendEmptyMessage(RESUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(REFAIL);
                    }
                }
            }
        }).start();
    }

    //从布局获取控件
    private void iniView() {
        setToolbar("点菜");
        mLView = (ListView)findViewById(R.id.bill_list_view);
        mspFoods = (Spinner)findViewById(R.id.sp_foodbill_food);
        mspAttachs = (Spinner)findViewById(R.id.sp_foodbill_attach);
        metSerachFood = (EditText)findViewById(R.id.et_foodbill_serach_food);
        metSerachAttach = (EditText)findViewById(R.id.et_foodbill_serach_attach);
        metDDCnt = (EditText)findViewById(R.id.et_foodbill_food_cnt);
        mbtAddAttach = (Button)findViewById(R.id.bt_foodbill_add_attach);
        mbtAddFood = (Button)findViewById(R.id.bt_foodbill_add_food);
        mbtSend = (Button)findViewById(R.id.bt_foodbill_send);
        mtvAttachs = (TextView)findViewById(R.id.tv_foodbill_attachs);
    }

    //设置控件适配器
    private void iniAdapter() {
        mLView.setAdapter(mFBAdapter = new FoodBillAdapter(this, null, R.layout.cmenu_food_bill_list));
        mspFoods.setAdapter(mFoodAdapter = new CommonAdapter<Food>(this,
                CmenuService.getInstance().getSqlite().getFoods(null, true),
                R.layout.cmenu_food_bill_list) {
            @Override
            public void convert(ViewHolder holder, Food food) {
                holder.setVisible(R.id.layout_bill, false).setVisible(R.id.layout_bill_attach, false)
                        .setVisible(R.id.layout_bill_serach_cnt, false)
                        .setVisible(R.id.tv_bill_serach_amt, false);
                holder.setText(R.id.tv_bill_serach_des, food.Des);
                        //.setText(R.id.tv_bill_serach_amt, " - " + food.Price + " 元 ");
            }
        });
        mspAttachs.setAdapter(mAttachAdapter = new CommonAdapter<Attach>(this,
                CmenuService.getInstance().getSqlite().getAttachs(),
                R.layout.cmenu_food_bill_list) {
            @Override
            public void convert(ViewHolder holder, Attach attach) {
                holder.setVisible(R.id.layout_bill, false)
                        .setVisible(R.id.layout_bill_serach, false);
                holder.setText(R.id.tv_bill_attach_des, attach.Des)
                        .setVisible(R.id.tv_bill_attach_amt, false);
            }
        });
    }

    //设置控件监听
    private void iniListeners() {
        metSerachFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    mFoodAdapter.upDatas(CmenuService.getInstance().getSqlite().getFoods(null, true));
                } else {
                    mFoodAdapter.upDatas(CmenuService.getInstance().searchFoods(s.toString()));
                }
                mFoodAdapter.notifyDataSetChanged();
            }
        });
        metSerachAttach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    mAttachAdapter.upDatas(CmenuService.getInstance().getSqlite().getAttachs());
                }else {
                    mAttachAdapter.upDatas(CmenuService.getInstance().searchAttachs(s.toString()));
                }
                mAttachAdapter.notifyDataSetChanged();
            }
        });
        mbtAddAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdx < mAttachs.length && mBill != null) {
                    Attach attach = (Attach) mspAttachs.getSelectedItem();
                    mAttachs[mIdx++] = attach;
                    mAttachInfo += " " + attach.Des;
                    mtvAttachs.setText(mAttachInfo);
                }
            }
        });
        mbtAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBill != null && mDDIdx <= mBill.getDishes().size()) {
                    mCurrentDD = new DishDetail((Food) mspFoods.getSelectedItem());
                    mCurrentDD.Cnt = metDDCnt.getText().toString();
                    mCurrentDD.Color = 0xF03232FF;  //蓝色
                    for (Attach attach: mAttachs) {
                        if (attach != null) {
                            if (!mCurrentDD.setAttach(attach)) break;
                        }
                    }
                    mBill.getDishes().add(mDDIdx++, mCurrentDD);
                    //刷新列表
                    mFBAdapter.notifyDataSetChanged();
                    resetAttachs();
                    mtvAttachs.setText(mAttachInfo);
                }
            }
        });
        mbtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!misSend && mBill != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //传菜
                            SoapObject cmethod = new SoapObject(CmenuWebServices.PACE, methodName.SENDTAB[0]);
                            cmethod.addProperty("PdaID", CmenuService.getInstance().getPdaid());
                            cmethod.addProperty("User", "5555-8020");
                            cmethod.addProperty("PdaSerial", CmenuService.getInstance().incPdaSerial());
                            cmethod.addProperty("Acct", mBill.id != null ? mBill.id : "");
                            cmethod.addProperty("TblInit", mTbl.init);
                            cmethod.addProperty("Water", CmenuService.getInstance().getUser());
                            cmethod.addProperty("Pax", mBill.pax);
                            cmethod.addProperty("zCnt", mBill.getDishes().size());
                            cmethod.addProperty("Typ", mBill.type);
                            cmethod.addProperty("sbBuffer", mBill.packageDishes());
                            String result = CmenuService.getInstance().getWebService().invoke_RPC(cmethod);

                            if (result.equals("调用异常.")) {
                                //提示
                            } else if (result.equals("超时无应答.")) {
                                //提示
                            } else {
                                mAttachInfo = CmenuService.getInstance().getMsg(methodName.SENDTAB[1], result);
                                misSend = true;
                                mBill = null;
                                CmenuService.getInstance().delCurrentBill();
                                mHandler.sendEmptyMessage(RESENDTAB);
                            }
                        }
                    }).start();
                }
            }
        });
    }





}
