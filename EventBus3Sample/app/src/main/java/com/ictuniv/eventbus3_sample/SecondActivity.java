package com.ictuniv.eventbus3_sample;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.ictuniv.eventbus3_sample.fragment.FragmentA;
import com.ictuniv.eventbus3_sample.fragment.FragmentB;
import com.ictuniv.eventbus3_sample.fragment.FragmentC;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecondActivity extends AppCompatActivity {


    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);


        if(viewpager != null){
            setupviewpager(viewpager);
        }
        tabs.setupWithViewPager(viewpager);


    }


    private void setupviewpager(ViewPager viewpager) {
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentA(),"fragmentA");
        adapter.addFragment(new FragmentB(),"fragmentB");
        adapter.addFragment(new FragmentC(),"fragmentC");
        viewpager.setAdapter(adapter);
    }

    static class MyAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        private FragmentManager fm;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }
        public void addFragment(Fragment fragment,String title){
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }
        @Override
        public Fragment getItem(int position) {

            return mFragments.get(position);

        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
             fm.beginTransaction().show(fragment).commit();
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fm.beginTransaction().hide(mFragments.get(position)).commit();
        }
    }

}
