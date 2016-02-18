package com.example.ephow.tabtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public abstract class CommonAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mDatas;
	protected LayoutInflater mInflater;
	private int layoutId;

	//layoutId用来选择不同的布局
	public CommonAdapter(Context context, List<T> datas, int layoutId) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = datas;
		//this.mDatas = datas != null ? datas : new ArrayList<T>();
		this.layoutId = layoutId;
	}
	//更新数据源
	public void upDatas(List<T> newdatas) { mDatas = newdatas;}

	@Override
	public int getCount() {
		return null != mDatas ? mDatas.size() : 0;
	}
	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);
		convert(holder, getItem(position));
		return holder.getConvertView();
	}
	//虚方法,待实现
	public abstract void convert(ViewHolder holder, T t);

}
