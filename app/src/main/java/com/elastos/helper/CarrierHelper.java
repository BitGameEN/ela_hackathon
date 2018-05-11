package com.elastos.helper;

import com.elastos.chat.MyApplication;
import com.elastos.chat.util.ToastUtils;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.UserInfo;

/**
 * @author rczhang on 2018/05/10.
 */
public class CarrierHelper {

    public static boolean isReady;
    private static TestHandler handler = new TestHandler();

    private static CallbackHandler callback;

    public static void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TestOptions options = new TestOptions(getAppPath());
                    Carrier carrierInst = Carrier.getInstance(options, handler);
                    carrierInst.start(1000);
                    handler.synch.await();
                } catch (org.elastos.carrier.exceptions.ElastosException e) {
                    ToastUtils.shortT("Carrier初始化出错了，请退出重进");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void setCallback(CallbackHandler callback) {
        CarrierHelper.callback = callback;
    }

    public static class CallbackHandler extends AbstractCarrierHandler {

    }

    static class TestHandler extends AbstractCarrierHandler {
        Synchronizer synch = new Synchronizer();
        String CALLBACK = "call back";

        public void onReady(final Carrier carrier) {
            synch.wakeup();
            isReady = true;
            if (callback != null) {
                callback.onReady(carrier);
            }
        }

        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {
            if (callback != null) {
                callback.onFriendConnection(carrier, friendId, status);
            }
        }

        public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
            if (callback != null) {
                callback.onFriendRequest(carrier, userId, info, hello);
            }
        }

        public void onFriendMessage(Carrier carrier, String fromId, String message) {
            if (callback != null) {
                callback.onFriendMessage(carrier, fromId, message);
            }
        }
    }

    private static String getAppPath() {
        return MyApplication.getContext().getFilesDir().getAbsolutePath();
    }
}
