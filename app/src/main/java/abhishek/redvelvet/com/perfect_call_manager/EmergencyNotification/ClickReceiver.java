package abhishek.redvelvet.com.perfect_call_manager.EmergencyNotification;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by abhishek on 4/6/18.
 */

public class ClickReceiver extends BroadcastReceiver {

    public static final int REQUEST_CALL_PHONE = 1999;

    public ClickReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        int checkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    activity,
//                    new String[]{Manifest.permission.CALL_PHONE},
//                    REQUEST_CALL_PHONE);
        }

        String number = intent.getStringExtra("number");
        Intent intent1 = new Intent(Intent.ACTION_CALL);
        intent1.setData(Uri.parse("tel:" + number));
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
