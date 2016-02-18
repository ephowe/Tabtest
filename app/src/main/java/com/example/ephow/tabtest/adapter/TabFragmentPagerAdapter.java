package com.example.ephow.tabtest.adapter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ephow.tabtest.fragment.CommonUIFragment;
import com.example.ephow.tabtest.fragment.NavUIFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ephow on 2015/9/27.
 */
public class TabFragmentPagerAdapter extends FragmentPagerAdapter { //<T extends Fragment>

    List<Fragment> fts;
    String[] titles = null;
    public static final String ARGUMENTS_NAME = "arg";
    //private Class<T> cls;

    public TabFragmentPagerAdapter(FragmentManager fm, String[] Titles) {
        super(fm);

        this.titles = Titles;
        fts = new ArrayList<>();

        //将所有的页面都载入内存
        for (int i=0; i< titles.length; i++) fts.add(new CommonUIFragment());
            /*
            try {
                fts.add(cls.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
    }

    public TabFragmentPagerAdapter(FragmentManager fm, List<Fragment> fts) {
        super(fm);
        this.fts = fts;
    }

    @Override
    public Fragment getItem(int arg0) {
        if (titles != null) {
            Bundle args = new Bundle();
            args.putString(ARGUMENTS_NAME, titles[arg0]);
            //给fragment加上名称
            fts.get(arg0).setArguments(args);
        }
        return fts.get(arg0);
    }

    @Override
    public int getCount() {
        return fts.size();
    }
}



