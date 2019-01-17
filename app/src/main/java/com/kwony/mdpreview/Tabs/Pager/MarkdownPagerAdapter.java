package com.kwony.mdpreview.Tabs.Pager;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.kwony.mdpreview.Tabs.MarkdownTabs.CodeTab;
import com.kwony.mdpreview.Tabs.MarkdownTabs.IMarkdownTab;
import com.kwony.mdpreview.Tabs.MarkdownTabs.PreviewTab;

public class MarkdownPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 2;
    CharSequence mTitles[];
    int mImageResources[];

    SparseArray<IMarkdownTab> registeredTabs = new SparseArray<>();

    public MarkdownPagerAdapter(FragmentManager fm, CharSequence titles[],
                                int numOfTabs) {
        super(fm);

        this.mTitles = titles;
        this.mNumOfTabs = numOfTabs;
    }

    public MarkdownPagerAdapter(FragmentManager fm, CharSequence titles[], int images[],
                                int numOfTabs) {
        super(fm);

        this.mTitles = titles;
        this.mNumOfTabs = numOfTabs;
        this.mImageResources = images;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment tab = null;

        switch (position) {
        case 0:
            tab = new PreviewTab();
            break;
        case 1:
            tab = new CodeTab();
            break;
        }

        return tab;
    }

    @Override
    public int getCount() {
        return this.mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredTabs.put(position, (IMarkdownTab)fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredTabs.remove(position);
        super.destroyItem(container, position, object);
    }

    public int getPageImageResource(int position) { return mImageResources[position]; }

    public IMarkdownTab getRegisteredTab(int position) {
        return registeredTabs.get(position);
    }
}
