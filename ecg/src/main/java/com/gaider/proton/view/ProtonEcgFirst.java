package com.gaider.proton.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.health.common.core.Config;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.device.standard.Constant;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.views.TipTitleView;
import com.guider.health.ecg.R;
import com.guider.health.ecg.view.ECGFragment;
import com.jay.easykeyboard.action.KeyBoardActionListener;
import com.proton.ecgcard.connector.EcgCardManager;
import com.tc.keyboard.SystemKeyBoardEditTextV2;


/**
 * 红豆心电
 */
public class ProtonEcgFirst extends ECGFragment implements KeyBoardActionListener {


    private View view;
    private SystemKeyBoardEditTextV2 etAge, etWidth, etHeight;
    private RadioButton man , woman , smoke , noSmoke;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.proton_first, container, false);
        EcgCardManager.init(_mActivity.getApplicationContext());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_ECG_HD));
        ((TextView) view.findViewById(R.id.title)).setText("操作指南");
        TipTitleView tips = view.findViewById(R.id.tip_view);
        tips.setTips("心电测量", "输入参数", "开始测量", "结果展示");
        tips.toTip(1);
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toNext();
            }
        });

        initView();

    }

    private void toNext() {
        if (Constant.isCreazyDebug) {
            startWithPop(new ProtonEcgMeasure());
            return;
        }
        if (TextUtils.isEmpty(etAge.getText().toString())) {
            Toast.makeText(_mActivity, "请输入年龄", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(etWidth.getText().toString())) {
            Toast.makeText(_mActivity, "请输入体重", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(etHeight.getText().toString())) {
            Toast.makeText(_mActivity, "请输入身高", Toast.LENGTH_SHORT).show();
            return;
        } else if (!man.isChecked() && !woman.isChecked()) {
            Toast.makeText(_mActivity, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        } else if (!smoke.isChecked() && !noSmoke.isChecked()) {
            Toast.makeText(_mActivity, "请选择是否吸烟", Toast.LENGTH_SHORT).show();
            return;
        }
        UserManager.getInstance().setBirth(MyUtils.ageToDate(etAge.getText().toString(), UserManager.getInstance().getBirth()));
        UserManager.getInstance().setWeight(Integer.valueOf(etWidth.getText().toString()));
        UserManager.getInstance().setHeight(Integer.valueOf(etHeight.getText().toString()));
        UserManager.getInstance().setSex(man.isChecked() ? "MAN" : "WOMAN");
        UserManager.getInstance().synchronizeInfo(_mActivity);
        start(new ProtonEcgMeasure());
    }

    private void initView() {
        man = view.findViewById(R.id.check_man);
        woman = view.findViewById(R.id.check_women);
        smoke = view.findViewById(R.id.check_yes);
        noSmoke = view.findViewById(R.id.check_no);
        etAge = view.findViewById(R.id.et_age);
        etWidth = view.findViewById(R.id.et_weight);
        etHeight = view.findViewById(R.id.et_height);
        etAge.setOnKeyboardActionListener(this);
        etWidth.setOnKeyboardActionListener(this);
        etHeight.setOnKeyboardActionListener(this);

        if (!Constant.isCreazyDebug) {
            etAge.setText(MyUtils.getAgeFromBirthTime(UserManager.getInstance().getBirth()) + "");
            etWidth.setText(UserManager.getInstance().getWeight() + "");
            etHeight.setText(UserManager.getInstance().getHeight() + "");
            if ("MAN".equals(UserManager.getInstance().getSex())) {
                man.setChecked(true);
            } else {
                woman.setChecked(true);
            }
        }
    }

    @Override
    public void onComplete() {
        toNext();
    }

    @Override
    public void onTextChange(Editable editable) {

    }

    @Override
    public void onClear() {

    }

    @Override
    public void onClearAll() {

    }
}
