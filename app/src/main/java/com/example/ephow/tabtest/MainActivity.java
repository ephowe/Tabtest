package com.example.ephow.tabtest;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.ephow.tabtest.adapter.TabFragmentPagerAdapter;
import com.example.ephow.tabtest.fragment.NavUIFragment;
import com.example.ephow.tabtest.fragment.TblListUIFragment;
import com.example.ephow.tabtest.ui.touchViewPager;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static Activity mActivity = null;

    private touchViewPager mViewPager;
    private RadioGroup mRadioGr;
    private TabFragmentPagerAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private DisplayMetrics dm;
    private static  String[] rbTitle = { "菜单", "台位"};    //RB标题

    ///
    TblListUIFragment mTbl;

    public static Activity getThis() { return mActivity;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        //获取屏幕分辨率等信息到dm
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        initView();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private void initView() {
        //获取DRAWERLAYOUT
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        //设置左侧菜单宽度为屏幕宽度的3/4
        ViewGroup.LayoutParams params = mDrawerLayout.getChildAt(1).getLayoutParams();
        params.width = (int)(dm.widthPixels * 0.75f);
        mDrawerLayout.getChildAt(1).setLayoutParams(params);
        //获取TOOLBAR
        toolbar = (Toolbar)findViewById(R.id.m_toolbar);
        toolbar.setTitle("点菜"); //设置Toolbar标题
        //toolbar.setTitleTextColor(Color.parseColor("#f0f0f")); //设置标题颜色
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置导航切换页面
        List<Fragment> fts = new ArrayList<>();
        fts.add(new NavUIFragment());
        fts.add(mTbl = new TblListUIFragment());
        //fts.add(new fragment);//待添加更多的页面
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), fts);
        //获取VP
        mViewPager = (touchViewPager)findViewById(R.id.mViewPager);
        mViewPager.setScanScroll(false);    //设为不能滑动
        mViewPager.setAdapter(mAdapter);

        //获取RG导航栏
        mRadioGr = (RadioGroup)findViewById(R.id.rg_bottom_nav);
        //RB监听, 负责切换对应fragment
        mRadioGr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getChildAt(checkedId) != null && mViewPager.getCurrentItem() != checkedId) {
                    //恢复RB的底色
                    for (int i = 0; i < group.getChildCount(); i++)
                        if (i != checkedId) group.getChildAt(i).setBackgroundColor(0xf0323232);
                    //设置选中的RB的底色
                    group.getChildAt(checkedId).setBackgroundColor(0xF0ACD215);
                    //fragment跟着换
                    mViewPager.setCurrentItem(checkedId);
                    //当前FRAGMENT页为显示台位的页面时允许线程刷新台位
                    if (mAdapter.getItem(checkedId) == mTbl) {
                        mTbl.setActivity(true);
                    } else {
                        mTbl.setActivity(false);
                    }
                }
            }
        });     //在check(0)之前设置好监听, 在check时会运行监听正确对应
        initNavRB(mRadioGr,rbTitle);
    }

    private boolean initNavRB(RadioGroup rg, String[] rbTitle) {
        boolean result =false;
        int width = dm.widthPixels / rbTitle.length ;
        if (rg != null) {
            rg.removeAllViews();
            for(int i=0;i<rbTitle.length;i++){
                RadioButton rb = (RadioButton)LayoutInflater.from(this).inflate(R.layout.nav_radiogroup_item, null);
                rb.setId(i);
                rb.setText(rbTitle[i]);
                rb.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
                rg.addView(rb);
            }
            //检查RG是否有RB
            if (rg.getChildCount() > 0) {
                //复位ViewGroup.mCheckedId, 保持按钮ID与显示内容ID的一致性。
                //例如rb当前为id=2, view.当前itemid=1,当按下id=2的RB时激活不了监听内容
                rg.check(0);
                result = true;
            }
        }
        return result;
    }

    private void initEvents() {
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //获取主界面view
                View mContent = mDrawerLayout.getChildAt(0);
                if (drawerView.getTag().equals("LEFT")) {
                    //移动主界面
                    ViewHelper.setTranslationX(mContent, drawerView.getMeasuredWidth() * slideOffset * 0.34f );
                }
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


}  