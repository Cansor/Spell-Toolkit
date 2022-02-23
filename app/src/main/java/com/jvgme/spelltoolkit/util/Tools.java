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

/**
 * 全局工具类
 */
public class Tools {
    private static final String TAG = "Cantor";
    // 日志开关，可以调试的时候设为true，正式发布时设为false
    private static final boolean isPrintLog = true;

    private static PluginManager pluginManager;

    /**
     * 获取SD卡根目录所在路径（如 /storage/emulated/0），如果是 Android 10 以上，
     * 则返回应用目录所在路径（如 /storage/emulated/0/Android/data/包名/files）。
     *
     * @param type 子目录，不允许为null，不指定子目录请传空字符串 ""
     *
     * @return SD卡根目录+子目录 的路径
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
     *
     * @return PluginManager
     */
    public static PluginManager getPluginManager(@NonNull Context context) {
        if (pluginManager == null) {
            final String path = getExternalStorageDirectory(context, PluginLoaderImpl.PLUGIN_PATH);
            if (path == null) return null;

            // 插件所在的路径，如果不存在则创建
            File pluginPath = new File(path);
            if (!pluginPath.exists()) {
                // 如果创建失败则弹出 Toast 提示
                if (!pluginPath.mkdirs()) {
                    Toast.makeText(context, R.string.mkdirs_failure + pluginPath.getPath(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            pluginManager = PluginManager.instance(
                    new PluginLoaderImpl(pluginPath.getAbsolutePath(), context));
        }

        return pluginManager;
    }

    /**
     * 根据文件名获取 assets 目录的文本文件的内容
     *
     * @return 文本内容
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
     * 把 @version 替换为 app 版本
     *
     * @return 替换后的文本
     */
    private static String handlerPlaceholder(Context context, String text) {
        final String version = "@version";
//        final String year = "@year";
        final String pluginPath = "@pluginPath";

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
//        if (text.contains(year))
//            text = text.replace(year, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        if (text.contains(pluginPath))
            text = text.replace(pluginPath, getExternalStorageDirectory(context,
                    PluginLoaderImpl.PLUGIN_PATH));

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
