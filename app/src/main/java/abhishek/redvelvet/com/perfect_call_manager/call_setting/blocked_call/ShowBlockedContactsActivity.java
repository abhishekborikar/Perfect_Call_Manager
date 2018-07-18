package abhishek.redvelvet.com.perfect_call_manager.call_setting.blocked_call;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import abhishek.redvelvet.com.perfect_call_manager.CustomRealmMigration;
import abhishek.redvelvet.com.perfect_call_manager.R;
import abhishek.redvelvet.com.perfect_call_manager.database.BlockedContactsData;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by abhishek on 18/7/18.
 */

public class ShowBlockedContactsActivity extends AppCompatActivity {

    ListView lv;
    TextView tv;
    ArrayList al = new ArrayList();
    final String[] keys = {"name", "number"};
    final int[] ids = {R.id.rc_name, R.id.time};
    Realm Crealm;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contact);

        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        lv = (ListView) findViewById(R.id.contactList);

        Realm.init(this);

        //realm config
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(4)
                .migration(new CustomRealmMigration())
                .build();
        Realm.setDefaultConfiguration(config);

        loadBlockedContacts();
        Crealm = Realm.getDefaultInstance();

        //unblock alert message
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap hm = (HashMap) al.get(position);
                final String number = hm.get(keys[1]).toString();
                new AlertDialog.Builder(ShowBlockedContactsActivity.this)
                        .setTitle("Unblock Contact")
                        .setMessage("Do You want to unblock contact?")
                        .setPositiveButton("Unblock", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //block the numbers if feed in the data of Realm
                                Crealm.executeTransactionAsync(
                                        new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                realm.where(BlockedContactsData.class)
                                                        .equalTo(BlockedContactsData.NUMBER, number)
                                                        .findAll()
                                                        .deleteAllFromRealm();
                                            }
                                        },
                                        new Realm.Transaction.OnSuccess() {
                                            @Override
                                            public void onSuccess() {
                                                al.clear();
                                                loadBlockedContacts();
                                                adapter.notifyDataSetChanged();
                                                Log.e("Sucesssss", "Done");
                                            }
                                        });
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
            }
        });

    }

    public void loadBlockedContacts() {
        RealmResults<BlockedContactsData> result = Realm.getDefaultInstance().where(BlockedContactsData.class)
                .findAll();
        Log.e("result",""+result.size());

        if (result != null) {
            for (int i = 0; i < result.size(); i++) {

                Log.w("name ",""+result.get(i).getName());
                HashMap hm = new HashMap();
                hm.put(keys[0], result.get(i).getName());
                hm.put(keys[1], result.get(i).getNumber());
                al.add(hm);
            }
        } else
            Toast.makeText(getApplicationContext(), "No Contact Blocked", Toast.LENGTH_SHORT).show();

        adapter = new SimpleAdapter(
                ShowBlockedContactsActivity.this,
                al,
                R.layout.listview_style_recent_contact,
                keys,
                ids
        );
        lv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right );
    }
}
