package cn.kavel.demo.javantwk.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import cn.kavel.demo.javantwk.R;
import cn.kavel.demo.javantwk.adapter.SectionsPagerAdapter;
import cn.kavel.demo.javantwk.fragments.AboutFragment;
import cn.kavel.demo.javantwk.fragments.Exp1Fragment;
import cn.kavel.demo.javantwk.fragments.Exp2Fragment;
import cn.kavel.demo.javantwk.fragments.Exp4Fragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final List<Fragment> pages = new ArrayList<>();
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private void loadPages() {
        pages.add(new AboutFragment());
        pages.add(new Exp1Fragment());
        pages.add(new Exp2Fragment());
        pages.add(new Exp4Fragment());
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        loadPages();
        mViewPager.setCurrentItem(1);
    }
}
