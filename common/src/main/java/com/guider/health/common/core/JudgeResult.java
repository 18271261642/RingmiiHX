package com.guider.health.common.core;

/**
 * Created by haix on 2019/7/26.
 */

public class JudgeResult {

    private String result = "正常";
    private int direction = 0;//0为正常  //-1 -2 // 1 2

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
