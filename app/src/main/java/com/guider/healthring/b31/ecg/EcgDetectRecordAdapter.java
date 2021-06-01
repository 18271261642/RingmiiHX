package com.guider.healthring.b31.ecg;

import android.content.Context;

import com.google.gson.Gson;
import com.guider.healthring.R;
import com.guider.healthring.b31.ecg.bean.EcgDetectStateBean;
import com.guider.healthring.b31.ecg.bean.EcgSourceBean;
import com.guider.healthring.w30s.adapters.CommonRecyclerAdapter;
import com.guider.healthring.w30s.adapters.MyViewHolder;
import com.veepoo.protocol.model.datas.EcgDetectResult;

import java.util.List;

/**
 * ecg测量记录展示adapter
 * Created by Admin
 * Date 2021/4/21
 */
class EcgDetectRecordAdapter extends CommonRecyclerAdapter<EcgSourceBean> {


    public EcgDetectRecordAdapter(Context context, List<EcgSourceBean> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(MyViewHolder holder, EcgSourceBean item) {
        holder.setText(R.id.itemEcgRecordTimeTv,item.getDetectTime());
//        String ecgDetectState = item.getEcgDetectStateBeanStr();
//        EcgDetectStateBean ecgDetectStateBean = new Gson().fromJson(ecgDetectState,EcgDetectStateBean.class);
        String ecgDetectResult = item.getEcgDetectResult();
        if(ecgDetectResult == null){
            showEmptyData(holder,item);
        }else{
            EcgDetectResult er = new Gson().fromJson(ecgDetectResult,EcgDetectResult.class);
            if(er == null){
                showEmptyData(holder,item);
                return;
            }
            holder.setText(R.id.itemEcgRecordHeartTv,(er.getAveHrv() == 0 ?"--":er.getAveHrv())+" " + mContext.getString(R.string.ecg_cnt_m));
            holder.setText(R.id.itemEcgRecordQtTv,(er.getAveQT() == 0 ? "--" : er.getAveQT()) +" " + mContext.getString(R.string.ecg_ms));
            holder.setText(R.id.itemEcgRecordHrvTv,(er.getAveHrv() == 0 || er.getAveHrv() == 255 ? "--" : er.getAveHrv()) + " " + mContext.getString(R.string.ecg_ms));
        }
//        EcgDetectResult ecgDetectResult = new Gson().fromJson(e)
//        holder.setText(R.id.itemEcgRecordTimeTv,item.getDetectTime());

    }


    private void showEmptyData(MyViewHolder holder,EcgSourceBean item){

        holder.setText(R.id.itemEcgRecordHeartTv,"--" + mContext.getString(R.string.ecg_cnt_m));
        holder.setText(R.id.itemEcgRecordQtTv,"--" + mContext.getString(R.string.ecg_ms));
        holder.setText(R.id.itemEcgRecordHrvTv, "--" + mContext.getString(R.string.ecg_ms));
    }

}


