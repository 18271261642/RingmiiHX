package com.guider.glu.model;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.guider.glu.R;
import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.UserManager;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by haix on 2019/6/4.
 */

public class GLUMeasureModel {
    private static String TAG = "GLUMeasureModel";
    // 血糖值转换比例
    private int mScale = 10;
    public ArrayList<String> inputArrayList = new ArrayList<String>();

    /**
     * 向设备写入参数
     * 参数格式
     * DDYHSJ + 饮食量 + 生病权重 + 血糖 + bmi + 用药参数 + $
     * DDYHSJ   0/2     0/2       000    00    0000
     * DDYHSJ   0       2         088    35    0101     $
     * DDYHSJ   0       2         088    35    01010    $"
     *
     * @param timeMeal 0 为空腹  2 为餐后两小时
     * @param diabetesType  "" 为糖尿病  "Normal" 为正常
     * @param glucose   8.8 如果是糖尿病患者,需要写入这个值
     * @param weight    体重
     * @param height    身高
     */
    public void makeInputData(String timeMeal, String diabetesType, String glucose, String weight, String height) {
        inputArrayList.add("CCDLJC$");
        StringBuilder userDataStr = new StringBuilder();
        userDataStr.append("DDYHSJ");
        // 饮食时间
        userDataStr.append(timeMeal);
        // 生病权重
        userDataStr.append(TextUtils.isEmpty(diabetesType) ? 2 : 0);
        // 血糖
        if (TextUtils.isEmpty(diabetesType)) {
            Float aFloat = Float.valueOf(glucose);
            int v = Math.round (aFloat * mScale);
            userDataStr.append(v >= 100 ? v : "0" + v);
        } else {
            userDataStr.append("000");
        }
        // bmi
        userDataStr.append(getBMIValue(weight, height));
        // 用药参数
        userDataStr.append(BodyIndex.getInstance().getSulphonylureasState());
        userDataStr.append(BodyIndex.getInstance().getBiguanidesState());
        userDataStr.append(BodyIndex.getInstance().getGlucosedesesSate());
        userDataStr.append("0");
        // 结束
        userDataStr.append("$");
        inputArrayList.add(userDataStr.toString());
        inputArrayList.add("CCHJJC$");
        inputArrayList.add("CCSTAT$");
        Log.d(TAG, "makeInputData " + userDataStr.toString());
    }

    public void reSetList(){
        inputArrayList.clear();
        inputValueIndex = -1;
        tipCount = 1;
        fourthPacketCount = 0;
    }

    public void makeInputData() {
        Log.d(TAG, "makeInputData");
        makeInputData(BodyIndex.getInstance().getTimeMeal(),
                BodyIndex.getInstance().getDiabetesType(),
                BodyIndex.getInstance().getValue() + "",
                UserManager.getInstance().getWeight() + "",
                UserManager.getInstance().getHeight() + "");
        /*
        inputArrayList.add("CCDLJC$");

        String userDataStr = "DDYHSJ";
        userDataStr = userDataStr + BodyIndex.getInstance().getTimeMeal(); // 0and2
        if (BodyIndex.getInstance().getDiabetesType().equals("Normal")) {  // 正常不正常
            userDataStr = userDataStr + "0";
            userDataStr = userDataStr + "000";
        } else {
            userDataStr = userDataStr + "2";

            int testValue = (int) BodyIndex.getInstance().getValue();
            Float gulcoseValue = BodyIndex.getInstance().getValue() * mScale;
            if (testValue < 10) {
                userDataStr = userDataStr + "0" + String.format("%.0f", gulcoseValue);
            } else {
                userDataStr = userDataStr + String.format("%.0f", gulcoseValue);
            }
        }

        // 三种药哪个吃药哪个为1, 不吃为0, 最后拼个0
        userDataStr = userDataStr + getBMIValue(UserManager.getInstance().getWeight()+"", UserManager.getInstance().getHeight()+"");
        String medication = "" + BodyIndex.getInstance().getSulphonylureasState() + "" +
                BodyIndex.getInstance().getBiguanidesState() + "" +
                BodyIndex.getInstance().getGlucosedesesSate() + "0";
        userDataStr = userDataStr + medication + "$";

        inputArrayList.add(userDataStr);
        inputArrayList.add("CCHJJC$");
        inputArrayList.add("CCSTAT$");
        */
    }

