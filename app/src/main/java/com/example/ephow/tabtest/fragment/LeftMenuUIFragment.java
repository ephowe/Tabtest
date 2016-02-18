package com.example.ephow.tabtest.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ephow.tabtest.adapter.CommonAdapter;
import com.example.ephow.tabtest.adapter.TblItemAdapter;
import com.example.ephow.tabtest.adapter.ViewHolder;
import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.food.CmenuService;
import com.example.ephow.tabtest.food.CmenuWebServices;
import com.example.ephow.tabtest.ui.CoverActivity;
import org.ksoap2.serialization.SoapObject;
import java.util.List;


public class LeftMenuUIFragment extends Fragment implements BaseCmenu{

    private final int REFRESH = 16;

    private String mLoadingWords;
    private TextView tName;
    private Handler mHandler = null;

    //GridView tblGView;
    //TblItemAdapter madapter = null;

    private EditText metFoodSerach;
    private ListView mlvFoodSerach;
    private CommonAdapter mCommonAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_leftmenu, container, false);
        Button button = (Button)rootView.findViewById(R.id.bt_click);
        //Button button2 = (Button)rootView.findViewById(R.id.bt_tbllist);
        tName = (TextView) rootView.findViewById(R.id.tv_intro);
        //tblGView = (GridView)rootView.findViewById(R.id.lm_tbl_gd_view);

        mlvFoodSerach = (ListView)rootView.findViewById(R.id.leftmenu_lv_food_serach);
        mlvFoodSerach.setAdapter(mCommonAdapter = new CommonAdapter<Food>(getActivity(),
                CmenuService.getInstance().getSqlite().getFoods(null, true),
                R.layout.cmenu_food_bill_list) {
            @Override
            public void convert(ViewHolder holder, Food f) {
                holder.setVisible(R.id.layout_bill, false).setVisible(R.id.layout_bill_attach, false);
                holder.setText(R.id.tv_bill_serach_des, f.Des)
                        .setText(R.id.tv_bill_serach_amt, " - " + f.Price + " 元 ")
                        .setVisible(R.id.layout_bill_serach_cnt, false);
            }
        });

        metFoodSerach = (EditText) rootView.findViewById(R.id.leftmenu_et_serach);
        metFoodSerach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    mCommonAdapter.upDatas(CmenuService.getInstance().getSqlite().getFoods(null, true));
                }else {
                    mCommonAdapter.upDatas(CmenuService.getInstance().searchFoods(s.toString()));
                }
                mCommonAdapter.notifyDataSetChanged();
            }
        });

        //同步更新text
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                // call update gui method.

                int what = msg.what;

                switch (what)
                {
                    case REFRESH:
                    {
                        // 刷新页面的文字
                        // 无法在其它线程中更新文本,只能通过HANDLE来更新
                        tName.setText(mLoadingWords);
                        break;
                    }

                }
            }
        };

        //this.set

        //设置监听
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /*
                Context context = v.getContext();
                context = context.getApplicationContext();
                //CmenuWebServices cmenu = new CmenuWebServices("192.168.0.104:8010");
                //Toast.makeText(context, cmenu.pLogin("15","15"), Toast.LENGTH_LONG).show();
                //tName.setText("111");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 下载
                        try {

                            //文件下载
                            CmenuFTP cftp = new CmenuFTP("192.168.0.114","21","ipad","ipad");
                            //new CmenuFTP()

                            cftp.DownloadAny("/booksystem",
                                    android.os.Environment.getExternalStorageDirectory().getAbsolutePath(), CmenuFTP.DOWN_DIRS, new String[]{".png", ".sqlite"},
                                    new CmenuFTP.ProgressListener() {
                                        //下载监听回调函数
                                        @Override
                                        public void onProgress(String currentStep) {

                                            mLoadingWords = currentStep;

                                            //发送同步信号
                                            mHandler.sendEmptyMessage(REFRESH);
                                        }

                                    });

                            //cftp.DeleteFiles(new File( android.os.Environment.getExternalStorageDirectory() + "/test" ));


                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                            mLoadingWords = e.toString();
                            mHandler.sendEmptyMessage(REFRESH);
                        }

                    }
                }).start();
                */
                Intent intent = new Intent(getActivity(), CoverActivity.class);
                //intent.
                startActivity(intent);
            }
        });




        /*
        //设置监听
        button2.setOnClickListener(new Button.OnClickListener()

        {

            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                //Context context = v.getContext();
                //context = context.getApplicationContext();

                //CmenuWebServices cmenu = new CmenuWebServices("192.168.0.104:8010");
                //Toast.makeText(context, cmenu.pLogin("15","15"), Toast.LENGTH_LONG).show();
                //tName.setText("111");

                String ip =null;//= CmenuService.getInstance().getIP();
                String user =null;
                //CmenuService a = new CmenuService();

                ip = CmenuService.getInstance().getJson().getValue("ftp", "addr");
                user = CmenuService.getInstance().getJson().getValue("login", "user");

                if (ip != null){
                    //设置需要用到的WEBSERVICE的方法
                    SoapObject cmethod = new SoapObject(CmenuWebServices.PACE, CmenuService.methodName.LISTTABLE[0]);
                    cmethod.addProperty("User", user);
                    cmethod.addProperty("Floor","");
                    cmethod.addProperty("Area","");
                    cmethod.addProperty("Status","");
                    cmethod.addProperty("PdaID","8-1");
                    cmethod.addProperty("iRecNo","");

                    RPC_Methods mts = CmenuService.getInstance().getWebService();
                    String s = mts.invoke_RPC(cmethod);

                    if (madapter == null) {
                        madapter = new TblItemAdapter(getActivity(), CmenuService.getInstance().getListTable(s));
                        tblGView.setAdapter(madapter);
                    }else {
                        //更新数据源,刷新item
                        //madapter.upDatas(tblrs);
                        madapter.notifyDataSetChanged();
                    }
                }
            }

        });
        */

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub  
        super.onActivityCreated(savedInstanceState);
    }

}  