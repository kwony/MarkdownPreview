package com.kwony.mdpreview.Tabs.Pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kwony.mdpreview.Tabs.MarkdownTab;
import com.kwony.mdpreview.Tabs.RecentTab;
import com.kwony.mdpreview.Tabs.ThemeTab;

public class MainPagerAdapter extends FragmentPagerAdapter {
    int nNumOfTabs = 3;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment tab = null;
        switch (position) {
        case 0:
            tab = new MarkdownTab();
            break;
        case 1:
            tab = new RecentTab();
            break;
        case 2:
            tab = new ThemeTab();
        }

        return tab;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
