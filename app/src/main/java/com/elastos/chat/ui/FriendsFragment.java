package com.elastos.chat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elastos.chat.R;
import com.elastos.chat.activity.MainActivity;
import com.elastos.chat.adapter.BaseTypeAdapter;
import com.elastos.chat.ui.item.FriendItemViewBinder;
import com.elastos.chat.ui.item.FriendItemViewModel;

import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.Items;

/**
 * @author rczhang on 2018/05/08.
 */
public class FriendsFragment extends BaseFragment {

    @BindView(R.id.recyclerview) RecyclerView recyclerView;

    private BaseTypeAdapter adapter;

    public static FriendsFragment newInstance() {
        FriendsFragment meFragment = new FriendsFragment();
        return meFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    public void setupRecyclerView() {
        adapter = new BaseTypeAdapter();
        adapter.register(FriendItemViewModel.class, new FriendItemViewBinder());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPageFirstStart() {
        // TODO: 2018/5/8 carrierInst 可能为空
        try {
            List<FriendInfo> friendInfoList = ((MainActivity) getActivity()).carrierInst.getFriends();
            Items items = new Items();
            for (FriendInfo friendInfo : friendInfoList) {
                items.add(new FriendItemViewModel(friendInfo));
            }
            adapter.addData(items);
        } catch (ElastosException e) {
            e.printStackTrace();
        }
    }
}