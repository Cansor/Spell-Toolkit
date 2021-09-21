package com.jvgme.spelltoolkit.core.android;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;


import com.jvgme.spelltoolkit.core.Plugin;
import com.jvgme.spelltoolkit.core.PluginLoader;
import com.jvgme.spelltoolkit.util.Tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import dalvik.system.DexClassLoader;

/**
 * 插件加载器
 */
public class PluginLoaderImpl implements PluginLoader {
    // 插件所在路径
    private final String path;

    private final Context context;

    public PluginLoaderImpl(String path, Context context) {
        this.path = path;
        this.context = context;
    }

    @Override
    public Map<String, Plugin> load() throws IOException, ClassNotFoundException {
        // 读取指定目录下所有jar文件
        List<File> jarFileList = getJarFile();

        Map<String, Plugin> pluginsMap = new HashMap<>();

        // 读取每一个jar，载入插件
        for (File jarFile : jarFileList) {
            Plugin plugin = getPlugins(jarFile);
            pluginsMap.put(plugin.getId(), plugin);
        }

        return pluginsMap;
    }

    /**
     * 解析jar插件包，获取PluginsBean插件对象
     *
     * @param jFile jar插件包对象
     */
    private Plugin getPlugins(File jFile) throws IOException, ClassNotFoundException {

        JarFile jarFile = new JarFile(jFile);
        // 获取清单文件
        Attributes attributes = jarFile.getManifest().getMainAttributes();

        Plugin plugin = new Plugin();

        // 添加相关信息
        plugin.setId(attributes.getValue("Spell-Id"));
        plugin.setName(attributes.getValue("Spell-Name"));
        plugin.setAuthor(attributes.getValue("Spell-Author"));
        plugin.setVersion(attributes.getValue("Spell-Version"));

        // 载入主类
        String jar = context.getDir("Jar", 0).getAbsolutePath();
        DexClassLoader dexClassLoader = new DexClassLoader(jFile.getAbsolutePath(), jar, null,
                getClass().getClassLoader());
        Class<?> main = dexClassLoader.loadClass(attributes.getValue("Spell-Class"));
        plugin.setMainClass(main);

        // 载入icon
        JarEntry entryIcon = jarFile.getJarEntry(attributes.getValue("Spell-Icon"));
        if (entryIcon != null) {
            InputStream in = jarFile.getInputStream(entryIcon);
            Icon icon = Icon.createWithBitmap(BitmapFactory.decodeStream(in));
            plugin.setIcon(icon);
            in.close();
        }

        // 载入描述文档
        JarEntry entryDescription = jarFile.getJarEntry(attributes.getValue("Spell-Description"));
        if (entryDescription != null) {
            InputStream in = jarFile.getInputStream(entryDescription);
            byte[] bytes = new byte[(int) entryDescription.getSize()];
            in.read(bytes);
            in.close();
            handlerPlaceholder(plugin, new String(bytes));
        }

        jarFile.close();

        return plugin;
    }

    // 处理占位符
    private void handlerPlaceholder(Plugin plugin, String text) {
        final String ver = "@version";
        final String aut = "@author";

        if (text.contains(ver))
            text = text.replace(ver, plugin.getVersion());
        if (text.contains(aut))
            text = text.replace(aut, plugin.getAuthor());

        plugin.setDescription(text);
    }

    /**
     * 读取指定目录下所有jar文件
     *
     * @return 指定目录下的所有jar文件
     */
    private List<File> getJarFile() {
        List<File> jarList = new ArrayList<>();

        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            Tools.logInfo("路径不存在！ in " + file.getAbsolutePath());
            return jarList;
        }

        // 获取路径下的所有文件
        File[] files = file.listFiles();
        if (files == null) return jarList;

        // 把jar文件筛选出来放到List里
        for (File f : files) {
            if (f.getName().endsWith(".jar"))
                jarList.add(f);
        }

        return jarList;
    }
}