    public String getBMIValue(String weight, String height) {
        String bmiValue = "";
        Float weightValue = Float.parseFloat(weight);
        Float heightValue = Float.parseFloat(height);

        Float lowerValue = heightValue / 100;
        lowerValue = lowerValue * lowerValue;

        Float bmi = (weightValue / lowerValue) * 10; // 10 for remove the decimal

        bmiValue = String.format("%.0f", bmi);
        return bmiValue;
    }

    String firstPacketString;
    String secondPacketString;
    String thirdPacketString;
    String fourthPacketString;
    int fourthPacketCount = 0;

    public void readData(String data) {
        // Log.d(TAG, "+++++++++data: "+data);
        if (data.startsWith("DDDLZT")) {
            String strBlock = data.substring(6, data.length() - 1);
            // TODO  这里有转换异常"087$DDJSOK"
            try {
                int batteryLevel = Integer.parseInt(strBlock);
                // int batteryLevel = Integer.valueOf(strBlock);
                if (batteryLevel <= 20) {
                    changeScreensAccordingToCase("Battery_Level_Low");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendDataToDevice();
        } else if (data.startsWith("DDHJER")) {
            String strBlock = data.substring(6, data.length() - 1);
            int envTemp = Integer.valueOf(strBlock);
            if (envTemp == 0) {
                changeScreensAccordingToCase("Env_Temp_Normal");
            } else if (envTemp == 1) {
                changeScreensAccordingToCase("Env_Temp_Low");
            } else if (envTemp == 2) {
                changeScreensAccordingToCase("Env_Temp_High");
            } else if (envTemp == 3) {
                changeScreensAccordingToCase("Env_Temp_Dry");
            } else if (envTemp == 4) {
                changeScreensAccordingToCase("Env_Temp_Humidity");
            }
            sendDataToDevice();
        } else if (data.contains("CWHM")) {
            String strBlock = data.substring(6, data.length() - 1);
            int envTemp = Integer.valueOf(strBlock);
            if (envTemp == 01) {
                changeScreensAccordingToCase("Finger_Temp_Low");
            } else if (envTemp == 06) {
                changeScreensAccordingToCase("Finger_Temp_High");
            } else {
                changeScreensAccordingToCase("ERBW");
            }
        } else if (data.contains("FRSZ")) {
            changeScreensAccordingToCase("Env_Start");
            changeScreensAccordingToCase("Finger_Temp_Normal");
            writeData("CCJSOK$");
        } else if (data.contains("ERBW")) {
            changeScreensAccordingToCase("ERBW");
        } else if (data.contains("ERAD")) {
            changeScreensAccordingToCase("ERAD");
        } else if (data.contains("ERTT")) {
            changeScreensAccordingToCase("ERTT");
        } else if (data.contains("ERRO")) {
            changeScreensAccordingToCase("ERRO");
        } else if (data.contains("CCFRCS")) {
            changeScreensAccordingToCase("CCFRCS");
        } else if (data.contains("CSZY")) {
            firstPacketString = data;
            writeData("CCJSOK$");
            readFirstPacket();
        } else if (data.contains("CSZE")) {
            secondPacketString = data;
            writeData("CCJSOK$");
            readSecondPacket();
        } else if (data.contains("CSZS")) {
            thirdPacketString = data;
            writeData("CCJSOK$");
            readThirdPacket();
        } else if (data.contains("FXCS")) {
            fourthPacketString = data;
            fourthPacketCount = fourthPacketCount + 1;
            writeData("CCJSOK$");

            Log.i("haix", "查看结果的重要Count: "+fourthPacketCount);
            if (fourthPacketCount == 4) {
                writeData("CCJSOK$");

                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                Log.i("haix", "测量结束");
                // 跳转到 ResultActivity
                measureResultListener.measureFinish();
            }
        }
    }

    public String glucose = "";  //血糖
    public String oxygenSaturation = "";  //血氧
    public String hemoglobin = ""; //血紅蛋白
    public String speed = ""; //血流速度
    public String pulse = ""; //心率
    public String envTemp = "";
    public String envHumidity = "";
    public String surfaceTemp = "";
    public String surfaceHumidity = "";
    public String batteryLevel = ""; //電量

    public void readFirstPacket() {
        firstPacketString = firstPacketString.replace("DDCSZY", "");
        firstPacketString = firstPacketString.replace("$", "");
        glucose = firstPacketString.substring(0, 3);
        oxygenSaturation = firstPacketString.substring(3, 5);
        hemoglobin = firstPacketString.substring(5, 9);
        speed = firstPacketString.substring(9);

        BigDecimal bd = BigDecimal.valueOf(Double.parseDouble(glucose) / mScale)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        Glucose.getInstance().setGlucose(bd.doubleValue());
        Glucose.getInstance().setOxygenSaturation(oxygenSaturation);
        Glucose.getInstance().setHemoglobin(hemoglobin);
        Glucose.getInstance().setSpeed(speed);
    }

    public void readSecondPacket() {
        secondPacketString = secondPacketString.replace("DDCSZE", "");
        secondPacketString = secondPacketString.replace("$", "");
        envTemp = secondPacketString.substring(0, 3);
        envHumidity = secondPacketString.substring(3, 6);
        surfaceTemp = secondPacketString.substring(6, 9);
        surfaceHumidity = secondPacketString.substring(9);

        Glucose.getInstance().setFingerTemperature(envTemp);
        Glucose.getInstance().setFingerHumidity(envHumidity);
        Glucose.getInstance().setEnvironmentHumidity(surfaceHumidity);
        Glucose.getInstance().setEnvironmentTemperature(surfaceTemp);
    }

    public void readThirdPacket() {
        thirdPacketString = thirdPacketString.replace("DDCSZS", "");
        thirdPacketString = thirdPacketString.replace("$", "");
        pulse = thirdPacketString.substring(0, 3);
        batteryLevel = thirdPacketString.substring(3, 6);

        pulse = Integer.valueOf(pulse).toString();
        batteryLevel = Integer.valueOf(batteryLevel).toString();

        Glucose.getInstance().setPulse(pulse);
        Glucose.getInstance().setBatteryLevel(batteryLevel);
    }

    int inputValueIndex = -1;

    public void sendDataToDevice() {
        Log.i("haix", "测量------- list大小: "+ inputArrayList.size() + " inputValueIndex的值: "+inputValueIndex);
        if (inputValueIndex < inputArrayList.size() - 1) {
            inputValueIndex = inputValueIndex + 1;
            String str = inputArrayList.get(inputValueIndex);
            Log.i("haix","定入字符串: "+ str);
            writeData(str);
            // sendDataTimeHandler.postDelayed(sendDataRunnable, 8000);
        }
    }

    int packagesCount = 0;
    ArrayList<byte[]> packages = new ArrayList<byte[]>();

    public void writeData(String value) {
        writeData(value.getBytes());
    }

    public boolean writeData(byte[] value) {
        //ArrayList<byte[]> packages = new ArrayList<byte[]>();

        int rest = value.length;
        int count = 0;
        packages.clear();
        while (rest > 0) {
            byte[] blePackage = null;
            rest -= 20;

            if (rest >= 0) {
                blePackage = new byte[20];
                for (int j = 0; j < 20; j++) {
                    blePackage[j] = value[j + count * 20];
                }
            } else {
                rest += 20;
                blePackage = new byte[rest];
                for (int j = 0; j < rest; j++) {
                    blePackage[j] = value[j + count * 20];
                }
                rest -= 20;
            }
            packages.add(blePackage);
            count++;
        }
        packagesCount = (value.length / 20) + (value.length % 20 > 0 ? 1 : 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Log.i("haix", "向设备发送字符串: " +BleBluetooth.getInstance().bytesToHexString(packages.get(0)) );
                if (packages.size() >= 1) {
                    BleBluetooth.getInstance().writeBuffer(packages.get(0));
                }
            }
        }).start();

        return true;
    }


