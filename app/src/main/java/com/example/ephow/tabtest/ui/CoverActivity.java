package com.example.ephow.tabtest.ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.food.CmenuFTP;
import com.example.ephow.tabtest.food.CmenuJson;
import com.example.ephow.tabtest.food.CmenuService;
import com.example.ephow.tabtest.food.CmenuWebServices;
import org.ksoap2.serialization.SoapObject;

import java.util.List;


/**
 * Created by ephow on 2015/10/4.
 */
public class CoverActivity extends AppCompatActivity implements BaseCmenu { //Activity {


    private final int REFRESH = 16;

    private String mLoadingWords;

    private Handler mHandler = null;

    private boolean downdone = false;

    private EditText et_ftp;
    private EditText et_user;
    private EditText et_pass;
    private TextView tName;

    private Button bt_update;
    private Button bt_return;
    private Button bt_del;
    private Button bt_sav;

    public Activity getThis() { return this;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);


        iniView();

        setListener();

        //同步更新text
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                // call update gui method.

                switch (msg.what)
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

    }


    void iniView() {
        bt_update = (Button)findViewById(R.id.bt_update);
        bt_return = (Button)findViewById(R.id.bt_return);
        bt_del = (Button)findViewById(R.id.bt_delmenu);
        bt_sav = (Button)findViewById(R.id.bt_sav_set);
        tName = (TextView) findViewById(R.id.tv_into);
        et_ftp = (EditText)findViewById(R.id.et_ftp);
        et_user = (EditText)findViewById(R.id.et_user);
        et_pass = (EditText)findViewById(R.id.et_password);
        {
            CmenuJson tjson = CmenuService.getInstance().getJson();
            et_ftp.setText(tjson.getValue("ftp", "addr"));
            et_user.setText(tjson.getValue("ftp", "user"));
            et_pass.setText(tjson.getValue("ftp", "pass"));
        }
    }

    void setListener() {

        bt_del.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //if (cftp != null) cftp.DeleteFiles(new File( android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/booksystem" ), null);

                //设置需要用到的WEBSERVICE的方法
                SoapObject cmethod = new SoapObject(CmenuWebServices.PACE,"pLogin");
                cmethod.addProperty("User", "5555" );
                cmethod.addProperty("Pass", "8020" );

                //CWS_Methods mts = new CmenuWebServices( et_ftp.getText().toString() + ":8010");
                mLoadingWords = CmenuService.getInstance().getWebService().invoke_RPC(cmethod);
                //mLoadingWords = mts.invoke_RPC(cmethod);
                mHandler.sendEmptyMessage(REFRESH);
            }
        });

        bt_sav.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                CmenuJson tjson = CmenuService.getInstance().getJson();
                tjson.setValue("webservice", "addr", et_ftp.getText().toString());
                tjson.setValue("webservice", "port", "8010");
                tjson.setValue("ftp", "addr", et_ftp.getText().toString());
                tjson.setValue("ftp", "user", et_user.getText().toString());
                tjson.setValue("ftp", "pass", et_pass.getText().toString());
                //保存到JSON文件
                tjson.saveJsonToFile(true);
            }
        });


        bt_return.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getThis(), MainActivity.class);
                //CoverActivity.this
                //startActivity(intent);
                //if (downdone) finish();
                //CmenuService.setIP(et_ftp.getText().toString());
                String s = "+query<tab:249116;Total$:271.5;String:5人 ;account:1833240^金牌酸菜鲈鱼^1^58.9^份^ ^ ^#57 ^&1833241^还想吃的虾米粉丝^1^22.9^份^ ^ ^#57 ^&1833242^蜂蜜芝士焗南瓜^1^22.9^份^ ^ ^#57 ^&1833243^二师兄的碳烧肉^1^38.9^份^ ^ ^#57 ^&1833244^铁板金针菇^1^18.9^份^ ^ ^#57 ^&1833245^金桔柠檬茶^1^9.9^杯^ ^ ^#57 ^&1833246^面包诱惑^1^28.9^份^ ^ ^#57 ^&1833247^烤菜年糕^1^19.9^份^ ^ ^#57 ^&1833248^尖椒土豆丝^1^9.9^份^ ^ ^#57 ^&1833249^湿巾^5^7.5^条^ ^ ^#57 ^&1833250^朗姆酒烤中翅^1^32.9^份^ ^ ^#57 ^&>";
                Bill m = CmenuService.getInstance().getQuery(s);

                //List<Food> d = CmenuService.getInstance().searchFoods("ly");
                //List<Attach> f = CmenuService.getInstance().searchAttachs("盐");

                DishDetail dd = new DishDetail(CmenuService.getInstance().getFood("金牌酸菜鲈鱼"));
                dd.setAttach(CmenuService.getInstance().getAttach("少盐"));
                String aa = dd.toString("|");

                finish();
            }
        });


        //设置监听
        bt_update.setOnClickListener(new Button.OnClickListener()

        {

            @Override

            public void onClick(View v) {
                // TODO Auto-generated method stub
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
                            downdone = false;
                            //文件下载
                            CmenuService.getInstance().getFtp()
                            //new CmenuFTP()
                            .DownloadAny("/booksystem",
                                    android.os.Environment.getExternalStorageDirectory().getAbsolutePath(), CmenuFTP.DOWN_DIRS, new String[]{".png", ".sqlite"},
                                    new CmenuFTP.ProgressListener() {
                                        //下载监听回调函数
                                        @Override
                                        public void onProgress(String currentStep) {
                                            mLoadingWords = currentStep;
                                            //发送同步信号
                                            mHandler.sendEmptyMessage(REFRESH);
                                            //tName.setText(mLoadingWords);
                                        }

                                    });
                            downdone = true;
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                            mLoadingWords = e.toString();
                            mHandler.sendEmptyMessage(REFRESH);
                        }
                    }
                }).start();
            }

        });
    }


}
