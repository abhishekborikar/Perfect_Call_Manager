package abhishek.redvelvet.com.perfect_call_manager.EmergencyNotification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import abhishek.redvelvet.com.perfect_call_manager.R;
import abhishek.redvelvet.com.perfect_call_manager.contacts.LoadAllContacts;
import abhishek.redvelvet.com.perfect_call_manager.fragment.RecentFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnItemClick;

public class EmergencyActivity extends Activity {

    @BindView(R.id.emegy_switch)
    Switch mSwitch;
    @BindView(R.id.emegy_lv)
    ListView emegy_lv;
    @BindView(R.id.emegy_contact_info)
    RelativeLayout rl;
    @BindView(R.id.emegy_img_letter)
    ImageView img_letter;
    @BindView(R.id.emegy_info_name)
    TextView emgy_name;

    public static final String EM_CNT = "emergency contact";
    ArrayList contact_al,number_al;

    public static final int ADD = 0;
    public static final int REMOVE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);

        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //1-> check prefrences
        String contactInfo = getEmgyPrefrence();
        if(!contactInfo.contains("Empty")){
            mSwitch.setChecked(true);
            emegy_lv.setVisibility(View.GONE);
            rl.setVisibility(View.VISIBLE);

            setRlParameters(contactInfo);
            new Notification(getApplicationContext()).makeEmgyNotification();
        }
        else{
            mSwitch.setChecked(false);
            emegy_lv.setVisibility(View.GONE);
            rl.setVisibility(View.GONE);

            //1-> retrive contact
            //2 -> populate listview

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    @OnCheckedChanged(R.id.emegy_switch)void OnCheckedChange(boolean isChecked){
        if(mSwitch.isChecked()) {
            rl.setVisibility(View.GONE);
            emegy_lv.setVisibility(View.VISIBLE);
            loadAllContact();

        }
        else{
            emegy_lv.setVisibility(View.GONE);
            rl.setVisibility(View.GONE);

            action_add_remove_emg_cnt(REMOVE,null,null);
            new Notification(getApplicationContext()).destoryNotification();
        }
    }

    @OnItemClick(R.id.emegy_lv)void OnItemClick(int position){

        final String displayName = contact_al.get(position).toString();
        if (!displayName.matches("[0-9]*") || !displayName.matches("[0-9+]*")) {
            loadNumbers(displayName);


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Number");
            ListAdapter adapter = new ArrayAdapter<String>(this,
                    R.layout.simple_listview_style,
                    R.id.simple_list_tv,
                    number_al);
            builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String displayNumber = number_al.get(which).toString();
                    action_add_remove_emg_cnt(ADD, displayName, displayNumber);
                    setRlParameters(displayName + "," + displayNumber);

                    //setVisiblitiy
                    mSwitch.setChecked(true);
                    emegy_lv.setVisibility(View.GONE);
                    rl.setVisibility(View.VISIBLE);

                    dialog.dismiss();

                    new Notification(getApplicationContext()).makeEmgyNotification();
                }
            });
            builder.create();
            builder.show();
        }


    }

    /**
     * Either add or remove emergency contact
     * 0 -> add emergency contact
     * 1 -> remove emergency contact
     * @param operation
     * @return
     */
    public boolean action_add_remove_emg_cnt(@Nonnull int operation, @Nullable String displayName, @Nullable String displayNumber){


        switch (operation){
            case 0:{
                SharedPreferences.Editor pre_editor = getSharedPreferences(EM_CNT, MODE_PRIVATE).edit();
                pre_editor.putString(LoadAllContacts.keys[0], displayName);
                pre_editor.putString(LoadAllContacts.keys[1], displayNumber);
                return pre_editor.commit();
            }
            case 1:{
                SharedPreferences preferences = getSharedPreferences(EM_CNT, MODE_PRIVATE);
                return preferences.edit().remove(LoadAllContacts.keys[0]).commit();
            }
        }
        return false;
    }

    public void loadNumbers(String displayName){
        ArrayList number_al_hm = new LoadAllContacts(
                getApplicationContext().getContentResolver(),
                getApplicationContext())
                .loadNumber(displayName);

        number_al = new ArrayList<String>();

        for (int i = 0; i < number_al_hm.size(); i++) {
            HashMap hm = (HashMap)number_al_hm.get(i);
            if(hm.get(RecentFragment.keys[0])!=null) {
                String number = hm.get(RecentFragment.keys[0]).toString();
                number_al.add(number);
            }
        }
    }

    public void loadAllContact(){
        contact_al = new LoadAllContacts(
                getApplicationContext().getContentResolver(),
                getApplicationContext())
                .loadContacts();


        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.simple_listview_style,R.id.simple_list_tv,contact_al);
        emegy_lv.setAdapter(adapter);

    }

    /**
     * this is used for 2 purposes
     * 1 -> get the emergency contact info
     * 2 -> check if emergency contact info is available
     * @return
     */
    public String getEmgyPrefrence(){
        SharedPreferences preferences = getSharedPreferences(EM_CNT, MODE_PRIVATE);
        return preferences.getString(LoadAllContacts.keys[0], "Empty")+","+preferences.getString(LoadAllContacts.keys[1], "Empty");
    }

    public void setRlParameters(String name){
        String[] s = name.split(",");
        String set = s[0]+"\n"+s[1];

        TextDrawable drawablec = TextDrawable.builder().buildRound(""+set.toUpperCase().charAt(0), getResources().getColor(R.color.colorPrimary));
        img_letter.setImageDrawable(drawablec);

        emgy_name.setText(set);
    }

}