package com.guider.health.apilib.model.hd.standard;

public class StandardResultBean {

    /**
     * anlysis : 疑似高血压
     * normal : false
     * anlysis2 : 偏高,偏高
     */

    private String anlysis; // 文字描述 , 用于展示给用户看的结论
    private boolean normal;
    private String anlysis2;// 等级描述 , 固定的用于判断是否绘制上下箭头和
    private String type;

    public String getType() {
        return type;
    }

    public StandardResultBean setType(String type) {
        this.type = type;
        return this;
    }

    public String getAnlysis() {
        return anlysis;
    }

    public void setAnlysis(String anlysis) {
        this.anlysis = anlysis;
    }

    public boolean isNormal() {
        return normal;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    public String getAnlysis2() {
        return anlysis2;
    }

    public void setAnlysis2(String anlysis2) {
        this.anlysis2 = anlysis2;
    }
}
