package com.elastos.chat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.elastos.chat.R;
import com.elastos.chat.adapter.BaseTypeAdapter;
import com.elastos.chat.ui.item.FriendItemViewBinder;
import com.elastos.chat.ui.item.FriendItemViewModel;
import com.elastos.chat.util.ToastUtils;

import org.elastos.carrier.Carrier;
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
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    public void setupRecyclerView() {
        adapter = new BaseTypeAdapter();
        adapter.register(FriendItemViewModel.class, new FriendItemViewBinder(new FriendItemViewBinder.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, FriendItemViewModel item) {
                showMenu(item);
            }
        }));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPageFirstStart() {
        updateFriendList();
    }

    private void showMenu(final FriendItemViewModel itemViewModel) {
        new MaterialDialog.Builder(getContext())
                .items("删除好友")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        if (position == 0) {
                            deleteFriend(itemViewModel);
                        }
                    }
                })
                .show();
    }

    private void deleteFriend(FriendItemViewModel itemViewModel) {
        try {
            String toDelFriendUid = itemViewModel.getUserId();
            Carrier.getInstance().removeFriend(toDelFriendUid);
            adapter.removeItem(getIndex(itemViewModel.getUserId()));
        } catch (ElastosException e) {
            e.printStackTrace();
            ToastUtils.shortT("删除好友失败");
        }
    }

    private int getIndex(String userId) {
        Items items = adapter.getData();
        for (int i = 0; i < items.size(); i++) {
            Object data = items.get(i);
            if (data instanceof FriendItemViewModel) {
                if (((FriendItemViewModel) data).getUserId().equals(userId)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void updateFriendList() {
        try {
            List<FriendInfo> friendInfoList = Carrier.getInstance().getFriends();
            Items items = new Items();
            for (FriendInfo friendInfo : friendInfoList) {
                items.add(new FriendItemViewModel(friendInfo));
            }
            adapter.setData(items);
        } catch (ElastosException e) {
            e.printStackTrace();
        }
    }
}
