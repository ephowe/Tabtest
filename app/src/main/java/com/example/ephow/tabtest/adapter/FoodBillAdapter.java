package com.example.ephow.tabtest.adapter;

import android.content.Context;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.food.BaseCmenu;
import java.util.List;

/**
 * Created by ephow on 2015/9/27.
 */
public class FoodBillAdapter extends CommonAdapter<BaseCmenu.DishDetail> implements BaseCmenu {
    public FoodBillAdapter(Context context, List<DishDetail> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, DishDetail dd) {
        Float sub = Float.parseFloat(dd.getFood().Price) * Float.parseFloat(dd.Cnt);
        //设置文字信息
        holder.setText(R.id.tv_bill_des, dd.getFood().Des)
                .setText(R.id.tv_bill_cnt, " x " + dd.Cnt + " - ")
                .setText(R.id.tv_bill_price, dd.getFood().Price)
                .setText(R.id.tv_bill_unit, " 元/" + dd.getFood().Unit)
                .setText(R.id.tv_bill_sub, " 小计:" + Float.toString(sub) + " 元")
                .setVisible(R.id.layout_bill_serach, false);

        //有附加项就显示，没有就隐藏
        String attinfo = "附加项:";
        boolean bl = false;
        for (int i = 0; i < dd.ATTCNT; i++) {
            Attach att = dd.getAttach(i);
            if (att != null) {
                attinfo += " " + (i + 1) + "." + att.Des;
                bl = true;
            }
        }
        if (bl) {
            holder.setText(R.id.tv_bill_attach_des, attinfo)
                    .setVisible(R.id.tv_bill_attach_amt, false);
        } else {
            holder.setVisible(R.id.layout_bill_attach, false);
        }

        //设置字体颜色
        if (dd.Color != -1) {
            holder.setTextColor(R.id.tv_bill_des, dd.Color)
                    .setTextColor(R.id.tv_bill_cnt, dd.Color)
                    .setTextColor(R.id.tv_bill_price, dd.Color)
                    .setTextColor(R.id.tv_bill_unit, dd.Color)
                    .setTextColor(R.id.tv_bill_sub, dd.Color)
                    .setTextColor(R.id.tv_bill_attach_des, dd.Color);
        }

    }

}




