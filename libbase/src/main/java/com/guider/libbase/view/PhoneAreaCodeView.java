package com.guider.libbase.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IRingApi;
import com.guider.health.apilib.model.AreaCode;
import com.guider.libbase.R;
import com.guider.libbase.adapter.PhoneAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * 国家手机号的区号
 * Created by Admin
 * Date 2019/6/28
 */
public class PhoneAreaCodeView extends AlertDialog implements AdapterView.OnItemClickListener {

    private static final String TAG = "PhoneAreaCodeView";

    private ListView listView;
    private Context mContext;

    private List<AreaCode> list;
    private PhoneAdapter phoneAdapter;

    public PhoneAreaClickListener phoneAreaClickListener;

    public void setPhoneAreaClickListener(PhoneAreaClickListener phoneAreaClickListener) {
        this.phoneAreaClickListener = phoneAreaClickListener;
    }

    public PhoneAreaCodeView(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_area_code_layout);
        initViews();
        getAreaData();
    }

    private void getAreaData() {
        IRingApi iRingApi = ApiUtil.createRingApi(IRingApi.class);
        iRingApi.verifyThirdAccount().enqueue(new ApiCallBack<List<AreaCode>>() {
            @Override
            public void onApiResponse(Call<List<AreaCode>> call, Response<List<AreaCode>> response) {
                List<AreaCode> areaCodes = response.body();
                list.addAll(areaCodes);
                phoneAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initViews() {
        listView = findViewById(R.id.phoneAreaCodeListView);
        list = new ArrayList<>();
        phoneAdapter = new PhoneAdapter(list, mContext);
        listView.setAdapter(phoneAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (phoneAreaClickListener != null) {
            phoneAreaClickListener.chooseAreaCode(list.get(position));
        }
    }

    public interface PhoneAreaClickListener {
        void chooseAreaCode(AreaCode areCodeBean);
    }
}
