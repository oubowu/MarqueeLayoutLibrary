##一个支持四个方向循环滚动的自定义控件

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MarqueeLayoutLibrary-green.svg?style=true)](https://android-arsenal.com/details/1/3795)

### 效果图
![](/pic/demo.gif) 

### 使用
```groovy
compile 'com.oushangfeng:MarqueeLayout:1.0.1'
```

### 属性
|Attribute 属性          | Description 描述 |
|:---				     |:---|
|  switchTime | 多久滚动一次的时间间隔 |
| scrollTime | 一次滚动的时间 |
| orientation | 滚动的方向，有up、down、left、right四种方向 |

### XML
```
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:context="com.oushangfeng.marqueelayoutlibrary.MainActivity"
    tools:showIn="@layout/activity_main">
    
    <!--垂直方向向下滚动-->
    <com.oushangfeng.marqueelayout.MarqueeLayout
        android:id="@+id/marquee_layout"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:background="#118866"
        app:orientation="down">

    </com.oushangfeng.marqueelayout.MarqueeLayout>

    <!--水平方向向右滚动-->
    <com.oushangfeng.marqueelayout.MarqueeLayout
        android:id="@+id/marquee_layout1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@+id/marquee_layout"
        android:background="#dcdcdc"
        app:orientation="right">

    </com.oushangfeng.marqueelayout.MarqueeLayout>

</RelativeLayout>
```

### 填充布局和数据，点击事件可在InitViewCallBack中自己处理
```
 mMarqueeLayout = (MarqueeLayout) findViewById(R.id.marquee_layout);
 final List<String> list = new ArrayList<>();
 list.add("我听见了你的声音 也藏着颗不敢见的心");
 list.add("我们的爱情到这刚刚好 剩不多也不少 还能忘掉");
 list.add("像海浪撞过了山丘以后还能撑多久 他可能只为你赞美一句后往回流");
 list.add("少了有点不甘 但多了太烦");
 MarqueeLayoutAdapter<String> adapter = new MarqueeLayoutAdapter<>();
 adapter.setCustomView(mMarqueeLayout, R.layout.item_simple_text, list, new MarqueeLayoutAdapter.InitViewCallBack<String>() {
     @Override
     public void init(View view, String item) {
         ((TextView) view).setText(item);
     }
 });
 mMarqueeLayout.setAdapter(adapter);
 mMarqueeLayout.start();


 mMarqueeLayout1 = (MarqueeLayout) findViewById(R.id.marquee_layout1);
 final List<String> imgs = new ArrayList<>();
 imgs.add("http://img3.imgtn.bdimg.com/it/u=936722914,2010466745&fm=11&gp=0.jpg");
 imgs.add("http://img5.imgtn.bdimg.com/it/u=793061750,504065085&fm=11&gp=0.jpg");
 imgs.add("http://img5.imgtn.bdimg.com/it/u=506823331,38014690&fm=11&gp=0.jpg");
 imgs.add("http://h.hiphotos.baidu.com/baike/pic/item/2fdda3cc7cd98d10e6a5b4aa273fb80e7bec903c.jpg");
 MarqueeLayoutAdapter<String> adapter1 = new MarqueeLayoutAdapter<>();
 adapter1.setCustomView(mMarqueeLayout, R.layout.item_simple_image, imgs, new MarqueeLayoutAdapter.InitViewCallBack<String>() {
     @Override
     public void init(View view, String item) {
         Glide.with(view.getContext()).load(item).into((ImageView) view);
     }
 });
 mMarqueeLayout1.setAdapter(adapter1);
 mMarqueeLayout1.start();
```

#### License
```
Copyright 2016 oubowu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```




