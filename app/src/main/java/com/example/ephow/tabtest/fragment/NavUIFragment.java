package com.example.ephow.tabtest.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ephow.tabtest.MainActivity;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.adapter.TabFragmentPagerAdapter;
import com.example.ephow.tabtest.food.CmenuService;
import com.example.ephow.tabtest.ui.SyncHorizontalScrollView;
import com.example.ephow.tabtest.ui.touchViewPager;


public class NavUIFragment extends Fragment {

    View rootView;
    private touchViewPager tabViPager;
    private SyncHorizontalScrollView mHsv;
    private TabFragmentPagerAdapter mAdapter;

    private String[] tabTitle = { "选项1", "选项2", "选项3", "选项4", "选项5" };    //标题

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null || tabViPager == null || mHsv == null ) {
            //获取VIEW
            rootView = inflater.inflate(R.layout.fragment_nav_common, container, false);
            //获取viewpageer
            tabViPager = (touchViewPager) rootView.findViewById(R.id.navViewPager);
            //navViPager.setScanScroll(false);
            //获取导航栏
            mHsv = (SyncHorizontalScrollView) rootView.findViewById(R.id.mHsv);
            //获取导航菜单类别
            tabTitle = CmenuService.getInstance().getSqlite().getNavMenu();
            mHsv.setSomeParam(tabViPager, getActivity(), tabTitle , 4);
            //标记
            mAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(), tabTitle);
            tabViPager.setAdapter(mAdapter);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub  
        super.onActivityCreated(savedInstanceState);
    }

}  