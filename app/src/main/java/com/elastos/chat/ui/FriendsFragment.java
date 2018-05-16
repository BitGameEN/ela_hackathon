package com.elastos.chat.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.elastos.chat.R;
import com.elastos.chat.SharedPreferencesHelper;
import com.elastos.chat.activity.MainActivity;
import com.elastos.chat.adapter.BaseTypeAdapter;
import com.elastos.chat.ui.item.FriendItemViewBinder;
import com.elastos.chat.ui.item.FriendItemViewModel;
import com.elastos.chat.util.AndroidUtilities;
import com.elastos.chat.util.ToastUtils;
import com.elastos.helper.BusProvider;
import com.squareup.otto.Subscribe;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import me.drakeet.multitype.Items;

/**
 * @author rczhang on 2018/05/08.
 */
public class FriendsFragment extends BaseFragment {

    public static int DISAPPEAR_SECOND_TIME = 5;

    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.imageview) ImageView imageView;

    private BaseTypeAdapter adapter;
    private EditText etFriendAddress;
    private Button butAddFriend;

    private Timer timer;

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

        BusProvider.getInstance().register(this);

        etFriendAddress = view.findViewById(R.id.home_et_friend_address);
        butAddFriend = view.findViewById(R.id.home_but_add_friend);
        butAddFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String friendadd = etFriendAddress.getText().toString();
                if (!friendadd.equals("")) {
                    try {
                        Log.v("", "向" + friendadd + "发送好友申请");
                        String selfAddr = Carrier.getInstance().getAddress();
                        Carrier.getInstance().addFriend(friendadd, selfAddr);
                        SharedPreferencesHelper.put(Carrier.getIdFromAddress(friendadd), friendadd);
                        ToastUtils.shortT("添加好友成功");
                    } catch (ElastosException e) {
                        e.printStackTrace();
                        ToastUtils.shortT("添加好友失败");
                    }
                }
            }
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onPageFirstStart() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                updateFriendItem();
            }
        };
        timer.schedule(timerTask, 0, 1000);
        updateFriendList();
    }

    private void updateFriendItem() {
        Items items = adapter.getData();
        final List<Integer> updatePositionList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            if (!(item instanceof FriendItemViewModel)) {
                continue;
            }
            FriendItemViewModel friendItemViewModel = (FriendItemViewModel) item;
            if (friendItemViewModel.getTime() <= 0) {
                continue;
            }
            friendItemViewModel.setTime(friendItemViewModel.getTime() - 1);
            if (friendItemViewModel.getTime() <= 0) {
                updatePositionList.add(i);
                friendItemViewModel.setMessage(null);
            }
        }
        if (updatePositionList.size() > 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    for (Integer integer : updatePositionList) {
                        adapter.notifyItemChanged(integer);
                    }
                }
            });
        }
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

    private void updateFriendList() {
        if (!isAdded()) {
            return;
        }
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

    public void updateFriendStatus(String friendId, ConnectionStatus status) {
        if (!isAdded()) {
            return;
        }
        int index = getIndex(friendId);
        if (index < 0) {
            return;
        }
        ((FriendItemViewModel) adapter.getData().get(index)).setConnectStatus(status);
        adapter.notifyItemChanged(index);
    }

    public void addFriend(FriendInfo info) {
        if (!isAdded()) {
            return;
        }
        Items items = new Items();
        items.add(new FriendItemViewModel(info));
        adapter.addData(items);
    }

    public void removeFriend(String friendId) {
        if (!isAdded()) {
            return;
        }
        int index = getIndex(friendId);
        if (index < 0) {
            return;
        }
        adapter.removeItem(index);
    }

    public void updateFriendInfo(String friendId, FriendInfo info) {
        if (!isAdded()) {
            return;
        }
        int index = getIndex(friendId);
        if (index < 0) {
            return;
        }
        ((FriendItemViewModel) adapter.getData().get(index)).setFriendInfo(info);
        adapter.notifyItemChanged(index);
    }

    @Subscribe
    public void onMessageEvent(MainActivity.FriendMessage friendMessage) {
        int index = getIndex(friendMessage.getsFriendId());
        if (index < 0) {
            return;
        }
        FriendItemViewModel data = ((FriendItemViewModel) adapter.getData().get(index));
        data.setMessage(friendMessage.getsMessage());
        data.setTime(DISAPPEAR_SECOND_TIME);
        adapter.notifyItemChanged(index);
    }

    @Subscribe
    public void onMessageEvent(MainActivity.FriendStreamData friendStreamData) {
        // 目前只支持图片
        byte[] bytes = friendStreamData.getBytes();
        int len = friendStreamData.getLen();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, len);
        imageView.setImageBitmap(bitmap);
    }
}
