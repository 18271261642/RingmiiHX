package com.guider.health.apilib;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ResultCallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if(returnType == Result.class)
            return new TCallAdapter();
        return null;
    }

    class TCallAdapter <T> implements CallAdapter<Result<T>, T>{
        @Override
        public Type responseType() {
            return getClass(1);
        }

        @Override
        public T adapt(Call<Result<T>> call) {
            try {
                Result<T> result = call.execute().body();
                if (result.getCode().getId() < 0)
                    throw new RuntimeException(result.getMsg());
                return result.getData();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("未知错误");
            }
        }

        protected Class getClass(int index) {
            Type genType = getClass().getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                return Object.class;
            }
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index >= params.length || index < 0) {
                return Object.class;
            }

            if (!(params[index] instanceof Class)) {
                return Object.class;
            }

            return (Class) params[index];
        }
    }
}
