package com.lljjcoder.style.cityjd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.CityBeanNew;
import com.lljjcoder.style.citypickerview.R;

import java.util.List;

import static com.lljjcoder.style.cityjd.JDConst.INDEX_INVALID;

/**
 * 作者：liji on 2018/1/29 17:01
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public class CityAdapterNew extends BaseAdapter {

    Context context;

    List<CityBeanNew> mCityList;

    private int cityIndex = INDEX_INVALID;

    public CityAdapterNew(Context context, List<CityBeanNew> mCityList) {
        this.context = context;
        this.mCityList = mCityList;
    }

    public int getSelectedPosition() {
        return this.cityIndex;
    }

    public void updateSelectedPosition(int index) {
        this.cityIndex = index;
    }

    @Override
    public int getCount() {
        return mCityList.size();
    }

    @Override
    public CityBeanNew getItem(int position) {
        return mCityList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return Long.parseLong(mCityList.get(position).getArea_code());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_jdcitypicker_item, parent, false);

            holder = new Holder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.selectImg = (ImageView) convertView.findViewById(R.id.selectImg);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        CityBeanNew item = getItem(position);
        holder.name.setText(item.getArea_name());

        boolean checked = cityIndex != INDEX_INVALID && mCityList.get(cityIndex).getArea_name().equals(item.getArea_name());
        holder.name.setEnabled(!checked);
        holder.selectImg.setVisibility(checked ? View.VISIBLE : View.GONE);


        return convertView;
    }


    class Holder {
        TextView name;
        ImageView selectImg;
    }
}
