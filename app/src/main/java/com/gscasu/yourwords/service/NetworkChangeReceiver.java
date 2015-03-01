package com.gscasu.yourwords.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.gscasu.yourwords.DetailActivity;
import com.gscasu.yourwords.MainActivity;
import com.gscasu.yourwords.R;
import com.gscasu.yourwords.data.WordsContract;

/*
This broadcast receiver queries all words queries while the phone was offline
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    private final String LOG_TAG = NetworkChangeReceiver.class.getSimpleName();
    public static final int WORDS_NOTIFICATION_ID = 3010;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "fired");
        if(isConnected(context)){
            Log.v(LOG_TAG, "connected");
            Cursor cursor = context.getContentResolver().query(
                    WordsContract.OfflineEntry.CONTENT_URI,
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );

            StringBuilder notificationTextBuilder = new StringBuilder();
            notificationTextBuilder.append("Checkout your offline words got received:");

            while (cursor.moveToNext()) {
                String word = cursor.getString(1);
                Log.v(LOG_TAG, word);
                Intent serviceIntent = new Intent(context, WordsService.class);
                serviceIntent.putExtra(DetailActivity.HEADWORD_KEY, word);
                context.startService(serviceIntent);
                notificationTextBuilder.append(" " + word);
            }
            String notificationText = notificationTextBuilder.toString();
            context.getContentResolver().delete(WordsContract.OfflineEntry.CONTENT_URI, null, null);

            if(cursor.moveToFirst()) {
                Intent openAppIntent = new Intent(context, MainActivity.class);
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(openAppIntent);
                PendingIntent pendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(notificationText)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(WORDS_NOTIFICATION_ID, builder.build());
            }
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}