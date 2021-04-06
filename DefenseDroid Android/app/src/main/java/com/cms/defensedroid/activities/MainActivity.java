package com.cms.defensedroid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.cms.defensedroid.R;
import com.cms.defensedroid.helperclasses.AppListAdapter;
import com.cms.defensedroid.helperclasses.AppListModel;
import com.cms.defensedroid.helperclasses.ExtractDialog;
import com.cms.defensedroid.helperclasses.NameSaveModel;
import com.cms.defensedroid.helperclasses.Preferences;
import com.cms.defensedroid.helperclasses.ThemeConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AppListAdapter.ItemClickListener, ActionMode.Callback, MenuItem.OnActionExpandListener {
    Preferences preferences;
    androidx.appcompat.widget.SearchView searchView;
    int counter, totalApps, themeNo, flag, position, count = 0;
    ThemeConstant themeConstant;
    RecyclerView recycler_apps;
    MyFirebaseInstanceIDService myFirebaseInstanceIDService;
    TextView txt_noapps, txt_copyright;
    ProgressBar progressBar;
    Drawable icon;
    boolean dark, isInActionMode = false, isExtracted = false, selectAll = false, sys = true, toastDisplay = true;
    AppListAdapter appListAdapter;
    String name, packagename, size, version, uid, targetsdk, minsdk = null, permissions = null, vername;
    List<AppListModel> list = new ArrayList<>();
    List<AppListModel> updatedlist;
    List<NameSaveModel> filesList = new ArrayList<>();
    List<ApplicationInfo> packagelist = new ArrayList<>();
    List<NameSaveModel> requestFilesList = new ArrayList<>();
    List<File> newFilesList = new ArrayList<>();
    List<Integer> pos = new ArrayList<>();
    File file;
    ActionMode mode;
    long longsize;
    String uniqueID;
    String color, fileName;
    String ipv4Address = "127.0.0.1"; //Add your Cloud Machine IP Address Here
    String portNumber = "80";
    String token;
    ImageView imageIcon;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        preferences = new Preferences(MainActivity.this);
        myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
        color = preferences.getCircleColor();
        dark = preferences.getMode();
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_main);
        recycler_apps = findViewById(R.id.recycler_apps);
        txt_noapps = findViewById(R.id.txt_noapps);
        progressBar = findViewById(R.id.progressBar);
        txt_copyright = findViewById(R.id.txt_copyright);
        imageIcon = findViewById(R.id.imageView);
        txt_copyright.setTextColor(Color.parseColor(color));
        txt_noapps.setTextColor(Color.parseColor(color));
        PackageManager packageManager = getPackageManager();
        packagelist = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        totalApps = packagelist.size();
        invalidateOptionsMenu();
        NewThread newThread = new NewThread();
        newThread.start();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FailedToken", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token1 = task.getResult();
                        token = token1;

                        // Log and toast
                        Log.d("TokenSuccess", token1);
