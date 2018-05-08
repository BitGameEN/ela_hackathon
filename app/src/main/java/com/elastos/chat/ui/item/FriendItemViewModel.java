package com.elastos.chat.ui.item;

import org.elastos.carrier.FriendInfo;

/**
 * @author rczhang on 2018/05/08.
 */
public class FriendItemViewModel {
    private FriendInfo friendInfo;

    public FriendItemViewModel(FriendInfo friendInfo) {
        this.friendInfo = friendInfo;
    }

    public String getUserId() {
        return friendInfo.getUserId();
    }
}