    public void characteristicUUID(BluetoothGatt gatt,
                                   BluetoothGattCharacteristic characteristic) {
        if (characteristic.getValue()[0] == 0x05) {
            byte[] handshake = {0x05};
            characteristic.setValue(handshake);
            gatt.writeCharacteristic(characteristic);
        }
    }

    StringBuilder dataRecievedSB = new StringBuilder();

    Handler handler = new Handler(Looper.getMainLooper());

    public void dataLineUUID(byte[] values) {
        if (dataRecievedSB != null && dataRecievedSB.toString().contains("$")) {
            dataRecievedSB = new StringBuilder();
        }

        for (byte b : values) {
            if ((b & 0xff) <= 0x0f) {
                dataRecievedSB.append("0");
            }
            dataRecievedSB.append((char) (b & 0xff));
        }
        final String mReceivedData = dataRecievedSB.toString();

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mReceivedData.contains("$")) {
                    if (mReceivedData.equals("DDJSOK$")) {
                        if (inputValueIndex != 0 && inputValueIndex != 2) {
                            //Log.i("haix", "--------sendDataToDevice--------");
                            sendDataToDevice();
                        }
                    } else {
                        Log.i("haix", "--------数据-------- "+ mReceivedData);
                        readData(mReceivedData);
                    }
                }
            }
        });

    }


    public static final String GLU_TIP = MyUtils.application.getString(R.string.measure_3_qingyongshouzhi);
    public void changeScreensAccordingToCase(String caseStr) {

        Log.i("haix", "血糖打印结果: "+caseStr);
        switch (caseStr) {
            case "Not_Connected":
                // ,设备连接失败, 请确保电源已经打开
                // measureResultListener.measureRemind(MyUtils.application.getString(R.string.measure_3_lianjieshibai));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }
                measureResultListener.closeBluetoothConnect();
                measureResultListener.error(MyUtils.application.getString(R.string.measure_3_lianjieshibai));
                break;
            case "Env_Start":
                //符合环境条件
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.measure_3_fuhehuanjing));
                break;
            case "Battery_Level_Low":
                //請用手指插入到手指夾型傳感器, 设备电量低, 请在测试前充电
                measureResultListener.measureRemind(GLU_TIP + MyUtils.application.getString(R.string.glu_shebeidianliangdi));
                break;
            case "Env_Temp_Normal":
                //請用手指插入到手指夾型傳感器
                measureResultListener.measureRemind(GLU_TIP);
                insertFingerCountTime();
                break;
            case "Env_Temp_Low":
                //請用手指插入到手指夾型傳感器\n環境溫度太低, 可能會影響測試結果, 謝謝!
                measureResultListener.measureRemind(GLU_TIP + MyUtils.application.getString(R.string.glu_huanjingwendudi));
                insertFingerCountTime();
                break;
            case "Env_Temp_High":
                //請用手指插入到手指夾型傳感器\n環境溫度太高, 可能會影響測試結果, 謝謝!
                measureResultListener.measureRemind(GLU_TIP + MyUtils.application.getString(R.string.glu_huanjingwendugao));
                insertFingerCountTime();
                break;
            case "Env_Temp_Dry":
                //請用手指插入到手指夾型傳感器\n環境太干燥,可能會影響測試結果, 謝謝!
                measureResultListener.measureRemind(GLU_TIP + MyUtils.application.getString(R.string.glu_huanjingtaigan));
                insertFingerCountTime();
                break;
            case "Env_Temp_Humidity":
                //請用手指插入到手指夾型傳感器\n環境濕度太高, 可能會影響測試結果, 謝謝!
                measureResultListener.measureRemind(GLU_TIP + MyUtils.application.getString(R.string.glu_huanjingwendugao));
                insertFingerCountTime();
                break;
            case "Finger_Temp_Normal":
                measureResultListener.startTime60Measure();
                fetchDataFromDevice();
                break;
            case "Finger_Temp_Low":
                //手指溫度太低, 可能會影響測試結果
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_shouzhiwendutaidi));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                measureResultListener.closeBluetoothConnect();
                break;
            case "Finger_Temp_High":
                //手指溫度太高, 可能會影響測試結果
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.shouzhiwendugao));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                measureResultListener.closeBluetoothConnect();
                break;
            case "ERBW":
                //手指未正確伸入!
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_shouzhiweizhengque));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                measureResultListener.error(MyUtils.application.getString(R.string.glu_shouzhiweizhengque));
                measureResultListener.closeBluetoothConnect();
                break;
            case "ERAD":
                //錯誤的數據收集，可能探針被nit插入正確。
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_shujushoujicuowu));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                measureResultListener.closeBluetoothConnect();
                break;
            case "ERTT":
                //手指感測有誤，可能導致錯誤的數據收集。
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_shouzhiganceyouhu));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                measureResultListener.closeBluetoothConnect();
                break;
            case "ERRO":
                //手指已移開!
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_shouzhiyiyikai));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                measureResultListener.error(MyUtils.application.getString(R.string.glu_shouzhiyiyikai));
                measureResultListener.closeBluetoothConnect();
                break;
            case "CCFRCS":
                //手指未伸入!
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_shouzhiweishenru));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                measureResultListener.error(MyUtils.application.getString(R.string.glu_shouzhiweishenru));
                measureResultListener.closeBluetoothConnect();
                break;
            case "SomeThing_Wrong":
                //出錯的測試, 請再次測試
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_chucuodeceshi));
                if (timerForTest != null) {
                    timerForTest.cancel();
                    timerForTest = null;
                }

                if (executionTimeCounter != null) {
                    executionTimeCounter.cancel();
                    executionTimeCounter = null;
                }

                measureResultListener.closeBluetoothConnect();
                break;
        }
    }


    CountDownTimer insertFingerTimerCounter;
    CountDownTimer executionTimeCounter;
    CountDownTimer timerForTest;

    public void insertFingerCountTime() {
        measureResultListener.insertFinger();

        insertFingerTimerCounter = new CountDownTimer(12000, 1000) {
            public void onTick(long millisUntilFinished) {
                measureResultListener.fingerTime((int)millisUntilFinished/1000);
            }

            public void onFinish() {
                // insertFingerTimer.setVisibility(View.GONE);
                changeScreensAccordingToCase("CCFRCS");
            }
        };
        insertFingerTimerCounter.start();
    }

    int tipCount = 1;

    public void fetchDataFromDevice() {
        if (insertFingerTimerCounter != null) {
            insertFingerTimerCounter.cancel();
            insertFingerTimerCounter = null;
        }
        countTime();
        timerForTest = new CountDownTimer(65000, 10000) {
            public void onTick(long millisUntilFinished) {
                changeTips(tipCount);
                tipCount++;
            }

            public void onFinish() {
                // saveDataAndMoveToNextScreen();
            }
        };
        timerForTest.start();
    }

    public void countTime() {
        executionTimeCounter = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // 测试
                measureResultListener.measureTime(millisUntilFinished/1000+"");
            }

            public void onFinish() {
            }
        };
        executionTimeCounter.start();
    }

    public void changeTips(int tipCount) {
        Log.i("haix", "血糖打印结果2: "+tipCount);
        switch (tipCount) {
            case 1:
                //定期健康檢查
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_dingqijiankangjiancha));
                break;
            case 2:
                //每天運動30分鐘
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.meitianyundong30fenzhong));
                break;
            case 3:
                //從事抒壓活動，讓自己多放松
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.congshishuyahuodong));
                break;
            case 4:
                //正確飲食\n依照下列飲食順序和份量\n2份高纖蔬菜\n2份蛋白質\n1份碳水化合物
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_zhengqueyinshi));
                break;
            case 5:
                //維持充足的睡眠
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_weichichongzudeshuimian));
                break;
            case 6:
                //補充水份
                measureResultListener.measureRemind(MyUtils.application.getString(R.string.glu_buchongshuifen));
                break;
        }

        // 請保持冷靜、保持當前的姿態
        // instructionFirstTV.setText(getResources().getString(R.string.stayCalm));
    }

    private MeasureResultListener measureResultListener;
    public void setMeasureResultListener(MeasureResultListener measureResultListener){
        this.measureResultListener = measureResultListener;
    }


    public interface MeasureResultListener{
        void measureTime(String time);
        void measureRemind(String remind);
        void measureFinish();
        void closeBluetoothConnect();
        void insertFinger();
        void startTime60Measure();//插入手指后开始正式测量
        void error(String er);
        void fingerTime(int time);
    }
}
