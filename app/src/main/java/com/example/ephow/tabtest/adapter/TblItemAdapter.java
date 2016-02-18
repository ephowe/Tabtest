package com.example.ephow.tabtest.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ephow.tabtest.MainActivity;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.food.CmenuService;
import com.example.ephow.tabtest.ui.FoodBillActivity;

/**
 * Created by ephow on 2015/9/27.
 */
public class TblItemAdapter extends BaseAdapter implements BaseCmenu, View.OnTouchListener, DialogInterface.OnClickListener {
    //布局控件类
    class ViewHolder {
        public ImageView state;
        public TextView des;
    }
    private LayoutInflater inflater;
    private SparseArray<TblResult> tbls;
    private Context context;
    private int clickTemp = -1;
    DisplayMetrics dm;

    //dialog
    private boolean misDialogClose;
    EditText metPax;
    AlertDialog mDialog;

    //
    //方法
    public TblItemAdapter(Context context,SparseArray<TblResult> tbls) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.tbls = tbls;
        //获取屏幕分辨率等信息到dm
        dm = new DisplayMetrics();
        ((Activity)(context)).getWindowManager().getDefaultDisplay().getMetrics(dm);
        ////////左菜单宽度
        dm.widthPixels *= 0.75f;

        mDialog = createDialog();
    }

    //设置选择位置
    public void SetSeclection(int position) {
        clickTemp = position;
    }
    //更新数据源
    public void upDate(SparseArray<TblResult> tbls) { this.tbls = tbls;}
    @Override
    public int getCount()
    {
        return null != tbls ? tbls.size() : 0;
    }
    @Override
    public TblResult getItem(int position)
    {
        return tbls.valueAt(position);
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            //获取布局
            convertView = inflater.inflate(R.layout.fragment_tbllist_item, null);
            viewHolder = new ViewHolder();
            //从布局获取控件
            viewHolder.state = (ImageView)convertView.findViewById(R.id.it_tbl_state);
            viewHolder.des = (TextView)convertView.findViewById(R.id.it_tbl_des);
            //设置IV监听
            viewHolder.state.setOnTouchListener(this);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder)convertView.getTag();
        //设置imageview高度，解决图片大小不同的对齐问题
        ViewGroup.LayoutParams cursor_Params = viewHolder.state.getLayoutParams();
        ///////////////3列-4间隔
        cursor_Params.width = dm.widthPixels / 3 - 4;
        cursor_Params.height = cursor_Params.width;
        viewHolder.state.setLayoutParams(cursor_Params);
        int bk = Integer.parseInt(getItem(position).state);
        viewHolder.state.setBackgroundColor(CmenuService.COLORSTATE.getState(bk));
        viewHolder.state.setTag(getItem(position));
        //viewHolder.state.
        //viewHolder.state.setTag(foods.get(position));
        viewHolder.des.setText(getItem(position).des);
        return convertView;
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
                TblResult tbl = (TblResult)v.getTag();
                //保存台位信息到当前台位
                CmenuService.getInstance().setCurrentTbl(tbl);

                misDialogClose = true;

                if (tbl.state.equals("3")) {
                    //状态3义意不明确,暂空
                    break;
                } else if (tbl.state.equals("2")) {
                    //开台需设置dialog确认
                    metPax.setText("1");
                    misDialogClose = false;
                    mDialog.show();
                }
                callBillActivity(misDialogClose);
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha(1f);
                break;
        }
        return true;
    }

    //dialog Listener
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case Dialog.BUTTON_POSITIVE:
                //dialog.dismiss();
                //mDialog.hide();
                break;
            case Dialog.BUTTON_NEGATIVE:
                int pax = Integer.parseInt(metPax.getText().toString());
                if (pax < 12) {
                    CmenuService.getInstance().getCurrentBill().pax = metPax.getText().toString();
                    callBillActivity(true);
                    //dialog.dismiss();
                    //mDialog.hide();
                }else {
                    Toast.makeText(context, "用餐人数过大", Toast.LENGTH_SHORT).show();
                }
                break;
            case Dialog.BUTTON_NEUTRAL:
                break;
        }
    }

    private AlertDialog createDialog() {
        ////dialog
        View contentView = inflater.inflate(R.layout.popwin_is_start_tab, null);
        metPax = (EditText)contentView.findViewById(R.id.et_popwin_pax);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setTitle("确认开台").
                setView(contentView.findViewById(R.id.layout_dialog)).
                setPositiveButton("取消", this).
                setNegativeButton("确定", this).create();
    }

    private void callBillActivity(boolean b) {
        if ( MainActivity.getThis() != null && b) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.getThis(), FoodBillActivity.class);
            MainActivity.getThis().startActivity(intent);
        }
    }

}




