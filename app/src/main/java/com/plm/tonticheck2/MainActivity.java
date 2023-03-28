package com.plm.tonticheck2;

import static com.plm.tonticheck2.Ctes.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.plm.tonticheck2.databinding.ActivityMainBinding;
import com.plm.tonticheck2.model.MySharedModel;
import com.plm.tonticheck2.model.TontiApp;
import com.plm.tonticheck2.model.TontiTask;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

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

        createNotificationChannel();

        activityResult=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int type=result.getResultCode();

                        if(result.getResultCode() != Activity.RESULT_OK){
                            Log.d(TAG,"Error en activity result");
                            return;
                        }



                        //TODO DELETE
                        int extra=new ViewModelProvider(MainActivity.this).get(MySharedModel.class).getExtraInt(ACTION_EXPORT_OR_IMPORT);

                        try{
                            if(extra==ACTION_EXPORT){
                                TontiApp app = new ViewModelProvider(MainActivity.this).get(MySharedModel.class).getApp(MainActivity.this);
                                GsonUtils.saveAppToUri(MainActivity.this,result.getData().getData());

                            }else if(extra==ACTION_IMPORT){
                                TontiApp importedApp = GsonUtils.loadAppFromUri(MainActivity.this,result.getData().getData());
                                TontiApp actualApp = new ViewModelProvider(MainActivity.this).get(MySharedModel.class).getApp(MainActivity.this);
                                buildImportDialog(importedApp,actualApp);

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

        if(getIntent()!=null){
            if(getIntent().getIntExtra("id",-111)==-111){
                Log.d(Ctes.TAG,"Arrancada la aplicación");
            }else{
                Log.d(Ctes.TAG,"Desed notificacion");
            }
        }
        //comprobamos si viene desde pulsar una notificación
    }

    private void buildImportDialog(TontiApp importedApp, TontiApp actualApp)  {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importar Archivo");
        builder.setMessage("Como quieres importar el archivo?");

        builder.setPositiveButton("Añadir a los datos existentes",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG,"positive");

                boolean result=importedApp.list.addAll(actualApp.list);

                if(result){
                    GsonUtils.saveApp(MainActivity.this,importedApp);
                    restartActivity();
                }
            }
        });

        builder.setNegativeButton("Reemplazar datos existentes",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GsonUtils.saveApp(MainActivity.this,importedApp);
                restartActivity();
            }
        });

        builder.setNeutralButton("Cancelar",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG,"neutral");
            }
        });


        builder.create().show();
    }

    private void restartActivity(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);

        startActivity(intent);
        overridePendingTransition(0, 0);

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

            //intent.putExtra(ACTION_EXPORT_OR_IMPORT,ACTION_EXPORT);
            new ViewModelProvider(MainActivity.this).get(MySharedModel.class).putExtraInt(ACTION_EXPORT_OR_IMPORT,ACTION_EXPORT);

            activityResult.launch(Intent.createChooser(intent,"Export  File"));
            return true;
        } else if (id == R.id.action_import_json) {
            //import
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            //intent.putExtra(ACTION_EXPORT_OR_IMPORT,ACTION_IMPORT);
            new ViewModelProvider(MainActivity.this).get(MySharedModel.class).putExtraInt(ACTION_EXPORT_OR_IMPORT,ACTION_IMPORT);
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


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.channel_id);
            CharSequence channelName =getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.setDescription(getString(R.string.channel_description));

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }


    public AppBarConfiguration getAppBarConfiguration() {
        return appBarConfiguration;
    }



}