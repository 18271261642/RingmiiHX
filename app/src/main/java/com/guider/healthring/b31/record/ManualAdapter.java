package com.guider.healthring.b31.record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.R;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 手动测量的adapter
 * Created by Admin
 * Date 2020/12/4
 */
public class ManualAdapter extends RecyclerView.Adapter<ManualAdapter.ManualViewHolder> {

    private List<String> list;
    private Context mContext;

    private OnFloatingBtnListener onFloatingBtnListener;

    public void setOnFloatingBtnListener(OnFloatingBtnListener onFloatingBtnListener) {
        this.onFloatingBtnListener = onFloatingBtnListener;
    }

    public ManualAdapter(List<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ManualViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.item_manual_img,parent,false);
        return new ManualViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManualViewHolder holder, int position) {
        String nameStr = list.get(position);

        if(nameStr.equals("HEART")){
            holder.nameTv.setText(mContext.getResources().getString(R.string.heart_rate));
            holder.img.setImageResource(R.drawable.fgm_home_function_heart);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onFloatingBtnListener != null)
                        onFloatingBtnListener.heartItem();
                }
            });
        }

        if(nameStr.equals("BLOOD")){
            holder.nameTv.setText(mContext.getResources().getString(R.string.blood));
            holder.img.setImageResource(R.drawable.ic_meaure_bload_bottom);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onFloatingBtnListener != null)
                        onFloatingBtnListener.bloodItem();
                }
            });
        }

        if(nameStr.equals("SPO2")){
            holder.nameTv.setText(mContext.getResources().getString(R.string.vpspo2h_spo2h));
            holder.img.setImageResource(R.drawable.fgm_home_function_sp);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onFloatingBtnListener != null)
                        onFloatingBtnListener.spo2Item();
                }
            });
        }

        if(nameStr.equals("BREATH")){
            holder.nameTv.setText(mContext.getResources().getString(R.string.vpspo2h_toptitle_breath));
            holder.img.setImageResource(R.drawable.fgm_home_function_breath);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onFloatingBtnListener != null)
                        onFloatingBtnListener.breathItem();
                }
            });
        }

        if(nameStr.equals("FATIGUE")){
            holder.nameTv.setText("疲劳度");
            holder.img.setImageResource(R.drawable.fgm_home_function_ftg);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onFloatingBtnListener != null)
                        onFloatingBtnListener.fatigueItem();
                }
            });
        }

        if(nameStr.equals("ECG")){
            holder.nameTv.setText("心电");
            holder.img.setImageResource(R.drawable.fgm_home_function_ftg);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onFloatingBtnListener != null)
                        onFloatingBtnListener.ecgItem();
                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ManualViewHolder extends RecyclerView.ViewHolder{

        private ImageView img;
        private TextView nameTv;

        public ManualViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.itemManualImg);
            nameTv = itemView.findViewById(R.id.itemManualTv);
        }
    }

    public interface OnFloatingBtnListener{
        void heartItem();

        void bloodItem();

        void spo2Item();

        void breathItem();

        void fatigueItem();

        void ecgItem();

    }
}
