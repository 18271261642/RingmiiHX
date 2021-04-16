package com.guider.healthring.b31.ecg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.guider.healthring.R;
import com.guider.libbase.util.Log;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ECG展示adapter
 * Created by Admin
 * Date 2021/4/15
 */
public class EcgShowAdapter extends RecyclerView.Adapter<EcgShowAdapter.EcgViewHolder> {

    private static final String TAG = "EcgShowAdapter";


    private List<int[]> ecgData;

    private Context mContext;

    public EcgShowAdapter(List<int[]> ecgData, Context mContext) {
        this.ecgData = ecgData;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public EcgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_show_ecg_layout,parent,false);
        return new EcgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EcgViewHolder holder, int position) {
        EcgHeartRealthView ecgView = holder.ecgHeartRealthView;
        int[] dt = ecgData.get(position);

        Log.e(TAG,"-----dt="+dt.length+"\n"+new Gson().toJson(dt));

        ecgView.setCoumlnQutoCount(5 * 5);
        ecgView.changeData(dt,dt.length);
    }

    @Override
    public int getItemCount() {

        return ecgData.size();
    }

    class EcgViewHolder extends RecyclerView.ViewHolder{

        private EcgHeartRealthView ecgHeartRealthView;

        public EcgViewHolder(@NonNull View itemView) {
            super(itemView);
            ecgHeartRealthView = itemView.findViewById(R.id.itemShowEcgView);
        }
    }

}
