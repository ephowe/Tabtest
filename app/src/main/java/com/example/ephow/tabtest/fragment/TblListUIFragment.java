package com.example.ephow.tabtest.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.adapter.TblItemAdapter;
import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.food.CmenuService;
import com.example.ephow.tabtest.food.CmenuWebServices;
import org.ksoap2.serialization.SoapObject;

import java.lang.Thread.State;

public class TblListUIFragment extends Fragment implements BaseCmenu{

    GridView tblGView;
    private final int REFRESH = 16;
    private Handler mHandler = null;
    private Thread mThread = null;

    String mIp;
    String mUser;
    SoapObject mCmethod;

    TblItemAdapter mAdapter;

    private boolean mRefresh = true;
    private boolean mAct = true;

    public void setActivity(boolean t) {
        if (mThread == null) return;
        mAct = t;
        State a = mThread.getState();
        if (a.equals(State.NEW) ) mThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tbllist, container, false);
        tblGView = (GridView)rootView.findViewById(R.id.it_tbl_gd_view);

        mIp = CmenuService.getInstance().getJson().getValue("ftp", "addr");
        //mUser = CmenuService.getInstance().getJson().getValue("login", "user");
        mUser = "5555";
        //设置需要用到的WEBSERVICE的方法
        mCmethod = new SoapObject(CmenuWebServices.PACE, methodName.LISTTABLE[0]);
        mCmethod.addProperty("User", mUser);
        mCmethod.addProperty("Floor", "");
        mCmethod.addProperty("Area", "");
        mCmethod.addProperty("Status", "");
        mCmethod.addProperty("PdaID", "8-1");
        mCmethod.addProperty("iRecNo", "");
        //String s = CmenuService.getInstance().getWebService().invoke_RPC(mCmethod);
        String s = "+listtable<>";
        tblGView.setAdapter(mAdapter = new TblItemAdapter(getActivity(), CmenuService.getInstance().getListTable(s)));

        //刷新台位状态
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                // call update gui method.
                switch (msg.what) {
                    case REFRESH: {
                        //无法在其它线程中更新adapter,只能通过HANDLE来更新
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        };
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRefresh) {
                    //设置需要用到的WEBSERVICE的方法
                    if (mAct & mIp != null) {
                        String s = CmenuService.getInstance().getWebService().invoke_RPC(mCmethod);
                        if (s.equals("调用异常.")) {
                            //提示
                        } else if (s.equals("超时无应答.")) {
                            //提示
                        } else {
                            CmenuService.getInstance().getListTable(s);
                            mHandler.sendEmptyMessage(REFRESH);
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub  
        super.onActivityCreated(savedInstanceState);
    }


}  