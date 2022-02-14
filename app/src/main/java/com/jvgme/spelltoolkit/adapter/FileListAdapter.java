package com.jvgme.spelltoolkit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvgme.spelltoolkit.R;
import com.jvgme.spelltoolkit.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FLViewHolder> {
    private final List<File> data;
    private final Context context;
    private OnTouchListener onTouchListener;
    private OnUpdatedListener onUpdatedListener;

    public FileListAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setOnUpdatedListener(OnUpdatedListener onUpdatedListener) {
        this.onUpdatedListener = onUpdatedListener;
    }

    @NonNull
    @Override
    public FileListAdapter.FLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_file_list, null);
        return new FLViewHolder(view);
    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(@NonNull FileListAdapter.FLViewHolder holder, int position) {
        File file = data.get(position);
        if (file == null) return;

        // 文件夹和文件采用不同的icon
        if (file.isDirectory()) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
            holder.fileIcon.setImageBitmap(bitmap);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file);
            holder.fileIcon.setImageBitmap(bitmap);
        }

        // 添加文件信息
        if (position == 0){
            holder.fileName.setText("------");
        } else {
            holder.fileName.setText(file.getName());
            holder.lastTime.setText(FileUtils.stampToTime(file.lastModified()));
            if (file.isFile())
                holder.fileSize.setText(FileUtils.fileSizeFormat(file.length()));
            else
                holder.fileSize.setText("");

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 更新数据
     *
     * @param data 文件列表数据
     */
    public void updateData(List<File> data) {
        // 移除视图上所有Item
        notifyItemRangeRemoved(0, this.data.size());
        // 清空旧数据并添加新数据
        this.data.clear();
        this.data.addAll(data);

        // 遍历逐个添加Item，这样有插入动画
        for (int i = 0; i < this.data.size(); i++) {
            notifyItemInserted(i);
        }
        if (onUpdatedListener != null)
            onUpdatedListener.onUpdated();
    }


    class FLViewHolder extends RecyclerView.ViewHolder {
        private final ImageView fileIcon;
        private final TextView fileName;
        private final TextView lastTime;
        private final TextView fileSize;

        public FLViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.file_icon);
            fileName = itemView.findViewById(R.id.file_name);
            lastTime = itemView.findViewById(R.id.file_last_time);
            fileSize = itemView.findViewById(R.id.file_size);

            // 添加触摸监听事件
            itemView.setOnTouchListener((view, motionEvent) -> {
                // 调用触摸事件回调接口的实例，把参数传过去
                if (onTouchListener != null) {
                    int position = getBindingAdapterPosition();
                    return onTouchListener.onTouch(view, data.get(position), motionEvent);
                }

                view.performClick();
                return false;
            });
        }
    }

    /**
     * 触摸事件的回调接口。
     * 之所以使用回调的方式，是因为直接 set 监听器无法把用户选择的文件传递过去交给插件处理。
     */
    public interface OnTouchListener {
        boolean onTouch(View view, File file, MotionEvent motionEvent);
    }

    /**
     * 更新数据后回调
     */
    public interface OnUpdatedListener {
        void onUpdated();
    }
}
