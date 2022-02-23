package com.jvgme.spelltoolkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jvgme.spelltoolkit.adapter.FileListAdapter;
import com.jvgme.spelltoolkit.core.PluginExecutor;
import com.jvgme.spelltoolkit.core.android.PluginExecutorImpl;
import com.jvgme.spelltoolkit.core.android.widget.DialogImpl;
import com.jvgme.spelltoolkit.core.android.widget.EditTextDialogImpl;
import com.jvgme.spelltoolkit.core.android.widget.LogWindowImpl;
import com.jvgme.spelltoolkit.core.android.widget.MessageImpl;
import com.jvgme.spelltoolkit.core.android.widget.WidgetManagerImpl;
import com.jvgme.spelltoolkit.core.widget.Dialog;
import com.jvgme.spelltoolkit.core.widget.EditTextDialog;
import com.jvgme.spelltoolkit.core.widget.LogWindow;
import com.jvgme.spelltoolkit.core.widget.Message;
import com.jvgme.spelltoolkit.core.widget.WidgetManager;
import com.jvgme.spelltoolkit.listener.OnRecyclerViewItemTouchListener;
import com.jvgme.spelltoolkit.util.FileUtils;
import com.jvgme.spelltoolkit.util.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileListActivity extends BaseActivity {
    private FileListAdapter fileListAdapter;
    private String SDCardRootPath;

    private String pluginsId; // 插件ID
    private File currPathFile; // 记录当前路径
    private TextView filePath; // 用于在操作栏上显示路径
    private final List<File> fileList = new ArrayList<>();

    private PluginExecutor pluginExecutor;
    private LogWindow logWindow;

    private final Handler handler = new Handler();

    // 每层目录 item 滚动的坐标
    private final Map<String, Integer> itemPositionMap = new HashMap<>();
    // 记录文件夹的名称
    private String lastPath;

    private String[] menu;
    private File currFile; // 当前选中的文件
    private AlertDialog.Builder fileMenuDialog; // 文件操作对话框
    private EditTextDialog fileRenameDialog; // 文件重命名对话框
    private AlertDialog.Builder fileDeleteDialog; // 文件删除对话框

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
        int itemId = item.getItemId();
        if (itemId == R.id.file_list_menu_console) {
            logWindow.show();
            return true;
        }
        else if (itemId == R.id.file_list_menu_refresh) {
            updateFileList(currPathFile.getAbsolutePath());
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
        //文件操件菜单
        menu = new String[] {
                getResources().getString(R.string.file_menu_execute_plugin),
                getResources().getString(R.string.file_menu_rename),
                getResources().getString(R.string.file_menu_delete)
        };

        fileRenameDialog = new EditTextDialogImpl(this);
        fileRenameDialog.setTextColor("#333333");
        fileRenameDialog.setTitle(menu[1])
                .setPositiveButton(getResources().getString(R.string.ok), ((d, w) -> {
                    boolean b = currFile.renameTo(new File(currFile.getParent()
                            + "/" + fileRenameDialog.getText()));
                    if (b) {
                        Toast.makeText(this, getResources().getString(R.string.file_rename_succeed),
                                Toast.LENGTH_SHORT).show();
                        updateFileList(currPathFile.getAbsolutePath());
                    } else
                        Toast.makeText(this, getResources().getString(R.string.file_rename_failure),
                                Toast.LENGTH_SHORT).show();
                }))
                .setNegativeButton(getResources().getString(R.string.cancel), null);

        fileDeleteDialog = new AlertDialog.Builder(this)
                .setTitle(menu[2])
                .setPositiveButton(R.string.yes, (d, w) -> {
                    if (currFile.delete()) {
                        Toast.makeText(this, getResources().getString(R.string.file_delete_succeed),
                                Toast.LENGTH_SHORT).show();
                        updateFileList(currPathFile.getAbsolutePath());
                    } else
                        Toast.makeText(this, getResources().getString(R.string.file_delete_failure),
                                Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null);

        fileMenuDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.file_menu_title))
                .setIcon(R.mipmap.icon_launcher)
                .setItems(menu, (dialog, which) -> {
                    if (which == 0)
                        pluginExecutor.execute(pluginsId, currFile);
                    if (which == 1) {
                        fileRenameDialog.setText(currFile.getName());
                        fileRenameDialog.show();
                        fileRenameDialog.requestFocus();
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    } else if (which == 2) {
                        fileDeleteDialog.setMessage(getResources().getString(R.string.file_delete_ask)
                                .replace("%fileName%", currFile.getName()));
                        fileDeleteDialog.show();
                    }
                });

        logWindow = new LogWindowImpl(this); // 日志窗口

        WidgetManager widgetManager = WidgetManagerImpl.instance();
        // 注册控件
        widgetManager.registerWidget(LogWindow.ID, logWindow);
        widgetManager.registerWidget(Message.ID, new MessageImpl(this));
        widgetManager.registerWidget(Dialog.ID, DialogImpl.create(this));
        widgetManager.registerWidget(EditTextDialog.ID, new EditTextDialogImpl(this));

        pluginExecutor = new PluginExecutorImpl(Tools.getPluginManager(this), widgetManager);

        // 创建文件列表
        RecyclerView recyclerView = findViewById(R.id.rv_file_list);
        // 添加布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 添加适配器
        fileListAdapter = new FileListAdapter(this);
        recyclerView.setAdapter(fileListAdapter);

        // 添加触摸监听器
        recyclerView.addOnItemTouchListener(new OnRecyclerViewItemTouchListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                handler.postDelayed(() -> onFileClickEvent(fileList.get(position)), 150);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh, int position) {
                currFile = fileList.get(position);
                fileMenuDialog.show();
            }
        });

        // 添加滚动监听器
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当 RecyclerView 滚动结束时，记录滚动位置
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (currPathFile != null) {
                        int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                        itemPositionMap.put(currPathFile.getAbsolutePath(), position);
                    }
                }
            }
        });

        // 添加更新数据监听器
        fileListAdapter.setOnUpdatedListener(() -> {
            // 数据更新结束时滚动到记录的位置
            if (currPathFile != null) {
                // 在返回父目录时移除子目录的记录
                String path = currPathFile.getAbsolutePath();
                itemPositionMap.remove(path + "/" + this.lastPath);
                this.lastPath = currPathFile.getName();

                Integer integer = itemPositionMap.get(path);
                linearLayoutManager.scrollToPosition(integer!=null ? integer : 0);
            }
        });
    }

    /**
     * 文件列表项的点击事件
     * @param file 选中的文件
     */
    private void onFileClickEvent(File file) {
        // 如果是文件夹，则显示该路径下的文件列表，如果是文件，则执行插件
        if (file.isDirectory()) {
            updateFileList(file.getAbsolutePath());
        } else {
            // 执行插件
            pluginExecutor.execute(pluginsId, file);
        }
    }

    /**
     * 更新文件列表
     */
    private void updateFileList(String path) {
        fileList.clear();
        // 获取文件列表
        File file = new File(path);
        File[] files = file.listFiles();

        currPathFile = file;

        if (files != null)
            fileList.addAll(Arrays.asList(files));

        // 排序
        FileUtils.sort(fileList);

        // 添加父级目录
        if (file.getAbsolutePath().equals(SDCardRootPath)) {
            fileList.add(0, file);
        } else {
            fileList.add(0, file.getParentFile());
        }

        filePath.setText(currPathFile.getPath());
        fileListAdapter.updateData(fileList);
    }

    /**
     * 返回键的事件
     */
    @Override
    public void onBackPressed() {
        // 如果当前目录与根目录一致，则退出，否则返回上一级目录
        if (!currPathFile.getPath().equals(SDCardRootPath)){
            File parentFile = currPathFile.getParentFile();
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