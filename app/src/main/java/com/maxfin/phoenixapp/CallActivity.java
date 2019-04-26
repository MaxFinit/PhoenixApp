package com.maxfin.phoenixapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CallActivity extends AppCompatActivity {
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);



        mViewPager = findViewById(R.id.container_vp);
        setUpViewPager(mViewPager);




        TabLayout tabLayout = findViewById(R.id.call_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(R.string.call_text);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(R.string.contact_text);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        Intent homeIntent = new Intent(CallActivity.this, MainActivity.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.menu_message:
                        Intent messageIntent = new Intent(CallActivity.this, DialogListActivity.class);
                        startActivity(messageIntent);
                        break;
                }
                return false;
            }
        });


    }

    private void setUpViewPager(ViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new JournalFragment());
        mViewPagerAdapter.addFragment(new ContactFragment());
        viewPager.setAdapter(mViewPagerAdapter);


    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }



}
