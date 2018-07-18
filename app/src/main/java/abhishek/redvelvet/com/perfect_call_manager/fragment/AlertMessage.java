package abhishek.redvelvet.com.perfect_call_manager.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import abhishek.redvelvet.com.perfect_call_manager.CustomRealmMigration;
import abhishek.redvelvet.com.perfect_call_manager.contacts.LoadAllContacts;
import abhishek.redvelvet.com.perfect_call_manager.database.BlockedContactsData;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by abhishek on 4/6/18.
 */

public class AlertMessage extends DialogFragment {

    static Context context;

    public static AlertMessage newInstance(String name, ArrayList number,Context context){
        AlertMessage f = new AlertMessage();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putStringArrayList("number",number);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String name = getArguments().getString("name");
        final ArrayList<String> numbers = getArguments().getStringArrayList("number");

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Block Contact")
                .setPositiveButton("Block", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //init realm
                        Realm.init(getActivity().getApplicationContext());

                        //realm config
                        RealmConfiguration config = new RealmConfiguration.Builder()
                                .schemaVersion(4)
                                .migration(new CustomRealmMigration())
                                .build();
                        Realm.setDefaultConfiguration(config);

                        //------- Custom realm instacne----------
                        final Realm Crealm = Realm.getDefaultInstance();

                        //---code to block number---
                        Crealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                //check if the contact already exist in the blocked list
                                RealmResults<BlockedContactsData>result = realm.where(BlockedContactsData.class)
                                        .equalTo(BlockedContactsData.NAME,name)
                                        .findAll();

                                ArrayList numbers = new LoadAllContacts(
                                        getActivity().getApplicationContext().getContentResolver(),
                                        getActivity().getApplicationContext()
                                ).loadNumber(name);

                                HashMap hm = (HashMap)numbers.get(0);
                                String number = hm.get(RecentFragment.keys[0]).toString();

                                if(result.size()==0) {
                                    BlockedContactsData blockedData = realm.createObject(BlockedContactsData.class, (String) name);
                                    blockedData.setNumber(number+"");
                                    Toast.makeText(getActivity().getApplicationContext(), "Blocked " + name + " " + number, Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(context, "Already Blocked", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                        //---end code to block---

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity().getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .create();
        return alertDialog;
    }
}
