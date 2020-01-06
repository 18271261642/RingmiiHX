package com.guider.health.common.views.popdata;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.guider.health.common.R;


/**
 * Created by haix on 2019/7/2.
 */

public class DropDownMenu {

    private final OnListCkickListence mListener;  //item单击事件接口
    private static DropDownMenu instance;
    private static Context sContext;
    private PopupWindow popupWindow;
    private View dissmiss;                          //点击展开后的半透明挡板
    private Boolean showShadow = true;             //是否需要半透明挡板
    private String showName;                        //适配器中所需要显示内容的名字,仅在modifyText中有值时使用
    private String selectName;                      //搜索用的key
    private int itemNum = 4;
    private int indexColor;                         //设置外部所点击的View的颜色
    //private static int viewColor;                    //外部所点击的View本来的颜色


    public static DropDownMenu getInstance(Context context, OnListCkickListence mListener) {
        instance = new DropDownMenu(context,mListener);
        return instance;
    }


    private DropDownMenu(Context context, OnListCkickListence mListener) {
        sContext = context;
        this.mListener = mListener;
    }

    /***
     *
     * @param searchAdapter 设配器
     * @param item            listView的item
     * @param layout   含有ListView的布局文件
     * @param dropview  菜单弹出后在哪个View下
     * @param modifyText    点击后需要修改的TextView
     * @param type          点击了哪一个标签
     * @param menuSize           是否需要限制弹出菜单大小,若需要,则传true，默认为6个item高,通过setItemNum方法进行设定
     */
    public void showSelectList(final Madapter searchAdapter, View layout, View item, final TextView dropview, final TextView modifyText, final String type, final boolean menuSize) {

        final ListView listview = layout.findViewById(R.id.listview);

        //viewColor = dropview.getDrawingCacheBackgroundColor();

        if (menuSize && searchAdapter!=null && searchAdapter.getCount()!=0) {
            ViewGroup.LayoutParams para = listview.getLayoutParams();
            int width = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.EXACTLY);
            int height = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.EXACTLY);
            item.measure(width, height);
            item.getMeasuredWidth(); // 获取宽度
            item.getMeasuredHeight(); // 获取高度
            para.height = itemNum * (item.getMeasuredHeight()) + 5;
            listview.setLayoutParams(para);
        }else {
            listview.setLayoutParams( new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT));
        }

        if (showShadow) {
            dissmiss = (View) layout.findViewById(R.id.dissmiss);
            if (dissmiss != null) {
                dissmiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        //dropview.setBackgroundColor(viewColor);
                    }
                });
            }
        } else {
            dissmiss = (View) layout.findViewById(R.id.dissmiss);
            dissmiss.setVisibility(View.GONE);
        }


        listview.setAdapter(searchAdapter);
//        int position = -1;
//        List<?> items = searchAdapter.getItems();
//        for (int i = 0; i < items.size(); i++) {
//            if (items.get(i) instanceof String) {
//                String s = (String) items.get(i);
//                if (s.equals(currentValue)) {
//                    position = i;
//                    break;
//                }
//            }
//        }
//        if (position < 0) {
//            position = searchAdapter.getCount() / 2;
//        }
//        listview.setSelection(position);
        listview.setSelection(searchAdapter.getCount() / 2);
        listview.deferNotifyDataSetChanged();

        popupWindow = new PopupWindow(layout, FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, true);
        /**
         * 有了mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
         * 这句可以使点击popupwindow以外的区域时popupwindow自动消失 但这句必须放在showAsDropDown之前
         */
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        /**
         * popupwindow的位置，第一个参数表示位于哪个控件之下 第二个参数表示向左右方向的偏移量，正数表示向左偏移，负数表示向右偏移
         * 第三个参数表示向上下方向的偏移量，正数表示向下偏移，负数表示向上偏移
         *
         */

//        if (indexColor != 0){
//            dropview.setBackgroundColor(indexColor);
//        }else {
//            dropview.setBackgroundColor(viewColor);
//        }
        popupWindow.showAsDropDown(dropview, 0, 0);// 在控件下方显示popwindow
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //dropview.setBackgroundColor(viewColor);
            }
        });
        popupWindow.update();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                                                searchAdapter.setSelectedPosition(position);
                                                searchAdapter.notifyDataSetInvalidated();
//                                                if(modifyText != null){
//                                                    modifyText.setText(searchAdapter.getShowKey(position,showName));
//                                                }
                                                mListener.search(position, type);
                                                mListener.changeSelectPanel(searchAdapter,dropview);
                                                popupWindow.dismiss();
                                                if (modifyText != null) {
                                                    modifyText.setText((CharSequence) searchAdapter.getItems().get(position));
                                                }
                                            }
                                        }
        );
    }

    public void setShowShadow(Boolean showShadow) {
        this.showShadow = showShadow;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    public void setIndexColor(int color){
        indexColor = sContext.getResources().getColor(color);
    }

    public interface OnListCkickListence {
        void search(int position, String type);  //根据选择的数据进行查询
        void changeSelectPanel(Madapter madapter, View view); //修改选中后item的颜色，以及点击后对View进行一些修改
    }
}

