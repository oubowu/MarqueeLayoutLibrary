package com.oushangfeng.marqueelayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oubowu on 2016/6/24 11:19.
 */
public class MarqueeLayoutAdapter<T> {

    private ArrayList<View> mViews;

    private List<T> mDatas;

    public ArrayList<View> getViews() {
        return mViews;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setCustomView(Context context, int orientation, int layoutId, List<T> data, InitViewCallBack<T> callBack) {
        if (data == null) {
            return;
        } else if (data.size() > 1) {
            switch (orientation) {
                case MarqueeLayout.ORIENTATION_UP:
                case MarqueeLayout.ORIENTATION_LEFT:
                    data.add(data.get(0));
                    break;
                case MarqueeLayout.ORIENTATION_DOWN:
                case MarqueeLayout.ORIENTATION_RIGHT:
                    data.add(0, data.get(data.size() - 1));
                    break;
            }
        }

        mViews = new ArrayList<>(data.size());
        mDatas = data;

        for (int i = 0; i < data.size(); i++) {
            final View view = LayoutInflater.from(context).inflate(layoutId, null);
            if (callBack != null) {
                callBack.init(view, data.get(i));
            }
            mViews.add(view);
        }
    }

    public interface InitViewCallBack<T> {
        void init(View view, T item);
    }

}
