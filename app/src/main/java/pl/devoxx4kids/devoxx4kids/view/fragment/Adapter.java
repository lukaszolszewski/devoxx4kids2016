package pl.devoxx4kids.devoxx4kids.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class Adapter extends FragmentStatePagerAdapter {

    public Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        Fragment fragment = null;

        if (i == 1) {
            fragment = new ListFragment();
        } else {
            fragment = new ScannerFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
