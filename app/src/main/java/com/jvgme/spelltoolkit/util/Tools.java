package com.jvgme.spelltoolkit.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jvgme.spelltoolkit.R;
import com.jvgme.spelltoolkit.core.PluginManager;
import com.jvgme.spelltoolkit.core.android.PluginLoaderImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * 全局工具类
 */
public class Tools {
    private static final String TAG = "Cansor";
    // 日志开关，可以调试的时候设为true，正式发布时设为false
    private static final boolean isPrintLog = true;

    private static PluginManager pluginManager;

    /**
     * 获取SD卡根目录（如 /storage/emulated/0），如果是 Android 10 以上，
     * 则返回应用目录（如 /storage/emulated/0/Android/data/包名/files）。
     *
     * @param type 子目录，不允许为null，不指定子目录请传空字符串 ""
     */
    public static String getExternalStorageDirectory(@NonNull Context context, @NonNull String type) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
                return context.getExternalFilesDir(type).getAbsolutePath();
            else
                return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();
        }

        logInfo("获取不到外部路径！");
        return null;
    }

    /**
     * 获得一个插件管理器的实例
     */
    public static PluginManager getPluginManager(@NonNull Context context) {
        final String path = getExternalStorageDirectory(context, PluginManager.PLUGIN_PATH);
        if (path == null) return null;

        if (pluginManager == null) {
            // 插件所在的路径，如果不存在则创建
            File pluginsPath = new File(path);
            if (!pluginsPath.exists()) {
                // 如果创建失败则弹出 Toast 提示
                if (!pluginsPath.mkdirs()) {
                    Toast.makeText(context, R.string.mkdirs_failure + pluginsPath.getPath(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            pluginManager = PluginManager.getPluginsManager(
                    new PluginLoaderImpl(pluginsPath.getAbsolutePath(), context));
        }

        return pluginManager;
    }

    /**
     * 根据文件名获取 assets 目录的文本文件的内容
     */
    public static String getAssetsText(Context context, String fileName) {
        AssetManager assets = context.getAssets();
        StringBuilder sb = new StringBuilder();

        try (InputStream in = assets.open(fileName)) {
            byte[] bytes = new byte[2048];
            int len;
            while ((len = in.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return handlerPlaceholder(context, sb.toString());
    }

    /**
     * 替换文本中的占位符
     */
    public static String handlerPlaceholder(Context context, String text) {
        final String version = "@version";
        final String year = "@year";

        if (text.contains(version)) {
            try {
                // 获取apk版本号
                text = text.replace(version, context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0)
                        .versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (text.contains(year))
            text = text.replace(year, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        return text;
    }

    /**
     * 在控制台中打印日志
     *
     * @param msg 日志信息
     */
    public static void logInfo(String msg) {
        if (isPrintLog) Log.i(TAG, msg);
    }
}
