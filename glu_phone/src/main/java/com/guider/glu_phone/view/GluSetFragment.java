package com.guider.glu_phone.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.guider.glu.model.BodyIndex;
import com.guider.glu_phone.R;
import com.guider.glu_phone.net.NetRequest;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.utils.StringUtil;
import com.guider.health.common.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class GluSetFragment extends GlocoseFragment {
    private boolean mFlagInfo = false;
    private boolean mFlagSetting = false;
    private View view;

    private EditText etHeight, etWeight, etGlu;
    private RadioGroup rg;
    private RadioButton rbNormal;
    private RadioButton rbAbnormal;

    public void setFlagInfo(boolean flag) {
        mFlagInfo = flag;
    }

    public boolean getFlagInfo() {
        return mFlagInfo;
    }

    public void setFlagSetting(boolean flag) {
        mFlagSetting = flag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_glu_set, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TextView)view.findViewById(R.id.head_title)).setText(R.string.setting);
        view.findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyUtils.isNormalClickTime()){
                    return;
                }
                updateData();
            }
        });

        // 单选按钮
        rg = view.findViewById(R.id.rg_glu_set);
        rbNormal = view.findViewById(R.id.rb_normal);
        rbAbnormal = view.findViewById(R.id.rb_abnormal);
        LinearLayout llGluValue = view.findViewById(R.id.ll_glu_value);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                if (id == R.id.rb_normal) {
                    rbAbnormal.setChecked(false);
                    llGluValue.setVisibility(View.GONE);
                } else if (id == R.id.rb_abnormal) {
                    rbNormal.setChecked(false);
                    llGluValue.setVisibility(View.VISIBLE);
                }
            }
        });

        etHeight = view.findViewById(R.id.et_height);
        etWeight = view.findViewById(R.id.et_weight);
        etGlu = view.findViewById(R.id.et_glu);

        init();
    }

    private void init() {
        // 两者均未成功获取
        if (!mFlagSetting && !mFlagInfo) {
            getAll(new Runnable() {
                @Override
                public void run() {
                    updaeInfoView();
                    updateSettingView();
                }
            });
            return;
        }

        if (mFlagInfo) {
            updaeInfoView();
        } else {
            getUserInfo(new Runnable() {
                @Override
                public void run() {
                    updaeInfoView();
                }
            });
        }

        if (mFlagSetting) {
            updateSettingView();
        } else {
            getSettingInfo(new Runnable() {
                @Override
                public void run() {
                    updateSettingView();
                }
            });
        }
    }

    private void updateSettingView() {
        if (BodyIndex.getInstance().getDiabetesType() == BodyIndex.Normal) {
            rbNormal.setChecked(true);
            rbAbnormal.setChecked(false);
        } else {
            rbNormal.setChecked(false);
            rbAbnormal.setChecked(true);
            etGlu.setText(String.valueOf(BodyIndex.getInstance().getValue()));
        }
    }

    private void updaeInfoView() {
        etHeight.setText(String.valueOf(UserManager.getInstance().getHeight()));
        etWeight.setText(String.valueOf(UserManager.getInstance().getWeight()));
    }

    private void updateData() {
        // 身高 体重
        String strHeight = etHeight.getText().toString();
        String strWeight = etWeight.getText().toString();
        if (StringUtil.isEmpty(strHeight) || StringUtil.isEmpty(strWeight)) {
            showInvalidParam();
            return;
        }
        int height = Integer.valueOf(strHeight);
        int weight = Integer.valueOf(strWeight);
        if (height <= 0 || weight <= 0) {
            showInvalidParam();
            return;
        }
        UserManager.getInstance().setHeight(height);
        UserManager.getInstance().setWeight(weight);
        // 无创参数
        boolean normal = rbNormal.isChecked() ? true : false;
        BodyIndex.getInstance().setDiabetesType(normal ? BodyIndex.Normal : BodyIndex.Abnormal);
        if (!normal) {
            String strValue = etGlu.getText().toString();
            if (StringUtil.isEmpty(strValue)) {
                showInvalidParam();
                return;
            }
            int value = Integer.valueOf(strValue);
            if (value <= 0) {
                showInvalidParam();
                return;
            }
            BodyIndex.getInstance().setValue(Float.valueOf(etGlu.getText().toString()));
        }

        setAll();
    }

    private void setAll() {
        NetRequest.getInstance().eidtSimpleUserInfo(new WeakReference<Activity>(_mActivity), new NetRequest.NetCallBack() {
            @Override
            public void result(int code, String result) {
                if (code == 0){
                    NetRequest.getInstance().setNonbsSet(new WeakReference<Activity>(_mActivity), new NetRequest.NetCallBack() {
                        @Override
                        public void result(int code, String result) {
                            if (code != 0) {
                                fail();
                            } else {
                                ToastUtil.showLong(_mActivity, getResources().getString(R.string.op_success));
                                pop();
                            }
                        }
                    });
                } else {
                    fail();
                }
            }
        });
    }
    private void  getUserInfo(Runnable action) {
        NetRequest.getInstance().getUserInfo(new WeakReference<Activity>(_mActivity), new NetRequest.NetCallBack() {
            @Override
            public void result(int code, String result) {
                if (code == 0){
                    action.run();
                } else {
                    fail();
                }
            }
        });
    }

    private void  getSettingInfo(Runnable action) {
        NetRequest.getInstance().getNonbsSet(new WeakReference<Activity>(_mActivity), new NetRequest.NetCallBack() {
            @Override
            public void result(int code, String result) {
                if (code == 0){
                    action.run();
                } else {
                    fail();
                }
            }
        });
    }

    private void  getAll(Runnable action) {
       getUserInfo(new Runnable() {
           @Override
           public void run() {
               getSettingInfo(new Runnable() {
                   @Override
                   public void run() {
                       action.run();
                   }
               });
           }
       });
    }

    private void fail() {
        ToastUtil.showLong(_mActivity, getResources().getString(R.string.op_error));
    }

    private void showInvalidParam() {
        ToastUtil.showLong(_mActivity, getResources().getString(R.string.invalid_param));
    }
}
