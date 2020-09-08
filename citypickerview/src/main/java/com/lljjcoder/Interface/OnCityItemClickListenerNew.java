package com.lljjcoder.Interface;

import com.lljjcoder.bean.CityBeanNew;
import com.lljjcoder.bean.DistrictBeanNew;
import com.lljjcoder.bean.ProvinceBeanNew;

/**
 * 作者：liji on 2017/11/16 10:06
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public abstract class OnCityItemClickListenerNew {
    
    /**
     * 当选择省市区三级选择器时，需要覆盖此方法
     * @param province
     * @param city
     * @param district
     */
    public void onSelected(ProvinceBeanNew province, CityBeanNew city, DistrictBeanNew district) {
        
    }
    
    /**
     * 取消
     */
    public void onCancel() {
        
    }
}
