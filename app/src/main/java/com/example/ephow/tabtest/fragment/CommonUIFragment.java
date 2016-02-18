package com.example.ephow.tabtest.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.example.ephow.tabtest.adapter.FoodItemAdapter;
import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.food.CmenuService;

public class CommonUIFragment extends Fragment implements BaseCmenu{    //implements GridView.OnItemClickListener {

    GridView gridView;
    FoodItemAdapter mAdapter;
    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null || mAdapter == null || gridView == null ){

            //获取VIEW
            rootView = inflater.inflate(R.layout.fragment_selection_common, container, false);
            //获取GRIDVIEW
            gridView = (GridView) rootView.findViewById(R.id.it_fod_gd_view);
            //找到对应的菜类就加载该菜类下的菜品
            Cls cls = CmenuService.getInstance().getCls(getArguments().getString("arg", ""));

            mAdapter = new FoodItemAdapter(getActivity(), cls.getFoods(), R.layout.cmenu_food_item_list,
                    CmenuService.getInstance().getSqlite().DBPATH);
            gridView.setAdapter(mAdapter);
            //暂时不进行ITEM监听，转而在GRIDVIEW.IV中进行监听
            //gridView.setOnItemClickListener(this);
        }
        //切换activity
        //getActivity().startActivity(intent);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub  
        super.onActivityCreated(savedInstanceState);
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //parent.
        //view.setBackgroundColor(0x7fffffff);
        //mAdapter.SetSeclection(position);
        //mAdapter.notifyDataSetChanged();
    }
    */

}  