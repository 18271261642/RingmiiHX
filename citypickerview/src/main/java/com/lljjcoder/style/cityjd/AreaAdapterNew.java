package com.lljjcoder.style.cityjd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.DistrictBeanNew;
import com.lljjcoder.style.citypickerview.R;

import java.util.List;

import static com.lljjcoder.style.cityjd.JDConst.INDEX_INVALID;

/**
 * 作者：liji on 2018/1/29 17:01
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public class AreaAdapterNew extends BaseAdapter {

    Context context;

    List<DistrictBeanNew> mDistrictList;

    private int districtIndex = INDEX_INVALID;

    public AreaAdapterNew(Context context, List<DistrictBeanNew> mDistrictList) {
        this.context = context;
        this.mDistrictList = mDistrictList;
    }


    public int getSelectedPosition() {
        return this.districtIndex;
    }

    public void updateSelectedPosition(int index) {
        this.districtIndex = index;
    }

    @Override
    public int getCount() {
        return mDistrictList.size();
    }

    @Override
    public DistrictBeanNew getItem(int position) {
        return mDistrictList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return Long.parseLong(mDistrictList.get(position).getArea_code());
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

        DistrictBeanNew item = getItem(position);
        holder.name.setText(item.getArea_name());

        boolean checked = districtIndex != INDEX_INVALID && mDistrictList.get(districtIndex).getArea_name().equals(item.getArea_name());
        holder.name.setEnabled(!checked);
        holder.selectImg.setVisibility(checked ? View.VISIBLE : View.GONE);


        return convertView;
    }


    class Holder {
        TextView name;
        ImageView selectImg;
    }
}
