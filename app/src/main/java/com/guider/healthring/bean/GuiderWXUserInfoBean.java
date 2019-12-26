package com.guider.healthring.bean;

/**
 *
 * 盖德微信登录返回用户信息
 * Created by Admin
 * Date 2019/9/24
 */
public class GuiderWXUserInfoBean {


    /**
     * code : 0
     * msg : 成功
     * data : {"flag":false,"WechatInfo":{"appId":"b3c327c04d8b0471","headimgurl":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK7cJSic0C6etnnMaQnS8zOgc4P8rcy7sjSV4jnWaGDFvEcyNoaw2gyS7k4rkQ9OlDsBUNebbd3Xjw/132","nickname":"孫建華","openid":"onxAK54a_WgeoGSavJJ32-iqw7bw","unionid":"oaVtW5zyWeYujB5bPpttPiRYwojE","sex":1},"TokenInfo":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMSIsInBhc3N3ZCI6ImUxMGFkYzM5NDliYTU5YWJiZTU2ZTA1N2YyMGY4ODNlIiwiaWQiOjMxLCJleHAiOjE1NjkzMDE3MTEsImlhdCI6MTU2OTI5NDUxMSwianRpIjoiZWUwZjUyYWEtZTIxNi00YmVkLTkyOWYtZDk4YzNmNmZjNmJhIn0.L5JjKKm-3i6yB0R8q4AGsQF5gwFuh49T3MajUVCb6nI","refreshToken":"NA3aH20L7f6rnjsoK6mdsTi5HMmVONZL","accountId":31,"expired":7200}}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * flag : false
         * WechatInfo : {"appId":"b3c327c04d8b0471","headimgurl":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK7cJSic0C6etnnMaQnS8zOgc4P8rcy7sjSV4jnWaGDFvEcyNoaw2gyS7k4rkQ9OlDsBUNebbd3Xjw/132","nickname":"孫建華","openid":"onxAK54a_WgeoGSavJJ32-iqw7bw","unionid":"oaVtW5zyWeYujB5bPpttPiRYwojE","sex":1}
         * TokenInfo : {"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMSIsInBhc3N3ZCI6ImUxMGFkYzM5NDliYTU5YWJiZTU2ZTA1N2YyMGY4ODNlIiwiaWQiOjMxLCJleHAiOjE1NjkzMDE3MTEsImlhdCI6MTU2OTI5NDUxMSwianRpIjoiZWUwZjUyYWEtZTIxNi00YmVkLTkyOWYtZDk4YzNmNmZjNmJhIn0.L5JjKKm-3i6yB0R8q4AGsQF5gwFuh49T3MajUVCb6nI","refreshToken":"NA3aH20L7f6rnjsoK6mdsTi5HMmVONZL","accountId":31,"expired":7200}
         */

        private boolean flag;
        private WechatInfoBean WechatInfo;
        private TokenInfoBean TokenInfo;

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public WechatInfoBean getWechatInfo() {
            return WechatInfo;
        }

        public void setWechatInfo(WechatInfoBean WechatInfo) {
            this.WechatInfo = WechatInfo;
        }

        public TokenInfoBean getTokenInfo() {
            return TokenInfo;
        }

        public void setTokenInfo(TokenInfoBean TokenInfo) {
            this.TokenInfo = TokenInfo;
        }

        public static class WechatInfoBean {
            /**
             * appId : b3c327c04d8b0471
             * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK7cJSic0C6etnnMaQnS8zOgc4P8rcy7sjSV4jnWaGDFvEcyNoaw2gyS7k4rkQ9OlDsBUNebbd3Xjw/132
             * nickname : 孫建華
             * openid : onxAK54a_WgeoGSavJJ32-iqw7bw
             * unionid : oaVtW5zyWeYujB5bPpttPiRYwojE
             * sex : 1
             */

            private String appId;
            private String headimgurl;
            private String nickname;
            private String openid;
            private String unionid;
            private int sex;

            public String getAppId() {
                return appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public String getHeadimgurl() {
                return headimgurl;
            }

            public void setHeadimgurl(String headimgurl) {
                this.headimgurl = headimgurl;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getOpenid() {
                return openid;
            }

            public void setOpenid(String openid) {
                this.openid = openid;
            }

            public String getUnionid() {
                return unionid;
            }

            public void setUnionid(String unionid) {
                this.unionid = unionid;
            }

            public int getSex() {
                return sex;
            }

            public void setSex(int sex) {
                this.sex = sex;
            }
        }

        public static class TokenInfoBean {
            /**
             * token : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMSIsInBhc3N3ZCI6ImUxMGFkYzM5NDliYTU5YWJiZTU2ZTA1N2YyMGY4ODNlIiwiaWQiOjMxLCJleHAiOjE1NjkzMDE3MTEsImlhdCI6MTU2OTI5NDUxMSwianRpIjoiZWUwZjUyYWEtZTIxNi00YmVkLTkyOWYtZDk4YzNmNmZjNmJhIn0.L5JjKKm-3i6yB0R8q4AGsQF5gwFuh49T3MajUVCb6nI
             * refreshToken : NA3aH20L7f6rnjsoK6mdsTi5HMmVONZL
             * accountId : 31
             * expired : 7200
             */

            private String token;
            private String refreshToken;
            private int accountId;
            private int expired;

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public String getRefreshToken() {
                return refreshToken;
            }

            public void setRefreshToken(String refreshToken) {
                this.refreshToken = refreshToken;
            }

            public int getAccountId() {
                return accountId;
            }

            public void setAccountId(int accountId) {
                this.accountId = accountId;
            }

            public int getExpired() {
                return expired;
            }

            public void setExpired(int expired) {
                this.expired = expired;
            }
        }
    }
}
