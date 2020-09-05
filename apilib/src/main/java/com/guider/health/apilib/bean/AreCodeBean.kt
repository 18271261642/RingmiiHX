package com.guider.health.apilib.bean

/**
 * 注册时手机验证码区号
 * Created by Admin
 * Date 2019/6/28
 * @param phoneAreCode 国家英文代码
 * @param phoneCountry 国家名称
 * @param phoneAreCode 国际域名缩写
 * @param phoneCode 区号
 * Countries and Regions 	国家或地区 	国际域名缩写 	电话代码 	时差
 * Angola 	安哥拉 	AO 	244
 */
data class AreCodeBean(var phoneRegious: String?, var phoneCountry: String?,
                       var phoneAreCode: String?, var phoneCode: String?)