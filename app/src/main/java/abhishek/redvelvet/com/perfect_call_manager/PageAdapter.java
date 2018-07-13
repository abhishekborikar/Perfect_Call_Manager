package abhishek.redvelvet.com.perfect_call_manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import abhishek.redvelvet.com.perfect_call_manager.fragment.ContactFragment;
import abhishek.redvelvet.com.perfect_call_manager.fragment.RecentFragment;

/**
 * Created by abhishek on 31/5/18.
 */

public class PageAdapter extends FragmentPagerAdapter {

    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        switch (position){
            case 0:{
                fragment = new ContactFragment();
                break;
            }
            case 1:{
                fragment = new RecentFragment();
                break;
            }
        }

        return fragment;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence r = null;
        if(position==0){
            r = "Contact";
        }
        else if(position ==1){
            r = "Recent Contact";
        }
        return r;
    }
}
