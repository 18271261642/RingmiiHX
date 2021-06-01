package com.guider.healthring.b31.ecg;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;
import androidx.annotation.Nullable;


/**
 * 测量结果 心率、HRV、QT间期描述
 * Created by Admin
 * Date 2021/5/24
 */
public class ECGResultDescActivity extends WatchBaseActivity {

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    TextView ecgResultDescRangeTv;
    TextView ecgResultDescValueTv;
    TextView ecgResultDescStatusTv;
    TextView ecgResultDescTv;
    TextView ecgResultDescUnitTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_result_desc_layout);


        initViews();

        int code = getIntent().getIntExtra("ecg_code",0);
        int value = getIntent().getIntExtra("ecg_value",0);
        showDescRand(code,value);

    }


    private void showDescRand(int code,int value){
        try {
            ecgResultDescValueTv.setText((value == 0 ?"--":value)+"");
            switch (code){
                case 0:
                    commentB30TitleTv.setText("心率详情");
                    ecgResultDescRangeTv.setText(heartRang);
                    ecgResultDescTv.setText(heartDesc);
                    ecgResultDescUnitTv.setText("次/分");
                    if(value == 0){
                        ecgResultDescStatusTv.setText("--");
                        return;
                    }

                    if(value<60){
                        ecgResultDescStatusTv.setText("偏低");
                        return;
                    }
                    if(value>100){
                        ecgResultDescStatusTv.setText("偏高");
                        return;
                    }
                    ecgResultDescStatusTv.setText("正常");
                    break;
                case 1:
                    commentB30TitleTv.setText("QT间期");
                    ecgResultDescUnitTv.setText("毫秒");
                    ecgResultDescRangeTv.setText(qtRang);
                    ecgResultDescTv.setText(qtDesc);
                    if(value == 0){
                        ecgResultDescStatusTv.setText("--");
                        return;
                    }

                    if(value<300){
                        ecgResultDescStatusTv.setText("偏低");
                        return;
                    }

                    if(value>450){
                        ecgResultDescStatusTv.setText("偏高");
                        return;
                    }

                    ecgResultDescStatusTv.setText("正常");
                    break;
                case 2:
                    commentB30TitleTv.setText("HRV详情");
                    ecgResultDescUnitTv.setText("毫秒");
                    ecgResultDescRangeTv.setText(hrvRang);
                    ecgResultDescTv.setText(hrvDesc);
                    if(value == 0){
                        ecgResultDescStatusTv.setText("--");
                        return;
                    }

                    if(value<0){
                        ecgResultDescStatusTv.setText("偏低");
                        return;
                    }

                    if(value>230){
                        ecgResultDescStatusTv.setText("偏高");
                        return;
                    }

                    ecgResultDescStatusTv.setText("正常");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private void initViews() {
        findViews();
        commentB30BackImg.setVisibility(View.VISIBLE);

        commentB30BackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void findViews(){
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        ecgResultDescRangeTv = findViewById(R.id.ecgResultDescRangeTv);
        ecgResultDescValueTv = findViewById(R.id.ecgResultDescValueTv);
        ecgResultDescStatusTv = findViewById(R.id.ecgResultDescStatusTv);
        ecgResultDescTv = findViewById(R.id.ecgResultDescTv);
        ecgResultDescUnitTv = findViewById(R.id.ecgResultDescUnitTv);
    }



    private String heartRang = "参考范围: 60-100次/分";
    private String qtRang = "参考范围: 300-452毫秒";
    private String hrvRang = "参考范围:0-230毫秒";

    private String heartDesc = "心率是指正常人安静状态下每分钟心跳的次数，可因年龄、性别或其他生理因素产生个人差异。一般来说，年龄越小，心率越快，老年人心跳比年轻人慢，女性的心率比同龄男性快。";
    private String qtDesc = "QT间期代表心室去极化和复极化过程的总时程，测定值随年龄和性别而变化。QT间期与心率快慢有密切关系，正常人心率加速则QT间期缩短，反之则延长。QT改变在临床心电图诊断上具有重要价值，特别是QT延长对预报恶性心室律失常和心脏性猝死有重要意义。";
    private String hrvDesc = "心率变异性(HRV)是指逐次心跳周期差异的变化情况，它含有神经体液因素对心血管系统调节的信息，从而判断其对心血管等疾病的病情及预防，是预测心脏性猝死和心率失常的一个有价值的指标。";

}
