package com.elastos.chat.ui.item;

import android.text.TextUtils;
import android.view.TextureView;

import com.elastos.chat.MyApplication;
import com.elastos.chat.R;

import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;

/**
 * @author rczhang on 2018/05/08.
 */
public class FriendItemViewModel {
    private FriendInfo friendInfo;

    public FriendItemViewModel(FriendInfo friendInfo) {
        this.friendInfo = friendInfo;
    }

    public String getUserName() {
        if (TextUtils.isEmpty(friendInfo.getName())) {
            return MyApplication.getContext().getString(R.string.default_friend_user_name);
        } else {
            return friendInfo.getName();
        }
    }

    public String getUserId() {
        return friendInfo.getUserId();
    }

    public String getConnectStatus() {
        if (friendInfo.getConnectionStatus() == ConnectionStatus.Connected) {
            return "[在线]";
        } else {
            return "[离线]";
        }
    }
}
