package com.guider.health.arouter_core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;


import com.guider.health.arouter_annotation.model.RouteMeta;
import com.guider.health.arouter_core.callback.NavigationCallback;
import com.guider.health.arouter_core.exception.NoRouteFoundException;
import com.guider.health.arouter_core.template.IRouteGroup;
import com.guider.health.arouter_core.template.IRouteRoot;
import com.guider.health.arouter_core.template.IService;
import com.guider.health.arouter_core.utils.ClassUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;


/**
 * @author Lance
 * @date 2018/2/22
 */

public class DNRouter {
    private static final String TAG = "DNRouter";
    private static final String ROUTE_ROOT_PAKCAGE = "com.guider.health.dnrouter";
    private static final String SDK_NAME = "DNRouter";
    private static final String SEPARATOR = "$$";
    private static final String SUFFIX_ROOT = "Root";
    private static Application mContext;

    private static DNRouter instance;
    private Handler mHandler;

    private DNRouter() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static DNRouter getInstance() {
        synchronized (DNRouter.class) {
            if (instance == null) {
                instance = new DNRouter();
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param application
     */
    public static void init(Application application) {
        mContext = application;
        try {
            loadInfo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "初始化失败!", e);
        }
    }


    /**
     * 分组表制作
     * A分组-》 路由表
     * B分组-》 路由表
     *
     * @throws InterruptedException
     * @throws IOException
     * @throws PackageManager            .NameNotFoundException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private static void loadInfo() throws InterruptedException, IOException, PackageManager
            .NameNotFoundException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        //1. 获得ROUTE_ROOT_PAKCAGE包下所有的类
        Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
        Log.i("look", "获取 " + ROUTE_ROOT_PAKCAGE + "下所有的类: 数量为: " + routerMap.size());
        for (String className : routerMap) {
            //2. 判断如果这个类是以DNRouter$$Root$$ 开头的, 就把它new出来, 调用它的loadInto方法
            //为了把所有的 组 <-> Group类 都保存在Warehouse.groupsIndex这个Map中
            Log.i("look", "获取到的类中 " + className + " 以 " + ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR +
                    SUFFIX_ROOT + " 开头的类");
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR +
                    SUFFIX_ROOT)) {
                // root中注册的是分组信息 将分组信息加入仓库中
                ((IRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInto
                        (Warehouse.groupsIndex);
            }
        }
        for (Map.Entry<String, Class<? extends IRouteGroup>> stringClassEntry : Warehouse
                .groupsIndex.entrySet()) {
            Log.i("look", "Map中 Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry
                    .getValue() + "]");
        }

    }

    public Postcard build(int idResource, String path, String tag) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(idResource, tag, path, extractGroup(path));
        }
    }

    public Postcard build(int idResource, String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(idResource, path, extractGroup(path));
        }
    }


    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(0, path, extractGroup(path));
        }
    }

    public Postcard build(int id, String tag, String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return new Postcard(id, tag, path, group);
        }
    }


    /**
     * 获得组别
     *
     * @param path
     * @return
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException(path + " : 不能提取group.");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new RuntimeException(path + " : 不能提取group.");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据跳卡跳转页面
     *
     * @param context
     * @param postcard
     * @param requestCode
     * @param callback
     */
    protected Object navigation(Context context, final Postcard postcard, final int requestCode,
                                final NavigationCallback callback) {
        try {
            prepareCard(postcard);
        } catch (NoRouteFoundException e) {
            e.printStackTrace();
            //没找到
            if (null != callback) {
                callback.onLost(postcard);
            }
            return null;
        }
        if (null != callback) {
            callback.onFound(postcard);
        }

        switch (postcard.getType()) {
            case ACTIVITY:
                try {

                    final Context currentContext = null == context ? mContext : context;
                    final Intent intent = new Intent(currentContext, postcard.getDestination());

                    Log.i("look", "要跳的类只是Activity " + postcard.getDestination());
                    intent.putExtras(postcard.getExtras());
                    int flags = postcard.getFlags();
                    if (-1 != flags) {
                        intent.setFlags(flags);
                    } else if (!(currentContext instanceof Activity)) {
                        Log.i("look", "context是Application, 跨应用跳转");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //6. startActivity跳转  可能需要返回码   (ActivityCompat为兼容包)
                            if (requestCode > 0) {
                                ActivityCompat.startActivityForResult((Activity) currentContext, intent,
                                        requestCode, postcard.getOptionsBundle());
                            } else {
                                currentContext.startActivity(intent, postcard
                                        .getOptionsBundle());
//                                ActivityCompat.startActivity(currentContext, intent,
//                                         postcard.getOptionsBundle());
                            }

                            if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) &&
                                    currentContext instanceof Activity) {
                                //老版本
                                ((Activity) currentContext).overridePendingTransition(postcard
                                                .getEnterAnim()
                                        , postcard.getExitAnim());
                            }
                            //跳转完成
                            if (null != callback) {
                                callback.onArrival(postcard);
                            }

                            Log.i("look", "跳转Activity成功");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ISERVICE:
                return postcard.getService();

            case FRAGMENT:
                Log.i("look", "要跳到Fragment");

                final Context currentContext = null == context ? mContext : context;

                Fragment fragment = null;
                try {
                    fragment = (Fragment) postcard.getDestination().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

//                .setCustomAnimations(
//                    R.animator.fragment_slide_right_in, R.animator.fragment_slide_left_out,
//                    R.animator.fragment_slide_left_in, R.animator.fragment_slide_right_out)


                ((FragmentActivity) currentContext).getSupportFragmentManager().beginTransaction()

                        .add(postcard.getIdR(), fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName()).commit();


                break;
            default:
                break;
        }
        return null;
    }

    /**
     * 准备卡片
     *
     * @param card
     */
    private void prepareCard(Postcard card) {
        RouteMeta routeMeta = Warehouse.routes.get(card.getPath());
        Log.i("look", "根据路径 " + card.getPath() + " 找到了 " + routeMeta);

        //还没准备的
        if (null == routeMeta) {
            //1. 通过card.getGroup()组找到 IRouteGroup类
            //创建并调用 loadInto 函数,然后记录在仓库
            Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(card
                    .getGroup());
            Log.i("look", "根据组 " + card
                    .getGroup() + " 找类, 找到 " + groupMeta);
            if (null == groupMeta) {
                throw new NoRouteFoundException("没找到对应路由: " + card.getGroup() + " " +
                        card.getPath());
            }
            IRouteGroup iGroupInstance;
            try {
                //2. 创建找到的类
                iGroupInstance = groupMeta.getConstructor().newInstance();
                Log.i("look", "创建 " + iGroupInstance + " 成功");
            } catch (Exception e) {
                throw new RuntimeException("路由分组映射表记录失败.", e);
            }
            //3. 调用loadInto方法, 这样Warehouse.routes中就保存了所有 路径<->javaBean
            iGroupInstance.loadInto(Warehouse.routes);

            for (Map.Entry<String, RouteMeta> stringClassEntry : Warehouse
                    .routes.entrySet()) {
                Log.i("look", "Map中 Group映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry
                        .getValue() + "]");
            }
            //已经准备过了就可以移除了 (不会一直存在内存中)
            Warehouse.groupsIndex.remove(card.getGroup());
            //4. 再次进入 prepareCard 中, 因为这时候Warehouse.routes中有值了, 所以会进入else流程
            prepareCard(card);
        } else {
            //5. 把要跳转的类放到card中  (如果是跳到Activity, 第6步在上面)
            card.setDestination(routeMeta.getDestination());
            card.setType(routeMeta.getType());
            switch (routeMeta.getType()) {
                case ISERVICE:
                    Log.i("look", "要跳的类是service ");
                    Class<?> destination = routeMeta.getDestination();
                    IService service = Warehouse.services.get(destination);
                    if (null == service) {
                        try {
                            service = (IService) destination.getConstructor().newInstance();
                            Warehouse.services.put(destination, service);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    card.setService(service);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 注入
     *
     * @param instance
     */
    public void inject(Activity instance) {
        ExtraManager.getInstance().loadExtras(instance);
    }


}
