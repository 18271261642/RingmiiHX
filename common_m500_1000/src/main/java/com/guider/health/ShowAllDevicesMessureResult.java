package com.guider.health;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.health.adapter.ResultListView;
import com.guider.health.all.R;
import com.guider.health.arouter_annotation.Route;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.device.MeasureDeviceManager;
import com.guider.health.common.net.NetStateController;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by haix on 2019/6/24.
 */
@Route(path = RouterPathManager.END_PATH)
public class ShowAllDevicesMessureResult extends BaseFragment {

    private View view;
    private ResultListView resultListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_result_show, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }

        view.findViewById(R.id.back).setVisibility(View.GONE);
        view.findViewById(R.id.middle_line).setVisibility(View.GONE);
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

        ((TextView) view.findViewById(R.id.title)).setText("测量结果");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        view.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    popTo(Class.forName(Config.HOME_DEVICE), false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        view.findViewById(R.id.end_measure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.finish();
            }
        });
        view.findViewById(R.id.end_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    popTo(Class.forName(Config.HOME_DEVICE), false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        view.findViewById(R.id.app_doctor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(new DoctorListFragment());
            }
        });


        if ("M100".equals(DeviceInit.getInstance().getType())){
            view.findViewById(R.id.app_printing).setVisibility(View.GONE);
        }

        view.findViewById(R.id.app_printing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    System.out.println("ddd");
                    start((ISupportFragment) Class.forName(Config.PRINT_FRAGMENT).newInstance());

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                }
            }
        });


        resultListView = view.findViewById(R.id.result_list);
        resultListView.setDeviceList(new MeasureDeviceManager().getMeasureDevices());
        if (resultListView.getChildCount() <= 0) {
            try {
                popTo(Class.forName(Config.HOME_DEVICE), false);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }

        request();

    }

    int requestNum;

    private void request() {
        if (!NetStateController.isNetworkConnected(_mActivity)) {
            Toast.makeText(_mActivity, "没有网络, 请打开网络", Toast.LENGTH_SHORT).show();
            return;
        }
        requestNum = resultListView.getNeedRequestNum();

        MeasureDataUploader.getInstance(_mActivity).checkAndReuploadFaillData();
//        resultListView.startRequest(new RequestCallback() {
//            @Override
//            public void onRequestFinish(String name, String msg, int code) {
//                Log.i("ShowAllDevices", "上传" + name + (code == 0 ? "成功!" : "失败...." + msg));
//                if (code == RequestCallback.CODE_OK) {
//                    requestNum--;
//                }
//                if (requestNum == 0) {
//                    Log.i("ShowAllDevices", "上传完毕");
//                }
//            }
//        });

    }

}
