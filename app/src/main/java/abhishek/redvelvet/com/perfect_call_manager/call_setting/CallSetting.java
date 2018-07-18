package abhishek.redvelvet.com.perfect_call_manager.call_setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.prefs.Preferences;

import abhishek.redvelvet.com.perfect_call_manager.R;
import abhishek.redvelvet.com.perfect_call_manager.call_setting.blocked_call.ShowBlockedContactsActivity;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CallSetting extends AppCompatActivity {

    public static final String TEL_BLK_STATUS = "tele_marketing_call_block_status";
    public static final String key = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_setting);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right );
    }

    @OnCheckedChanged(R.id.blk_tel_call)
    public void checkedChangedListener(CompoundButton buttonView, boolean isChecked){
        if (isChecked){

            SharedPreferences.Editor tel_blk_call = getSharedPreferences(TEL_BLK_STATUS, MODE_PRIVATE).edit();
            tel_blk_call.putBoolean(key,true);
            tel_blk_call.commit();

            Toast.makeText(this, "Blocked", Toast.LENGTH_SHORT).show();
        }
        else{
            SharedPreferences.Editor tel_blk_call = getSharedPreferences(TEL_BLK_STATUS, MODE_PRIVATE).edit();
            tel_blk_call.putBoolean(key,false);
            tel_blk_call.commit();
            Toast.makeText(this, "Unblocked", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.block_contact_list)
    public void onClick(View v){
        startActivity(new Intent(this,ShowBlockedContactsActivity.class));
    }

}
