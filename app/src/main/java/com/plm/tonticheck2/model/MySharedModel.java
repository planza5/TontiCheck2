package com.plm.tonticheck2.model;

import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.plm.tonticheck2.GsonUtils;
import com.plm.tonticheck2.R;

public class MySharedModel extends ViewModel {
    private TontiApp app;
    private int position;
    private Bundle bundle=new Bundle();

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    
    public TontiApp getApp(Context ctx) throws Exception{
        if (app == null) {
            app=GsonUtils.loadApp(ctx);
        }

        if(app==null){
            app=new TontiApp();
            boolean result=GsonUtils.saveApp(ctx,app);

            if(result==false){
                throw new Exception("No fue posible cargar la app del modelo");
            }
        }


        return app;
    }

    public void setApp(TontiApp app) {
        this.app=app;
    }

    public void putExtraInt(String name,int n){
        bundle.putInt(name,n);
    }

    public int getExtraInt(String name){
        return bundle.getInt(name);
    }
}