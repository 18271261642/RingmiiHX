package com.guider.health.bp.view.yfbp;

import com.guider.health.common.core.HeartPressYf;

import ble.BleClient;

public class YfkjDataAdapter {

    public YfkjMeasurementBean getReadData() {
        byte[] bytes = BleClient.instance().getClassicClient().read();
        if (bytes == null || bytes.length != 43) {
            return null;
        }
        YfkjMeasurementBean bean = new YfkjMeasurementBean();
        bean.setAge(byteToInt(bytes[4], bytes[5]));
        bean.setHight(byteToInt(bytes[6], bytes[7]));
        bean.setWidth(byteToInt(bytes[8], bytes[9]));

        bean.setASI(byteToInt(bytes[10], bytes[11]));
        bean.setSBP(byteToInt(bytes[12], bytes[13]));
        bean.setDBP(byteToInt(bytes[14], bytes[15]));
        bean.setMAP(byteToInt(bytes[16], bytes[17]));
        bean.setPP(byteToInt(bytes[18], bytes[19]));
        bean.setHR(byteToInt(bytes[20], bytes[21]));
        bean.setBMI(byteToInt(bytes[30], bytes[31]));
        bean.setC(byteToInt(bytes[32], bytes[33]));
        HeartPressYf.getInstance().setDbp(bean.getDBP() + "");
        HeartPressYf.getInstance().setSbp(bean.getSBP() + "");
        HeartPressYf.getInstance().setHeart(bean.getHR() + "");
        HeartPressYf.getInstance().setASI(bean.getASI() + "");
        HeartPressYf.getInstance().setMAP(bean.getMAP() + "");
        HeartPressYf.getInstance().setPP(bean.getPP() + "");
        HeartPressYf.getInstance().setBMI(bean.getBMI() + "");
        HeartPressYf.getInstance().setC(bean.getC() + "");
        return bean;
    }

    public void setPersionInfo(YfkjDataAdapter.YfkjPersionData persionData) {
        byte[] bytes = new byte[12];
        bytes[0] = 0x3f;
        bytes[1] = 0x08;
        bytes[2] = 0x32;
        bytes[3] = 0x00;
        byte[] ageByte = intToByte(persionData.age);
        bytes[4] = ageByte[0];
        bytes[5] = ageByte[1];
        byte[] hightByte = intToByte(persionData.hight);
        bytes[6] = hightByte[0];
        bytes[7] = hightByte[1];
        byte[] widthByte = intToByte(persionData.width);
        bytes[8] = widthByte[0];
        bytes[9] = widthByte[1];
        bytes[10] = (byte) (bytes[0] ^ bytes[1]^ bytes[2]^ bytes[3]^ bytes[4]^ bytes[5]^ bytes[6]^ bytes[7]^ bytes[8]^ bytes[9]);
        bytes[11] = 0x0d;
        BleClient.instance().getClassicClient().write(bytes);
        BleClient.instance().getClassicClient().read();
    }

    public int byteToInt(byte hight, byte low) {
        return ((hight & 0xff) << 8) | (low & 0xff);
    }

    public byte[] intToByte(int i) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((i >> 8) & 0xff);
        bytes[1] = (byte) (i & 0xff);
        return bytes;
    }

    static class YfkjPersionData {
        public int age = 40;    // 年龄
        public int hight = 170;  // 身高
        public int width = 160;  // 体重
    }

    static class YfkjMeasurementBean {
        int age;    // 年龄
        int hight;  // 身高
        int width;  // 体重

        int ASI;    // 动脉硬化指数
        int SBP;    // 收缩压
        int DBP;    // 舒张压
        int MAP;    // 平均压
        int PP;    // 脉压
        int HR;    // 心率
        double BMI;    // 体重指数
        int C;    // 血管顺应性

        public int getAge() {
            return age;
        }

        public YfkjMeasurementBean setAge(int age) {
            this.age = age;
            return this;
        }

        public int getHight() {
            return hight;
        }

        public YfkjMeasurementBean setHight(int hight) {
            this.hight = hight;
            return this;
        }

        public int getWidth() {
            return width;
        }

        public YfkjMeasurementBean setWidth(int width) {
            this.width = width;
            return this;
        }

        public int getASI() {
            return ASI;
        }

        public YfkjMeasurementBean setASI(int ASI) {
            this.ASI = ASI;
            return this;
        }

        public int getSBP() {
            return SBP;
        }

        public YfkjMeasurementBean setSBP(int SBP) {
            this.SBP = SBP;
            return this;
        }

        public int getDBP() {
            return DBP;
        }

        public YfkjMeasurementBean setDBP(int DBP) {
            this.DBP = DBP;
            return this;
        }

        public int getMAP() {
            return MAP;
        }

        public YfkjMeasurementBean setMAP(int MAP) {
            this.MAP = MAP;
            return this;
        }

        public int getPP() {
            return PP;
        }

        public YfkjMeasurementBean setPP(int PP) {
            this.PP = PP;
            return this;
        }

        public int getHR() {
            return HR;
        }

        public YfkjMeasurementBean setHR(int HR) {
            this.HR = HR;
            return this;
        }

        public double getBMI() {
            return BMI;
        }

        public YfkjMeasurementBean setBMI(double BMI) {
            this.BMI = BMI / 10;
            return this;
        }

        public int getC() {
            return C;
        }

        public YfkjMeasurementBean setC(int c) {
            C = c;
            return this;
        }
    }
}
