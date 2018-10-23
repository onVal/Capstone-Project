package com.onval.capstone;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.onval.capstone.service.PlayerService;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_NAME;

/**
 * Implementation of App Widget functionality.
 */
public class PlayerAppWidget extends AppWidgetProvider {
    public final static String WIDGET_MANUAL_UPDATE = "com.onval.capstone.APPWIDGET_MANUAL_UPDATE";

    private final static String NO_RECORDING_SELECTED = "No recording playing";
    private final static String NO_CATEGORY_SELECTED = "from nowhere";
    private final static String DEFAULT_REC_DURATION = "00:00:00";

    private static String categoryName;
    private static String categoryColor;
    private static String recName;
    private static String recDuration;
    private static boolean status;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.player_app_widget);
//
        if (!PlayerService.isRunning) {
            views.setTextViewText(R.id.playing_rec, NO_RECORDING_SELECTED);
            views.setTextViewText(R.id.from_category, NO_CATEGORY_SELECTED);
            views.setTextViewText(R.id.rec_duration, DEFAULT_REC_DURATION);
            views.setInt(R.id.cat_color, "setBackgroundColor", Color.parseColor("#777777"));
            views.setImageViewResource(R.id.play_pause, R.drawable.ic_play_white_24dp);

        } else {
            views.setTextViewText(R.id.playing_rec, recName);
            views.setTextViewText(R.id.from_category, "from " + categoryName);
            views.setTextViewText(R.id.rec_duration, recDuration);
            views.setInt(R.id.cat_color, "setBackgroundColor", Color.parseColor(categoryColor));
            views.setImageViewResource(R.id.play_pause, (status) ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_white_24dp);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction() != null &&
                intent.getAction().equals(WIDGET_MANUAL_UPDATE)) {
            Bundle extras = intent.getExtras();

            if (extras != null) {
                categoryName = extras.getString(CATEGORY_NAME);
                categoryColor = extras.getString("cat-color");
                recName = extras.getString("rec-name");
                recDuration = extras.getString("rec-duration");
                status = extras.getBoolean("status");
            }
        }

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(
                new ComponentName(context, PlayerAppWidget.class));

        onUpdate(context, widgetManager, ids);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

