package com.guider.glu_phone.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.glu.model.BodyIndex;
import com.guider.glu_phone.R;
import com.guider.glu_phone.net.NetRequest;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.IUnit;
import com.guider.health.common.utils.ToastUtil;
import com.guider.health.common.utils.UnitUtil;
import com.kyleduo.switchbutton.SwitchButton;

import java.lang.ref.WeakReference;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.guider.glu_phone.view.ChooseTime.Ti.EMPTY_FOOD;
import static com.guider.glu_phone.view.ChooseTime.Ti.ONETIME_FOOD;
import static com.guider.glu_phone.view.ChooseTime.Ti.THREETIME_FOOD;
import static com.guider.glu_phone.view.ChooseTime.Ti.TWOTIME_FOOD;


/**
 * Created by haix on 2019/7/18.
 */

public class ChooseTime extends GlocoseFragment {


    private View view;

    private static String GLU_TIME = "0";
    private ConstraintLayout drop_time1;
    private SwitchButton toogle_1;
    private SwitchButton toogle_2;
    private SwitchButton toogle_3;
    private SwitchButton toogle_4;
    private ConstraintLayout statusLayout;
    private TextView normal;
    private TextView abnormal;
    private TextView gluReminder;
    private TextView unitTv;
    private EditText gluEdit;

    class Ti {
        public final static String EMPTY_FOOD = "0";
        public final static String ONETIME_FOOD = "1";
        public final static String TWOTIME_FOOD = "2";
        public final static String THREETIME_FOOD = "3";
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            statusInit();
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_time, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        statusLayout = view.findViewById(R.id.statusLayout);
        normal = view.findViewById(R.id.normal);
        abnormal = view.findViewById(R.id.abnormal);
        gluReminder = view.findViewById(R.id.gluReminder);
        unitTv = view.findViewById(R.id.unitTv);
        gluEdit = view.findViewById(R.id.gluEdit);
        ((TextView) view.findViewById(R.id.head_title)).setText(R.string.measure_2_kaijicaozuo);
        view.findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });


//        private String sulphonylureasState = "0";//磺酰脲
//        private String biguanidesState = "0";//双胍类
//        private String glucosedesesSate = "0";//甘梅抑制剂
        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                if (toogle_1.isChecked()) {
//                    BodyIndex.getInstance().setSulphonylureasState("0");
//                    BodyIndex.getInstance().setBiguanidesState("0");
//                    BodyIndex.getInstance().setGlucosedesesSate("0");
//                } else {
//                    BodyIndex.getInstance().setEatmedicine(true);
//                    BodyIndex.getInstance().setSulphonylureasState(toogle_2.isChecked() == false ? "0" : "1");
//                    BodyIndex.getInstance().setBiguanidesState(toogle_3.isChecked() == false ? "0" : "1");
//                    BodyIndex.getInstance().setGlucosedesesSate(toogle_4.isChecked() == false ? "0" : "1");
//                }

                //如果是选择了异常记录填写的空腹血糖值
                if (abnormal.isSelected()) {
                    String input_glu = gluEdit.getText().toString().trim();
                    if (!TextUtils.isEmpty(input_glu)) {
                        // 单位处理
                        IUnit iUnit = UnitUtil.getIUnit(_mActivity);
                        double value = iUnit.getGluRealValue(Double.parseDouble(input_glu),
                                2);
                        BodyIndex.getInstance().setValue((float) value);
                    } else {
                        ToastUtil.showShort(_mActivity, "请填写空腹血糖值");
                        return;
                    }
                }

                NetRequest.getInstance().setNonbsSet(new WeakReference<Activity>(_mActivity), new NetRequest.NetCallBack() {
                    @Override
                    public void result(int code, String result) {

                    }
                });


                start(new TurnOnOperation());
            }
        });

        final ImageView select_time_empty = view.findViewById(R.id.select_time_empty);
        final ImageView select_time_1 = view.findViewById(R.id.select_time_1);
        final ImageView select_time_2 = view.findViewById(R.id.select_time_2);
        final ImageView select_time_3 = view.findViewById(R.id.select_time_3);
        //drop_empty = view.findViewById(R.id.drop_empty);
        drop_time1 = view.findViewById(R.id.drop_time1);
//        drop_time2 = view.findViewById(R.id.drop_time2);
//        drop_time3 = view.findViewById(R.id.drop_time3);

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
                if (b) {
                    toogle_2.setBackColorRes(R.color.color_F18937);//背景
                    toogle_1.setChecked(false);
                    BodyIndex.getInstance().setEatmedicine(true);
                    toogle_2.setChecked(true);
                    BodyIndex.getInstance().setSulphonylureasState("1");
                } else {
                    toogle_2.setChecked(false);
                    BodyIndex.getInstance().setSulphonylureasState("0");
                    toogle_2.setBackColorRes(R.color.color_E0E0E0);//背景
                }
            }
        });

        toogle_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    toogle_3.setBackColorRes(R.color.color_F18937);//背景
                    toogle_1.setChecked(false);
                    BodyIndex.getInstance().setEatmedicine(true);
                    toogle_3.setChecked(true);
                    BodyIndex.getInstance().setBiguanidesState("1");
                } else {
                    toogle_3.setChecked(false);
                    BodyIndex.getInstance().setBiguanidesState("0");
                    toogle_3.setBackColorRes(R.color.color_E0E0E0);//背景
                }
            }
        });

        toogle_4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    toogle_4.setBackColorRes(R.color.color_F18937);//背景
                    toogle_1.setChecked(false);
                    BodyIndex.getInstance().setEatmedicine(true);
                    toogle_4.setChecked(true);
                    BodyIndex.getInstance().setGlucosedesesSate("1");
                } else {
                    toogle_4.setChecked(false);
                    BodyIndex.getInstance().setGlucosedesesSate("0");
                    toogle_4.setBackColorRes(R.color.color_E0E0E0);//背景
                }
            }
        });


        statusInit();

        BodyIndex.getInstance().setWeigh(UserManager.getInstance().getWeight() == 0 ? "" : UserManager.getInstance().getWeight() + "");
        BodyIndex.getInstance().setHeight(UserManager.getInstance().getHeight() == 0 ? "" : UserManager.getInstance().getHeight() + "");

        //默认设置
        Glucose.getInstance().setFoodTime(0);
        BodyIndex.getInstance().setTimeMeal("0");
