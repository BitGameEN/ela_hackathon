package com.elastos.chat.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.elastos.chat.R;
import com.elastos.chat.ui.FriendsFragment;
import com.elastos.chat.ui.MeFragment;

/**
 * @author rczhang on 2018/05/08.
 */
public class MainFragmentPagerAdapter extends BaseFragmentPagerAdapter {

    private final static int COUNT = 2;

    public final static int ME = 0;
    public final static int FRIENDS = 1;

    private Context context;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments != null && mFragments.length > position && mFragments[position] != null) {
            return mFragments[position];
        }

        if (mFragments == null) {
            mFragments = new Fragment[getCount()];
        }

        switch (position) {
            case ME:
                mFragments[position] = MeFragment.newInstance();
                break;
            case FRIENDS:
                mFragments[position] = FriendsFragment.newInstance();
                break;
            default:
                break;
        }
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.me);
        } else if (position == 1) {
            return context.getString(R.string.friends);
        } else {
            return context.getString(R.string.me);
        }
    }
}
