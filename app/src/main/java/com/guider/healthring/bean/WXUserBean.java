package com.guider.healthring.bean;

import java.util.List;

/**
 *
 */
public class WXUserBean {

    /**
     * openid : onxAK59awxJ17IjZnaYslUiOshEE
     * nickname : 丶
     * sex : 1
     * language : zh_CN
     * city : Shenzhen
     * province : Guangdong
     * country : CN
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/qndNbICrNwia9HQHMq8Bu4CAJ6KCfum9RQe408vq76KibSYiaicibbQXOuhlJzibEL8PrX1E3l6iaQH4UMjllrM6icVhIQ/132
     * privilege : []
     * unionid : oaVtW5_Yp-o9NPhbmlqFfUn-5He0
     */

    private String openid;
    private String nickname;
    private String nickName;
    private String sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;
    private List<?> privilege;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<?> privilege) {
        this.privilege = privilege;
    }

    @Override
    public String toString() {
        return "WXUserBean{" +
                "openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", language='" + language + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", unionid='" + unionid + '\'' +
                ", privilege=" + privilege +
                '}';
    }
}
