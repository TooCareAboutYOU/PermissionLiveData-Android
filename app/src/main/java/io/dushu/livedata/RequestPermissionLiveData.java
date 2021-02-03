package io.dushu.livedata;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import io.dushu.permission.livedata.BuildConfig;

/**
 * @author zhangshuai
 * @date 2021/1/28 13:57
 * @description 请求权限
 */

public class RequestPermissionLiveData extends LiveData<String> implements Observer<String> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AppCompatActivity mActivity;
    private Context context;
    public static String DEFAULT_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static int REQUEST_PERMISSION_CODE = 100;

    @SuppressLint("StaticFieldLeak")
    private static volatile RequestPermissionLiveData instance = null;

    @SuppressLint("StaticFieldLeak")
    public static synchronized RequestPermissionLiveData getInstance(
            @NonNull AppCompatActivity activity) {
        if (instance == null) {
            synchronized (RequestPermissionLiveData.class) {
                if (instance == null) {
                    instance = new RequestPermissionLiveData(activity);
                }
            }
        }
        return instance;
    }

    private RequestPermissionLiveData(@NonNull AppCompatActivity activity) {
        printLog("RequestPermissionLiveData初始化");
        this.mActivity = activity;
        observe(this.mActivity, this);
    }

    @Override
    protected void onActive() {
        printLog("LiveData活跃中");
    }

    @Override
    protected void onInactive() {
        printLog("LiveData非活跃中");
    }

    /**
     * 动态请求单个权限
     */
    public void setPermission(String permission) {
        this.setValue(permission);
    }

    @Override
    public void onChanged(final String sm) {
        printLog("RequestPermissionLiveData数据发生改变：" + sm);
        requestPermission(sm);
    }

    private void requestPermission(String permission) {
        if (this.mActivity == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.mActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                printLog("权限请求过了");
//                this.mActivity.startActivity(new Intent(this.mActivity, Main2Activity.class));
                this.mActivity.startActivity(new Intent(this.mActivity, PermissionDialogActivity.class));
            } else {
                if (this.mActivity.shouldShowRequestPermissionRationale(permission)) {
                    this.mActivity.requestPermissions(new String[]{permission},
                                                      REQUEST_PERMISSION_CODE);
                    printLog("权限请求中-1");
                } else {
                    this.mActivity.requestPermissions(new String[]{permission},
                                                      REQUEST_PERMISSION_CODE);
                    printLog("权限请求中-2");
                }
            }
        }
    }

    private void printLog(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "printLog: " + msg);
        }
    }
}
