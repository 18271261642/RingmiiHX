package com.guider.health.common.device;

import java.math.BigDecimal;

public class UnitImpl implements IUnit {
    private BaseUnit mBaseUnit;

    public UnitImpl(BaseUnit baseUnit) {
        mBaseUnit = baseUnit;
    }


    @Override
    public double getGluShowValue(double value, int scale) {
        BigDecimal bd = BigDecimal.valueOf(value * mBaseUnit.gain);
        return bd.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public double getGluRealValue(double value, int scale) {
        BigDecimal bd = BigDecimal.valueOf(value / mBaseUnit.gain);
        return bd.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public String getGluUnit() {
        return mBaseUnit.unit;
    }
}
