package abhishek.redvelvet.com.perfect_call_manager.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import abhishek.redvelvet.com.perfect_call_manager.InfoActivity;
import abhishek.redvelvet.com.perfect_call_manager.R;
import abhishek.redvelvet.com.perfect_call_manager.contacts.LoadAllContacts;
import abhishek.redvelvet.com.perfect_call_manager.contacts.RecentContacts;

/**
 * Created by abhishek on 31/5/18.
 */

public class RecentFragment extends Fragment {

    ListView listView;
    private static ArrayList al,rtv_cnt = new ArrayList(),cnt;
    Cursor cursor_r_contacts;
    InfoActivity infoActivity;
    public static  final String[] keys = {"name", "date"};
    public static  final int[] ids = {R.id.rc_name,R.id.time};

    public RecentFragment() {
        // Required empty public constructor
    }

    public static RecentFragment newInstace(int page){
        RecentFragment fragment = new RecentFragment();
        Bundle args = new Bundle();
        args.putInt("Recent", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        listView = view.findViewById(R.id.contactList);

        al = new RecentContacts(getActivity().getApplicationContext()).loadCallLog();
        if(al.isEmpty() || al==null)
            Log.e("al","empty");

        final SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(),al,R.layout.listview_style_recent_contact,keys,ids);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                   HashMap hm = (HashMap)al.get(position);
                   String name = hm.get(keys[0]).toString();
                   String date = hm.get(keys[1]).toString();

                   if(name.matches("[0-9+]+")){
                       call(name);
                   }else {

                       String number = new RecentContacts(getActivity().getApplicationContext()).getNumber(name,date);
                       if(number!=null){
                           call(number);
                       }


                   }

            }
        });

        return view;
    }

    public void call(String number){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+number));

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    /*
    * Stirng to integer converter
    * */
    private int s2i(String s){
        return Integer.parseInt(s);
    }

}
