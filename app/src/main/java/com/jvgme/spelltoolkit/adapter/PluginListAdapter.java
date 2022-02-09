package com.jvgme.spelltoolkit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvgme.spelltoolkit.R;
import com.jvgme.spelltoolkit.core.Plugin;

import java.io.File;
import java.util.List;

public class PluginListAdapter extends RecyclerView.Adapter<PluginListAdapter.PluginViewHolder> {
    private final List<Plugin> data;
    private final Context context;

    private OnTouchListener onTouchListener;

    public PluginListAdapter(List<Plugin> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    @NonNull
    @Override
    public PluginViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_plugins_list, null);
        return new PluginViewHolder(view);
    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(@NonNull PluginViewHolder holder, int position) {
        Plugin plugin = data.get(position);
        if (plugin.getIcon() != null)
            holder.icon.setImageIcon(plugin.getIcon());
        holder.name.setText(plugin.getName());
        holder.author.setText(plugin.getAuthor());
        holder.version.setText(plugin.getVersion());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 更新数据
     */
    public void updateData(List<Plugin> data) {
        // 移除视图上所有Item
        notifyItemRangeRemoved(0, this.data.size());
        // 清空旧数据并添加新数据
        this.data.clear();
        this.data.addAll(data);

        // 遍历逐个添加 Item，这样有插入动画
        for (int i = 0; i < this.data.size(); i++) {
            notifyItemInserted(i);
        }
    }

    class PluginViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView name;
        private final TextView author;
        private final TextView version;

        @SuppressLint("ClickableViewAccessibility")
        public PluginViewHolder(@NonNull View itemView) {
            super(itemView);

            this.icon = itemView.findViewById(R.id.iv_plugins_icon);
            this.name = itemView.findViewById(R.id.tv_plugins_name);
            this.author = itemView.findViewById(R.id.tv_plugins_author);
            this.version = itemView.findViewById(R.id.tv_plugins_version);

            itemView.setOnTouchListener((view, motionEvent) -> {
                if (onTouchListener != null) {
                    return onTouchListener.onTouch(view, data.get(getBindingAdapterPosition()),
                            motionEvent);
                }
                return false;
            });
        }
    }

    // 触摸监听器接口
    public interface OnTouchListener {
        boolean onTouch(View view, Plugin plugin, MotionEvent motionEvent);
    }
}
