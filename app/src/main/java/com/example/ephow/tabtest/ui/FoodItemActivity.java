package com.example.ephow.tabtest.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.ephow.tabtest.R;
import com.example.ephow.tabtest.adapter.CommonAdapter;
import com.example.ephow.tabtest.adapter.FoodItemAdapter;
import com.example.ephow.tabtest.adapter.ViewHolder;
import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.food.CmenuService;

import java.util.List;

/**
 * Created by ephow on 2015/10/6.
 */
public class FoodItemActivity extends BaseActivity implements BaseCmenu { //AppCompatActivity {
    ImageView bigpic;
    TextView des;
    TextView price;
    TextView memberprice;
    TextView priceunit;
    TextView memberpriceunit;
    private Spinner mspAttach;
    private EditText metSerach;
    private CommonAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooditem);
        //inflater = LayoutInflater.from(this);
        //view = inflater.inflate(R.layout.cmenu_food_item_list, null);
        //smallipic = (ImageView) findViewById(R.id.it_food_small_pic);
        /*
        des = (TextView) findViewById(R.id.it_food_des);
        price = (TextView) findViewById(R.id.it_price_val);
        memberprice = (TextView) findViewById(R.id.it_member_price_val);
        priceunit = (TextView) findViewById(R.id.it_price_unit);
        memberpriceunit = (TextView) findViewById(R.id.it_member_price_unit);
        */
        //this.
        setToolbar("菜品详情");
        bigpic = (ImageView)findViewById(R.id.iv_big_pic);
        if ( FoodItemAdapter.getCurrentFood() != null && FoodItemAdapter.getImgLoader() != null) {
            //加载菜品大图片
            bigpic.setTag(FoodItemAdapter.getCurrentFood());
            FoodItemAdapter.getImgLoader().loadImage(bigpic, FoodItemAdapter.getCurrentFood().PicbigId);
        }

        mspAttach = (Spinner)findViewById(R.id.sp_attach);
        mspAttach.setAdapter(mAdapter = new CommonAdapter<Attach>(this,
                CmenuService.getInstance().getSqlite().getAttachs(),
                R.layout.cmenu_food_bill_list) {
            @Override
            public void convert(ViewHolder holder, Attach attach) {
                holder.setVisible(R.id.layout_bill, false)
                        .setVisible(R.id.layout_bill_serach, false);
                holder.setText(R.id.tv_bill_attach_des, attach.Des).setVisible(R.id.tv_bill_attach_amt, false);
            }
        });

        metSerach = (EditText)findViewById(R.id.et_serach);
        metSerach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    mAdapter.upDatas(CmenuService.getInstance().getSqlite().getAttachs());
                }else {
                    mAdapter.upDatas(CmenuService.getInstance().searchAttachs(s.toString()));
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        //Attach at = (Attach)mspAttach.getSelectedItem();


    }
}
