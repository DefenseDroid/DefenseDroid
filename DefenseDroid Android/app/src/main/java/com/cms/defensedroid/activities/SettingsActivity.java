package com.cms.defensedroid.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.cms.defensedroid.BuildConfig;
import com.cms.defensedroid.R;
import com.cms.defensedroid.helperclasses.Preferences;
import com.cms.defensedroid.helperclasses.ThemeConstant;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rel_main, rel_theme, rel_color, rel_app_version;
    Preferences preferences;
    TextView text_themename, text_version;
    ThemeConstant themeConstant;
    int themeNo;
    boolean flag = false;
    ArrayList<String> colors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(SettingsActivity.this);
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.circle);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (themeNo != 0) {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(preferences.getCircleColor()));
        } else {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#0063B3"));
        }
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        colors.add("#f44236");
        colors.add("#ea1e63");
        colors.add("#9d27b2");
        colors.add("#673bb7");
        colors.add("#1029AD");
        colors.add("#0063B3");
        colors.add("#04a8f5");
        colors.add("#00bed2");
        colors.add("#009788");
        colors.add("#00D308");
        colors.add("#ff9700");
        colors.add("#FFC000");
        colors.add("#D2E41D");
        colors.add("#fe5722");
        colors.add("#5E4034");

        rel_color = findViewById(R.id.rel_color);
        rel_theme = findViewById(R.id.rel_theme);
        rel_app_version = findViewById(R.id.rel_app_version);
        text_version = findViewById(R.id.text_version);
        text_themename = findViewById(R.id.text_themename);
        rel_main = findViewById(R.id.rel_main);

        rel_color.setOnClickListener(this);
        rel_theme.setOnClickListener(this);
        rel_app_version.setOnClickListener(this);
        if (preferences.getMode()) {
            flag = true;
            text_themename.setText(getResources().getString(R.string.dark));
        }
        text_version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_color:
                showColorDialog();
                break;

            case R.id.rel_theme:
                showDialogBox();
                break;
        }
    }

    public void showColorDialog() {
        final ColorPicker colorPicker = new ColorPicker(SettingsActivity.this);
        colorPicker.setColors(colors).setColumns(5).setDefaultColorButton(Color.parseColor(preferences.getCircleColor())).setRoundColorButton(true).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                preferences.setThemeNo(position + 1);
                preferences.setCircleColor(colors.get(position));
                recreate();
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }

    public void showDialogBox() {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        Button btn_cancel;
        RadioButton radio1, radio2;
        dialog.setContentView(R.layout.dialog_mode);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        radio1 = dialog.findViewById(R.id.radio1);
        radio2 = dialog.findViewById(R.id.radio2);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (flag) {
            radio2.setChecked(true);
        } else {
            radio1.setChecked(true);
        }
        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                preferences.setMode(false);
                recreate();
                dialog.dismiss();

            }
        });
        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                preferences.setMode(true);
                recreate();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        finish();
    }

}