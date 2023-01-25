package com.plm.tonticheck2;

import static com.plm.tonticheck2.Ctes.TAG;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.plm.tonticheck2.databinding.ActivityMainBinding;
import com.plm.tonticheck2.model.MySharedModel;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_EXPORT = 111;
    private static final int ACTION_IMPORT = 222;
    private static final String ACTION_EXPORT_OR_IMPORT = "EXPORT_OR_IMPORT";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> activityResult;
private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GsonUtils.deleteFile(this);



        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        activityResult=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int type=result.getResultCode();

                        if(result.getResultCode() != Activity.RESULT_OK){
                            Log.d(TAG,"Error en acivity result");
                            return;
                        }

                        int test=getIntent().getIntExtra(ACTION_EXPORT_OR_IMPORT,-1);
                        Bundle bundle = result.getData().getExtras();
                        int extra=result.getData().getIntExtra(ACTION_EXPORT_OR_IMPORT,-1);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Log.d(TAG,"c="+counter);
                                counter++;
                            }
                        }, 0, 1000);

                        Uri uri = result.getData().getData();

                        try{
                            if(extra==ACTION_EXPORT){
                                byte b[]=GsonUtils.readUri(MainActivity.this,uri);
                                String s=new String(b);
                                System.out.println(s);
                            }else if(extra==ACTION_IMPORT){
                                MySharedModel model = new ViewModelProvider(MainActivity.this).get(MySharedModel.class);
                                GsonUtils.saveApp(model.getApp(MainActivity.this),new File(uri.getPath()));
                                Log.d(TAG,"Saving app!!!");
                            }else{
                                Log.d(TAG,"Valor inesperdado");
                            }
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }



                        Log.d(TAG,"Result");
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_export_json) {
            //export
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(ACTION_EXPORT_OR_IMPORT,ACTION_EXPORT);
            activityResult.launch(Intent.createChooser(intent,"Export  File"));
            return true;
        } else if (id == R.id.action_import_json) {
            //import
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(ACTION_EXPORT_OR_IMPORT,ACTION_IMPORT);

            activityResult.launch(Intent.createChooser(intent,"Import  File"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public AppBarConfiguration getAppBarConfiguration() {
        return appBarConfiguration;
    }



}