package com.lljjcoder.citywheel;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lljjcoder.CityLibConstant;
import com.lljjcoder.bean.CityBeanNew;
import com.lljjcoder.bean.DistrictBeanNew;
import com.lljjcoder.bean.ProvinceBeanNew;
import com.lljjcoder.utils.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 省市区解析辅助类
 * 作者：liji on 2017/11/4 10:09
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public class CityParseHelperNew {

    /**
     * 省份数据
     */
    private ArrayList<ProvinceBeanNew> mProvinceBeanArrayList = new ArrayList<>();

    /**
     * 城市数据
     */
    private ArrayList<ArrayList<CityBeanNew>> mCityBeanArrayList;

    /**
     * 地区数据
     */
    private ArrayList<ArrayList<ArrayList<DistrictBeanNew>>> mDistrictBeanArrayList;

    private List<ProvinceBeanNew> mProvinceBeenArray;

    private ProvinceBeanNew mProvinceBean;

    private CityBeanNew mCityBean;

    private DistrictBeanNew mDistrictBean;

    //    private CityConfig config;

    /**
     * key - 省 value - 市
     */
    private Map<String, List<CityBeanNew>> mPro_CityMap = new HashMap<String, List<CityBeanNew>>();

    /**
     * key - 市 values - 区
     */
    private Map<String, List<DistrictBeanNew>> mCity_DisMap = new HashMap<String, List<DistrictBeanNew>>();

    /**
     * key - 区 values - 邮编
     */
    private Map<String, DistrictBeanNew> mDisMap = new HashMap<String, DistrictBeanNew>();

    public ArrayList<ProvinceBeanNew> getProvinceBeanArrayList() {
        return mProvinceBeanArrayList;
    }

    public void setProvinceBeanArrayList(ArrayList<ProvinceBeanNew> provinceBeanArrayList) {
        mProvinceBeanArrayList = provinceBeanArrayList;
    }

    public ArrayList<ArrayList<CityBeanNew>> getCityBeanArrayList() {
        return mCityBeanArrayList;
    }

    public void setCityBeanArrayList(ArrayList<ArrayList<CityBeanNew>> cityBeanArrayList) {
        mCityBeanArrayList = cityBeanArrayList;
    }

    public ArrayList<ArrayList<ArrayList<DistrictBeanNew>>> getDistrictBeanArrayList() {
        return mDistrictBeanArrayList;
    }

    public void setDistrictBeanArrayList(ArrayList<ArrayList<ArrayList<DistrictBeanNew>>> districtBeanArrayList) {
        mDistrictBeanArrayList = districtBeanArrayList;
    }

    public List<ProvinceBeanNew> getProvinceBeenArray() {
        return mProvinceBeenArray;
    }

    public void setProvinceBeenArray(List<ProvinceBeanNew> provinceBeenArray) {
        mProvinceBeenArray = provinceBeenArray;
    }

    public ProvinceBeanNew getProvinceBean() {
        return mProvinceBean;
    }

    public void setProvinceBean(ProvinceBeanNew provinceBean) {
        mProvinceBean = provinceBean;
    }

    public CityBeanNew getCityBean() {
        return mCityBean;
    }

    public void setCityBean(CityBeanNew cityBean) {
        mCityBean = cityBean;
    }

    public DistrictBeanNew getDistrictBean() {
        return mDistrictBean;
    }

    public void setDistrictBean(DistrictBeanNew districtBean) {
        mDistrictBean = districtBean;
    }

    public Map<String, List<CityBeanNew>> getPro_CityMap() {
        return mPro_CityMap;
    }

    public void setPro_CityMap(Map<String, List<CityBeanNew>> pro_CityMap) {
        mPro_CityMap = pro_CityMap;
    }

    public Map<String, List<DistrictBeanNew>> getCity_DisMap() {
        return mCity_DisMap;
    }

    public void setCity_DisMap(Map<String, List<DistrictBeanNew>> city_DisMap) {
        mCity_DisMap = city_DisMap;
    }

    public Map<String, DistrictBeanNew> getDisMap() {
        return mDisMap;
    }

    public void setDisMap(Map<String, DistrictBeanNew> disMap) {
        mDisMap = disMap;
    }

    public CityParseHelperNew() {

    }

    /**
     * 初始化数据，解析json数据
     */
    public void initData(Context context) {

        String cityJson = utils.getJson(context, CityLibConstant.BD_ADDRESS_DATA);
        Type type = new TypeToken<ArrayList<ProvinceBeanNew>>() {
        }.getType();

        mProvinceBeanArrayList = new Gson().fromJson(cityJson, type);

        if (mProvinceBeanArrayList == null || mProvinceBeanArrayList.isEmpty()) {
            return;
        }

        mCityBeanArrayList = new ArrayList<>(mProvinceBeanArrayList.size());
        mDistrictBeanArrayList = new ArrayList<>(mProvinceBeanArrayList.size());

        //*/ 初始化默认选中的省、市、区，默认选中第一个省份的第一个市区中的第一个区县
        if (mProvinceBeanArrayList != null && !mProvinceBeanArrayList.isEmpty()) {
            mProvinceBean = mProvinceBeanArrayList.get(0);
            List<CityBeanNew> cityList = mProvinceBean.getSub();
            if (cityList != null && !cityList.isEmpty() && cityList.size() > 0) {
                mCityBean = cityList.get(0);
                List<DistrictBeanNew> districtList = mCityBean.getSub();
                if (districtList != null && !districtList.isEmpty() && districtList.size() > 0) {
                    mDistrictBean = districtList.get(0);
                }
            }
        }

        //省份数据
        mProvinceBeenArray = new ArrayList<ProvinceBeanNew>();

        for (int p = 0; p < mProvinceBeanArrayList.size(); p++) {

            //遍历每个省份
            ProvinceBeanNew itemProvince = mProvinceBeanArrayList.get(p);

            //每个省份对应下面的市
            ArrayList<CityBeanNew> cityList = itemProvince.getSub();

            //当前省份下面的所有城市

//            List<CityBean> cityNames = new ArrayList<>();

            //遍历当前省份下面城市的所有数据
            for (int j = 0; j < cityList.size(); j++) {
//                cityNames[j] = cityList.get(j);

                //当前省份下面每个城市下面再次对应的区或者县
                List<DistrictBeanNew> districtList = cityList.get(j).getSub();
                if (districtList == null) {
                    break;
                }
//                DistrictBean[] distrinctArray = new DistrictBean[districtList.size()];

                for (int k = 0; k < districtList.size(); k++) {

                    // 遍历市下面所有区/县的数据
                    DistrictBeanNew districtModel = districtList.get(k);

                    //存放 省市区-区 数据
                    mDisMap.put(itemProvince.getArea_name() + cityList.get(j).getArea_name() + districtList.get(k).getArea_name(),
                            districtModel);

//                    districtList.add(districtModel);

                }
                // 市-区/县的数据，保存到mDistrictDatasMap
                mCity_DisMap.put(itemProvince.getArea_name() + cityList.get(j).getArea_name(), districtList);

            }

            // 省-市的数据，保存到mCitisDatasMap
            mPro_CityMap.put(itemProvince.getArea_name(), cityList);

            mCityBeanArrayList.add(cityList);

            //只有显示三级联动，才会执行
            ArrayList<ArrayList<DistrictBeanNew>> array2DistrictLists = new ArrayList<>(cityList.size());

            for (int c = 0; c < cityList.size(); c++) {
                CityBeanNew cityBean = cityList.get(c);
                array2DistrictLists.add(cityBean.getSub());
            }
            mDistrictBeanArrayList.add(array2DistrictLists);

            //            }
            mProvinceBeenArray.add(p, itemProvince);
            //赋值所有省份的名称
//            mProvinceBeenArray[p] = ;

        }

    }

}
