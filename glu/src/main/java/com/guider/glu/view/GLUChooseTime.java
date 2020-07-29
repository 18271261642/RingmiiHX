package com.guider.glu.view;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.glu.R;
import com.guider.glu.model.BodyIndex;
import com.guider.glu.presenter.GLUServiceManager;
import com.guider.health.arouter_annotation.Route;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.device.IUnit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.utils.UnitUtil;
import com.guider.health.common.views.RoundCheckBox;
import com.kyleduo.switchbutton.SwitchButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haix on 2019/6/4.
 * 无创血糖参数设置页
 */

@Route(path = RouterPathManager.GLU_PATH)
public class GLUChooseTime extends GLUFragment {

    private View view;
    private EditText mWeight;
    private EditText mHeight;
    private DropDownMenu dropDownMenu;
    private String glu = "0";

    ///--- todo 吃药变量
    private View not_normal;
    private View not_normal_page;
    private TextView text_foot_time;
    private EditText input_glu_value;
    private SwitchButton toogle_1;
    private SwitchButton toogle_2;
    private SwitchButton toogle_3;
    private SwitchButton toogle_4;
    private View empty_food_seleted;
    private View time1_food_selected;
    ///---

    private class Ti {
        public final static String EMPTY_FOOD = "0";
        public final static String ONETIME_FOOD = "1";
        public final static String TWOTIME_FOOD = "2";
        public final static String THREETIME_FOOD = "3";
    }

