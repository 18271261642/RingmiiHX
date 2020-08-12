package com.guider.health;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.guider.health.all.R;
import com.guider.health.arouter_annotation.Route;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.HeartPressBp;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.DeviceInit;
import com.orhanobut.logger.Logger;

import java.util.LinkedList;
import java.util.List;

import ble.BleClient;
import me.yokeyword.fragmentation.ISupportFragment;


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
    }

    //当增加设备时, 需要 resetTag setDeviceTag两个方法

    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_device_fragment, container, false);
        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            resetTag = true;
            DeviceInit.getInstance().init(new DeviceInit.OnHasDeviceList() {
                @Override
                public void needLoad() {
                    showDialog();
                }

                @Override
                public void onHaveList() {
                    hideDialog();
                    init();
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (normalAdapter != null) {
            normalAdapter.notifyDataSetChanged();
            resetTag = true;
            list.clear();
        }

        // TODO 如果测量项中有脏音 , 就检查是否已经安装了apk , 否则下载安装
//        if (Config.DEVICE_KEYS.contains(DeviceInit.DEV_ECG_tzq)) {
//            new CardiartAppUtil().checkAndLoadApk(_mActivity);
//        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserManager.getInstance().getUserInfoOnServer(_mActivity);
        BleClient.init(_mActivity);
        if (savedInstanceState == null) {
            deviceInit = DeviceInit.getInstance();
            DeviceInit.getInstance().init(new DeviceInit.OnHasDeviceList() {
                @Override
                public void needLoad() {
                    showDialog();
                }

                @Override
                public void onHaveList() {
                    hideDialog();
                    init();
                }
            });
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
        ((TextView) view.findViewById(R.id.title)).setText(getResources().getString(R.string.choose_device));
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.finish();
            }
        });

        if (recyclerView == null) {
            recyclerView = view.findViewById(R.id.recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(_mActivity,
                    LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            normalAdapter = new NormalAdapter();
            recyclerView.setAdapter(normalAdapter);
            recyclerView.addItemDecoration(new SimplePaddingDecoration(_mActivity));
        } else {
            normalAdapter.notifyDataSetChanged();
        }

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

                if (RouterPathManager.Devices.size() == 0) {
                    for (String key : Config.DEVICE_KEYS) {

                        RouterPathManager.Devices.add(deviceInit.fragments.get(key));
                        deviceInit.setDeviceTag(key, true);
                    }
                }
                UserManager.getInstance();
                Log.i("haix", "=============选择了:  bp: " + HeartPressBp.getInstance().isTag()
                        + " glu: " + Glucose.getInstance().isTag()
                        + " ecg: " + HearRate.getInstance().isTag());


                Toast.makeText(_mActivity, getResources().getString(R.string.choose_tips_pre)
                        + RouterPathManager.Devices.size()
                        + getResources().getString(R.string.choose_tips_tail), Toast.LENGTH_LONG).show();

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
                    } catch (Exception e) {
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
            // 什么都不做
        }
    }


    // 重新设置单例的tag
    private boolean resetTag = true;

    private void resetTag() {
        if (resetTag) {
            resetTag = false;
            deviceInit.setDeviceTagFalse();
        }
    }


    // ① 创建Adapter
    public class NormalAdapter extends RecyclerView.Adapter<NormalAdapter.VH> {
        // ② 创建ViewHolder
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

        // ③ 在Adapter中实现3个方法
        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            String key = Config.DEVICE_KEYS.get(position);
            String deviceName = Config.DEVICE_OBJ.get(key) == null ? "" : Config.DEVICE_OBJ.get(key).getName();
            if (!TextUtils.isEmpty(deviceName)) {
                holder.text.setText(deviceName);
            } else {
                holder.text.setText(deviceInit.names.get(key));
            }

            String picURL = Config.DEVICE_OBJ.get(Config.DEVICE_KEYS.get(position)) == null ?
                    "" : Config.DEVICE_OBJ.get(Config.DEVICE_KEYS.get(position)).getImgUrl();
            if (!TextUtils.isEmpty(picURL)) {

                Glide.with(_mActivity)
                        .load(picURL)
//                        .resize(256, 256)
                        .into(holder.pic);
            } else {
                Glide.with(_mActivity)
                        .load(deviceInit.pics.get(Config.DEVICE_KEYS.get(position)))
//                        .resize(256, 256)
                        .into(holder.pic);
                // holder.pic.setImageResource(deviceInit.pics.get(Config.DEVICE_KEYS.get(position)));
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
                    // item 点击事件
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
            // LayoutInflater.from指定写法
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.device_view, parent, false);
            return new VH(v);
        }
    }

    static class SimplePaddingDecoration extends RecyclerView.ItemDecoration {
        private final int dividerHeight;

        public SimplePaddingDecoration(Context context) {
            dividerHeight = context.getResources().getDimensionPixelSize(R.dimen.dp_15);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildLayoutPosition(view);
            if (position != 0) {
                outRect.left = dividerHeight;//类似加了一个bottom padding
            }
        }
    }
}
