package com.jvgme.spelltoolkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jvgme.spelltoolkit.adapter.FileListAdapter;
import com.jvgme.spelltoolkit.core.PluginManager;
import com.jvgme.spelltoolkit.core.android.PluginLogImpl;
import com.jvgme.spelltoolkit.util.FileUtils;
import com.jvgme.spelltoolkit.util.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListActivity extends BaseActivity {
    private PluginManager pluginManager;
    private FileListAdapter fileListAdapter;
    private String SDCardRootPath;

    private String pluginsId; // 插件ID
    private File currFile; // 记录当前路径
    private TextView filePath; // 用于在操作栏上显示路径

    private PluginRunLogWindow logWindow;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取传递过来的参数
        pluginsId = getIntent().getStringExtra("pluginsId");

        pluginManager = Tools.getPluginManager(this);
        SDCardRootPath = Tools.getExternalStorageDirectory(this, "");

        initializeView();
        updateFileList(SDCardRootPath);
    }

    @Override
    protected int setContentViewLayout() {
        return R.layout.activity_file_list;
    }

    @Override
    protected void handlerToolbar(Toolbar toolbar) {
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 添加显示路径的 TextView
        @SuppressLint("InflateParams")
        TextView textView = getLayoutInflater().inflate(R.layout.path_file_list_toolbar, null)
                .findViewById(R.id.path_file);
        toolbar.addView(textView);
        filePath = textView;
    }

    /**
     * 在操作栏上创建菜单项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_list_menu, menu);
        return true;
    }

    /**
     * 操作栏上的菜单项处理
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_console) {
            logWindow.showWindow();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void initializeView() {
        // 创建日志窗口
        logWindow = new PluginRunLogWindow(this);
        logView = logWindow.getTextView();

        // 创建文件列表
        RecyclerView recyclerView = findViewById(R.id.rv_file_list);
        // 添加布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fileListAdapter = new FileListAdapter(this);
        recyclerView.setAdapter(fileListAdapter);

        // 点击事件
        fileListAdapter.setRecyclerItemClickListener((view, file, position) -> {
            // 动画效果
            ObjectAnimator objectAnimator = ObjectAnimator.ofArgb(view,
                    "backgroundColor",Color.WHITE, Color.GRAY, Color.WHITE);
            objectAnimator.setDuration(500);
            objectAnimator.start();

            // 如果是文件夹，则显示该路径下的文件列表，如果是文件，则弹出对话框
            if (file.isDirectory()) {
                updateFileList(file.getAbsolutePath());
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.execute_plugins)
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                            try {
                                // 显示日志窗口
                                logWindow.showWindow();
                                // 执行插件
                                pluginManager.execute(pluginsId, new PluginLogImpl(logView) , file);
                                updateFileList(currFile.getAbsolutePath());
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
    }

    /**
     * 更新文件列表
     */
    private void updateFileList(String path) {
        // 获取文件列表
        File file = new File(path);
        File[] files = file.listFiles();

        currFile = file;
        List<File> list = new ArrayList<>();

        if (files != null)
            list.addAll(Arrays.asList(files));

        // 排序
        FileUtils.sort(list);

        // 添加父级目录
        if (file.getAbsolutePath().equals(SDCardRootPath)) {
            list.add(0, file);
        } else {
            list.add(0, file.getParentFile());
        }

        filePath.setText(currFile.getPath());
        fileListAdapter.updateData(list);
    }

    /**
     * 返回键的事件
     */
    @Override
    public void onBackPressed() {
        // 如果当前目录与根目录一致，则退出，否则返回上一级目录
        if (!currFile.getPath().equals(SDCardRootPath)){
            File parentFile = currFile.getParentFile();
            if (parentFile != null) {
                updateFileList(parentFile.getPath());
            }
        } else {
            finish();
        }
    }
}