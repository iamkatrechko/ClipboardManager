package com.iamkatrechko.clipboardmanager.view;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor;
import com.iamkatrechko.clipboardmanager.view.activity.ClipEditActivity;
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.domain.services.ClipboardService;
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils;
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences;

/**
 * @author ivanov_m
 *         Date: 23.08.17
 */
public class RemoteViewCreator {

    public static RemoteViews createHistoryRemoteView(Context context) {
        boolean showOnlyFavorite = UtilPreferences.isShowOnlyFavoriteInNotification(context);

        RemoteViews generalRemoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        String currentClipText = ClipUtils.getClipboardText(context);

        generalRemoteViews.setTextViewText(R.id.tvCurrent, "> " + currentClipText);

        Intent intentAdd = new Intent(context, ClipEditActivity.class);
        intentAdd.setAction("ACTION_ADD");
        PendingIntent pIntentAdd = PendingIntent.getActivity(context, 612452, intentAdd, 0);
        generalRemoteViews.setOnClickPendingIntent(R.id.button_add, pIntentAdd);

        Intent intentFavorite = new Intent(context, ClipboardService.class);
        intentFavorite.setAction(ClipboardService.ACTION_SHOW_ONLY_FAVORITE);
        PendingIntent pIntentFavorite = PendingIntent.getService(context, 171251, intentFavorite, 0);
        generalRemoteViews.setOnClickPendingIntent(R.id.image_view_star, pIntentFavorite);

        ClipCursor lastRecords;
        if (showOnlyFavorite) {
            generalRemoteViews.setImageViewResource(R.id.image_view_star, R.drawable.ic_star);
            generalRemoteViews.setInt(R.id.image_view_star, "setColorFilter", Color.parseColor("#009688"));

            lastRecords = new ClipCursor(context.getContentResolver().query(DatabaseDescription.Clip.CONTENT_URI,
                    null,
                    DatabaseDescription.Clip.COLUMN_CONTENT + " <> ? AND " + DatabaseDescription.Clip.COLUMN_IS_FAVORITE + " = ?",
                    new String[]{currentClipText, "1"},
                    DatabaseDescription.Clip.COLUMN_DATE + " LIMIT 4"));
        } else {
            generalRemoteViews.setImageViewResource(R.id.image_view_star, R.drawable.ic_star_border);
            generalRemoteViews.setInt(R.id.image_view_star, "setColorFilter", Color.parseColor("#808080"));

            lastRecords = new ClipCursor(context.getContentResolver().query(DatabaseDescription.Clip.CONTENT_URI,
                    null,
                    DatabaseDescription.Clip.COLUMN_CONTENT + " <> ?",
                    new String[]{currentClipText},
                    DatabaseDescription.Clip.COLUMN_DATE + " LIMIT 4"));
        }

        generalRemoteViews.removeAllViews(R.id.linear_clips);

        for (int i = 0; i < lastRecords.getCount(); i++) {
            lastRecords.moveToPosition(i);
            long id = lastRecords.getID();
            String title = lastRecords.getTitle();
            RemoteViews clipRemoteViews = createClipListItem(context, id, title);
            if (i == 0) {
                clipRemoteViews.setViewVisibility(R.id.flSeparator, View.GONE);
            }
            generalRemoteViews.addView(R.id.linear_clips, clipRemoteViews);
        }
        return generalRemoteViews;
    }


    private static RemoteViews createClipListItem(Context context, long id, String text) {
        RemoteViews clipRemoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification_list_item);
        clipRemoteViews.setTextViewText(R.id.tvTitle, text);

        Intent serviceCopy = new Intent(context, ClipboardService.class);
        serviceCopy.setAction(ClipboardService.ACTION_COPY_TO_CLIPBOARD);
        serviceCopy.putExtra("id", id);
        PendingIntent pIntentCopy = PendingIntent.getService(context, (int) id, serviceCopy, 0);
        clipRemoteViews.setOnClickPendingIntent(R.id.ivCopy, pIntentCopy);

        return clipRemoteViews;
    }
}
