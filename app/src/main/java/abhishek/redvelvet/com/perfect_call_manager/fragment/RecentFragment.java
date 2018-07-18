package abhishek.redvelvet.com.perfect_call_manager.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;

import abhishek.redvelvet.com.perfect_call_manager.R;
import abhishek.redvelvet.com.perfect_call_manager.contacts.RecentContacts;

/**
 * Created by abhishek on 31/5/18.
 */

public class RecentFragment extends Fragment {

    ListView listView;
    Cursor cursor_r_contacts;
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

        ArrayList al = new RecentContacts(getActivity().getApplicationContext()).loadCallLog();
        if(al.isEmpty() || al==null)
            Log.e("al","empty");

        SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(),al,R.layout.listview_style_recent_contact,keys,ids);
        listView.setAdapter(adapter);

        return view;
    }


}
