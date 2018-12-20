package com.onval.capstone.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.onval.capstone.R;

public class GuiUtility {
    public final static int DARK_BG = Color.parseColor("#2a2a2a");

    @SuppressLint("DefaultLocale")
    public static String timeFormatFromMills(long millis, boolean forceLongFormat) {
        int seconds = (int) (millis / 1000);

        int hh = seconds / 3600;
        int mm = seconds / 60 % 60;
        int ss = seconds % 60;

        if (!forceLongFormat && hh == 0)
            return String.format("%02d:%02d", mm, ss);

        return String.format("%02d:%02d:%02d", hh, mm, ss);
    }

    public static void initCustomTheme(Context context) {
        context.setTheme(getTheme(context).equals("Light") ? R.style.LightTheme : R.style.DarkTheme);
    }

    public static String getTheme(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString("pref_theme", "Light");
    }

    public static boolean isLightTheme(Context context) {
        return getTheme(context).equals("Light");
    }
}
