package com.example.ephow.tabtest.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ephow.tabtest.MainActivity;
import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.function.ImageLoader;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.ui.FoodItemActivity;

import java.util.List;

/**
 * Created by ephow on 2015/9/27.
 */
public class FoodItemAdapter extends CommonAdapter<BaseCmenu.Food> implements BaseCmenu, View.OnTouchListener{
    private static Food mCurrentFood = null;
    private static ImageLoader mILoader = null;
    private int clickTemp = -1;

    //
    //方法
    public FoodItemAdapter(Context context,List<Food> Foods, int layoutId, String path) {
        super(context, Foods, layoutId);
        //一个适配器对应一个图片加载器
        mILoader = new ImageLoader( mDatas.size() > 30 && mDatas.size() < 100  ? mDatas.size() : 10 , path);
    }
    //设置选择位置
    public void SetSeclection(int position) {
        clickTemp = position;
    }
    public static ImageLoader getImgLoader() {
        return mILoader;
    }
    public static Food getCurrentFood() {
        return mCurrentFood;
    }

    @Override
    public void convert(ViewHolder holder, Food food) {
        ImageView vSmallPic = holder.getView(R.id.it_food_small_pic);
        ViewGroup.LayoutParams cursor_Params = vSmallPic.getLayoutParams();
        cursor_Params.height = 250;
        cursor_Params.width = 250;
        vSmallPic.setLayoutParams(cursor_Params);
        //imageview绑定对应的food
        vSmallPic.setTag(food);
        //设置监听
        vSmallPic.setOnTouchListener(this);
        //加载图片
        mILoader.loadImage(vSmallPic, food.PicsmallId);
        //设置文字信息
        holder.setText(R.id.it_food_des, food.Des)
                .setText(R.id.it_price_val, food.Price)
                .setText(R.id.it_member_price_val, food.Memberprice)
                .setText(R.id.it_price_unit, "元/" + food.Unit)
                .setText(R.id.it_member_price_unit, "元/" + food.Unit);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setAlpha(.8f);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                v.setAlpha(1f);
                //在此处添加跳转到其它页面的代码
                mCurrentFood = (Food) v.getTag();
                Intent intent = new Intent();
                intent.setClass(MainActivity.getThis(), FoodItemActivity.class);
                MainActivity.getThis().startActivity(intent);
                //MainActivity.getThis().finish();
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha(1f);
                break;
        }
        return true;
    }

}




