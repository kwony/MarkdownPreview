package com.kwony.mdpreview.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kwony.mdpreview.R;
import com.kwony.mdpreview.Tabs.MarkdownTabs.IMarkdownTab;
import com.kwony.mdpreview.Tabs.Pager.MarkdownPagerAdapter;
import com.kwony.mdpreview.Tabs.SlidingTabLib.SlidingTabLayout;

public class MarkdownTab extends Fragment {
    SlidingTabLayout tabs;
    MarkdownPagerAdapter adapter;
    ViewPager pager;
    CharSequence Titles[] = {"Preview", "Code"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_markdown, container,false);

        pager = v.findViewById(R.id.markdown_pager);

        // TODO: Search difference between getFragmentManager() and getChildFragmentManager()
        adapter = new MarkdownPagerAdapter(getChildFragmentManager(), Titles, 2);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int prePosition = -1;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int curPosition) {
                IMarkdownTab curImt = adapter.getRegisteredTab(curPosition);
                IMarkdownTab preImt = adapter.getRegisteredTab(prePosition);

                if (curPosition == prePosition || curImt == null)
                    return;

                if (preImt!=null)
                    preImt.cbPageUnSelected();

                curImt.cbPageSelected();
                prePosition = curPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        tabs = v.findViewById(R.id.markdown_sliding_tab);
        tabs.setDistributeEvenly(true);
        tabs.setViewPager(pager);

        return v;
    }
}
