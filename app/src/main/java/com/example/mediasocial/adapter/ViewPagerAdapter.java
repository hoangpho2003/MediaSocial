package com.example.mediasocial.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.mediasocial.fragment.Add;
import com.example.mediasocial.fragment.Home;
import com.example.mediasocial.fragment.Notifications;
import com.example.mediasocial.fragment.Profile;
import com.example.mediasocial.fragment.Search;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int noOfTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int noOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            default:
            case 0:
                return new Home();
            case 1:
                return new Search();
            case 2:
                return new Add();
            case 3:
                return new Notifications();
            case 4:
                return new Profile();
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
