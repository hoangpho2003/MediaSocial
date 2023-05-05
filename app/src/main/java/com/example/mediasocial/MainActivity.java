package com.example.mediasocial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.example.mediasocial.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    ViewPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        addTabs();

    }

    private void addTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_heart));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_heart_fill));

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {

                    case 0:
//                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);
                        tab.setIcon(R.drawable.ic_home_fill);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.ic_search);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.ic_add);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.ic_heart_fill);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        tab.setIcon(R.drawable.ic_home);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.ic_search);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.ic_add);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.ic_heart);
                        break;


                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        tab.setIcon(R.drawable.ic_home_fill);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.ic_search);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.ic_add);
                        break;

                    case 3:

                        tab.setIcon(R.drawable.ic_heart_fill);
                        break;

                }

            }
        });
    }

    private void init(){
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);
    }
}