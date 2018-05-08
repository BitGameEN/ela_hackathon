package com.elastos.chat.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.elastos.chat.common.Extra;

/**
 * @author rczhang on 2018/05/08.
 * @see <a href="https://github.com/google/iosched/blob/6a33a887ab1de307922bc33b1ee49c14e6124d92/lib/src/main/java/com/google/samples/apps/iosched/info/InfoViewPagerAdapter.java#L58">InfoViewPagerAdapter</a>
 */
abstract public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    protected Fragment[] mFragments;
    protected FragmentManager mFragmentManager;

    public BaseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    public Fragment[] getFragments() {
        if (mFragments == null) {
            // Force creating the fragments
            int count = getCount();
            for (int i = 0; i < count; i++) {
                getItem(i);
            }
        }
        return mFragments;
    }

    public void setRetainedFragmentsTags(String[] tags) {
        if (tags != null && tags.length > 0) {
            mFragments = new Fragment[tags.length];
            for (int i = 0; i < tags.length; i++) {
                Fragment fragment = mFragmentManager.findFragmentByTag(tags[i]);
                mFragments[i] = fragment;
                if (fragment == null) {
                    getItem(i);
                }
            }
        }
    }

    public void saveInstanceState(Bundle outState) {
        if (getFragments() != null) {
            Fragment[] infoFragments = getFragments();
            String[] tags = new String[infoFragments.length];
            for (int i = 0; i < tags.length; i++) {
                tags[i] = infoFragments[i].getTag();
            }
            outState.putStringArray(Extra.FRAGMENTS_TAGS, tags);
        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(Extra.FRAGMENTS_TAGS)) {
            String[] fragmentTags = savedInstanceState.getStringArray(
                    Extra.FRAGMENTS_TAGS);
            setRetainedFragmentsTags(fragmentTags);
        }
    }
}
