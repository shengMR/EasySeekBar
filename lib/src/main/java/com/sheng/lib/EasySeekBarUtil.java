package com.sheng.lib;

import android.content.Context;
import android.os.Environment;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EasySeekBarUtil {

    public static int getScreenState(Context context) {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }else{
            return 0;
        }
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static String formatFloat(float value) {
        String format = String.format("%.1f", value);
        return format;
    }

    public static String formatInt(float value) {
        String format = (int) value + "";
        return format;
    }


    // 检查手机类型
    private static final File BUILD_PROP_FILE = new File(Environment.getRootDirectory(), "build.prop");
    private static Properties sBuildProperties;
    private static final Object sBuildPropertiesLock = new Object();

    private static Properties getBuildProperties() {
        synchronized (sBuildPropertiesLock) {
            if (sBuildProperties == null) {
                sBuildProperties = new Properties();
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(BUILD_PROP_FILE);
                    sBuildProperties.load(fis);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return sBuildProperties;
    }

    static boolean isMIUI() {
        return getBuildProperties().containsKey("ro.miui.ui.version.name");
    }
}
