package com.plm.tonticheck2;

import android.content.Context;

import com.google.gson.Gson;
import com.plm.tonticheck2.model.TontiApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GsonUtils {
    private static Gson gson = new Gson();

    public static TontiApp loadApp(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            return gson.fromJson(reader, TontiApp.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static boolean saveApp(TontiApp app, File file) {
        try {
            OutputStreamWriter owriter = new OutputStreamWriter(new FileOutputStream(file));
            BufferedWriter writer = new BufferedWriter(owriter);
            String json = gson.toJson(app);
            writer.write(json);
            writer.flush();
            owriter.flush();
            writer.close();
            owriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void deleteFile(Context ctx){
        File file = new File(ctx.getFilesDir(), ctx.getString(R.string.json_file));
        file.delete();
    }

    public static File getFile(Context ctx) {
        File file = new File(ctx.getFilesDir(), ctx.getString(R.string.json_file));
        return file;
    }
}
