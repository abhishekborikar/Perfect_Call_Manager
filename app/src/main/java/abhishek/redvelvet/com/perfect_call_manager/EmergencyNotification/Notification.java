package abhishek.redvelvet.com.perfect_call_manager.EmergencyNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import abhishek.redvelvet.com.perfect_call_manager.R;
import abhishek.redvelvet.com.perfect_call_manager.contacts.LoadAllContacts;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by abhishek on 4/6/18.
 */

public class Notification {

    Context context;
    private static final int uniqueID = 3543;
    final static int requestCode = 91;

    public Notification(Context context){
        this.context = context;
    }

    public void makeEmgyNotification() {
        final String CHANNEL_ID = "button_click_channel";

        //get the number and name
        SharedPreferences preferences = context.getSharedPreferences(EmergencyActivity.EM_CNT, MODE_PRIVATE);
        String[] nn  = new String[]{
            preferences.getString(LoadAllContacts.keys[0], "Empty"),
            preferences.getString(LoadAllContacts.keys[1], "Empty")
        };

        if(!nn[0].contains("Empty") && !nn[1].contains("Empty")) {

            //use for final notify
            NotificationManager notificationManager
                    = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            //---------start procudure for the notification button click listener----
            //set the custom view with remote view
            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
            contentView.setTextViewText(R.id.notification_tv_name, nn[0]);
            contentView.setTextViewText(R.id.notification_tv_number, nn[1]);

            //set notification button listener
            //step 1 -> create intent(String)
            //same string "button_click" is used to register in the manifest
            Intent intent = new Intent("button_click");
            intent.putExtra("number", nn[1]);
            intent.putExtra("id", uniqueID);

            //step 2 -> create pending intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //step 3 -> set pending button intent
            contentView.setOnClickPendingIntent(R.id.notification_iv, pendingIntent);

            //--------------Notification button click listner is set------------

            //--------------set contentView onclickListener-----------------

            //build the notification
            //create builder and set basic component
            android.app.Notification.Builder mBuilder =
                    new android.app.Notification.Builder(context)
                            .setSmallIcon(R.drawable.ic_call_white_24dp)
                            .setAutoCancel(false)
                            .setOngoing(true)
                            .setContent(contentView)
                            .setPriority(android.app.Notification.PRIORITY_HIGH);

            //set the TaskBuilder
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(EmergencyActivity.class);
            stackBuilder.addNextIntent(new Intent(context, EmergencyActivity.class));


            //notifiy
            notificationManager.notify(uniqueID, mBuilder.build());
        }
    }

    public void destoryNotification() {
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(uniqueID);


    }
}
