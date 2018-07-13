package abhishek.redvelvet.com.perfect_call_manager.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import abhishek.redvelvet.com.perfect_call_manager.fragment.RecentFragment;

/**
 * Created by abhishek on 31/5/18.
 */

public class LoadAllContacts {
    //parameter to import
    private ListView lv;
    private ContentResolver contentResolver;
    private Context context;

    public static ArrayList al=null;

    private Cursor cursor_Android_Contacts ;
    public static final String[] keys = {"name", "number"};
//    public static final int[] ids = {R.id.name, R.id.number};

    public LoadAllContacts( ContentResolver contentResolver, Context context) {
        this.contentResolver = contentResolver;
        this.context = context;

        cursor_Android_Contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
    }

    public ArrayList loadContacts() {

        al = new ArrayList();

        //to get connection to database in android we use content resolver
        //get all contacts
        try {
            //check if it has contacts
//            if(cursor_Android_Contacts!=null)
            if (cursor_Android_Contacts.getCount() > 0) {


                if (cursor_Android_Contacts.moveToFirst()) {

                    do {
                        String contact_display_name = cursor_Android_Contacts
                                .getString(
                                        cursor_Android_Contacts.getColumnIndex(
                                                ContactsContract.Contacts.DISPLAY_NAME
                                        )
                                );
                        if(!al.contains(contact_display_name))
                            al.add(contact_display_name);

                    } while (cursor_Android_Contacts.moveToNext());
                }



                return al;
            }
        } catch (Exception e) {

            Log.e("error in contact", e.getMessage());
        }


        return al;
    }

    public ArrayList loadNumber(String displayName){

        ArrayList al = new ArrayList();

        try {

            Cursor name_cr = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,"DISPLAY_NAME = '"+displayName+"'",null,null);

            if(name_cr.moveToFirst()){
                String contactId = name_cr.getString(name_cr.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor number_cr = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,"CONTACT_ID = "+contactId,null,null);

                if(number_cr.moveToFirst()){
                    do {
                        String number = number_cr.getString(number_cr.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = number_cr.getInt(number_cr.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));


                        Log.e("number",number_cr.getCount()+"  "+number);

                        HashMap hm = new HashMap();
                        hm.put(RecentFragment.keys[0],number);
                        hm.put(RecentFragment.keys[1],getType(type));
                        al.add(hm);
                    }while (number_cr.moveToNext());

                    //set email id
                    al.add(isEmail(displayName));

                    //set Birthdate
//                    Uri uri = ContactsContract.Data.CONTENT_URI;
//
//                    String[] projection = new String[] {
//                            ContactsContract.Contacts.DISPLAY_NAME,
//                            ContactsContract.CommonDataKinds.Event.CONTACT_ID,
//                            ContactsContract.CommonDataKinds.Event.START_DATE
//                    };
//
//                    String selection =
//                            ContactsContract.Data.MIMETYPE + "= ? AND " +
//                                    ContactsContract.Data.CONTACT_ID+ "= ? AND" +
//                                    ContactsContract.CommonDataKinds.Event.TYPE + "=" +
//                                    ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
//                    String[] selectionArgs = new String[] {
//                            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
//                            contactId
//                    };
//
//                    Cursor birth_cr = contentResolver.query(uri,projection,selection,selectionArgs,null);
//                    if (birth_cr!=null){
//                        if(birth_cr.getCount()>0){
//                            if(birth_cr.moveToFirst()){
//                                do {
//                                    HashMap hm = new HashMap();
//                                    String b_day = birth_cr.getString(birth_cr.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
//                                    hm.put(RecentFragment.keys[0],b_day);
//                                    hm.put(RecentFragment.keys[1],"BIRTHDAY");
//                                    al.add(hm);
//
//                                }while (birth_cr.moveToNext());
//                            }
//                        }
//                    }
                }
                number_cr.close();
            }
            name_cr.close();

        }
        catch (Exception e){
            Log.e("LoadAllContact",""+e);
        }
        return al;
    }

    /**
     * Check if display name contact exist in whatsapp
     * if exist it will retrieve the number which is whatsapp number
     * @param displayName
     * it is name in show in contact list
     * @return
     */
    public String isWhatsapp(String displayName){
        String rawContactId = null;
        Log.e("i","1");
        try {
            Cursor name_cr = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, "DISPLAY_NAME = '" + displayName + "'", null, null);
            Log.e("i","2");
            if (name_cr.moveToFirst()) {
                String contactId = name_cr.getString(name_cr.getColumnIndex(ContactsContract.Contacts._ID));

                //get the rawContactId from Contacts._ID
                String[] projection = new String[]{ContactsContract.RawContacts._ID};
                String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
                String[] selectionArgs = new String[]{contactId, "com.whatsapp"};
                Cursor cursor = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
                boolean hasWhatsApp = cursor.moveToNext();
                if (hasWhatsApp) {
                    rawContactId = cursor.getString(0);
                    Log.e("contact Id", "" + contactId);
                }

                //get the any whatsapp number from rawContactId
                projection = new String[]{ContactsContract.Data.DATA3};
                selection = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.Data.RAW_CONTACT_ID + " = ? ";
                selectionArgs = new String[]{"vnd.android.cursor.item/vnd.com.whatsapp.profile", rawContactId};
                cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, "1 LIMIT 1");
                String phoneNumber = null;
                if (cursor.moveToNext()) {
                    phoneNumber = cursor.getString(0);
                    phoneNumber.replace("Message","");
                    Log.e("Phone ",""+phoneNumber);

                }
                return phoneNumber;
            }
        }
        catch (Exception e){
            Toast.makeText(context, "Not a WhatsApp Number", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    /**
     * Checks if display name has email saved
     * @param displayName
     * @return
     */

    public HashMap isEmail(String displayName){
        HashMap hm = new HashMap();
        String contactId = null;
        Cursor name_cr = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,"DISPLAY_NAME = '"+displayName+"'",null,null);

        if(name_cr.moveToFirst())
            contactId = name_cr.getString(name_cr.getColumnIndex(ContactsContract.Contacts._ID));

        Cursor mail_cr = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                new String[]{contactId},
                null);
        if(mail_cr!=null)
            if(mail_cr.getCount()>0)
                if(mail_cr.moveToFirst()){
                    do {

                        String emailId = mail_cr.getString(mail_cr.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        int emailType = mail_cr.getInt(mail_cr.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        hm.put(RecentFragment.keys[0],emailId);
                        hm.put(RecentFragment.keys[1],"EMAIL  |  "+getTypeEmail(emailType));
                        al.add(hm);
                    }while (mail_cr.moveToNext());
                }
        mail_cr.close();
        return hm;
    }

    public String getType(int type){
        switch (type){
            case 1:{
                return "HOME";
            }
            case 2:{
                return "MOBILE";
            }
        }
        return null;
    }

    public String getTypeEmail(int type){

        switch (type){
            case 1:{
                return "HOME";
            }
            case 2:{
                return "WORK";
            }
        }
        return null;
    }
}
