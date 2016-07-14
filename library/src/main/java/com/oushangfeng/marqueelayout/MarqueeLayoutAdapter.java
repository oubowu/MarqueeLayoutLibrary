package com.oushangfeng.marqueelayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Oubowu on 2016/6/24 11:19.
 */
public abstract class MarqueeLayoutAdapter<T> extends BaseAdapter {

    private List<T> mDatas;

    public MarqueeLayoutAdapter(List<T> datas) {
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
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
        final View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        initView(view, position, getItem(position));
        return view;
    }

    public abstract int getItemLayoutId();

    public abstract void initView(View view, int position, T item);

}
