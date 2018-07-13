package abhishek.redvelvet.com.perfect_call_manager.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import abhishek.redvelvet.com.perfect_call_manager.fragment.RecentFragment;

/**
 * Created by abhishek on 1/6/18.
 */

public class RecentContacts {

    Cursor cursor_r_contacts;
    Context context;
    ArrayList al_name = new ArrayList(),al_number= new ArrayList(),al_time = new ArrayList();

    public RecentContacts(Context context){
        this.context = context;
    }

    public ArrayList loadCallLog() {
        ArrayList al = new ArrayList();
        //get the recent call log
        try {
            ContentResolver contentResolver = context.getContentResolver();

            ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);

            cursor_r_contacts = contentResolver.query(CallLog.Calls.CONTENT_URI,
                    null,
                    null,
                    null,
                    CallLog.Calls.DATE+" DESC");

        } catch (Exception e) {
            Log.e("e",e+"");
        }

        if (cursor_r_contacts != null) {
            if (cursor_r_contacts.getCount() > 0) {
                if (cursor_r_contacts.moveToFirst()) {
                    do {
                        HashMap hm = new HashMap();

                        String displayName = cursor_r_contacts.getString(cursor_r_contacts.getColumnIndex(CallLog.Calls.CACHED_NAME));
                        String number = cursor_r_contacts.getString(cursor_r_contacts.getColumnIndex(CallLog.Calls.NUMBER));
                        String time = cursor_r_contacts.getString(cursor_r_contacts.getColumnIndex(CallLog.Calls.DATE));

                        if (displayName != null)
                            hm.put(RecentFragment.keys[0],displayName);
                        else
                            hm.put(RecentFragment.keys[0],number);



                        //--------------convert millisec time-----------------------
                        Calendar cl = Calendar.getInstance();
                        cl.setTimeInMillis(Long.parseLong(time));  //here your time in miliseconds
                        String date = "" + cl.get(Calendar.DAY_OF_MONTH) + "/" + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.YEAR);
                        String f_time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) ;
                        //----------------------------------------------------------

                        hm.put(RecentFragment.keys[1],date+" "+f_time);
                        al.add(hm);

                    } while (cursor_r_contacts.moveToNext());
                }
            } else {
                Toast.makeText(context, "empty count 0", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "empty cursor", Toast.LENGTH_SHORT).show();
        }

        return al;
    }

    public ArrayList loadCallLogNumber(){

        return al_number;
    }

    public ArrayList loadCallLogTime(){
        return al_time;
    }
}