package io.dushu.permission.livedata;

import android.annotation.SuppressLint;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

/**
 * @author zhangshuai
 * @date 2021/1/29 10:42
 * @description
 */
public class LiveDataPermission {

    private static final String TAG = "LiveDataPermission";

    private static volatile LiveDataFragment sLiveDataFragment = null;

    private LiveDataPermission(AppCompatActivity activity) {
        sLiveDataFragment = getInstance(activity.getSupportFragmentManager());
    }

    private LiveDataPermission(Fragment fragment) {
        sLiveDataFragment = getInstance(fragment.getChildFragmentManager());
    }


    @SuppressLint("StaticFieldLeak")
    private static volatile LiveDataPermission instance=null;
    @SuppressLint("StaticFieldLeak")
    public static synchronized LiveDataPermission getInstance(AppCompatActivity activity){
        if(instance==null){
            synchronized(LiveDataPermission.class){
                if(instance==null){
                    instance=new LiveDataPermission(activity);
                }
            }
        }
        return instance;
    }

    public static synchronized LiveDataPermission getInstance(Fragment fragment){
        if(instance==null){
            synchronized(LiveDataPermission.class){
                if(instance==null){
                    instance=new LiveDataPermission(fragment);
                }
            }
        }
        return instance;
    }


    private LiveDataFragment getInstance(FragmentManager fragmentManager) {
        if (sLiveDataFragment == null) {
            synchronized (this) {
                if (fragmentManager.findFragmentByTag(TAG) == null) {
                    sLiveDataFragment = new LiveDataFragment();
                    fragmentManager.beginTransaction()
                            .add(sLiveDataFragment, TAG)
                            .commitNow();
                } else {
                    sLiveDataFragment = (LiveDataFragment) fragmentManager.findFragmentByTag(TAG);
                }
            }
        }
        return sLiveDataFragment;
    }

    public MutableLiveData<PermissionResult> request(String... permissions) {
        return this.requestArray(Arrays.asList(permissions));
    }

    public MutableLiveData<PermissionResult> requestArray(@NonNull List<String> list) {
        try {
            sLiveDataFragment.requestPermission(list);
            return sLiveDataFragment.getLiveData();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

}
