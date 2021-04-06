package com.cms.defensedroid.helperclasses;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cms.defensedroid.R;

import java.io.File;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.Viewholder> {
    private Context context;
    private List<AppListModel> list;
    private int color;
    private View view;
    private ItemClickListener itemClickListener;
    private Preferences preferences;
    private boolean isInAction = false;

    public AppListAdapter(Context context, List<AppListModel> list, int color, View view, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.color = color;
        this.view = view;
        preferences = new Preferences(context);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_app_layout, parent, false);
        return new Viewholder(v, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.get(0) instanceof Integer) {
                if (list.get(position).isSelected()) {
                    holder.multi_check.setChecked(true);
                } else {
                    holder.multi_check.setChecked(false);
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        holder.img_icon.setImageDrawable(list.get(position).getIcon());
        holder.name.setText(list.get(position).getName());
        holder.imgmore.setVisibility(View.VISIBLE);
        holder.multi_check.setVisibility(View.GONE);
        holder.packagename.setText(list.get(position).getPackageName());
        holder.size.setText(list.get(position).getSize());
        if (list.get(position).isSelected()) {
            holder.multi_check.setChecked(true);
        } else {
            holder.multi_check.setChecked(false);
        }
        if (isInAction) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.multi_check.getLayoutParams();
            holder.imgmore.setVisibility(View.GONE);
            holder.multi_check.setVisibility(View.VISIBLE);
            params.addRule(RelativeLayout.LEFT_OF, R.id.item_linear);
            params.addRule(RelativeLayout.LEFT_OF, R.id.appname);
            holder.multi_check.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_icon, imgmore;
        TextView name, packagename, size;
        RelativeLayout rel_main;
        CheckBox multi_check;
        LinearLayout item_linear;
        ItemClickListener mItemClickListener;

        public Viewholder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            this.mItemClickListener = itemClickListener;
            rel_main = itemView.findViewById(R.id.rel_main);
            img_icon = itemView.findViewById(R.id.appicon);
            imgmore = itemView.findViewById(R.id.imgmore);
            multi_check = itemView.findViewById(R.id.check_multi);
            name = itemView.findViewById(R.id.appname);
            packagename = itemView.findViewById(R.id.pkgname);
            size = itemView.findViewById(R.id.txt_size);
            item_linear = itemView.findViewById(R.id.item_linear);
            rel_main.setOnClickListener(this);
            imgmore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition(), v, list);
        }
    }

    public void setIsInAction(boolean b) {
        isInAction = b;
    }

    public void filteredList(List<AppListModel> arrayList) {
        list = arrayList;
        notifyDataSetChanged();
    }

    public void extract(int position, String name) {
        File file = new File(String.valueOf(list.get(position).getFile()));
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/DefenseDroid/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File newFile = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/DefenseDroid/"), name + ".apk");
        ExtractThread extractThread = new ExtractThread(context, file, newFile, newFile.getPath(), color, view);
        extractThread.start();
    }

    public String newFileName(String appname, String vercode, String vername, String pkgname) {
        StringBuilder builder = new StringBuilder();
        if (preferences.getAppName()) {
            builder.append(appname + "_");
        }
        if (preferences.getPkgName()) {
            builder.append(pkgname + "_");
        }
        if (preferences.getVerName()) {
            builder.append(vername + "_");
        }
        if (preferences.getVerCode()) {
            builder.append("V" + vercode + "_");
        }
        int l = builder.toString().length();
        return builder.toString().substring(0, l - 1);
    }

    public interface ItemClickListener {
        void onItemClick(int position, View view, List<AppListModel> updatedList);
    }
}

