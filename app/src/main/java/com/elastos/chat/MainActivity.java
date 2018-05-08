package com.elastos.chat;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.elastos.chat.ui.BaseActivity;
import com.elastos.chat.ui.MainFragmentPagerAdapter;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;

    private MainFragmentPagerAdapter mainFragmentPagerAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainFragmentPagerAdapter.saveInstanceState(outState);
    }

    @Override
    protected void initVariables() {
        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        setupViewPager(savedInstanceState);
    }

    private void setupViewPager(Bundle savedInstanceState) {
        mainFragmentPagerAdapter.restoreInstanceState(savedInstanceState);
        viewPager.setOffscreenPageLimit(mainFragmentPagerAdapter.getCount());
        viewPager.setAdapter(mainFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
