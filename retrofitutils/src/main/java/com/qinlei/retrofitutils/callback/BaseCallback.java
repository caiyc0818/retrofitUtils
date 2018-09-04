package com.qinlei.retrofitutils.callback;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by cyc
 * Created on 2018/1/16
 * Created description :
 */

public abstract class BaseCallback<T> {

    public void onBefore(Call call) {

    }

    public void onAfter(Call call) {

    }

    public void inProgress(float progress, long total) {

    }

    public boolean validateReponse(Response response) {
        return response.isSuccessful();
    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response) throws Throwable;

    public abstract void onError(Call call, Throwable e,int id);

    public abstract void onResponse(Call call, T response,int id);

    public static BaseCallback CALLBACK_DEFAULT = new BaseCallback() {
        @Override
        public Object parseNetworkResponse(Response response) {
            return null;
        }

        @Override
        public void onError(Call call, Throwable e,int id) {

        }

        @Override
        public void onResponse(Call call, Object response,int id) {

        }
    };

}
