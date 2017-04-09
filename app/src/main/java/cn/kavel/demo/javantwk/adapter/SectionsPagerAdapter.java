package cn.kavel.demo.javantwk.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cn.kavel.demo.javantwk.R;
import cn.kavel.demo.javantwk.activities.MainActivity;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return MainActivity.pages.get(position);
    }

    @Override
    public int getCount() {
        return MainActivity.pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.about_title);
            case 1:
                return context.getString(R.string.exp1_title);
            case 2:
                return context.getString(R.string.exp2_title);
            case 3:
                return context.getString(R.string.exp4_title);
        }
        return null;
    }
}
