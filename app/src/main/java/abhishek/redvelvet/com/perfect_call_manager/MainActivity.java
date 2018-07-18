package abhishek.redvelvet.com.perfect_call_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import abhishek.redvelvet.com.perfect_call_manager.EmergencyNotification.EmergencyActivity;
import abhishek.redvelvet.com.perfect_call_manager.call_setting.CallSetting;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.pagerTabStrip)
    PagerTabStrip pagerTabStrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.more_options));
        ButterKnife.bind(this);

        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.text_color_light));


        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.toString()){
            case "Emergency Contact":{
                startActivity(new Intent(this,EmergencyActivity.class));
                break;
            }
            case "Call Setting":{
                Intent block = new Intent(MainActivity.this, CallSetting.class);
                startActivity(block);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void fabClick(View view){

    }
}
