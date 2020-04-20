package com.guider.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseResultViewHolder<T> {
    protected Context mContext;
    View view;
    int requestStatus = REQUEST_STATUS_NEVER;
    public static int REQUEST_STATUS_OK = 1;
    public static int REQUEST_STATUS_NEVER = 0;

    int code = 0;
    int request = 0;

    public BaseResultViewHolder(ViewGroup viewGroup) {
        mContext = viewGroup.getContext();
        this.view = LayoutInflater.from(viewGroup.getContext()).inflate(getLayout(), viewGroup, false);
    }

    void callback(int resultCode, RequestCallback callback) {
        request++;
        if (resultCode == 0) {
            code++;
        }
        if (request == getRequestNum()) {
            if (code == getRequestNum()) {
                requestStatus = REQUEST_STATUS_OK;
                callback.onRequestFinish(getName(), "", RequestCallback.CODE_OK);
            } else {
                requestStatus = REQUEST_STATUS_NEVER;
                callback.onRequestFinish(getName(), "", RequestCallback.CODE_FAIL);
            }
        }
    }

    protected abstract int getRequestNum();

    protected abstract int getLayout();

    protected abstract String getName();

    abstract void setResult(T result);

    public abstract void request(RequestCallback callback);

    public abstract boolean hasData();

    protected Context getContext() {
        return mContext;
    }
}
