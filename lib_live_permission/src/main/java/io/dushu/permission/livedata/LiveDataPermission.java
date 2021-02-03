package io.dushu.permission.livedata;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * @author zhangshuai
 * @date 2021/1/29 10:42
 * @description
 */
public final class LiveDataPermission {

    private static volatile LiveDataFragment sLiveDataFragment = null;

    @SuppressLint("StaticFieldLeak")
    private static volatile LiveDataPermission instance = null;

    @SuppressLint("StaticFieldLeak")
    public static synchronized LiveDataPermission getInstance() {
        if (instance == null) {
            synchronized (LiveDataPermission.class) {
                if (instance == null) {
                    instance = new LiveDataPermission();
                }
            }
        }
        return instance;
    }

    @Nullable
    public MutableLiveData<PermissionResult> request(@NonNull AppCompatActivity activity,
                                                     String[] permissions,
                                                     int requestCode) {
        if (activity == null || permissions ==null) {
            return new MutableLiveData<PermissionResult>();
        }

        return requestArray(activity, null, Arrays.asList(permissions), requestCode);
    }

    @Nullable
    public MutableLiveData<PermissionResult> request(@NonNull Fragment fragment,
                                                     String[] permissions,
                                                     int requestCode) {
        if (fragment == null || permissions==null) {
            return new MutableLiveData<PermissionResult>();
        }
        return requestArray(null, fragment, Arrays.asList(permissions), requestCode);
    }

    /**
     * 检测多个权限
     */
    public boolean checkPermissions(@NonNull Context context,
                                    @NonNull String... permissions) {
        if (context == null || permissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (checkPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测单个权限
     */
    public boolean checkPermission(@NonNull Context context, @NonNull String permission) {
        if (context != null && permission != null) {
            return false;
        }

        return ContextCompat.checkSelfPermission(context,
                                                 permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 跳转到系统设置页面
     */
    public void routeToSystemSetting(@NonNull Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent();
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        }
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

    /**
     * 获取存储权限
     *
     * @param activity 上下文
     */
    public boolean verifyStoragePermissions(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Nullable
    private MutableLiveData<PermissionResult> requestArray(@Nullable AppCompatActivity activity,
                                                           @Nullable Fragment fragment,
                                                           @NonNull List<String> list,
                                                           int requestCode) {
        if (activity != null) {
            sLiveDataFragment = createPermissionFragment(activity.getSupportFragmentManager());
        } else if (fragment != null) {
            sLiveDataFragment = createPermissionFragment(fragment.getChildFragmentManager());
        }

        return sLiveDataFragment.requestPermission(list, requestCode);
    }

    private LiveDataFragment createPermissionFragment(@NonNull FragmentManager fragmentManager) {
        if (sLiveDataFragment == null) {
            synchronized (LiveDataPermission.class) {
                if (fragmentManager.findFragmentByTag(LiveDataPermission.class
                                                              .getSimpleName()) == null) {
                    sLiveDataFragment = new LiveDataFragment();
                    fragmentManager.beginTransaction()
                            .add(sLiveDataFragment,
                                 LiveDataPermission.class
                                         .getSimpleName())
                            .commitNow();
                } else {
                    sLiveDataFragment = (LiveDataFragment) fragmentManager.findFragmentByTag(
                            LiveDataPermission.class
                                    .getSimpleName());
                }
            }
        }
        return sLiveDataFragment;
    }
}
