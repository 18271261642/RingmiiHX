package com.guider.health.widget;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.health.all.R;
import com.guider.health.apilib.model.DoctorInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DoctorAdapter extends BaseAdapter {

    List<DoctorInfo> list;

    public DoctorAdapter(List<DoctorInfo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DoctorInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);

        ImageView head = inflate.findViewById(R.id.doctor_head);
        TextView name = inflate.findViewById(R.id.doctor_name);
        TextView video = inflate.findViewById(R.id.doctor_video);

        if (!TextUtils.isEmpty(list.get(position).getHeadUrl())) {
            Picasso.with(parent.getContext())
                    .load(list.get(position).getHeadUrl())
                    .into(head);
        }
        name.setText(list.get(position).getName() + "\n" + list.get(position).getProfessionalTitle());
        video.setSelected(true);

        if (video.isSelected()) {
            video.setTextColor(Color.parseColor("#ffffff"));
        } else {
            video.setTextColor(Color.parseColor("#fff18937"));
        }

        return inflate;
    }
}
