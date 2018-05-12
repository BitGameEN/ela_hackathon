package com.elastos.helper;

import com.elastos.chat.MyApplication;
import com.elastos.chat.util.ToastUtils;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.Log;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.session.AbstractStreamHandler;
import org.elastos.carrier.session.Manager;
import org.elastos.carrier.session.Session;
import org.elastos.carrier.session.SessionRequestCompleteHandler;
import org.elastos.carrier.session.Stream;
import org.elastos.carrier.session.StreamState;
import org.elastos.carrier.session.StreamType;

/**
 * @author rczhang on 2018/05/10.
 */
public class CarrierHelper {

    private static final String TAG = "CarrierHelper";
    public static boolean isReady;
    private static TestHandler handler = new TestHandler();
    private static Manager sessionMgr;

    private static CallbackHandler callback;

    public static void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TestOptions options = new TestOptions(getAppPath());
                    Carrier carrierInst = Carrier.getInstance(options, handler);
                    carrierInst.start(1000);
                    sessionMgr = Manager.getInstance(carrierInst);
                    handler.synch.await();
                } catch (org.elastos.carrier.exceptions.ElastosException e) {
                    ToastUtils.shortT("Carrier初始化出错了，请退出重进");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void destroy() {
        if (Carrier.getInstance() != null) {
            sessionMgr.cleanup();
            Carrier.getInstance().kill();
        }
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
            Log.d("call back", "friendid:" + friendId + "connection changed to: " + status);
            if (callback != null) {
                callback.onFriendConnection(carrier, friendId, status);
            }
        }

        @Override
        public void onFriendAdded(Carrier carrier, FriendInfo info) {
            Log.d(TAG, "onFriendAdded");
            if (callback != null) {
                callback.onFriendAdded(carrier, info);
            }
        }

        @Override
        public void onFriendRemoved(Carrier carrier, String friendId) {
            Log.d(TAG, "onFriendRemoved");
            if (callback != null) {
                callback.onFriendRemoved(carrier, friendId);
            }
        }

        public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
            if (callback != null) {
                callback.onFriendRequest(carrier, userId, info, hello);
            }
        }

        @Override
        public void onFriendInfoChanged(Carrier carrier, String friendId, FriendInfo info) {
            if (callback != null) {
                callback.onFriendInfoChanged(carrier, friendId, info);
            }
        }

        public void onFriendMessage(Carrier carrier, String fromId, String message) {
            if (callback != null) {
                callback.onFriendMessage(carrier, fromId, message);
            }
        }
    }

    static class TestStreamHandler extends AbstractStreamHandler {
        Synchronizer synch = new Synchronizer();
        Stream stream;
        StreamState state;

        byte[] receivedData;

        @Override
        public void onStateChanged(Stream stream, StreamState state) {
            Log.i(TAG, "Stream " + stream.toString() + "'s state changed to be " + state.name());
            this.stream = stream;
            this.state = state;
            synch.wakeup();
        }

        @Override
        public void onStreamData(Stream stream, byte[] data) {
            this.stream = stream;
            this.receivedData = data;
            synch.wakeup();
        }
    }

    static class TestSessionRequestCompleteHandler implements SessionRequestCompleteHandler {
        Synchronizer synch = new Synchronizer();

        Session session;
        int status;
        String reason;
        String sdp;

        public void onCompletion(Session session, int status, String reason, String sdp) {
            this.session = session;
            this.status = status;
            this.reason = reason;
            this.sdp = sdp;

            synch.wakeup();
        }
    }

    public void sendDataWithSession(String toUid, byte[] data) {
        try {
            Session session = sessionMgr.newSession(toUid);
            TestStreamHandler streamHandler = new TestStreamHandler();
            Stream stream = session.addStream(StreamType.Text, 0, streamHandler);
            streamHandler.synch.await();
            TestSessionRequestCompleteHandler reqCompleteHandler = new TestSessionRequestCompleteHandler();
            session.request(reqCompleteHandler);
            streamHandler.synch.await();
            reqCompleteHandler.synch.await();
            session.start(reqCompleteHandler.sdp);
            streamHandler.synch.await();
            streamHandler.synch.await();

            stream.writeData(data);
            streamHandler.synch.await();

            session.removeStream(stream);
            session.close();
        } catch (org.elastos.carrier.exceptions.ElastosException e) {
            Log.e(TAG, "error: " + e.getErrorCode());
            e.printStackTrace();
        }
    }

    private static String getAppPath() {
        return MyApplication.getContext().getFilesDir().getAbsolutePath();
    }
}
