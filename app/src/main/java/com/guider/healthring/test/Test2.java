package com.guider.healthring.test;


import com.google.gson.Gson;
import com.guider.healthring.bean.GuiderUserInfo;

/**
 * Created by Admin
 * Date 2019/9/19
 */
public class Test2 {

    public static void main(String[] arg){

//        String finalPhotoName =
//                ( "NewBluetoothStrap/headImg"+"_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(System.currentTimeMillis())))
//                        + ".jpg";
//        System.out.print("------fff="+Environment.getExternalStorageDirectory()+"/"+finalPhotoName+"\n");


//        String loginUrl = "http://apihd.guiderhealth.com/api/v1/login/onlyphone"+"?"+"phone="+"15916947377" ;
//        Map<String,String> map = new HashMap<>();
//        map.put("phone","15916947377");
//        System.out.print("---------url="+loginUrl+"\n");
//        OkHttpTool.getInstance().doRequest(loginUrl, "", "1", new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//                System.out.print("--------result="+result+"\n");
//            }
//        });


//        String guiderImgUrl = "http://api.guiderhealth.com/upload/file";
//        OkHttpTool.getInstance().doRequestUploadFile(guiderImgUrl, "guider_head", path, "11", new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//               System.out.print("--------result="+result);
//            }
//        });
        /**
         * accountId : 1
         * addr : string
         * birthday : 1988-12-02T00:00:00Z
         * cardId : string
         * gender : MAN
         * headUrl : string
         * height : 0
         * name : string
         * phone : string
         * userState : ACTIVE
         * weight : 0
         */
        GuiderUserInfo guiderUserInfo = new GuiderUserInfo();
        guiderUserInfo.setAccountId(1);
        guiderUserInfo.setAddr("");
        guiderUserInfo.setCardId("");
        guiderUserInfo.setName("name");
        System.out.print("---------用户+="+new Gson().toJson(guiderUserInfo)+"\n"+guiderUserInfo.getAddr());


    }



}
