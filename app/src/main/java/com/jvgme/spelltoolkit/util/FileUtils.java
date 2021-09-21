package com.jvgme.spelltoolkit.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 关于File的一个辅助工具类
 */
public class FileUtils {

    /**
     * 把时间戳转换为 yyyy-MM-dd HH:mm 时间
     *
     * @param stamp 时间戳
     * @return 格式化后的String类型的时间
     */
    public static String stampToTime(long stamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        // 将时间戳转换为时间
        Date date = new Date(stamp);
        // 格式化时间
        return simpleDateFormat.format(date);
    }

    /**
     * 转换文件大小为 B K M G
     *
     * @param size 文件的字节大小
     * @return 格式化后的大小
     */
    public static String fileSizeFormat(long size){
        if(size>=1073741824){
            return size / 1073741824 +"G";
        }else if(size>=1048576){
            return size / 1048576 +"M";
        }else if(size>=1024){
            return size / 1024 +"K";
        }else
            return size+"B";
    }

    /**
     * 对文件列表按文件名称排序
     */
    public static void sort(List<File> list) {
        Collections.sort(list, (f1, f2) -> {
            if (f1.isDirectory() && f2.isFile())
                return -1;
            if (f1.isFile() && f2.isDirectory())
                return 1;
            return f1.getName().compareTo(f2.getName());
        });
    }


}
