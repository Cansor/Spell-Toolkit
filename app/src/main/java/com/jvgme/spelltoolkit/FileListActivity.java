package com.jvgme.spelltoolkit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jvgme.spelltoolkit.adapter.FileListAdapter;
import com.jvgme.spelltoolkit.core.PluginExecutor;
import com.jvgme.spelltoolkit.core.android.PluginExecutorImpl;
import com.jvgme.spelltoolkit.core.android.widget.DialogImpl;
import com.jvgme.spelltoolkit.core.android.widget.LogWindowImpl;
import com.jvgme.spelltoolkit.core.android.widget.MessageImpl;
import com.jvgme.spelltoolkit.core.android.widget.WidgetManagerImpl;
import com.jvgme.spelltoolkit.core.widget.Dialog;
import com.jvgme.spelltoolkit.core.widget.LogWindow;
import com.jvgme.spelltoolkit.core.widget.Message;
import com.jvgme.spelltoolkit.core.widget.WidgetManager;
import com.jvgme.spelltoolkit.util.FileUtils;
import com.jvgme.spelltoolkit.util.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListActivity extends BaseActivity {
    private FileListAdapter fileListAdapter;
    private String SDCardRootPath;

    private String pluginsId; // 插件ID
    private File currFile; // 记录当前路径
    private TextView filePath; // 用于在操作栏上显示路径

    private PluginExecutor pluginExecutor;
    private LogWindow logWindow;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            logWindow.show();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化
     */
    private void initializeView() {
        pluginsId = getIntent().getStringExtra("pluginId"); // 获取传递过来的参数
        SDCardRootPath = Tools.getExternalStorageDirectory(this, "");
        logWindow = new LogWindowImpl(this); // 日志窗口

        WidgetManager widgetManager = WidgetManagerImpl.instance();
        // 注册控件
        widgetManager.registerWidget(LogWindow.ID, logWindow);
        widgetManager.registerWidget(Message.ID, new MessageImpl(this));
        widgetManager.registerWidget(Dialog.ID, new DialogImpl(this));

        pluginExecutor = new PluginExecutorImpl(Tools.getPluginManager(this), widgetManager);

        // 创建文件列表
        RecyclerView recyclerView = findViewById(R.id.rv_file_list);
        // 添加布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加适配器
        fileListAdapter = new FileListAdapter(this);
        recyclerView.setAdapter(fileListAdapter);

        // 点击事件
        fileListAdapter.setRecyclerItemClickListener((view, file, position) -> {
            // 如果是文件夹，则显示该路径下的文件列表，如果是文件，则弹出对话框
            if (file.isDirectory()) {
                handler.postDelayed(() -> updateFileList(file.getAbsolutePath()), 70);
            } else {
                // 执行插件
                pluginExecutor.execute(pluginsId, file);
                // 更新文件列表
                handler.postDelayed(() -> updateFileList(currFile.getAbsolutePath()), 100);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}