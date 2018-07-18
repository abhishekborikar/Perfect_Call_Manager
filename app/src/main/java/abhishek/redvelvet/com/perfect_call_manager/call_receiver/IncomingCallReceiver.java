package abhishek.redvelvet.com.perfect_call_manager.call_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

import abhishek.redvelvet.com.android.Internal.telephony.ITelephony;
import abhishek.redvelvet.com.perfect_call_manager.CustomRealmMigration;
import abhishek.redvelvet.com.perfect_call_manager.call_setting.CallSetting;
import abhishek.redvelvet.com.perfect_call_manager.database.BlockedContactsData;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static abhishek.redvelvet.com.perfect_call_manager.call_setting.CallSetting.TEL_BLK_STATUS;
import static abhishek.redvelvet.com.perfect_call_manager.call_setting.CallSetting.key;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by abhishek on 18/7/18.
 */

public class IncomingCallReceiver extends BroadcastReceiver {
    private Cursor cursor = null;
    private AudioManager audioManager = null;
    String contactNumber = null;


    @Override
    public void onReceive(final Context context, final Intent intent) throws NullPointerException {

        try {
            //start intent service
            Intent callService = new Intent(context, IncomingCallReceiver.class);
            context.startService(callService);

            //state Listener class
            class StateListener extends PhoneStateListener {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);

                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING: {
                            Realm.init(context);

                            //get the contact number and display name if available in the contact list
//                            String
                            contactNumber = null;
                            contactNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            String displayName = getName(contactNumber, context);

                            //block idea company call series
                            if(contactNumber!=null) {

                                //-------------------------(Check for status of telemarketing calls)-----------------------------------------
                                SharedPreferences preferences = context.getSharedPreferences(CallSetting.TEL_BLK_STATUS,MODE_PRIVATE);

                                if(preferences.getBoolean(CallSetting.key,false)) {
                                    if (contactNumber.startsWith("+91140") || contactNumber.startsWith("0140") || contactNumber.startsWith("140")) {
                                        Log.e("call status company", "rejected" + contactNumber);
                                        rejectCall(context, intent);
                                        contactNumber = null;
                                    }
                                }
                                //-----------------------------------------------------------------------------

                                //----------------------new code to block number--------------------
                                Realm.init(context);

                                //realm config
                                RealmConfiguration config = new RealmConfiguration.Builder()
                                        .schemaVersion(4)
                                        .migration(new CustomRealmMigration())
                                        .build();
                                Realm.setDefaultConfiguration(config);

                                //block the numbers if feed in the data of Realm
                                RealmResults<BlockedContactsData> result =
                                        Realm.getDefaultInstance()
                                                .where(BlockedContactsData.class)
                                                .equalTo(BlockedContactsData.NAME, displayName)
                                                .findAll();

                                if (result.isEmpty())
                                    result = Realm.getDefaultInstance()
                                            .where(BlockedContactsData.class)
                                            .equalTo(BlockedContactsData.NUMBER, contactNumber)
                                            .findAll();
                                if (!result.isEmpty()) {
                                    rejectCall(context, intent);
                                    contactNumber = null;
                                }
                                //------------------------------------------------------------------

                            }
                            break;
                        }

                        //this state occure when phone is disconnected
                        case TelephonyManager.CALL_STATE_IDLE: {
                            Log.e("call state idle","on");
                            break;
                        }
                        case TelephonyManager.CALL_STATE_OFFHOOK: {
                            //called when call is ended
                            Log.e("call state offhook","on");
                            break;
                        }
                    }
                }
            }
            ;

            //get the incomming calls
            StateListener stateListener = new StateListener();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);


        } catch (Exception e) {
            Log.e("Error in getName", "" + e);
            Toast.makeText(context, "Exception " + e, Toast.LENGTH_SHORT).show();
        }
    }


    public String getName(String contactNumber, Context context) {
        String displayName = null;

        //get the database of android with contentResolver
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            displayName = cursor.getString(0);
        }

        return displayName;
    }

    public int getRingerMode(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getRingerMode();
    }

    public void rejectCall(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            Bundle bundle = intent.getExtras();
            String phoneNumber = bundle.getString("incoming_number");
            Log.d("INCOMING", phoneNumber);
            if ((phoneNumber != null)) {
                telephonyService.endCall();
                Log.d("HANG UP", phoneNumber);
                contactNumber = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
