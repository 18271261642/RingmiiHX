package com.guider.healthring.siswatch.adapter;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.siswatch.bean.CustomBlueDevice;
import com.guider.healthring.siswatch.utils.WatchUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

/**
 * 搜索页面适配器
 */
public class CustomBlueAdapter extends RecyclerView.Adapter<CustomBlueAdapter.CustomBlueViewHolder> {

    private List<CustomBlueDevice> customBlueDeviceList;
    private Context mContext;
    public OnSearchOnBindClickListener onBindClickListener;

    public void setOnBindClickListener(OnSearchOnBindClickListener onBindClickListener) {
        this.onBindClickListener = onBindClickListener;
    }

    public CustomBlueAdapter(List<CustomBlueDevice> customBlueDeviceList, Context mContext) {
        this.customBlueDeviceList = customBlueDeviceList;
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public CustomBlueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_bluedevice, null);
        return new CustomBlueViewHolder(view);
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NotNull final CustomBlueViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = customBlueDeviceList.get(position).getBluetoothDevice();
        if (bluetoothDevice != null) {
            //蓝牙名称
            holder.bleNameTv.setText(customBlueDeviceList.get(position).getBluetoothDevice().getName());
            //mac地址
            holder.bleMacTv.setText(customBlueDeviceList.get(position).getBluetoothDevice().getAddress());
            //信号
            holder.bleRiisTv.setText("" + customBlueDeviceList.get(position).getRssi() + "");
            //展示图片
            String bleName = customBlueDeviceList.get(position).getBluetoothDevice().getName();

            if (WatchUtils.isEmpty(bleName))
                return;

            //绑定按钮
            holder.circularProgressButton.setOnClickListener(view -> {
                if (onBindClickListener != null) {
                    int position1 = holder.getLayoutPosition();
                    onBindClickListener.doBindOperator(position1);
                }
            });

            if (bleName.contains("Ringmii")) {
                holder.img.setImageResource(R.mipmap.hx_search);
                return;
            }
            if (bleName.contains("B30")) {
                holder.img.setImageResource(R.mipmap.ic_b30_search);
                return;
            }
            if ((bleName.length() >= 3 && (bleName.substring(0, 3).equals("B31"))) ||
                    bleName.length() >= 4 && bleName.substring(0, 4).equals("B31S")
            ) {
                holder.img.setImageResource(R.mipmap.ic_b31_search);
                return;
            }
            if (bleName.contains("500S") || bleName.contains("500H")) {
                holder.img.setImageResource(R.mipmap.ic_seach_500s);
                return;
            }
            holder.img.setImageResource(R.mipmap.ic_seach_null);
        }
    }

    @Override
    public int getItemCount() {
        return customBlueDeviceList.size();
    }

    static class CustomBlueViewHolder extends RecyclerView.ViewHolder {

        TextView bleNameTv, bleMacTv, bleRiisTv;
        ImageView img;  //显示手表或者手环图片
        Button circularProgressButton;
        CardView cardViewItem;

        CustomBlueViewHolder(View itemView) {
            super(itemView);
            bleNameTv = (TextView) itemView.findViewById(R.id.blue_name_tv);
            bleMacTv = (TextView) itemView.findViewById(R.id.snmac_tv);
            bleRiisTv = (TextView) itemView.findViewById(R.id.rssi_tv);
            img = (ImageView) itemView.findViewById(R.id.img_logo);
            circularProgressButton = itemView.findViewById(R.id.bind_btn);
            cardViewItem = (CardView) itemView.findViewById(R.id.list_devices_item);
        }
    }

    public interface OnSearchOnBindClickListener {
        void doBindOperator(int position);
    }
}
