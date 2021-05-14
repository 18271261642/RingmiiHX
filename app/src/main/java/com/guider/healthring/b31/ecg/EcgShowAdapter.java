package com.guider.healthring.b31.ecg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.guider.healthring.R;
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


    private List<List<Integer>> ecgData;

    private Context mContext;

    public EcgShowAdapter(List<List<Integer>> ecgData, Context mContext) {
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

        List<Integer> lt = ecgData.get(position);
        holder.itemEcgView.setSourceList(lt);
    }

    @Override
    public int getItemCount() {

        return ecgData.size();
    }

    class EcgViewHolder extends RecyclerView.ViewHolder{

        private CardiogramView itemEcgView;

        public EcgViewHolder(@NonNull View itemView) {
            super(itemView);
            itemEcgView = itemView.findViewById(R.id.itemEcgView);
        }
    }

}
