package com.oushangfeng.marqueelayout;

import android.support.annotation.Nullable;
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

    private OnItemClickListener mItemClickListener;
    private int[] mClickIds;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        initView(view, position, getItem(position));
        if (mItemClickListener != null) {
            if (mClickIds == null || mClickIds.length == 0) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onClick(v, position);
                    }
                });
            } else {
                for (int id : mClickIds) {
                    final View child = view.findViewById(id);
                    if (child != null) {
                        child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mItemClickListener.onClick(v, position);
                            }
                        });
                    }
                }
            }
        }
        return view;
    }

    protected abstract int getItemLayoutId();

    protected abstract void initView(View view, int position, T item);

    /**
     * 设置点击世界
     * @param itemClickListener 监听
     * @param ids 点击的view的id，若为空的话默认点击最外层的view
     */
    public void setItemClickListener(OnItemClickListener itemClickListener, @Nullable int... ids) {
        mItemClickListener = itemClickListener;
        mClickIds = ids;
    }
}
