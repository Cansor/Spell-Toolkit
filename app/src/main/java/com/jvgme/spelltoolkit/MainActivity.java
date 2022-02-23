package com.jvgme.spelltoolkit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jvgme.spelltoolkit.adapter.PluginListAdapter;
import com.jvgme.spelltoolkit.core.Plugin;
import com.jvgme.spelltoolkit.core.PluginManager;
import com.jvgme.spelltoolkit.listener.OnRecyclerViewItemTouchListener;
import com.jvgme.spelltoolkit.util.Tools;

import java.util.List;

/**
 * app 启动的主界面
 */
public class MainActivity extends BaseActivity {
    private AlertDialog.Builder exitAppAlertDialog;
    private RecyclerView recyclerView;
    private PluginListAdapter pluginListAdapter;
    private TextView tv_notFindPlugin;

    private final Handler handler = new Handler();

    private List<Plugin> pluginList;
    /*private long downTime;
    private long upTime;

    // 最大长按时间
    private final int MAX_LONG_PRESS_TIME = 500;
    // 最大点击范围，超出则算移动
    private final int MAX_CLICK_RANGE = 80;
    // 手指触摸的坐标
    private int startX, startY, lastX, lastY;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isStoragePermissionGranted())
            initializeView();
    }

    @Override
    protected int setContentViewLayout() {
        return R.layout.activity_main;
    }

    /**
     * 初始化视图
     */
    private void initializeView() {
        // 退出App时弹出的对话框
        exitAppAlertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.exit_app)
                .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.no, null);

        // 未找到插件时的文字
        tv_notFindPlugin = findViewById(R.id.tv_not_find_plugin);

        // 获取插件管理器
        PluginManager pluginManager = Tools.getPluginManager(this);
        if (pluginManager == null) return;
        // 加载插件
        pluginManager.load();
        pluginList = pluginManager.getAllPlugins();

        // 创建插件列表视图
        recyclerView = findViewById(R.id.rv_plugin_list);
        // 添加布局，线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加适配器
        pluginListAdapter = new PluginListAdapter(pluginList,this);
        recyclerView.setAdapter(pluginListAdapter);

        recyclerView.addOnItemTouchListener(new OnRecyclerViewItemTouchListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                handler.postDelayed(() -> pluginItemOnClickEvent(pluginList.get(position)), 150);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh, int position) {
                pluginItemOnLongClickEvent(vh.itemView, pluginList.get(position));
            }
        });

        // 如果加载不到插件则出现提示
        if (pluginManager.getPluginQuantity() < 1) {
            recyclerView.setVisibility(View.GONE);
            tv_notFindPlugin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 插件项的长按事件；弹出一个菜单。
     */
    private void pluginItemOnLongClickEvent(View view, Plugin plugin) {
        // 创建弹出菜单
        PopupMenu popupMenu = new PopupMenu(this, view);
        // 填充菜单
//        popupMenu.getMenuInflater().inflate(R.menu.plugin_item_menu, popupMenu.getMenu());
        popupMenu.inflate(R.menu.plugin_item_menu);
        // 菜单项的点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_plugin_description) {
                gotoInfoActivity(getResources().getString(R.string.plugin_description), plugin.getDescription());
            }
            return true;
        });
        // 显示菜单
        popupMenu.show();
    }

    /**
     * 插件项的点击事件；进入文件浏览。
     */
    private void pluginItemOnClickEvent(Plugin plugin) {
        // 创建意图，转到 FileListActivity
        Intent intent = new Intent(this, FileListActivity.class);
        // 把插件ID传过去
        intent.putExtra("pluginId", plugin.getId());
        // 跳转到Activity
        startActivity(intent);
    }

    /**
     * 在操作栏上创建菜单选项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    /**
     * 操作栏菜单项点击事件
     *
     * @param item 菜单项
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.file_list_menu_refresh) {
            PluginManager pluginManager = Tools.getPluginManager(this);
            if (pluginManager != null) {
                int reload = pluginManager.load();
                pluginListAdapter.updateData(pluginManager.getAllPlugins());
                if (reload > 0) {
                    tv_notFindPlugin.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tv_notFindPlugin.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (itemId == R.id.menu_about) {
            gotoInfoActivity(getResources().getString(R.string.main_about),
                    Tools.getAssetsText(this, "about.html"));
        }
        else if (itemId == R.id.menu_specification) {
            gotoInfoActivity(getResources().getString(R.string.main_specification),
                    Tools.getAssetsText(this, "specification.html"));
        }
        else if (itemId == R.id.menu_disclaimer){
            gotoInfoActivity(getResources().getString(R.string.main_disclaimer),
                    Tools.getAssetsText(this, "disclaimer.html"));
        }
        else if (itemId == R.id.menu_exit) {
            finish();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // 跳转到 InfoActivity
    private void gotoInfoActivity(String title, String info) {
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("info", info);
        startActivity(intent);
    }

    /**
     * 手机返回键的事件；弹出一个询问是否退出App的对话框。
     */
    @Override
    public void onBackPressed() {
        exitAppAlertDialog.show();
    }

    /**
     * 检查外部存储的读写权限，没有权限则进行动态申请
     *
     * @return 有权返回true，无权返回false.
     * 注意，返回的是申请前的状态
     */
    public boolean isStoragePermissionGranted() {
        final Context context = getApplicationContext();
        int readPermissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (readPermissionCheck == PackageManager.PERMISSION_GRANTED
                && writePermissionCheck == PackageManager.PERMISSION_GRANTED) {
            Tools.logInfo("已授予外部存储读写权限！");
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Tools.logInfo("未授予外部存储读写权限！");
            Tools.logInfo("申请外部存储读写权限……");
            return false;
        }
    }

    /**
     * 申请权限后回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 懒得判断是否申请成功了，如果用户拒绝了……那就随便发生什么奇怪的事吧。。。
        initializeView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 防止 Handler 的内存泄漏
        handler.removeCallbacksAndMessages(null);
    }
}