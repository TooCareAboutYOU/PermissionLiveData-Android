package io.dushu.permission.livedata;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

/**
 * @author zhangshuai
 * @date 2021/1/29 10:44
 * @description 权限碎片
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class LiveDataFragment extends Fragment {

    private MutableLiveData<PermissionResult> mLiveData;

    private int PERMISSIONS_REQUEST_CODE;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (BuildConfig.DEBUG) {
            Log.e("LiveDataPermission", "LiveDataFragment onCreate: 碎片创建成功");
        }

    }

    public MutableLiveData<PermissionResult> requestPermission(@NonNull List<String> permissions,
                                  int requestCode) {

        this.mLiveData = makeLiveData();

        PERMISSIONS_REQUEST_CODE = requestCode;
        final List<String> tempPermission = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            permissions.forEach(new Consumer<String>() {
                @Override
                public void accept(final String ps) {
                    if (getActivity() != null) {
                        if (getActivity().checkSelfPermission(ps) != PackageManager.PERMISSION_GRANTED) {
                            tempPermission.add(ps);

                        }
                    }
                }
            });
            if (tempPermission.isEmpty()) {
                if (BuildConfig.DEBUG) {
                    Log.e("LiveDataPermission", "LiveDataFragment requestPermission: GRANT 全部通过");
                }
                this.mLiveData.setValue(new PermissionResult(PermissionResult.GRANT));
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e("LiveDataPermission", "LiveDataFragment requestPermission: 请求");
                }
                requestPermissions(tempPermission.toArray(new String[tempPermission.size()]),
                                   PERMISSIONS_REQUEST_CODE);
            }
        }
        return this.mLiveData;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            List<String> denyPermissions = new ArrayList<>();
            List<String> rationalePermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(permissions[i])) {
                        rationalePermissions.add(permissions[i]);
                    } else {
                        denyPermissions.add(permissions[i]);
                    }
                }
            }
            if (denyPermissions.isEmpty() && rationalePermissions.isEmpty()) {
                if (BuildConfig.DEBUG) {
                    Log.e("LiveDataPermission",
                          "LiveDataFragment onRequestPermissionsResult: GRANT");
                }
                this.mLiveData.setValue(new PermissionResult(PermissionResult.GRANT));
            } else {
                if (rationalePermissions.size() > 0) {
                    this.mLiveData.setValue(new PermissionResult(PermissionResult.DENY,
                                                                 rationalePermissions));
                    return;
                }

                if (denyPermissions.size() > 0) {
                    this.mLiveData.setValue(new PermissionResult(PermissionResult.RATIONALE,
                                                                 denyPermissions));
                }
            }
        }
    }

    private MutableLiveData<PermissionResult> makeLiveData() {
        if (this.mLiveData != null) {
            this.mLiveData = null;
        }
        return this.mLiveData = new MutableLiveData<>();
    }

}
