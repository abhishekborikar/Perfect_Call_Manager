package abhishek.redvelvet.com.perfect_call_manager;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.HashMap;

import abhishek.redvelvet.com.perfect_call_manager.contacts.LoadAllContacts;
import abhishek.redvelvet.com.perfect_call_manager.fragment.AlertMessage;
import abhishek.redvelvet.com.perfect_call_manager.fragment.RecentFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class InfoActivity extends AppCompatActivity {

    @BindView(R.id.img_letter)ImageView img_letter;
    @BindView(R.id.info_numbers)ListView listView;
    @BindView(R.id.info_name)TextView info_name;
    @BindView(R.id.mail_area)LinearLayout mail_layout;

    String displayName = null;
    ArrayList al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        overridePendingTransition(R.anim.slide_from_bottom,R.anim.slide_to_top);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();


        Intent intent = getIntent();
        displayName = intent.getStringExtra("name");
        info_name.setText(displayName);
        TextDrawable drawablec = TextDrawable.builder().buildRound(""+displayName.toUpperCase().charAt(0), getResources().getColor(R.color.colorPrimary));
        img_letter.setImageDrawable(drawablec);

        al = new LoadAllContacts(
                getApplicationContext().getContentResolver(),
                getApplicationContext())
                .loadNumber(displayName);

        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),al,R.layout.listview_style_recent_contact, RecentFragment.keys,RecentFragment.ids);
        listView.setAdapter(adapter);

        int pad = Math.round(getResources().getDimension(R.dimen.pad_top_bottom));

        TextView text = new TextView(getApplicationContext());
        text.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.text_color_light));
        text.setText("Block");
        text.setTextSize(Math.round(getResources().getDimension(R.dimen.pad_top_bottom)-1));
        text.setPadding(pad+7,pad,pad,pad);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertMessage.newInstance(displayName,al,getApplicationContext()).show(getSupportFragmentManager(),"alert");
            }
        });
        listView.addFooterView(text);

        
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_to_bottom);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_to_bottom);
    }

    @OnItemClick(R.id.info_numbers) void onItemClick(int position){

        if(position < al.size()) {

            HashMap hm = (HashMap) al.get(position);
            String number = hm.get(RecentFragment.keys[0]).toString();
            String type = hm.get(RecentFragment.keys[1]).toString();
            if (!type.contains("EMAIL"))
                call(number);
            else if (type.contains("EMAIL"))
                mail(number);

        }


        Toast.makeText(this, "Clicked Me", Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.mail_area)void onClickMailArea(){
        //check for the email
        HashMap hm = new LoadAllContacts(getApplicationContext().getContentResolver(),
                getApplicationContext()).isEmail(displayName);
        if(hm.get(RecentFragment.keys[0])==null) {
            mail_layout.setClickable(false);
            Toast.makeText(this, "No mail found", Toast.LENGTH_SHORT).show();
        }
        else {

            String email = hm.get(RecentFragment.keys[0]).toString();

            mail(email);
        }

    }

    @OnClick(R.id.whatsapp_area)void onClickWhatsapp(){

        if(displayName.matches("[0-9+]*")) {
            Toast.makeText(this, "Not a WhatsApp Number", Toast.LENGTH_SHORT).show();
        }
        else{

            String whatsappNumber = new LoadAllContacts(getApplicationContext().getContentResolver(),
                    getApplicationContext()).isWhatsapp(displayName);


            try {

                whatsappNumber = whatsappNumber.replace("Message", "").trim();
                whatsappNumber = whatsappNumber.substring(3,whatsappNumber.length()).replace(" ","").trim();
                String in = "+91 ";
                whatsappNumber = in + whatsappNumber;
                whatsappNumber.trim();

                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new  ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                sendIntent.putExtra("jid",PhoneNumberUtils.stripSeparators(whatsappNumber)+"@s.whatsapp.net");//phone number without "+" prefix

                startActivity(sendIntent);


            }
            catch (Exception e){
                Log.e("whatsapp error",""+e);
                Toast.makeText(this, "something wen wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.message_area) void onMessageAreaClick(){

            AlertMsg(1);


    }

    @OnClick(R.id.call_area) void onCallAreaClick(){
            AlertMsg(2);
    }

    protected void sendSMS(String number) {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , number);
        smsIntent.putExtra("sms_body"  , "Test ");

        try {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(InfoActivity .this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void AlertMsg(final int type){
        ArrayList numbers = new ArrayList();
        String types   ;
        for (int i = 0; i < al.size(); i++) {
            HashMap hm = (HashMap)al.get(i);

            Object o = hm.get(RecentFragment.keys[1]);
            if(o!=null) {
                types = o.toString();
                if (!types.contains("EMAIL")) {
                    Log.e("types",types);
                    numbers.add(hm.get(RecentFragment.keys[0]).toString());
                }
            }
        }

        if(numbers.size()>1){
            try {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(InfoActivity.this);
                builderSingle.setTitle("Choose Number");
                Log.e("error","1");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(InfoActivity.this, android.R.layout.select_dialog_singlechoice);
                for (int i = 0; i < numbers.size(); i++) {
                    arrayAdapter.add(numbers.get(i).toString());
                }

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("error","2");
                        dialog.dismiss();

                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("error",""+3);
                        final String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(InfoActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(type==1)
                                    sendSMS(strName);
                                else
                                    call(strName);
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
        }catch (Exception e){
            Log.e("error",""+e);
        }
        }
        else{
            sendSMS(numbers.get(0).toString());
        }
    }

    public void call(String number){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+number));

        if (ActivityCompat.checkSelfPermission(InfoActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void mail(String email){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("plain/text");
        emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        startActivity(emailIntent);
    }

}