package com.guider.health.common.net.net;




import com.guider.health.common.net.net.callback.Bean;
import com.guider.health.common.net.net.callback.IError;
import com.guider.health.common.net.net.callback.IFailure;
import com.guider.health.common.net.net.callback.IRequest;
import com.guider.health.common.net.net.callback.ISuccess;
import com.guider.health.common.net.net.callback.RequestCallbacks;
import com.guider.health.common.net.net.download.DownloadHandler;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by jett on 2018/6/6.
 */
public class RestClient {
    private final HashMap<String,Object> PARAMS;
    private final String URL;
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;
    private final RequestBody BODY;
    //上传下载
    private final File FILE;

    private final String DOWNLOAD_DIR;
    private final String EXTENSION;
    private final String FILENAME;

    public RestClient(HashMap<String, Object> params,
                      String url,
                      IRequest request,
                      ISuccess success,
                      IFailure failure,
                      IError error,
                      RequestBody body,
                        File file,String downloadDir,String extension,String filename) {
        this.PARAMS = params;
        this.URL = url;
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.BODY = body;

        //上传
        this.FILE=file;

        this.DOWNLOAD_DIR=downloadDir;  ///sdcard/XXXX.ext
        //文件扩展名   downloadDir+时间作为名字.extension
        this.EXTENSION=extension;
        //如果用filename就不用上面两个了
        this.FILENAME=filename;
    }

    /**
     *
     * @return
     *
     * RestClient.create().url(url).request(IRequest).build().post()
     *
     * 1. RestClient.create() 得到builder, 后面每一个调用都返回builder, 直到build()
     * 2. build()返回RestClient对象,  到此构造部分完事
     * 3. post()接下来就是完成下面一段注释的代码
     */
    public static RestClientBuilder create(){
        return new RestClientBuilder();
    }


    /**
     *
     Retrofit retrofit=new Retrofit.Builder()
     .baseUrl("http://v.juhe.cn/weather/")
     .addConverterFactory(ScalarsConverterFactory.create())
     .build();
     //和接口关联起来
     ApiService service=retrofit.create(ApiService.class);

     HashMap<String,Object> params=new HashMap<>();
     params.put("cityname","长沙");
     params.put("key","fd0f609b22905a0a56a48d7cf59a558b");

     service.sendRequest(params).enqueue(new Callback<String>() {
    @Override
    public void onResponse(Call<String> call, Response<String> response) {
    Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
    });


     ScalarsConverterFactory是转换成字符串
     com.squareup.retrofit2:converter-scalars:2.4.0

     *
     */




    //开始实现真实的网络操作
    private void request(HttpMethod method){
        final RestService service=RestCreator.getRestService();
        Call<ResponseBody> call=null;
        if(REQUEST!=null){
            REQUEST.onRequestStart();
        }


        switch(method){
            case GET:
                call=service.get(URL,PARAMS);
                break;
            case POST:
                call=service.post(URL,PARAMS);
                break;
            case PUT:
                call=service.put(URL,PARAMS);
                break;
            case DELETE:
                call=service.delete(URL,PARAMS);
                break;
            case UPLOAD:
                //文件上传  用的okhttp的api
                final RequestBody requestBody = RequestBody.create(MultipartBody.FORM,FILE);
                final MultipartBody.Part body=MultipartBody.Part.createFormData(
                        "file",FILE.getName(),requestBody);
                call=service.upload(URL,body);
                break;
            default:
                break;
        }
        if(call!=null){
            //执行异步请求  传递一个callback
            call.enqueue(getReqeustCallback());
        }
    }

    private Callback getReqeustCallback(){
        return new RequestCallbacks(REQUEST,SUCCESS,FAILURE,ERROR);
    }

    //各种请求
    public final void get(){
        request(HttpMethod.GET);
    }
    public final void post(){
        request(HttpMethod.POST);
    }
    public final void put(){
        request(HttpMethod.PUT);
    }
    public final void delete(){
        request(HttpMethod.DELETE);
    }
    public final void upload(){
        request(HttpMethod.UPLOAD);
    }
    public final void download(){
        new DownloadHandler(PARAMS,URL,REQUEST,
                    SUCCESS,FAILURE,ERROR,DOWNLOAD_DIR,
                    EXTENSION,FILENAME)
                .handleDownload();
    }

}







