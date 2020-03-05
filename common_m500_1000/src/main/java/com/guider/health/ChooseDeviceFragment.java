package com.guider.health;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.all.R;
import com.guider.health.arouter_annotation.Route;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.HealthRange;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.HeartPressBp;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.net.NetStateController;
import com.guider.health.common.net.net.RestService;
import com.guider.health.common.net.net.RetrofitLogInterceptor;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
// import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ble.BleClient;
import me.yokeyword.fragmentation.ISupportFragment;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * Created by haix on 2019/6/12.
 */
@Route(path = RouterPathManager.CHOOSE_DEVICE_PATH)
public class ChooseDeviceFragment extends BaseFragment {

    private View view;
    private RecyclerView recyclerView;
    private NormalAdapter normalAdapter;
    private DeviceInit deviceInit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHealthRange();
    }

    //当增加设备时, 需要 resetTag setDeviceTag两个方法

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_device_fragment, container, false);
        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {

            resetTag = true;

            init();
            getDeviceList();

        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        if (layout3 != null) {
//            layout3.setChecked(false);
//            list.clear();
//        }

        if (normalAdapter != null) {
            normalAdapter.notifyDataSetChanged();
            resetTag = true;
            list.clear();
        }

        // TODO 如果测量项中有脏音 , 就检查是否已经安装了apk , 否则下载安装
//        if (Config.DEVICE_KEYS.contains(DeviceInit.DEV_ECG_tzq)) {
//            new CardiartAppUtil().checkAndLoadApk(_mActivity);
//        }

        getDeviceList();
    }

    private void getDeviceList() {
//        ApiUtil.createHDApi(IGuiderApi.class).getDeviceList(MyUtils.getMacAddress()).enqueue(
//                new ApiCallBack<List<Devices>>(){
//                    @Override
//                    public void onApiResponse(Call<List<Devices>> call, Response<List<Devices>> response) {
//                        super.onApiResponse(call, response);
//                        if (response != null && response.body() != null && !response.body().isEmpty()) {
//                            Config.DEVICE_KEYS.clear();
//                            for (Devices device : response.body()) {
//                                Config.DEVICE_KEYS.add(device.getBtName() + device.getVersion());
//                                Config.DEVICE_OBJ.put(device.getBtName() + device.getVersion(), device);
//                            }
//                            init();
//                        }
//                    }
//                }
//        );
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserManager.getInstance().getUserInfoOnServer(_mActivity);
        BleClient.init(_mActivity);
        if (savedInstanceState == null) {

            deviceInit = DeviceInit.getInstance();
            init();

        }
        Logger.i("accontID=" + UserManager.getInstance().getAccountId() + "\n" +
                "mac=" + MyUtils.getMacAddress() + "\n" +
                "doctorID = " + UserManager.getInstance().getDotorAccountId());
        // CrashReport.putUserData(_mActivity, "accountID", UserManager.getInstance().getAccountId() + "");
        MeasureDataUploader.getInstance(_mActivity).startWorking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MeasureDataUploader.getInstance(_mActivity).stopWorking();
    }

    private final List<String> list = new LinkedList<>();

    private void init() {

        list.clear();

        view.findViewById(R.id.middle_line).setVisibility(View.GONE);
        view.findViewById(R.id.home).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.title)).setText("选择测量设备");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.finish();
            }
        });

        if (recyclerView == null) {
            recyclerView = view.findViewById(R.id.recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(_mActivity);
            recyclerView.setLayoutManager(layoutManager);
            layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
            normalAdapter = new NormalAdapter();
            recyclerView.setAdapter(normalAdapter);
            recyclerView.addItemDecoration(new SimplePaddingDecoration(_mActivity));
        } else {
            normalAdapter.notifyDataSetChanged();
        }


//        final CheckableLinearLayout layout_glu = view.findViewById(R.id.device_glu);
//        layout_glu.setChecked(false);
//        final CheckableLinearLayout layout_ecg = view.findViewById(R.id.device_ecg);
//        layout_ecg.setChecked(false);
//        layout3 = view.findViewById(R.id.device3);
//        layout3.setChecked(false);
//        final CheckableLinearLayout layout_bp = view.findViewById(R.id.device_bp);
//        layout_bp.setChecked(false);


//        layout_glu.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                resetTag();
//                if (layout_glu.changeCheckedStatus()) {
//
//                    Glucose.getInstance().setTag(true);
//                    list.add(Config.GLU_FRAGMENT);
//
//                } else {
//                    Glucose.getInstance().setTag(false);
//                    list.remove(Config.GLU_FRAGMENT);
//
//                }
//
//            }
//        });
//
//
//        layout_ecg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetTag();
//                if (layout_ecg.changeCheckedStatus()) {
//                    HearRate.getInstance().setTag(true);
//                    list.add(Config.ECG_FRAGMENT);
//                } else {
//                    HearRate.getInstance().setTag(false);
//                    list.remove(Config.ECG_FRAGMENT);
//                }
//
//            }
//        });
//
//        layout3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetTag();
//                if (layout3.changeCheckedStatus()) {
//                    list.add(RouterPathManager.Null_PATH);
//                } else {
//                    list.remove(RouterPathManager.Null_PATH);
//                }
//            }
//        });
//
//        layout_bp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetTag();
//                if (layout_bp.changeCheckedStatus()) {
//                    HeartPressBp.getInstance().setTag(true);
//                    list.add(Config.BP_FRAGMENT);
//                } else {
//                    HeartPressBp.getInstance().setTag(false);
//                    list.remove(Config.BP_FRAGMENT);
//                }
//
//
//            }
//        });

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!MyUtils.isNormalClickTime()) {
                    return;
                }

                RouterPathManager.Devices.clear();
                RouterPathManager.Devices.addAll(list);

                Config.mapX.put("ecg", true);
                Config.mapX.put("glu", true);
                Config.mapX.put("bp", true);


                //如果没有选择, 默认都选
