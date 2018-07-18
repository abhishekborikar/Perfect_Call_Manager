package abhishek.redvelvet.com.perfect_call_manager.EmergencyNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by abhishek on 13/7/18.
 */

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String status = new EmergencyActivity().getEmgyPrefrence();

        if(!status.contains("Empty")){
            new Notification(context).makeEmgyNotification();
        }
        else {
            new Notification(context).destoryNotification();
        }
    }
}
