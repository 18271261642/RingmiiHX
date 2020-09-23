package com.aliyun.rtcdemo.activity;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.aliyun.rtcdemo.R;
import com.aliyun.rtcdemo.bean.ChartUserBean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartUserAdapter extends BaseRecyclerViewAdapter<ChartUserAdapter.ChartViewHolder> {

    private List<String> mList = new ArrayList<>();
    private Map<String, ChartUserBean> mMap = new LinkedHashMap<>();

    private View.OnClickListener onClickListener;

    public ChartUserAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ChartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_content_userlist_item, parent, false);
        rootView.setOnClickListener(onClickListener);
        return new ChartViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ChartViewHolder holder, int position) {
        holder.mScreenLayout.setVisibility(View.GONE);
        if(mList.isEmpty()){
            return;
        }
        ChartUserBean item = mMap.get(mList.get(position));
        //fill surface
        //如果没有surface则不显示
        holder.mSurfaceContainer.removeAllViews();
        holder.mScreenSurfaceContainer.removeAllViews();
        if (item == null){
            return;
        }

        if(item.mScreenSurface != null){
            //如果老的surfaceview还在之前的viewtree中，需要先移除
            holder.mScreenLayout.setVisibility(View.VISIBLE);
            ViewParent parent = item.mScreenSurface.getParent();
            if(parent != null){
                if(parent instanceof FrameLayout) {
                    ((FrameLayout) parent).removeAllViews();
                }
                holder.mScreenSurfaceContainer.removeAllViews();
            }
            holder.mScreenSurfaceContainer.addView(item.mScreenSurface,
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }

        if(item.mCameraSurface != null){
            //如果老的surfaceview还在之前的viewtree中，需要先移除
            ViewParent parent = item.mCameraSurface.getParent();
            if(parent != null){
                if(parent instanceof FrameLayout) {
                    ((FrameLayout) parent).removeAllViews();
                }
                holder.mSurfaceContainer.removeAllViews();
            }
            holder.mSurfaceContainer.addView(item.mCameraSurface,
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(List<ChartUserBean> list, boolean notify){
        mList.clear();
        mMap.clear();
        for(ChartUserBean item : list){
            mList.add(item.mUserId);
            mMap.put(item.mUserId, item);
        }
        if(notify){
            notifyDataSetChanged();
        }
    }

    public void addData(ChartUserBean data, boolean notify){
        mList.add(data.mUserId);
        mMap.put(data.mUserId, data);
        if(notify){
            notifyItemInserted(mList.size() - 1);
        }
    }

    public void remoteData(String uid, boolean notify){
        int index = mList.indexOf(uid);
        if(index < 0){
            return;
        }
        mList.remove(uid);
        mMap.remove(uid);
        if(notify){
            notifyItemRemoved(index);
        }
    }

    public void updateData(ChartUserBean data, boolean notify){
        if(mList.contains(data.mUserId)){
            int index = mList.indexOf(data.mUserId);
            mMap.put(data.mUserId, data);
            if(notify){
                notifyItemChanged(index);
            }
        }else{
            addData(data, notify);
        }
    }

    public ChartUserBean createDataIfNull(String uid){
        ChartUserBean ret;
        if(TextUtils.isEmpty(uid) || (ret = mMap.get(uid)) == null){
            ret = new ChartUserBean();
        }
        return ret;
    }

    public boolean containsUser(String uid){
        if(!mList.isEmpty() && mList.contains(uid)){
            return true;
        }
        return false;
    }

    public static class ChartViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout mScreenSurfaceContainer;
        public FrameLayout mScreenLayout;
        public FrameLayout mVideoLayout;
        public FrameLayout mSurfaceContainer;

        public ChartViewHolder(View itemView) {
            super(itemView);
            mVideoLayout = itemView.findViewById(R.id.chart_content_userlist_item_video_layout);
            mSurfaceContainer = itemView.findViewById(R.id.chart_content_userlist_item_surface_container);
            mScreenLayout = itemView.findViewById(R.id.chart_content_userlist_item_screen_layout);
            mScreenSurfaceContainer = itemView.findViewById(R.id.chart_content_userlist_item2_surface_container);
        }

    }

}