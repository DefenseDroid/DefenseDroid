package com.cms.defensedroid.helperclasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cms.defensedroid.R;
import com.cms.defensedroid.activities.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExtractThread extends Thread {
    private File file, newFile;
    private Context context;
    private String path;
    AlertDialog dialog;
    private int color;
    View view;

    public ExtractThread(Context context, File file, File newFile, String path, int color, View view) {
        this.file = file;
        this.newFile = newFile;
        this.context = context;
        this.path = path;
        this.color = color;
        this.view = view;
    }

    @Override
    public void run() {
        super.run();
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(true);
                }
            });
            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = new FileOutputStream(newFile);
            byte[] buf = new byte[9192];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                }
            });
        } catch (final FileNotFoundException e) {
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                    Log.d("ExceptionApk", e.getMessage());
                }
            });
            e.printStackTrace();
        } catch (final IOException e) {
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                    Log.d("ExceptionApk", e.getMessage());
                }
            });
            e.printStackTrace();
        }
    }

    public void showDialog(boolean visiblity) {
        if (visiblity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            dialog = builder.create();
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.dialog_progress, null, false);
            TextView tv_progress = customView.findViewById(R.id.tv_progress);
            tv_progress.setTextColor(color);
            dialog.setView(customView);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}
