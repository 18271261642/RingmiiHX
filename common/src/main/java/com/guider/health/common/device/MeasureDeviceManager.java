package com.guider.health.common.device;

import com.guider.health.common.core.Config;
import com.guider.health.common.core.ForaBO;
import com.guider.health.common.core.ForaET;
import com.guider.health.common.core.ForaGlucose;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.HearRate12;
import com.guider.health.common.core.HearRateHd;
import com.guider.health.common.core.HeartPressAve;
import com.guider.health.common.core.HeartPressBp;
import com.guider.health.common.core.HeartPressCx;
import com.guider.health.common.core.HeartPressMbb_88;
import com.guider.health.common.core.HeartPressMbb_9804;
import com.guider.health.common.core.HeartPressYf;

import java.util.ArrayList;
import java.util.List;

public class MeasureDeviceManager {

    /**
     * 获取所有参与测量的设备
     * @return List<设备蓝牙名>
     */
    public List<String> getMeasureDevices() {
        ArrayList<String> needShowList = new ArrayList<>();
        for (int i = 0; i < Config.DEVICE_KEYS.size(); i++) {
            String devase = Config.DEVICE_KEYS.get(i);
            switch (devase) {
                case DeviceInit.DEV_BP:   // 移动版
                    if (HeartPressBp.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_BP_MBB_88:   // 移动版
                    if (HeartPressMbb_88.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_BP_MBB_9804:   // 移动版
                    if (HeartPressMbb_9804.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_BP_CX:            // 插线版
                    if (HeartPressCx.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_BP_YF:            // 云峰
                    if (HeartPressYf.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_BP_AVE:        // AVE
                    if (HeartPressAve.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_ECG_6:          // 6导
                    if (HearRate.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_ECG_HD:          // 6导
                    if (HearRateHd.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_ECG_12:          // 12导
                    if (HearRate12.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_GLU:  // 血糖
                    if (Glucose.getInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_FORA_GLU:  // 福尔血糖
                    if(ForaGlucose.getForaGluInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_FORA_ET:  // 福尔耳温
                    if (ForaET.getForaETInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
                case DeviceInit.DEV_FORA_BO:  // 福尔血氧 TODO
                    if (ForaBO.getForaBOInstance().isTag()) {
                        needShowList.add(devase);
                    }
                    break;
            }
        }
        return needShowList;
    }

    /**
     * 从参与测量的设备列表中移除某个设备
     */
    public void removeDeviceFromList(String deviceName) {
        switch (deviceName) {
            case DeviceInit.DEV_BP:   // 移动版
                HeartPressBp.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_BP_MBB_88:   // 移动版
                HeartPressMbb_88.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_BP_MBB_9804:   // 移动版
                HeartPressMbb_9804.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_BP_CX:            // 插线版
                HeartPressCx.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_BP_YF:            // 云峰
                HeartPressYf.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_BP_AVE:        // AVE
                HeartPressAve.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_ECG_6:          // 6导
                HearRate.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_ECG_HD:          // 红豆
                HearRateHd.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_ECG_12:          // 12导
                HearRate12.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_GLU:  // 血糖
                Glucose.getInstance().setTag(false);
                break;
            case DeviceInit.DEV_FORA_GLU:  // 福尔血糖
                ForaGlucose.getForaGluInstance().setTag(false);
                break;
            case DeviceInit.DEV_FORA_ET:  // 福尔耳温
                ForaET.getForaETInstance().setTag(false);
                break;
            case DeviceInit.DEV_FORA_BO:  // 福尔血氧 TODO
                ForaBO.getForaBOInstance().setTag(false);
        }
    }
}
