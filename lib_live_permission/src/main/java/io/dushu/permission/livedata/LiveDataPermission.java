package io.dushu.permission.livedata;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;

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
        return requestArray(activity, null, Arrays.asList(permissions), requestCode);
    }

    @Nullable
    public MutableLiveData<PermissionResult> request(@NonNull Fragment fragment,
                                                     String[] permissions,
                                                     int requestCode) {
        return requestArray(null, fragment, Arrays.asList(permissions), requestCode);
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
            Log.i("LiveDataPermission", "创建 碎片");
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
