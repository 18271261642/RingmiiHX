package com.guider.health.bp.view.yfbp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.bp.view.TipTitleView;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.views.AgeEditView;
import com.guider.health.common.views.popdata.CustomDatePicker;
import com.guider.health.common.views.popdata.DropDownMenu;
import com.guider.health.common.views.popdata.Madapter;
import com.guider.health.common.views.popdata.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是动脉硬化款的血压仪四川云峰的
 * Created by haix on 2019/6/25.
 */

public class BPOperaterReminder1 extends BPFragment {

    private View view;
    YfkjDataAdapter yfkjDataAdapter;

    private DropDownMenu dropDownMenu;
    EditText mWeight;
    EditText mHeight;
//    TextView mAge;
    AgeEditView ageInput;
    private int weight;
    private int height;
    private String dataTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_yf_reminder1_next, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        initView();
        inflaterHistoryData();
        initDropMenu();
        initPopWindow();

        yfkjDataAdapter = new YfkjDataAdapter();
    }

    private void inflaterHistoryData() {
        dataTime = UserManager.getInstance().getBirth();
        if (!TextUtils.isEmpty(dataTime)) {
//            mAge.setText(dataTime);
            ageInput.setValue(dataTime);
        }
        mHeight.setText(UserManager.getInstance().getHeight() > 0 ? UserManager.getInstance().getHeight() + "" : "");
        mWeight.setText(UserManager.getInstance().getWeight() > 0 ? UserManager.getInstance().getWeight() + "" : "");
    }

    private void initView() {
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("信息录入");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_YF));
        TipTitleView tips = view.findViewById(R.id.tips);
        tips.setTips("动脉硬化测量","开机提醒","蓝牙连接", "信息录入", "操作指南", "测量结果");
        tips.toTip(3);
        mWeight = view.findViewById(R.id.weight);
        mHeight = view.findViewById(R.id.height);
        mWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String weight = mWeight.getText().toString();
                if (!TextUtils.isEmpty(weight)) {
                    if (Integer.valueOf(weight) > 300) {
                        mWeight.setText("300");
                        mWeight.setSelection(mWeight.getText().length());
                    }else if (Integer.valueOf(weight) < 1) {
                        mWeight.setText("1");
                        mWeight.setSelection(mWeight.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String height = mHeight.getText().toString();
                if (!TextUtils.isEmpty(height)) {
                    if (Integer.valueOf(height) > 300) {
                        mHeight.setText("300");
                        mHeight.setSelection(mHeight.getText().length());
                    } else if (Integer.valueOf(height) < 1) {
                        mHeight.setText("1");
                        mHeight.setSelection(mHeight.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ageInput = view.findViewById(R.id.age_input);
//        mAge = view.findViewById(R.id.birthday);
        view.findViewById(R.id.bt_printing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.isNormalClickTime()) {
                    next();
                }
            }
        });

    }

    private void initDropMenu() {
        dropDownMenu = DropDownMenu.getInstance(_mActivity, new DropDownMenu.OnListCkickListence() {
            @Override
            public void search(int code, String type) {
            }

            @Override
            public void changeSelectPanel(Madapter madapter, View view) {
            }
        });
        dropDownMenu.setShowShadow(true);//要不要在popwindow展示的时候背景变为半透明
        dropDownMenu.setShowName("name");//listView适配器中返回数据的名字（比如：我在适配器中传入List<Dic> list,在这个list中有n个Dic类，我要在性别、民族...View中显示的值在Dic这个类中的名字’）
        dropDownMenu.setSelectName("code");//listView适配器中返回数据的名字（返回用来查询的）

    }


    private void initPopWindow() {
        // 年龄
        List<String> listA = new ArrayList<>();
        for (int i = 10; i < 120; i++) {
            listA.add(i + "");
        }

        final View pupView = LayoutInflater.from(_mActivity).
                inflate(R.layout.pup_selectlist2, null);
        final View itemView = LayoutInflater.from(_mActivity).
                inflate(R.layout.item_listview2, null);

        final SearchAdapter searchAdapterA = new SearchAdapter(_mActivity);
        searchAdapterA.setItems(listA);

        DatePicker();

        String wValue = UserManager.getInstance().getWeight() == 0 ? "" : String.valueOf(UserManager.getInstance().getWeight());
        mWeight.setText(wValue);
        String hValue = UserManager.getInstance().getHeight() == 0 ? "" : String.valueOf(UserManager.getInstance().getHeight());
        mHeight.setText(hValue);
        // 下拉
//        mAge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(mAge.getText().toString().trim()))
//                    customDatePicker.show(now);
//                else  // 日期格式为yyyy-MM-dd
//                    customDatePicker.show(mAge.getText().toString());
//            }
//        });
    }

    private CustomDatePicker customDatePicker;
    private String now;

    /**
     * 显示时间
     */
    private void DatePicker() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
//        //获取当前时间
//        now = sdf.format(new Date());
//        //tvElectricalTime.setText(now.split(" ")[0]);
//        customDatePicker = new CustomDatePicker(_mActivity, new CustomDatePicker.ResultHandler() {
//            @Override
//            public void handle(String time) { // 回调接口，获得选中的时间
//                Log.d("yyyyy", time);
//                mAge.setText(time.split(" ")[0]);
//            }
//        }, "1890-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
//        customDatePicker.showSpecificTime(false); // 不显示时和分
//        customDatePicker.setIsLoop(false); // 不允许循环滚动
    }

    void next() {
//        dataTime = mAge.getText().toString().trim();
        dataTime = ageInput.getValue();
        int age = 0;
        if (AgeEditView.NULL.equals(dataTime) || AgeEditView.ILLEGAL.equals(dataTime)) {
            Toast.makeText(_mActivity, "请输入正确的日期", Toast.LENGTH_LONG).show();
            return;
        } else {
            age = MyUtils.getAgeFromBirthTime(dataTime);
        }

        if (TextUtils.isEmpty(mWeight.getText().toString())) {
            Toast.makeText(_mActivity, "请输入体重", Toast.LENGTH_LONG).show();
            return;
        } else {
            weight = Integer.parseInt(mWeight.getText().toString().trim());
        }
        if (TextUtils.isEmpty(mHeight.getText().toString())) {
            Toast.makeText(_mActivity, "请输入身高", Toast.LENGTH_LONG).show();
            return;
        } else {
            height = Integer.parseInt(mHeight.getText().toString().trim());
        }
        // 发送用户信息给设备
        sendToDevice(age , height , weight);
        // 同步用户信息到服务器
        UserManager.getInstance().setBirth(dataTime);
        UserManager.getInstance().setWeight(weight);
        UserManager.getInstance().setHeight(height);
        UserManager.getInstance().synchronizeInfo(_mActivity);
    }

    private void sendToDevice(int age , int height , int weight) {
        YfkjDataAdapter.YfkjPersionData info = new YfkjDataAdapter.YfkjPersionData();
        info.age = age;
        info.hight = height;
        info.width = weight;
        yfkjDataAdapter.setPersionInfo(info);
        start(new BPOperaterReminder2());
    }


}
