package io.dushu.livedata.app;

import android.app.Application;

/**
 * @author zhangshuai
 * @date 2021/1/28 14:46
 * @description
 */
public class LiveDataApplication extends Application {

    private static LiveDataApplication applications;

    @Override
    public void onCreate() {
        super.onCreate();
        applications=this;

    }
}
