package io.dushu.livedata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import io.dushu.livedata.base.BaseActivity;
import io.dushu.permission.livedata.LiveDataPermission;
import io.dushu.permission.livedata.PermissionResult;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    MutableLiveData<String> liveData1, liveData2;
    LiveData<String> liveData3, liveData4;
    MediatorLiveData<String> mediatorLiveData;

    private void loadMediatorLiveData() {
        if (mediatorLiveData == null) {
            liveData1 = new MutableLiveData<String>();
            liveData2 = new MutableLiveData<String>();
            mediatorLiveData = new MediatorLiveData<>();
        }

        liveData3 = Transformations.map(liveData2,
                                        new Function<String, String>() {
                                            @Override
                                            public String apply(final String input) {
                                                return input + " ,by map";
                                            }
                                        });

        liveData4 = Transformations.switchMap(liveData2, new Function<String, LiveData<String>>() {
            @Override
            public LiveData<String> apply(final String input) {

                MutableLiveData<String> mutableLiveData = new MediatorLiveData<String>();
                mutableLiveData.setValue(input + " ,by switchMap");

                return mutableLiveData;
            }
        });

        mediatorLiveData.addSource(liveData1, new Observer<String>() {
            @Override
            public void onChanged(final String sm) {
                mediatorLiveData.setValue("LiveData【1：" + sm);
            }
        });

        mediatorLiveData.addSource(liveData2, new Observer<String>() {
            @Override
            public void onChanged(final String sm) {
                mediatorLiveData.setValue("LiveData【2：" + sm);
            }
        });

        mediatorLiveData.addSource(liveData3, new Observer<String>() {
            @Override
            public void onChanged(final String sm) {
                mediatorLiveData.setValue("LiveData【3：" + sm);
            }
        });

        mediatorLiveData.addSource(liveData4, new Observer<String>() {
            @Override
            public void onChanged(final String sm) {
                mediatorLiveData.setValue("LiveData【4：" + sm);
            }
        });

        mediatorLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(final String sm) {
                String data = "输出: " + sm;
                Log.i(this.getClass()
                              .getSimpleName(), "onChanged: " + data);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMediatorLiveData();

        findViewById(R.id.acBtnChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                liveData1.setValue("I am from liveData1.");
                liveData2.setValue("I am from liveData2.");

//                questPermission1();
                questPermission2();
            }
        });

    }

    private void questPermission1() {
        RequestPermissionLiveData.getInstance(MainActivity.this)
                .setPermission(RequestPermissionLiveData.DEFAULT_PERMISSION);
    }

    private void questPermission2() {
        LiveDataPermission
                .getInstance()
                .request(this,
                         new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100)
                .observe(this,
                         new Observer<PermissionResult>() {
                             @Override
                             public void onChanged(
                                     final PermissionResult permissionResult) {
                                 if (permissionResult == null) {
                                     Toast.makeText(MainActivity.this,
                                                    "返回为null!",
                                                    Toast.LENGTH_SHORT)
                                             .show();
                                     return;
                                 }
                                 switch (permissionResult.getState()) {
                                     case PermissionResult.GRANT: {
                                         Toast.makeText(MainActivity.this,
                                                        "全部通过",
                                                        Toast.LENGTH_SHORT)
                                                 .show();
                                         if (BuildConfig.DEBUG) {
                                             Log.i("LiveDataPermission",
                                                   "MainActivity onChanged: ");
                                         }
                                         startActivity(new Intent(MainActivity.this,
                                                                  Main2Activity.class));
                                         break;
                                     }
                                     case PermissionResult.DENY: {
                                         Toast.makeText(MainActivity.this,
                                                        "被拒绝的权限",
                                                        Toast.LENGTH_SHORT)
                                                 .show();
                                         break;
                                     }
                                     case PermissionResult.RATIONALE: {
                                         Toast.makeText(MainActivity.this,
                                                        "永久禁止弹出弹出申请权限",
                                                        Toast.LENGTH_SHORT)
                                                 .show();
                                         break;
                                     }
                                     default:
                                         Toast.makeText(MainActivity.this, "默认",
                                                        Toast.LENGTH_SHORT)
                                                 .show();
                                         break;
                                 }
                             }
                         });

    }

//    @Override
//    public void onRequestPermissionsResult(final int requestCode,
//                                           @NonNull final String[] permissions,
//                                           @NonNull final int[] grantResults) {
//        Log.i("LiveDataPermission", "printLog: 来了权限回调");
//        if (requestCode == RequestPermissionLiveData.REQUEST_PERMISSION_CODE) {
//            if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Log.e("LiveDataPermission", "onChanged: 写入权限申请失败！！！");
//            } else {
//                Log.i("LiveDataPermission", "onChanged: 写入权限申请成功！");
//                startActivity(new Intent(MainActivity.this, Main2Activity.class));
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediatorLiveData != null) {
            mediatorLiveData.removeSource(liveData1);
            mediatorLiveData.removeSource(liveData2);
            mediatorLiveData.removeSource(liveData3);
            mediatorLiveData.removeSource(liveData4);
        }
    }
}
