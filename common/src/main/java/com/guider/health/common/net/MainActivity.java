package com.guider.health.common.net;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import java.util.HashMap;



/**
 *
 *
 * RestService: 里面都是get , post等方法
 * RestCreator: 创建了Retrofit对象, 配置了默认转换器和 调用.client中绑定了一个OkHttp用来扩展配置
 *              还调用.create绑定了RestService类中的接口方法
 * RestClient:  是提供给别人去使用的,  RestClient.create()就会创建它的builder对象, 然后可以调用get, post方法
 * RestClientBuilder: 就是RestClient的builder对象, 用来链式调用, 在每个方法中返回this,所有配置先保存到RestClientBuilder中,
 *                    最后调用build()会创建RestClient对象, 并且把配置传递过去
 * HttpMethod: 枚举, GET, POST等
 * callback包下: IError:是连接服务器后,返回的错误信息  IFailure: 是没有连接到服务器, 直接异常
 *              IRequest: 有两个方法, 一个是请求前, 一个是请求后, 用来判断下载有没有完成
 *              ISuccess: 请求成功  RequestCallbacks: 把IError,IFailure等关联到Retrofit的Callback上
 */

public class MainActivity extends AppCompatActivity {
    HashMap params=new HashMap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

    }

    public void click(View view){
        String url="/login/info";
        params.put("username","jett");
        params.put("password","123");
//        RxRestClient.create()
//                .url(url)
//                .params(params)
//                .build()
//                .get()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull String s) {
//                        //响应结果
//                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

//        params.put("username","jett");
//        params.put("password","123");
//        RestClient.create()
//                .url("/login/info")
//                .params(params)
//                .success(new ISuccess() {
//                    @Override
//                    public void onSuccess(String responce) {
//                        Toast.makeText(MainActivity.this, responce.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }).build()
//                .get();
        //上传  //http://dengpaoedu.com:8080/fileuploadanddownload/uploadServlet.
                //http://192.168.100.41:80/upload
//        params.put("filename","abc.txt");
//        RestClient.create()
//                .params(params)
//                .url("fileuploadanddownload/uploadServlet")
//                .file("/sdcard/test.txt")
//                .success(new ISuccess() {
//                    @Override
//                    public void onSuccess(String responce) {
//                        Toast.makeText(MainActivity.this, responce, Toast.LENGTH_SHORT).show();
//                    }
//                }).failure(new IFailure() {
//            @Override
//            public void onFailure() {
//                Toast.makeText(MainActivity.this, "N", Toast.LENGTH_SHORT).show();
//            }
//        }).error(new IError() {
//            @Override
//            public void onError(int code, String msg) {
//                Toast.makeText(MainActivity.this, "E", Toast.LENGTH_SHORT).show();
//            }
//        })
//                .build().upload();
        //测试下载  http://dengpaoedu.com:8080/examples/test.zip
//        RestClient.create()
//                .params(params)
//                .url("/examples/test.zip")
//                .dir("/sdcard")
//                .extension("zip")
//                .success(new ISuccess() {
//                    @Override
//                    public void onSuccess(String responce) {
//                        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
//                    }
//                }).build().download();


    }
}