    //todo 手动开关
    private final static boolean isOpenEatM = true;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.glu_choose_time, container, false);
        Log.i("haix", "设备数量: " + RouterPathManager.Devices.size());
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        String weight = mWeight.getText().toString().trim();
        String height = mHeight.getText().toString().trim();
        if (!"".equals(weight) && !"".equals(height)) {
            UserManager.getInstance().setWeight(Integer.valueOf(weight));
            UserManager.getInstance().setHeight(Integer.valueOf(height));
            UserManager.getInstance().synchronizeInfo(_mActivity);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final LinearLayout checkableButtonLeft = view.findViewById(R.id.green);
        final LinearLayout checkableButtonRight = view.findViewById(R.id.red);
        final RoundCheckBox leftCheckBox = view.findViewById(R.id.checkbox_left);
        final RoundCheckBox rightCheckBox = view.findViewById(R.id.checkbox_right);
        leftCheckBox.setChecked(true);
        rightCheckBox.setChecked(false);
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_GLU));

        // 根据国别处理单位
        IUnit iUnit = UnitUtil.getIUnit(_mActivity);
        // 异常单选框
        TextView tvGluUnit = view.findViewById(R.id.glu_abnormal_textview);
        String unit = iUnit.getGluShowValue(7, 2) + iUnit.getGluUnit();
        tvGluUnit.setText(tvGluUnit.getText().toString().replace("7mmol/L", unit));
        // 异常输入单位
        TextView tvGluInputUnit = view.findViewById(R.id.tv_abnormal_input_unit);
        tvGluInputUnit.setText(tvGluInputUnit.getText().toString().replace("mmol/L", iUnit.getGluUnit()));

        ///--------- todo 吃药初始化
        not_normal = view.findViewById(R.id.not_normal);
        not_normal.setVisibility(View.GONE);
        not_normal_page = view.findViewById(R.id.not_normal_page);
        not_normal_page.setVisibility(View.GONE);

        text_foot_time = view.findViewById(R.id.text_foot_time);
        input_glu_value = view.findViewById(R.id.input_glu_value);

        toogle_1 = view.findViewById(R.id.toogle_1);
        toogle_1.setThumbColorRes(R.color.color_ffffff);
        toogle_2 = view.findViewById(R.id.toogle_2);
        toogle_2.setThumbColorRes(R.color.color_ffffff);
        toogle_3 = view.findViewById(R.id.toogle_3);
        toogle_3.setThumbColorRes(R.color.color_ffffff);
        toogle_4 = view.findViewById(R.id.toogle_4);
        toogle_4.setThumbColorRes(R.color.color_ffffff);


        toogle_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {

                    toogle_1.setBackColorRes(R.color.color_F18937);//背景
                    //b为true 选中未敷药
                    toogle_1.setChecked(true);
                    toogle_2.setChecked(false);
                    toogle_3.setChecked(false);
                    toogle_4.setChecked(false);
                    BodyIndex.getInstance().setEatmedicine(false);

                } else {
                    toogle_1.setBackColorRes(R.color.color_E0E0E0);//背景

                    BodyIndex.getInstance().setEatmedicine(true);

                    if ("0".equals(BodyIndex.getInstance().getSulphonylureasState())) {
                        toogle_2.setChecked(false);
                    } else {
                        toogle_2.setChecked(true);
                    }

                    if ("0".equals(BodyIndex.getInstance().getBiguanidesState())) {
                        toogle_3.setChecked(false);
                    } else {
                        toogle_3.setChecked(true);
                    }

                    if ("0".equals(BodyIndex.getInstance().getGlucosedesesSate())) {
                        toogle_4.setChecked(false);
                    } else {
                        toogle_4.setChecked(true);
                    }

                }
            }
        });


        toogle_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    toogle_2.setBackColorRes(R.color.color_F18937);//背景
                    toogle_1.setChecked(false);
                    BodyIndex.getInstance().setEatmedicine(true);
                    toogle_2.setChecked(true);
                    BodyIndex.getInstance().setSulphonylureasState("1");
                }else{
                    toogle_2.setChecked(false);
                    BodyIndex.getInstance().setSulphonylureasState("0");
                    toogle_2.setBackColorRes(R.color.color_E0E0E0);//背景
                }
            }
        });

        toogle_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    toogle_3.setBackColorRes(R.color.color_F18937);//背景
                    toogle_1.setChecked(false);
                    BodyIndex.getInstance().setEatmedicine(true);
                    toogle_3.setChecked(true);
                    BodyIndex.getInstance().setBiguanidesState("1");
                }else{
                    toogle_3.setChecked(false);
                    BodyIndex.getInstance().setBiguanidesState("0");
                    toogle_3.setBackColorRes(R.color.color_E0E0E0);//背景
                }
            }
        });

        toogle_4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    toogle_4.setBackColorRes(R.color.color_F18937);//背景
                    toogle_1.setChecked(false);
                    BodyIndex.getInstance().setEatmedicine(true);
                    toogle_4.setChecked(true);
                    BodyIndex.getInstance().setGlucosedesesSate("1");
                }else{
                    toogle_4.setChecked(false);
                    BodyIndex.getInstance().setGlucosedesesSate("0");
                    toogle_4.setBackColorRes(R.color.color_E0E0E0);//背景
                }
            }
        });




        empty_food_seleted = view.findViewById(R.id.empty_food_selected);
        time1_food_selected = view.findViewById(R.id.time1_food_selected);

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyUtils.isNormalClickTime()){
                    return;
                }
                toNextView(Glucose.getInstance().getFoodTime());
            }
        });


        showDialog();
        GLUServiceManager.getInstance().getNonbsSet(new WeakReference<Activity>(_mActivity), new GLUServiceManager.CallBack() {
            @Override
            public void result(int code, String re) {
                if (code == 0){
                    IUnit iUnit = UnitUtil.getIUnit(_mActivity);
                    double value = iUnit.getGluShowValue(BodyIndex.getInstance().getValue(), 2);
                    input_glu_value.setText(value + "");
                    if ("".equals(BodyIndex.getInstance().getDiabetesType())){
                        // 后台返回这个用户有糖尿病
                        leftCheckBox.setChecked(false);
                        rightCheckBox.setChecked(true);


                        if (isOpenEatM){
                            not_normal.setVisibility(View.VISIBLE);
                            not_normal_page.setVisibility(View.VISIBLE);
                            BodyIndex.getInstance().setDiabetesType("");//设置不正常
                            if (Glucose.getInstance().getFoodTime() == 0){
                                text_foot_time.setText(getResources().getString(R.string.fpg) + ":");

                                Glucose.getInstance().setFoodTime(0);
                                if (isOpenEatM){
                                    text_foot_time.setText(getResources().getString(R.string.fpg) + ":");
                                    empty_food_seleted.setVisibility(View.VISIBLE);
                                    time1_food_selected.setVisibility(View.GONE);
                                }

                            }else{
                                text_foot_time.setText(getResources().getString(R.string.twohppg) + ":");

                                Glucose.getInstance().setFoodTime(2);
                                if (isOpenEatM){
                                    text_foot_time.setText(getResources().getString(R.string.twohppg) + ":");
                                    empty_food_seleted.setVisibility(View.GONE);
                                    time1_food_selected.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        glu = "1";

                    }else{

                        leftCheckBox.setChecked(true);
                        rightCheckBox.setChecked(false);

                        glu = "0";

                        if (isOpenEatM){
                            not_normal.setVisibility(View.GONE);
                            not_normal_page.setVisibility(View.GONE);
                            BodyIndex.getInstance().setDiabetesType(BodyIndex.Normal);//设置为正常
                        }

                    }
                }else{
//                    Toast.makeText(_mActivity, re, Toast.LENGTH_SHORT).show();
                }

                hideDialog();

            }
        });
        ///---------

        initDropMenu();

        List<String> listW = new ArrayList<>();
        for (int i = 30 ; i < 200; i++){

            listW.add(i+"");

        }
        List<String> listH = new ArrayList<>();
        for (int i = 80 ; i < 280; i++){


            listH.add(i+"");
        }


        final SearchAdapter searchAdapterW = new SearchAdapter(_mActivity);
        searchAdapterW.setItems(listW);
        final SearchAdapter searchAdapterH = new SearchAdapter(_mActivity);
        searchAdapterH.setItems(listH);

        final View pupView = LayoutInflater.from(_mActivity).
                inflate(R.layout.pup_selectlist,null);
        final View itemView = LayoutInflater.from(_mActivity).
                inflate(R.layout.item_listview,null);

        mWeight =  view.findViewById(R.id.weight);
        mHeight =  view.findViewById(R.id.height);
//        new KeyBoardUtil(_mActivity, mWeight);
//        new KeyBoardUtil(_mActivity, mHeight);
        String wValue = UserManager.getInstance().getWeight() == 0 ? "" : String.valueOf(UserManager.getInstance().getWeight());
        mWeight.setText(wValue);
        String hValue = UserManager.getInstance().getHeight() == 0 ? "" : String.valueOf(UserManager.getInstance().getHeight());
        mHeight.setText(hValue);
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

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText(getResources().getString(R.string.param_input));
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });

        if (isOpenEatM) {
            Glucose.getInstance().setFoodTime(0);
            empty_food_seleted.setVisibility(View.VISIBLE);
            time1_food_selected.setVisibility(View.GONE);
        }
        view.findViewById(R.id.empty_food).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //toNextView(Ti.EMPTY_FOOD);
                Glucose.getInstance().setFoodTime(0);
                if (isOpenEatM){
                    text_foot_time.setText(getResources().getString(R.string.fpg_));
                    empty_food_seleted.setVisibility(View.VISIBLE);
                    time1_food_selected.setVisibility(View.GONE);
                }

            }
        });
        view.findViewById(R.id.time1_food).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //toNextView(Ti.ONETIME_FOOD);
                Glucose.getInstance().setFoodTime(2);
                if (isOpenEatM){
                    text_foot_time.setText(getResources().getString(R.string.twohppg) + ":");
                    empty_food_seleted.setVisibility(View.GONE);
                    time1_food_selected.setVisibility(View.VISIBLE);
                }
            }
        });

        view.findViewById(R.id.time2_food).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //toNextView(Ti.TWOTIME_FOOD);
                Glucose.getInstance().setFoodTime(2);
            }
        });

        view.findViewById(R.id.time3_food).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //toNextView(Ti.THREETIME_FOOD);
                Glucose.getInstance().setFoodTime(2);
            }
        });



        checkableButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glu = "0";
                leftCheckBox.setChecked(true);
                rightCheckBox.setChecked(false);

                if (isOpenEatM){
                    not_normal.setVisibility(View.GONE);
                    not_normal_page.setVisibility(View.GONE);
                    BodyIndex.getInstance().setDiabetesType(BodyIndex.Normal);//设置为正常
                }
            }
        });

        checkableButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenEatM){
                    not_normal.setVisibility(View.VISIBLE);
                    not_normal_page.setVisibility(View.VISIBLE);
                    BodyIndex.getInstance().setDiabetesType("");//设置不正常
                    if (Glucose.getInstance().getFoodTime() == 0){
                        text_foot_time.setText(getResources().getString(R.string.fpg_));
                    }else{
                        text_foot_time.setText(getResources().getString(R.string.twohppg) + ":");
                    }

                    statusInit();
                }

                glu = "1";
                leftCheckBox.setChecked(false);
                rightCheckBox.setChecked(true);
            }
        });

    }

    private void toNextView(int time) {

        String weight = mWeight.getText().toString().trim();
        String height = mHeight.getText().toString().trim();

        Log.i("haix", "-------------身高体重: "+height);

        String input_glu = "0";
        if (isOpenEatM) {
            input_glu = input_glu_value.getText().toString().trim();
            if ("".equals(BodyIndex.getInstance().getDiabetesType())) {
                if (TextUtils.isEmpty(input_glu)) {
                    Toast.makeText(_mActivity, "请输入血糖值", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 单位处理
                IUnit iUnit = UnitUtil.getIUnit(_mActivity);
                double value = iUnit.getGluRealValue(Double.valueOf(input_glu), 2);
                BodyIndex.getInstance().setValue((float) value);
                BodyIndex.getInstance().setId(UserManager.getInstance().getId());
            }
        }


        if (!"".equals(weight) && !"".equals(height)) {

            if (isOpenEatM){
                GLUServiceManager.getInstance().setBodyIndex(time+"", BodyIndex.getInstance().getDiabetesType(), input_glu, weight, height);
            }else{
                GLUServiceManager.getInstance().setBodyIndex(time+"", "Normal", glu, weight, height);
            }

        } else {
            Toast.makeText(_mActivity, "身高体重不能为空", Toast.LENGTH_LONG).show();
            return;
        }

//        showDialog();
        GLUServiceManager.getInstance().uploadHeightAndWeight(Integer.valueOf(height), Integer.valueOf(weight));

        if (isOpenEatM){
            //上传吃药数据
            GLUServiceManager.getInstance().setNonbsSet(new WeakReference<Activity>(_mActivity));
        }
        start(new GLUOperateReminders());
    }

    @Override
    public void uploadPersonalInfoFailed() {
//        _mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                hideDialog();
//                Toast.makeText(_mActivity,"上传身高体重信息失败", Toast.LENGTH_SHORT).show();
//                start(new GLUOperateReminders());
//            }
//        });
    }

    @Override
    public void uploadPersonalInfoSucceed() {
//        _mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                hideDialog();
//                start(new GLUOperateReminders());
//            }
//        });

    }



    private void initDropMenu() {
        dropDownMenu = DropDownMenu.getInstance(_mActivity, new DropDownMenu.OnListCkickListence() {
            @Override
            public void search(String code, String type) {
                System.out.println("======" + code + "=========" + type);
                //写一些你自己的需求和方法，
                // 比如：点击某个item我们可以得到你点的是哪个列表和item的code，
                // 根据code查询后台并刷新页面
            }

            @Override
            public void changeSelectPanel(Madapter madapter, View view) {
                //提供了对适配器方法调用以及返回第一次点击的view的回调（本例中为：性别、民族、国家...）
            }
        });
        dropDownMenu.setIndexColor(R.color.colorAccent);//用来设置点击（性别、民族、国家...）后的颜色
        dropDownMenu.setShowShadow(true);//要不要在popwindow展示的时候背景变为半透明
        dropDownMenu.setShowName("name");//listView适配器中返回数据的名字（比如：我在适配器中传入List<Dic> list,在这个list中有n个Dic类，我要在性别、民族...View中显示的值在Dic这个类中的名字’）
        dropDownMenu.setSelectName("code");//listView适配器中返回数据的名字（返回用来查询的）

    }

    private void statusInit(){
        try {
            if (BodyIndex.Normal.equals(BodyIndex.getInstance().getDiabetesType())) {

                toogle_1.setChecked(false);
                toogle_2.setChecked(false);
                toogle_3.setChecked(false);
                toogle_4.setChecked(false);

            } else {


                if (BodyIndex.getInstance().isEatmedicine()) {

                    if ("0".equals(BodyIndex.getInstance().getSulphonylureasState())) {
                        toogle_2.setChecked(false);
                    } else {
                        toogle_2.setChecked(true);
                    }

                    if ("0".equals(BodyIndex.getInstance().getBiguanidesState())) {
                        toogle_3.setChecked(false);
                    } else {
                        toogle_3.setChecked(true);
                    }

                    if ("0".equals(BodyIndex.getInstance().getGlucosedesesSate())) {
                        toogle_4.setChecked(false);
                    } else {
                        toogle_4.setChecked(true);
                    }


                } else {

                    //b为true 选中未敷药
                    toogle_1.setChecked(true);
                    toogle_2.setChecked(false);
                    toogle_3.setChecked(false);
                    toogle_4.setChecked(false);

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
