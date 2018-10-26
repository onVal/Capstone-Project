package com.onval.capstone.utility;

import android.annotation.SuppressLint;
import android.graphics.Color;

public class UserInterfaceUtility {
    public static int darkenColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }

    @SuppressLint("DefaultLocale")
    public static String timeFormatFromMills(long millis, boolean forceLong) {
        int seconds = (int) (millis / 1000);

        int hh = seconds / 3600;
        int mm = seconds / 60 % 60;
        int ss = seconds % 60;

        if (!forceLong && hh == 0)
            return String.format("%02d:%02d", mm, ss);

        return String.format("%02d:%02d:%02d", hh, mm, ss);
    }
}
