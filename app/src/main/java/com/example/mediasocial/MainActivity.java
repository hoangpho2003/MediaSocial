package com.example.mediasocial;

import static com.example.mediasocial.Utils.Constants.PREF_DIRECTORY;
import static com.example.mediasocial.Utils.Constants.PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.mediasocial.adapter.ViewPagerAdapter;
import com.example.mediasocial.fragment.Search;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Search.OnDataPass {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter pagerAdapter;
    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;

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

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String directory = preferences.getString(PREF_DIRECTORY, "");
        Bitmap bitmap = loadProfileImage(directory);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

        tabLayout.addTab(tabLayout.newTab().setIcon(drawable));
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

    private Bitmap loadProfileImage(String directory) {
        try {
            File file = new File(directory, "profile.png");
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void init() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);
    }

    @Override
    public void onChange(String uid) {
        USER_ID = uid;
        IS_SEARCHED_USER = true;
        viewPager.setCurrentItem(4);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 4) {
            viewPager.setCurrentItem(0);
            IS_SEARCHED_USER = false;
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        updateStatus(false);
        super.onResume();
    }

    void updateStatus(boolean status){

        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(map);
    }
}