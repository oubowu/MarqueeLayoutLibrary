##一个支持四个方向循环滚动的自定义控件

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MarqueeLayoutLibrary-green.svg?style=true)](https://android-arsenal.com/details/1/3795)

### 效果图
![](/pic/demo.gif) 

### 使用
```groovy
compile 'com.oushangfeng:MarqueeLayout:1.0.6'
```

### 属性
|Attribute 属性          | Description 描述 |
|:---				     |:---|
|  switchTime | 多久滚动一次的时间间隔 |
| scrollTime | 一次滚动的时间 |
| orientation | 滚动的方向，有up、down、left、right四种方向 |
| enableAlphaAnim | 是否开启滑动时子View的透明度渐变 |
| enableScaleAnim | 是否开启滑动时子View的缩放渐变 |

### XML
```
    <com.oushangfeng.marqueelayout.MarqueeLayout
        android:id="@+id/marquee_layout"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#118866"
        app:enableAlphaAnim="true"
        app:enableScaleAnim="false"
        app:orientation="up">

    </com.oushangfeng.marqueelayout.MarqueeLayout>

    <com.oushangfeng.marqueelayout.MarqueeLayout
        android:id="@+id/marquee_layout1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@+id/marquee_layout"
        android:background="#dcdcdc"
        app:enableAlphaAnim="true"
        app:enableScaleAnim="true"
        app:orientation="left">

    </com.oushangfeng.marqueelayout.MarqueeLayout>

    <Button
        android:id="@+id/bt_delete"
        android:onClick="deleteSrc"
        android:text="删除歌词"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <Button
        android:onClick="addSrc"
        android:text="添加歌词"
        android:layout_above="@+id/bt_delete"
        android:layout_centerHorizontal="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```

### 填充布局和数据，点击事件可在InitViewCallBack中自己处理
```
        mMarqueeLayout = (MarqueeLayout) findViewById(R.id.marquee_layout);
        mSrcList = new ArrayList<>();
        mSrcList.add("我听见了你的声音 也藏着颗不敢见的心");
        mSrcList.add("我们的爱情到这刚刚好 剩不多也不少 还能忘掉");
        mSrcList.add("像海浪撞过了山丘以后还能撑多久 他可能只为你赞美一句后往回流");
        mSrcList.add("少了有点不甘 但多了太烦");
        mSrcAdapter = new MarqueeLayoutAdapter<String>(mSrcList) {
            @Override
            public int getItemLayoutId() {
                return R.layout.item_simple_text;
            }

            @Override
            public void initView(View view, int position, String item) {
                ((TextView) view).setText(item);
            }
        };
        mMarqueeLayout.setAdapter(mSrcAdapter);
        mMarqueeLayout.start();

        mMarqueeLayout1 = (MarqueeLayout) findViewById(R.id.marquee_layout1);
        final List<String> imgs = new ArrayList<>();
        imgs.add("http://img3.imgtn.bdimg.com/it/u=936722914,2010466745&fm=11&gp=0.jpg");
        imgs.add("http://img5.imgtn.bdimg.com/it/u=793061750,504065085&fm=11&gp=0.jpg");
        imgs.add("http://img5.imgtn.bdimg.com/it/u=506823331,38014690&fm=11&gp=0.jpg");
        imgs.add("http://h.hiphotos.baidu.com/baike/pic/item/2fdda3cc7cd98d10e6a5b4aa273fb80e7bec903c.jpg");
        MarqueeLayoutAdapter<String> adapter1 = new MarqueeLayoutAdapter<String>(imgs) {
            @Override
            public int getItemLayoutId() {
                return R.layout.item_simple_image;
            }

            @Override
            public void initView(View view, int position, String item) {
                Glide.with(view.getContext()).load(item).into((ImageView) view);
            }
        };
        // 设置点击事件，第二个参数为不定长id，为想要点击的view的id，若为空或者不传的话默认为点击最外层的view
        adapter1.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e("TAG", "MainActivity-74行-onClick(): " + position);
            }
        }, R.id.iv);        
        
        mMarqueeLayout1.setAdapter(adapter1);
        mMarqueeLayout1.start();
        
        // 删歌词
        public void deleteSrc(View view) {
            if (mSrcList.size() != 0) {
                mSrcList.remove(mSrcList.size() - 1);
                mSrcAdapter.notifyDataSetChanged();
            }
        }
        
        // 添加歌词
        public void addSrc(View view) {
            if (mSrcList != null) {
                Random random = new Random();
                mSrcList.add("添加歌词: " + random.nextInt(12345));
                mSrcAdapter.notifyDataSetChanged();
            }
        }
        
```

### 注意
```
主工程需要依赖support包
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




