package com.jvgme.spelltoolkit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    private File currFile; // 记录当前路径
    private TextView filePath; // 用于在操作栏上显示路径

    private PluginExecutor pluginExecutor;
    private LogWindow logWindow;

    private final Handler handler = new Handler();
    private long downTime;
    private long upTime;
    // 最大长按时间
    private final int MAX_LONG_PRESS_TIME = 500;
    // 最大点击范围，超出则算移动
    private final int MAX_CLICK_RANGE = 80;
    // 手指触摸的坐标
    private int startX, startY, lastX, lastY;
    // 每层目录 item 滚动的坐标
    private final Map<String, Integer> itemPositionMap = new HashMap<>();
    // 记录文件夹的名称
    private String lastPath;

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
            updateFileList(currFile.getAbsolutePath());
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

        // 添加滚动监听器
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当 RecyclerView 滚动结束时，记录滚动位置
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (currFile != null) {
                        int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                        itemPositionMap.put(currFile.getAbsolutePath(), position);
                    }
                }
            }
        });

        // 添加更新数据监听器
        fileListAdapter.setOnUpdatedListener(() -> {
            // 数据更新结束时滚动到记录的位置
            if (currFile != null) {
                // 在返回父目录时移除子目录的记录
                String path = currFile.getAbsolutePath();
                itemPositionMap.remove(path + "/" + this.lastPath);
                this.lastPath = currFile.getName();

                Integer integer = itemPositionMap.get(path);
                linearLayoutManager.scrollToPosition(integer!=null ? integer : 0);
            }
        });

        // 添加触摸监听器
        fileListAdapter.setOnTouchListener((view, file, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 记录手指触摸的位置与时间
                    startX = (int) motionEvent.getX();
                    startY = (int) motionEvent.getY();
                    downTime = motionEvent.getDownTime();
                    // 如果触模时间达到 MAX_LONG_PRESS_TIME 则调用长按事件
                    /*handler.postDelayed(() -> pluginItemOnLongClickEvent(view, plugin),
                            MAX_LONG_PRESS_TIME);*/
                    break;

                case MotionEvent.ACTION_MOVE:
                    // 记录手指触摸的位置
                    lastX = (int) motionEvent.getX();
                    lastY = (int) motionEvent.getY();
                    // 如果手指移动超出指定范围，则取消长按事件
                    if (Math.abs(lastX-startX) > MAX_CLICK_RANGE
                            || Math.abs(lastY-startY) > MAX_CLICK_RANGE) {
                        handler.removeCallbacksAndMessages(null);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    // 获取手指抬起的时间
                    upTime = motionEvent.getEventTime();
                    // 如果手指触摸屏幕的时间小于 MAX_LONG_PRESS_TIME 则视为点击，取消长按并调用点击事件
                    if (upTime-downTime < MAX_LONG_PRESS_TIME) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {
                            fileOnClickEvent(file);
                            view.performClick();
                        }, 200);

                    }
                    break;
            }

            // 反回 false, 事件将会传递下去
            return false;
        });
    }

    /**
     * 文件列表项的点击事件
     * @param file 选中的文件
     */
    private void fileOnClickEvent(File file) {
        // 如果是文件夹，则显示该路径下的文件列表，如果是文件，则执行插件
        if (file.isDirectory()) {
//            handler.postDelayed(() -> updateFileList(file.getAbsolutePath()), 70);
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