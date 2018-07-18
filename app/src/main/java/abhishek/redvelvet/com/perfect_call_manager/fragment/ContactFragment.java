package abhishek.redvelvet.com.perfect_call_manager.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import abhishek.redvelvet.com.perfect_call_manager.InfoActivity;
import abhishek.redvelvet.com.perfect_call_manager.R;
import abhishek.redvelvet.com.perfect_call_manager.contacts.LoadAllContacts;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    ListView listView;

    static ArrayList al;
    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstace(int page){
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putInt("Contact", page);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        listView = view.findViewById(R.id.contactList);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        populateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent info = new Intent(getActivity().getApplicationContext(), InfoActivity.class);
                info.putExtra("name",al.get(position).toString());
                startActivity(info);
            }
        });

    }

    private void populateList(){
        try {
            al = new LoadAllContacts(
                    getActivity().getApplicationContext().getContentResolver(),
                    getActivity().getApplicationContext())
                    .loadContacts();

            ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, al);
            synchronized (adapter){
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                listView.notifyAll();
            }
        }
        catch (Exception e){
            Log.e("Contact Fragment error",""+e);
        }
    }
}
