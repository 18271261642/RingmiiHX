package com.guider.baselib.device;

/**
 * 单项单位
 */
public interface IUnit {
    // 血糖

    /**
     * 返回显示的值，即值乘以比例
     * @param value     测量值
     * @param scale    小数点后几位
     * @return
     */
    double getGluShowValue(double value, int scale);
    /**
     * 返回真正的值，即单位为mmol/L的值
     * @param value     测量值
     * @param scale    小数点后几位
     * @return
     */
    double getGluRealValue(double value, int scale);
    String getGluUnit();
}