//        if (BodyIndex.getInstance().isEatmedicine()){
//            showEatMedicineView(0);
//        }

        view.findViewById(R.id.time_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLU_TIME = EMPTY_FOOD;
                select_time_empty.setVisibility(View.VISIBLE);
                select_time_1.setVisibility(View.INVISIBLE);
                select_time_2.setVisibility(View.INVISIBLE);
                select_time_3.setVisibility(View.INVISIBLE);

                Glucose.getInstance().setFoodTime(0);
                BodyIndex.getInstance().setTimeMeal("0");
//                if (BodyIndex.getInstance().isEatmedicine()){
//                    showEatMedicineView(0);
//                }
            }
        });

        view.findViewById(R.id.time_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLU_TIME = ONETIME_FOOD;
                select_time_empty.setVisibility(View.INVISIBLE);
                select_time_1.setVisibility(View.VISIBLE);
                select_time_2.setVisibility(View.INVISIBLE);
                select_time_3.setVisibility(View.INVISIBLE);

                Glucose.getInstance().setFoodTime(2);
                BodyIndex.getInstance().setTimeMeal("2");

            }
        });


        view.findViewById(R.id.time_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLU_TIME = TWOTIME_FOOD;
                select_time_empty.setVisibility(View.INVISIBLE);
                select_time_1.setVisibility(View.INVISIBLE);
                select_time_2.setVisibility(View.VISIBLE);
                select_time_3.setVisibility(View.INVISIBLE);

                Glucose.getInstance().setFoodTime(2);
                BodyIndex.getInstance().setTimeMeal("2");
//                if (BodyIndex.getInstance().isEatmedicine()) {
//                    showEatMedicineView(2);
//                }
            }
        });

        view.findViewById(R.id.time_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLU_TIME = THREETIME_FOOD;
                select_time_empty.setVisibility(View.INVISIBLE);
                select_time_1.setVisibility(View.INVISIBLE);
                select_time_2.setVisibility(View.INVISIBLE);
                select_time_3.setVisibility(View.VISIBLE);

                Glucose.getInstance().setFoodTime(2);
                BodyIndex.getInstance().setTimeMeal("2");
//                if (BodyIndex.getInstance().isEatmedicine()) {
//                    showEatMedicineView(3);
//                }
            }
        });


    }

    private void showEatMedicineView(int time) {

        setEatDropViewGone();

        switch (time) {
            case 0:
                //drop_empty.setVisibility(View.VISIBLE);
                break;

            case 1:
                drop_time1.setVisibility(View.VISIBLE);
                break;

            case 2:
                //drop_time2.setVisibility(View.VISIBLE);
                break;

            case 3:
                //drop_time3.setVisibility(View.VISIBLE);
                break;
        }


    }

    private void hideSoftKeyBoard() {
        try {
            gluEdit.clearFocus();
            ((InputMethodManager) _mActivity.getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(gluEdit.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setEatDropViewGone() {
        if (drop_time1 == null) {
            return;
        }

        //drop_empty.setVisibility(View.GONE);

        drop_time1.setVisibility(View.GONE);

        //drop_time2.setVisibility(View.GONE);

        //drop_time3.setVisibility(View.GONE);
    }


    private void statusInit() {
        BodyIndex.getInstance().setDiabetesType(BodyIndex.Normal);
        statusLayout.setVisibility(View.VISIBLE);
        normal.setSelected(true);
        abnormal.setSelected(false);
        //正常
        normal.setOnClickListener(v -> {
            hideSoftKeyBoard();
            normal.setSelected(true);
            abnormal.setSelected(false);
            gluReminder.setVisibility(View.GONE);
            unitTv.setVisibility(View.GONE);
            gluEdit.setVisibility(View.GONE);
            setEatDropViewGone();
            toogle_1.setChecked(false);
            toogle_2.setChecked(false);
            toogle_3.setChecked(false);
            toogle_4.setChecked(false);
        });
        //>=7的异常状态
        abnormal.setOnClickListener(v -> {
            normal.setSelected(false);
            abnormal.setSelected(true);
            gluReminder.setVisibility(View.VISIBLE);
            unitTv.setVisibility(View.VISIBLE);
            gluEdit.setVisibility(View.VISIBLE);
            showEatMedicineView(1);
            //b为true 选中未敷药
            toogle_1.setChecked(true);
            toogle_2.setChecked(false);
            toogle_3.setChecked(false);
            toogle_4.setChecked(false);
        });
        try {
            if (BodyIndex.Normal.equals(BodyIndex.getInstance().getDiabetesType())) {

                toogle_1.setChecked(false);
                toogle_2.setChecked(false);
                toogle_3.setChecked(false);
                toogle_4.setChecked(false);

            } else {

                showEatMedicineView(1);


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
