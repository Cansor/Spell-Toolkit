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
import java.util.Locale;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;

/**
 * 插件加载器
 */
public class PluginLoaderImpl implements PluginLoader {
    // 插件目录
    public static final String PLUGIN_PATH =  "/SpellToolkit/Plugins";

    // 插件所在路径
    private final String path;
    private final Context context;

    public PluginLoaderImpl(String path, Context context) {
        this.path = path;
        this.context = context;
    }

    @Override
    public Map<String, Plugin> load() {
        // 读取指定目录下所有jar文件
        List<File> jarFileList = getJarFile();
        if (jarFileList == null) return null;

        Map<String, Plugin> pluginMap = new HashMap<>();

        try {
            // 读取每一个jar，载入插件
            for (File jarFile : jarFileList) {
                Plugin plugin = getPlugin(jarFile);
                pluginMap.put(plugin.getId(), plugin);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return pluginMap;
    }

    /**
     * 解析jar插件包，获取 Plugin 插件对象
     *
     * @param jFile jar插件包的 File 对象
     * @return Plugin
     */
    private Plugin getPlugin(File jFile) throws IOException, ClassNotFoundException {

        JarFile jarFile = new JarFile(jFile);
        // 获取清单文件中定义的属性
        Attributes attributes = jarFile.getManifest().getMainAttributes();

        Plugin plugin = new Plugin();

        // 添加相关信息
        plugin.setId(attributes.getValue("Spell-Id"));
        plugin.setName(attributes.getValue("Spell-Name"));
        plugin.setAuthor(attributes.getValue("Spell-Author"));
        plugin.setVersion(attributes.getValue("Spell-Version"));

        // 载入插件入口类
        String jarDir = context.getDir("Jar", 0).getAbsolutePath();
        DexClassLoader dexClassLoader = new DexClassLoader(jFile.getAbsolutePath(), jarDir, null,
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
            int read = in.read(bytes);
            Tools.logInfo("Read plugin description, bytes:" + read);
            in.close();
            handlerPlaceholder(plugin, new String(bytes));
        }

        jarFile.close();

        return plugin;
    }

    /**
     * 处理插件描述中的占位符
     * 把 @version 替换成 插件版本
     * 把 @author 替换成 插件作者
     *
     * @param plugin Plugin
     * @param text 插件描述文本
     */
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
            Tools.logInfo("getJarFile -> 路径不存在！" + file.getAbsolutePath());
            return null;
        }

        // 获取路径下的所有文件
        File[] files = file.listFiles();
        if (files == null) return null;

        // 把jar文件筛选出来放到List里
        for (File f : files) {
            String lastName = f.getName().toLowerCase(Locale.ROOT);
            if (lastName.endsWith(".stp"))
                jarList.add(f);
        }

        return jarList;
    }
}