//                        Toast.makeText(MainActivity.this, token1, Toast.LENGTH_SHORT).show();
                    }
                });

        checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", 101);
        checkPermission("android.permission.READ_EXTERNAL_STORAGE", 102);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.search).getActionView();
        if (!sys)
            menu.findItem(R.id.systemapps).setTitle(getResources().getString(R.string.installed));
        else
            menu.findItem(R.id.systemapps).setTitle(getResources().getString(R.string.systemapps));

        menu.findItem(R.id.search).setOnActionExpandListener(this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!sys)
            menu.findItem(R.id.systemapps).setTitle(getResources().getString(R.string.installed));
        else
            menu.findItem(R.id.systemapps).setTitle(getResources().getString(R.string.systemapps));
        return true;
    }

    public void showPopupMenu(View view, final int position, final List<AppListModel> updatedList) {
        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.inflate(R.menu.threedot_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fileName = appListAdapter.newFileName(updatedList.get(position).getName(), updatedList.get(position).getVersion(), updatedList.get(position).getVername(), updatedList.get(position).getPackageName());
                switch (item.getItemId()) {
                    case R.id.appinfo:
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + updatedList.get(position).getPackageName()));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivity(intent);
                        }
                        break;

                    case R.id.shareapk:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            shareApk(position, fileName);
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                        break;

                    case R.id.checkApp:
                        uniqueID = UUID.randomUUID().toString();
                        appListAdapter.extract(position, uniqueID);
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/DefenseDroid/" + uniqueID + ".apk";
//                        uploadFile(path);
                        alertTwoButtons(path);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    public void shareApk(int position, String name) {
        appListAdapter.extract(position, name);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/DefenseDroid/" + name + ".apk";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        startActivity(Intent.createChooser(intent, "Share Now"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                finish();
                break;
            case R.id.contact:
                Intent contact_intent = new Intent(Intent.ACTION_SENDTO);
                contact_intent.setData(Uri.parse("mailto:"));
                contact_intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"20170107.clinecwc@student.xavier.ac.in", "20170102.mandarbdd@student.xavier.ac.in", "20170105.siddharthbav@student.xavier.ac.in"});
                contact_intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for " + getString(R.string.app_name) + " App");
                startActivity(Intent.createChooser(contact_intent, getResources().getString(R.string.email_via)));
                break;
            case R.id.systemapps:
                if (sys)
                    sortSystemApps();
                else
                    installedApps();
            case R.id.search:
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (!newText.isEmpty()) {
                            filter(newText);
                        }
                        return true;
                    }
                });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void filter(String s) {
        ArrayList<AppListModel> arrayList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                    arrayList.add(list.get(i));
                    txt_noapps.setVisibility(View.GONE);
                }
            }
            appListAdapter.filteredList(arrayList);
            if (arrayList.size() == 0) {
                txt_noapps.setVisibility(View.VISIBLE);
            }
        } else {
            if (toastDisplay) {
                Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
                toastDisplay = false;
            }
        }
    }

    public void sortSystemApps() {
        ArrayList<AppListModel> systemList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            sys = false;
            for (int i = 0; i < packagelist.size(); i++) {
                System.out.println("ivalue" + i + "\n");
                if (list.get(i).getFlag() == 0) {
                    systemList.add(list.get(i));
                }
            }
            appListAdapter.filteredList(systemList);
        } else {
            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void installedApps() {
        ArrayList<AppListModel> installedList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            sys = true;
            for (int i = 0; i < packagelist.size(); i++) {
                if (list.get(i).getFlag() == 1) {
                    installedList.add(list.get(i));
                }
            }
            appListAdapter.filteredList(installedList);
        } else {
            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void allApps() {
        if (list.size() == packagelist.size()) {
            txt_noapps.setVisibility(View.GONE);
            appListAdapter.filteredList(list);
        }
    }


    @Override
    public void onItemClick(final int position, View view, List<AppListModel> updatedList) {
        this.position = position;
        this.updatedlist = updatedList;
        if (!isInActionMode) {
            if (view.getId() == R.id.imgmore) {
                showPopupMenu(view, position, updatedList);
            } else {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        } else {
            if (updatedList.get(position).isSelected()) {
                updatedList.get(position).setSelected(false);
                if (filesList != null && pos != null) {
                    filesList.remove(filesList.get(pos.indexOf(position)));
                    pos.remove(pos.indexOf(position));
                    counter--;
                }
                setActionTitle(mode, counter);
            } else {
                filesList.add(new NameSaveModel(updatedList.get(position).getName(), updatedList.get(position).getPackageName(), updatedList.get(position).getVersion(), updatedList.get(position).getVername(), updatedList.get(position).getFile()));
                pos.add(position);
                counter++;
                setActionTitle(mode, counter);
                updatedList.get(position).setSelected(true);
            }
            appListAdapter.notifyItemChanged(position, 1);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.mode = mode;
        mode.getMenuInflater().inflate(R.menu.context_menu, menu);
        setActionTitle(mode, counter);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.extract:
                if (filesList.size() != 0) {
                    requestFilesList = filesList;
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/DefenseDroid/");
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        extractFunc(filesList);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                }
                mode.finish();
                break;
            case R.id.selectall:
                if (!selectAll) {
                    allApps();
                    appListAdapter.notifyDataSetChanged();
                    this.updatedlist = list;
                    pos.clear();
                    selectAll = true;
                    filesList.clear();
                    for (int i = 0; i < packagelist.size(); i++) {
                        list.get(i).setSelected(true);
                        pos.add(i);
                        filesList.add(new NameSaveModel(list.get(i).getName(), list.get(i).getPackageName(), list.get(i).getVersion(), list.get(i).getVername(), list.get(i).getFile()));
                    }
                    counter = packagelist.size();
                } else {
                    pos.clear();
                    filesList.clear();
                    for (int i = 0; i < packagelist.size(); i++) {
                        list.get(i).setSelected(false);
                    }
                    counter = 0;
                    selectAll = false;
                }
                setActionTitle(mode, counter);
                appListAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        isInActionMode = false;
        counter = 0;
        appListAdapter.setIsInAction(false);
        if (selectAll) {
            for (int i = 0; i < packagelist.size(); i++) {
                list.get(i).setSelected(false);
            }
            selectAll = false;
        }
        if (!isExtracted) {
            if (pos != null) {
                for (int i = 0; i < pos.size(); i++) {
                    updatedlist.get(pos.get(i)).setSelected(false);
                    Log.d("possize", " " + pos.size());
                    Log.d("updatedlist.setselected", " " + updatedlist.get(pos.get(i)).getName());
                }
            }
            filesList.clear();
            newFilesList.clear();
            pos.clear();
        }
        Log.d("isextracted", isExtracted + " ");
        appListAdapter.notifyDataSetChanged();
        this.mode = null;
    }

    public void setActionTitle(ActionMode mode, int itemCount) {
        mode.setTitle(itemCount + "/" + packagelist.size());
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        allApps();
        return true;
    }

    class NewThread extends Thread {
        @Override
        public void run() {
            list.clear();
            super.run();
            int i = 0;
            PackageManager manager = getPackageManager();
            if (packagelist.size() > 0) {
                for (ApplicationInfo applicationInfo : packagelist) {
                    i++;
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        flag = 1;
                    } else {
                        flag = 0;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    packagename = applicationInfo.packageName;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            version = String.valueOf((int) manager.getPackageInfo(packagename, 0).getLongVersionCode());
                        } else {
                            version = String.valueOf(manager.getPackageInfo(packagename, 0).versionCode);
                        }
                        vername = manager.getPackageInfo(packagename, 0).versionName;
                        String[] reqper = manager.getPackageInfo(packagename, PackageManager.GET_PERMISSIONS).requestedPermissions;
                        if (reqper != null) {
                            for (String per : reqper) {
                                stringBuilder.append("\n").append(per);
                            }
                        } else {
                            permissions = null;
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    permissions = stringBuilder.toString();
                    icon = applicationInfo.loadIcon(getPackageManager());
                    name = applicationInfo.loadLabel(getPackageManager()).toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        minsdk = String.valueOf(applicationInfo.minSdkVersion);
                    }
                    targetsdk = String.valueOf(applicationInfo.targetSdkVersion);
                    uid = String.valueOf(applicationInfo.uid);
                    file = new File(applicationInfo.publicSourceDir);
                    longsize = file.length();
                    if (longsize > 1024 && longsize <= 1024 * 1024) {
                        size = (longsize / 1024 + " KB");
                    } else if (longsize > 1024 * 1024 && longsize <= 1024 * 1024 * 1024) {
                        size = (longsize / (1024 * 1024) + " MB");
                    } else {
                        size = (longsize / (1024 * 1024 * 1024) + " GB");
                    }
                    list.add(new AppListModel(icon, name, packagename, file, size, flag, version, targetsdk, minsdk, uid, permissions, vername));
                    if (i == packagelist.size() - 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                appListAdapter = new AppListAdapter(MainActivity.this, list, Color.parseColor(preferences.getCircleColor()), recycler_apps, MainActivity.this);
                                appListAdapter.notifyDataSetChanged();
                                recycler_apps.setAdapter(appListAdapter);
                                recycler_apps.setLayoutManager(layoutManager);
                                recycler_apps.setHasFixedSize(true);
                                recycler_apps.setVisibility(View.VISIBLE);
                                imageIcon.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                txt_copyright.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void extractFunc(final List<NameSaveModel> fList) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/DefenseDroid/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (int i = 0; i < fList.size(); i++) {
            String saveName = appListAdapter.newFileName(fList.get(i).getName(), fList.get(i).getVerCode(), fList.get(i).getVerName(), fList.get(i).getPackageName());
            newFilesList.add(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/DefenseDroid/", saveName + ".apk"));
        }
        final ExtractDialog extractDialog = new ExtractDialog(MainActivity.this, Color.parseColor(color));
        extractDialog.showDialog(true);
        final Handler h = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if (count == fList.size()) {
                    String s = getResources().getString(R.string.totalextracted) + " " + fList.size() + " " + getResources().getString(R.string.apps);
                    isExtracted = false;
                    count = 0;
                    h.removeCallbacks(this);
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < pos.size(); i++) {
                        updatedlist.get(pos.get(i)).setSelected(false);
                        Log.d("updatedlist.setselected", " " + updatedlist.get(pos.get(i)).getName());
                    }
                    filesList.clear();
                    newFilesList.clear();
                    pos.clear();
                    extractDialog.showDialog(false);
                } else {
                    try {
                        InputStream inputStream = new FileInputStream(fList.get(count).getFile());
                        OutputStream outputStream = new FileOutputStream(newFilesList.get(count));
                        byte[] buf = new byte[9192];
                        int len;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    count++;
                    h.post(this);
                }
            }
        };
        h.post(r);
        isExtracted = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
            case 1:
                shareApk(position, fileName);
                break;
            case 2:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.select_again), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void uploadFile(String path, String isDeepScan) {

        // Get a non-default Storage bucket
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://your-bucket-name-here"); 

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = storageRef.child("apks/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        final Dialog dialog = new Dialog(MainActivity.this);
        WindowManager.LayoutParams aaa = new WindowManager.LayoutParams();
        aaa.copyFrom(dialog.getWindow().getAttributes());
        aaa.width = WindowManager.LayoutParams.MATCH_PARENT;
        aaa.height = WindowManager.LayoutParams.WRAP_CONTENT;
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_upload, null, false);
        dialog.setContentView(dialogView);
        ProgressBar progressbar = (ProgressBar) dialogView.findViewById(R.id.uploadProgressBar);
        dialog.show();

        Toast.makeText(getApplicationContext(), "File is being Uploaded...", Toast.LENGTH_SHORT).show();
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Unable to Upload File..", Toast.LENGTH_SHORT).show();
                fileDelete(path);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "File Uploaded for Testing Successfully..!!", Toast.LENGTH_SHORT).show();
                connectServer(isDeepScan);
                fileDelete(path);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (double) taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();

                int currentprogress = (int) progress;

                dialog.setContentView(R.layout.dialog_upload);
                dialog.setTitle("Custom Dialog");

                progressbar.setProgress(currentprogress);

                dialog.show();
            }
        });
    }

    public void connectServer(String isDeepScan) {

        String postUrl = "http://" + ipv4Address + ":" + portNumber + "/";

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

        byte[] b = (token).getBytes();
            File someFile = new File(uniqueID + ".txt");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(someFile);
                fos.write(b);
            } catch (IOException e) {
                e.printStackTrace();
            }

        multipartBodyBuilder.addFormDataPart(fileName, uniqueID + "696969" + token + "696969" + isDeepScan, RequestBody.create(MediaType.parse("*"), b));

        RequestBody postBodyImage = multipartBodyBuilder.build();

        postRequest(postUrl, postBodyImage);
    }


    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Failed", "Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i("server","Server's Response\n" + response.body().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void fileDelete(String filePathString){
        File filePath = new File(filePathString);
        boolean deleted = filePath.delete();
        if(!deleted){
            Toast.makeText(getApplicationContext(), "Failed Deleting File", Toast.LENGTH_SHORT).show();
        }

    }

    public void alertTwoButtons(String path) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Deep Scan")
                .setMessage("Performing Standard Scan by default. Deep Scan may take longer intervals than usual. Do you want to perform Deep Scan?")
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                String isDeepScan = "1";
                                uploadFile(path, isDeepScan);
                                Toast.makeText(getApplicationContext(), "Deep Scan Enabled.", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        String isDeepScan = "0";
                        uploadFile(path, isDeepScan);
                        Toast.makeText(getApplicationContext(), "Deep Scan Disabled.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }).show();
    }

}