package com.jvgme.spelltoolkit.core;

import android.graphics.drawable.Icon;

/**
 * 插件的实体类
 */
public class Plugin {
    private String id;
    private String name;
    private String author;
    private String version;
    private String description;
    private Icon icon;
    private Class<?> mainClass;

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<?> getMainClass() {
        return mainClass;
    }

    public void setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
    }
}