//                if (RouterPathManager.Devices.size() == 0) {
//                    RouterPathManager.Devices.add(Config.GLU_FRAGMENT);
//                    Glucose.getInstance().setTag(true);
//                    RouterPathManager.Devices.add(Config.ECG_FRAGMENT);
//                    HearRate.getInstance().setTag(true);
//                    RouterPathManager.Devices.add(Config.BP_FRAGMENT);
//                    HeartPressBp.getInstance().setTag(true);
//                    RouterPathManager.Devices.add(RouterPathManager.Null_PATH);
//                }

                if (RouterPathManager.Devices.size() == 0) {
                    for (String key : Config.DEVICE_KEYS) {

                        RouterPathManager.Devices.add(deviceInit.fragments.get(key));
                        deviceInit.setDeviceTag(key, true);
                    }
                }
                UserManager.getInstance();
                Log.i("haix", "=============选择了:  bp: " + HeartPressBp.getInstance().isTag() + " glu: " + Glucose.getInstance().isTag() + " ecg: " + HearRate.getInstance().isTag());


                Toast.makeText(_mActivity, "您共选择了 " + RouterPathManager.Devices.size() + " 个设备进行下面的测量!", Toast.LENGTH_LONG).show();

                String fragmentPath = RouterPathManager.Devices.remove();
                if (fragmentPath != null) {

                    try {
                        start((ISupportFragment) Class.forName(fragmentPath).newInstance());
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4) {
            //什么都不做
        }

    }


    //重新设置单例的tag
    private boolean resetTag = true;

    private void resetTag() {
        if (resetTag) {
            resetTag = false;

            deviceInit.setDeviceTagFalse();
        }
    }


    // ① 创建Adapter
    public class NormalAdapter extends RecyclerView.Adapter<NormalAdapter.VH> {
        //② 创建ViewHolder
        public class VH extends RecyclerView.ViewHolder {
            public final ImageView pic;
            public final TextView text;
            public final CheckableLinearLayout device;

            public VH(View v) {
                super(v);
                pic = (ImageView) v.findViewById(R.id.device_pic);
                text = (TextView) v.findViewById(R.id.device_text);
                device = (CheckableLinearLayout) v.findViewById(R.id.device);
            }
        }


//        public NormalAdapter(List<String> data) {
//            this.mDatas = data;
//
//        }

        //③ 在Adapter中实现3个方法
        @Override
        public void onBindViewHolder(final VH holder, final int position) {

            String key = Config.DEVICE_KEYS.get(position);

            String deviceName = Config.DEVICE_OBJ.get(key) == null ? "" : Config.DEVICE_OBJ.get(key).getName();
            if (!TextUtils.isEmpty(deviceName)) {
                holder.text.setText(deviceName);

            } else {
                holder.text.setText(deviceInit.names.get(key));
            }


            String picURL = Config.DEVICE_OBJ.get(Config.DEVICE_KEYS.get(position)) == null ? "" : Config.DEVICE_OBJ.get(Config.DEVICE_KEYS.get(position)).getImgUrl();
            if (!TextUtils.isEmpty(picURL)) {
                Picasso.with(_mActivity)
                        .load(picURL)
                        .into(holder.pic);
            } else {
                holder.pic.setImageResource(deviceInit.pics.get(Config.DEVICE_KEYS.get(position)));
            }

            holder.device.setChecked(false);
            String fragmentName = deviceInit.fragments.get(key);
            for (String s : list) {
                if (s.equals(fragmentName)) {
                    holder.device.setChecked(true);
                }
            }

            holder.device.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //item 点击事件

                    resetTag();
                    if (holder.device.changeCheckedStatus()) {

                        deviceInit.setDeviceTag(Config.DEVICE_KEYS.get(position), true);
                        list.add(deviceInit.fragments.get(Config.DEVICE_KEYS.get(position)));

                    } else {
                        deviceInit.setDeviceTag(Config.DEVICE_KEYS.get(position), false);
                        list.remove(deviceInit.fragments.get(Config.DEVICE_KEYS.get(position)));

                    }


                }
            });

        }

        @Override
        public int getItemCount() {
            return Config.DEVICE_KEYS.size();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {

            //LayoutInflater.from指定写法
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_view, parent, false);
            return new VH(v);
        }
    }

    public class SimplePaddingDecoration extends RecyclerView.ItemDecoration {

        private int dividerHeight;


        public SimplePaddingDecoration(Context context) {
            dividerHeight = context.getResources().getDimensionPixelSize(R.dimen.dp_15);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildLayoutPosition(view);
            if (position != 0) {

                outRect.left = dividerHeight;//类似加了一个bottom padding
            }

        }
    }

    private static final int TIME_OUT = 60;
    private OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(new RetrofitLogInterceptor())
            .build();

    private void getHealthRange() {
        if (!NetStateController.isNetworkConnected(_mActivity)) {

            Toast.makeText(_mActivity, "没有网络, 请打开网络", Toast.LENGTH_SHORT).show();

            return;
        }


        //00:00:46:79:E5:5A
        HashMap params = new HashMap();
        params.put("accountId", UserManager.getInstance().getAccountId());


        try {


            Retrofit retrofit = new Retrofit.Builder().baseUrl(NetIp.BASE_URL_apihd).client(OK_HTTP_CLIENT).build();
            RestService restService = retrofit.create(RestService.class);
            Call<ResponseBody> call = restService.get("api/v1/healthrange", params);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        if (response.body() == null) {
                            return;
                        }
                        String result = response.body().string();

                        JSONObject jsonObject = JSON.parseObject(result);

                        int code = jsonObject.getInteger("code");

                        if (code >= 0) {


                            JSONObject jb = jsonObject.getJSONObject("data");

                            if (jb != null && !jb.isEmpty()) {

                                HealthRange.getInstance().setSbpIdealMin(jb.getInteger("sbpIdealMin"));
                                HealthRange.getInstance().setSbpIdealMax(jb.getInteger("sbpIdealMax"));
                                HealthRange.getInstance().setDbpIdealMin(jb.getInteger("dbpIdealMin"));
                                HealthRange.getInstance().setDbpIdealMax(jb.getInteger("dbpIdealMax"));
                                HealthRange.getInstance().setSbpHMin(jb.getInteger("sbpHMin"));
                                HealthRange.getInstance().setDbpHMin(jb.getInteger("dbpHMin"));
                                HealthRange.getInstance().setFbsMin(jb.getDouble("fbsMin"));
                                HealthRange.getInstance().setFbsMax(jb.getDouble("fbsMax"));
                                HealthRange.getInstance().setPbsMax(jb.getDouble("pbsMax"));
                                HealthRange.getInstance().setPbsMin(jb.getDouble("pbsMin"));
                                HealthRange.getInstance().setBmiMin(jb.getDouble("bmiMin"));
                                HealthRange.getInstance().setBmiMax(jb.getDouble("bmiMax"));
                                HealthRange.getInstance().setBoMin(jb.getInteger("boMin"));
                                HealthRange.getInstance().setHrMin(jb.getInteger("hrMin"));
                                HealthRange.getInstance().setHrMax(jb.getInteger("hrMax"));

                                Log.i("haix", "测试健康数据: " + HealthRange.getInstance().toString());

                            } else {
                                //                        "code":0,"msg":"成功","data":[]
                                //失败
                            }


                        } else {
                            //失败
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Log.i("haix", "失败2---");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

}
