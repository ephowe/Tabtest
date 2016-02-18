package com.example.ephow.tabtest.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import com.example.ephow.tabtest.R;



/**
 * Created by ephow on 2015/10/6.
 */
public class BaseActivity extends AppCompatActivity {

    //private Handler handler = new Handler();
    protected Toolbar mToolbar;
    protected DisplayMetrics dm;
    private static Activity mActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }


    /**
     * 屏幕旋转时调用此方法
     *
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //待加功能
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        //屏幕切换横竖时，获取屏幕分辨率等信息到dm
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "现在是竖屏", Toast.LENGTH_SHORT).show();
        }
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            //Toast.makeText(this, "现在是横屏", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void setToolbar(String caption) {
        //获取TOOLBAR
        mToolbar = (Toolbar)findViewById(R.id.m_toolbar);
        mToolbar.setTitle(caption); //设置Toolbar标题
        mToolbar.setTitleTextColor(Color.argb(200, 255, 255, 255)); //设置标题颜色\
        mToolbar.setBackgroundColor(Color.argb(200, 200, 100, 100));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置返回键监听
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static Activity getThis() { return mActivity;}

}
