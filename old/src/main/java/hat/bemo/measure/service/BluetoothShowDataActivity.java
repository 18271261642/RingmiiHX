package hat.bemo.measure.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.guider.healthring.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import hat.bemo.APIupload.ChangeDateFormat;
import hat.bemo.BaseActivity;
import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.MyApplication;
import hat.bemo.MyService;
import hat.bemo.Tools;
import hat.bemo.measure.set.MeasureController;
import hat.bemo.measure.set.ParametersResult;
import hat.bemo.measure.setting_db.Insert;
import hat.bemo.measure.setting_db.VO_BG_DATA;
import hat.bemo.measure.setting_db.VO_BO_DATA;
import hat.bemo.measure.setting_db.VO_BP_DATA;




public class BluetoothShowDataActivity extends BaseActivity {

    private static final String TAG = "BluetoothShowDataActivity";
    private LinearLayout lin;
    private TextView dbpview;
    private TextView sbpview;
    private TextView heartview;
    private TextView parameters;
    private LinearLayout lin_bg;

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView unit;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;

    private MeasureController measurecontroller = new MeasureController();
    private Insert insert = new Insert();
    private VO_BP_DATA vobpdata = new VO_BP_DATA();
    private VO_BO_DATA vobodata = new VO_BO_DATA();
    private VO_BG_DATA vobgdata = new VO_BG_DATA();

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle == null)
            return;
        int Id = bundle.getInt("type");
        System.out.println("Mark 有嗎2?");
        Log.d(TAG, "BluetoothShowDataActivity" + "這裡這裡");
        Log.d("Id", "Id" + Id);
        switch (Id) {
//		case R.id.btn_item01:
//			 setContentView(R.layout.gd800_measure_bp);
//			 Bp_findviews();
//			 Dialog_bp();
//			break;
//		case R.id.btn_item02:
//			 setContentView(R.layout.gd800_measure_bo);
//			 Bo_findviews();
//			 Dialog_bo();
//			break;
//		case R.id.btn_item03:
//
//			 setContentView(R.layout.gd800_measure_bg);
//			 Bg_findviews();
//			 Dialog_bg();
//
//			break;
//		case R.id.btn_item04:
//
//			break;
            case 0:
                try {
                    String values = bundle.getString(MyService.PARAMETERS_KEY);
                    int type = bundle.getInt("TYPES");
                    System.out.println("Mark 有嗎3?");
                    Log.d("TYPES", "TYPES" + type);
                    if (type == 2) {
                        setContentView(R.layout.fora_record_history_details);
                        TextView bpValue = (TextView) findViewById(R.id.bpValue);
                        TextView bpTime = (TextView) findViewById(R.id.bpTime);
                        TextView mfffffff = (TextView) findViewById(R.id.fffffff);


                        boolean status = values.contains("/");
                        boolean statustt = values.contains("mg/dl");


                        Log.d("values", "values" + values);

                        if (status && !statustt) {
                            System.out.println("包含");
                            mfffffff.setText("血压");
                            String[] ccc = Tools.stoa(values);
                            bpValue.setText("收缩压:" + ccc[0] + "\n" +
                                    "舒张压:" + ccc[1] + "\n" +
                                    "脉搏:" + ccc[2] + "\n");
                            Log.d("values", "values" + values);

                        } else if (status && statustt) {
                            mfffffff.setText("血糖");
                            System.out.println("不包含");
                            bpValue.setText("血糖:" + values);
                            Log.d("values", "values" + values);
                        } else {
                            mfffffff.setText("体温");
                            System.out.println("不包含");
                            bpValue.setText("体温:" + values);
                            Log.d("values", "values" + values);
                        }

                        ScreenOn();
                        bpTime.setText(ChangeDateFormat.Year() + "." +
                                ChangeDateFormat.getMonth() + "." +
                                ChangeDateFormat.getDay() + "\n" + "      " +
                                ChangeDateFormat.getHour() + ":" + ChangeDateFormat.getMinute());
                        try {
                            Intent mIntent = new Intent(this, BLEScanService.class);
                            startService(mIntent);

                            Intent Intent2 = new Intent(this, MyService.class);
                            startService(Intent2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        setContentView(R.layout.gd800_measure_bp2);
                        Bp_findviews2();
                        parameters.setText(values);
                    }
                    ScreenOn();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        int Id = bundle.getInt("type");
        System.out.println("Mark 有嗎2?");
//		Log.d(TAG,"BluetoothShowDataActivity"+"這裡這裡");
        Log.d("Id", "Id" + Id);
        switch (Id) {
//		case R.id.btn_item01:
//			 setContentView(R.layout.gd800_measure_bp);
//			 Bp_findviews();
//			 Dialog_bp();
//			break;
//		case R.id.btn_item02:
//			 setContentView(R.layout.gd800_measure_bo);
//			 Bo_findviews();
//			 Dialog_bo();
//			break;
//		case R.id.btn_item03:
//
//			 setContentView(R.layout.gd800_measure_bg);
//			 Bg_findviews();
//			 Dialog_bg();
//
//			break;
//		case R.id.btn_item04:
//
//			break;
            case 0:
                try {
                    String values = bundle.getString(MyService.PARAMETERS_KEY);
                    int type = bundle.getInt("TYPES");
                    System.out.println("Mark 有嗎3?");
                    Log.d("TYPES", "TYPES" + type);

                    if (type == 2) {
                        setContentView(R.layout.fora_record_history_details);
                        TextView bpValue = (TextView) findViewById(R.id.bpValue);
                        TextView bpTime = (TextView) findViewById(R.id.bpTime);
                        TextView mfffffff = (TextView) findViewById(R.id.fffffff);


                        boolean status = values.contains("/");
                        boolean statustt = values.contains("mg/dl");


                        Log.d("values", "values" + values);

                        if (status && !statustt) {
                            System.out.println("包含");
                            mfffffff.setText("血压");
                            String[] ccc = Tools.stoa(values);
                            bpValue.setText("收缩压:" + ccc[0] + "\n" +
                                    "舒张压:" + ccc[1] + "\n" +
                                    "脉搏:" + ccc[2] + "\n");
                            Log.d("values", "values" + values);

                        } else if (status && statustt) {
                            mfffffff.setText("血糖");
                            System.out.println("不包含");
                            bpValue.setText("血糖:" + values);
                            Log.d("values", "values" + values);
                        } else {
                            mfffffff.setText("体温");
                            System.out.println("不包含");
                            bpValue.setText("体温:" + values);
                            Log.d("values", "values" + values);
                        }


                        bpTime.setText(ChangeDateFormat.Year() + "." +
                                ChangeDateFormat.getMonth() + "." +
                                ChangeDateFormat.getDay() + "\n" + "      " +
                                ChangeDateFormat.getHour() + ":" + ChangeDateFormat.getMinute());

                        ScreenOn();
//						Intent mIntent = new Intent(this, BLEScanService.class);
//						startService(mIntent);
//
//						Intent Intent2 = new Intent(this, MyService.class);
//						startService(Intent2);
                    } else {
                        setContentView(R.layout.gd800_measure_bp2);
                        Bp_findviews2();
                        parameters.setText(values);
                    }
                    ScreenOn();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
        try {
            Intent mIntent = new Intent(this, BLEScanService.class);
            startService(mIntent);

            Intent Intent2 = new Intent(this, MyService.class);
            startService(Intent2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //	private void Bp_findviews(){
//		 lin = (LinearLayout)findViewById(R.id.lins);
//		 dbpview = (TextView)findViewById(R.id.dbp);
//		 sbpview = (TextView)findViewById(R.id.sbp);
//		 heartview = (TextView)findViewById(R.id.heart);
//	}
//
    private void Bp_findviews2() {
        lin = (LinearLayout) findViewById(R.id.lins);
        parameters = (TextView) findViewById(R.id.parameters);
    }
//
//	private void Bg_findviews(){
//		 textView1 = (TextView)findViewById(R.id.HeartTxt);
//		 textView2 = (TextView)findViewById(R.id.SBP);
//		 textView3 = (TextView)findViewById(R.id.phonenum);
//		 unit = (TextView)findViewById(R.id.unit);
//		 lin_bg = (LinearLayout)findViewById(R.id.lin_bg);
//	}
//
//	private void Bo_findviews(){
//		  textView1 = (TextView)findViewById(R.id.HeartTxt);
//		  textView2 = (TextView)findViewById(R.id.SBP);
//	}


//	@Override
//	protected void onStart() {
//		super.onStart();
//
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		finish();
//	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		finish();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		finish();
//	}

    public void Dialog_bp() {
        try {
            ParametersResult.meaDataList.clear();
            BluetoothBaseActivity.dialogswitch = "OFF";
            if (BluetoothBaseActivity.anim != null) BluetoothBaseActivity.anim.stop();
            DisplayMetrics metrics = BluetoothBaseActivity.mContext.getResources().getDisplayMetrics();

            int height = Integer.valueOf((int) (metrics.heightPixels / 1.5f));

            lin.getLayoutParams().width = metrics.widthPixels;
            lin.getLayoutParams().height = height;

            dbpview.setText(BluetoothBaseActivity.result.Getbp_DBP());
            sbpview.setText(BluetoothBaseActivity.result.Getbp_SBP());
            heartview.setText(BluetoothBaseActivity.result.Getbp_HEART());

            vobpdata.setAccount(MyApplication.imei);
            vobpdata.setAFIB("N");
            vobpdata.setAM(WhenChengN());
            vobpdata.setARR("1");
            vobpdata.setBS_TIME(ChangeDateFormat.CreateDate());
            vobpdata.setCREATE_DATE(ChangeDateFormat.CreateDate());
            vobpdata.setDATA_TYPE("1");
            vobpdata.setDIAGNOSTIC_MODE("1");
            vobpdata.setHIGH_PRESSURE(BluetoothBaseActivity.result.Getbp_DBP());
            vobpdata.setLOW_PRESSURE(BluetoothBaseActivity.result.Getbp_SBP());
            vobpdata.setMODEL("0");
            vobpdata.setPLUS(BluetoothBaseActivity.result.Getbp_HEART());
            vobpdata.setPM(WhenChengY());
            vobpdata.setRES("1");
            vobpdata.setUSUAL_MODE("1");
            vobpdata.setYEAR(ChangeDateFormat.Year());
            insert.insert_BP_DATA(mContext, vobpdata);

            measurecontroller.LoData(mContext, MeasureController.type_0x36);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Dialog_bg() {
        try {
            ParametersResult.meaDataList.clear();
            BluetoothBaseActivity.dialogswitch = "OFF";
            if (BluetoothBaseActivity.anim != null) BluetoothBaseActivity.anim.stop();
            HashMap<String, String> dataresult = BluetoothBaseActivity.result.Getbg_BG2();

            if (dataresult.size() > 0) {
//				if(SharedPreferences_status.getUnit(this) == 0) {
//					textView1.setText(dataresult.get("GLUS")+"");
//					unit.setText("mmol/L");
//				}
//				else{
                String glus = dataresult.get("GLUS");
                float afterCalculatingGlus = Math.round(Float.valueOf(glus) * 18);
                textView1.setText(String.valueOf((int) afterCalculatingGlus));
                unit.setText("mg/dl");
//				}

                textView2.setText(dataresult.get("XLSD") + "\n" + "PU");
                textView3.setText(dataresult.get("XHDB") + "\n" + "g/L");

                float bf = Float.valueOf(dataresult.get("GLUS")) * 10;
                String bgv = String.valueOf((int) bf);
                vobgdata.setGlu(bgv);
                vobgdata.setAccount(MyApplication.imei);
                vobgdata.setBsTime(ChangeDateFormat.BsTime());
                vobgdata.setUnit("0");
                vobgdata.setBloodFlowVelocity(dataresult.get("XLSD"));
                vobgdata.setHemoglobin(dataresult.get("XHDB"));
                insert.insert_BG_DATA(mContext, vobgdata);
                dataresult.clear();
            } else {
//				if(SharedPreferences_status.getUnit(this) == 0){
//					textView1.setText(BluetoothBaseActivity.result.Getbg_BG());
//					unit.setText("mmol/L");
//				}
//				else{
                float afterCalculatingGlus = Math.round(Float.valueOf(BluetoothBaseActivity.result.Getbg_BG()) * 18);
                textView1.setText(String.valueOf((int) afterCalculatingGlus));
                unit.setText("mg/dl");
//				}
//				1116 配合後端type unit 0 去換算.......
                float bf = Float.valueOf(BluetoothBaseActivity.result.Getbg_BG()) * 10;
                String bgv = String.valueOf((int) bf);
                vobgdata.setGlu(bgv);
                vobgdata.setAccount(MyApplication.imei);
                vobgdata.setBsTime(ChangeDateFormat.BsTime());
                vobgdata.setUnit("0");
                vobgdata.setBloodFlowVelocity("");
                vobgdata.setHemoglobin("");
                insert.insert_BG_DATA(mContext, vobgdata);
            }

            measurecontroller.LoData(mContext, MeasureController.type_0x37);
            BluetoothBaseActivity.result.bg_map.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Dialog_bo() {
        try {
            ParametersResult.meaDataList.clear();
            BluetoothBaseActivity.dialogswitch = "OFF";
            if (BluetoothBaseActivity.anim != null) BluetoothBaseActivity.anim.stop();

            textView1.setText(BluetoothBaseActivity.result.Getbo_oxy() + " %");
            textView2.setText(BluetoothBaseActivity.result.Getbo_heart() + " bpm");

            vobodata.setAccount(MyApplication.imei);
            vobodata.setCREATE_DATE(ChangeDateFormat.CreateDate());
            vobodata.setOXY_COUNT("1");
            vobodata.setOXY_TIME(ChangeDateFormat.CreateDate());
            vobodata.setPLUS(BluetoothBaseActivity.result.Getbo_heart());
            vobodata.setSPO2(BluetoothBaseActivity.result.Getbo_oxy());
            insert.insert_BO_DATA(mContext, vobodata);

            measurecontroller.LoData(mContext, MeasureController.type_0x38);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String WhenChengN() {
        return Integer.valueOf(new SimpleDateFormat("HH").format(new Date())) >= 18 ? "Y" : "N";
    }

    private String WhenChengY() {
        return Integer.valueOf(new SimpleDateFormat("HH").format(new Date())) >= 18 ? "N" : "Y";
    }

    @SuppressWarnings("deprecation")
    public static void ScreenOn() {
        //Mark 休眠相關
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock mPowerWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.FULL_WAKE_LOCK, "TAG");
            mPowerWakeLock.acquire();
        }
    }
}