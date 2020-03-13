package com.guider.health.apilib.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TokenInfo implements Parcelable {

    /**
     * token : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1MzciLCJwYXNzd2QiOiJlMTBhZGMzOTQ5YmE1OWFiYmU1NmUwNTdmMjBmODgzZSIsImlkIjo1MzcsImV4cCI6MTU2NTUzMTcwNywiaWF0IjoxNTY1NTI0NTA3LCJqdGkiOiJmODI0MTRmYi1kYzczLTQ5MWUtOTE2OS04ZDA2ZDliM2EyMjcifQ.xUOAn8-wHqes8dwQzm992xcpH9v37_BPdm3w3CJL8xM
     * refreshToken : AS0YjOekBcPvlKPq4ou6zJ1j8XnEagT8
     * accountId : 537
     * expired : 7200
     */

    private String token;
    private String refreshToken;
    private long accountId;
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

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public int getExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.refreshToken);
        dest.writeLong(this.accountId);
        dest.writeInt(this.expired);
    }

    public TokenInfo() {
    }

    protected TokenInfo(Parcel in) {
        this.token = in.readString();
        this.refreshToken = in.readString();
        this.accountId = in.readInt();
        this.expired = in.readInt();
    }

    public static final Parcelable.Creator<TokenInfo> CREATOR = new Parcelable.Creator<TokenInfo>() {
        @Override
        public TokenInfo createFromParcel(Parcel source) {
            return new TokenInfo(source);
        }

        @Override
        public TokenInfo[] newArray(int size) {
            return new TokenInfo[size];
        }
    };
}
