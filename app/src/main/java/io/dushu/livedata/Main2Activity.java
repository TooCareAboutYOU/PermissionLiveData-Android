package io.dushu.livedata;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.dushu.livedata.base.BaseActivity;
import io.dushu.permission.livedata.LiveDataPermission;
import io.dushu.permission.livedata.PermissionResult;

public class Main2Activity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (BuildConfig.DEBUG) {
            Log.e("LiveDataPermission", "Main2Activity onCreate: ");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .show();

                questPermission2();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    private void questPermission2() {
        LiveDataPermission.getInstance()
                .request(this,
                         new String[]{
                                 Manifest.permission.WRITE_EXTERNAL_STORAGE
                         },
                         100)
                .observe(this,
                         new Observer<PermissionResult>() {
                             @Override
                             public void onChanged(
                                     final PermissionResult permissionResult) {
                                 if (permissionResult == null) {
                                     Toast.makeText(Main2Activity.this,
                                                    "返回为null!",
                                                    Toast.LENGTH_SHORT)
                                             .show();
                                     return;
                                 }
                                 switch (permissionResult.getState()) {
                                     case PermissionResult.GRANT: {
                                         Toast.makeText(Main2Activity.this,
                                                        "全部通过",
                                                        Toast.LENGTH_SHORT)
                                                 .show();
                                         if (BuildConfig.DEBUG) {
                                             Log.i("LiveDataPermission",
                                                   "Main2Activity onChanged: ");
                                         }
                                         break;
                                     }
                                     case PermissionResult.DENY: {
                                         Toast.makeText(Main2Activity.this,
                                                        "被拒绝的权限",
                                                        Toast.LENGTH_SHORT)
                                                 .show();
                                         break;
                                     }
                                     case PermissionResult.RATIONALE: {
                                         Toast.makeText(Main2Activity.this,
                                                        "永久禁止弹出弹出申请权限",
                                                        Toast.LENGTH_SHORT)
                                                 .show();
                                         break;
                                     }
                                     default:
                                         Toast.makeText(Main2Activity.this,
                                                        "默认",
                                                        Toast.LENGTH_SHORT)
                                                 .show();
                                         break;
                                 }
                             }
                         });

    }
}
