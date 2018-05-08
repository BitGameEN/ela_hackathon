package com.elastos.chat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elastos.chat.R;

/**
 * @author rczhang on 2018/05/08.
 */
public class FriendsFragment extends BaseFragment {

    public static FriendsFragment newInstance() {
        FriendsFragment meFragment = new FriendsFragment();
        return meFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }
}
