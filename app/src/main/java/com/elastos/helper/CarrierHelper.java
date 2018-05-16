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
import org.elastos.carrier.session.ManagerHandler;
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
    private static String sessionRequestFrom;
    private static String sessionRequestSdp;
    private static Session activeSession;
    private static Stream  activeStream;
    private static Synchronizer waitObj;

    private static CallbackHandler callback;

    public static void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    waitObj = new Synchronizer();
                    TestOptions options = new TestOptions(getAppPath());
                    Carrier carrierInst = Carrier.getInstance(options, handler);
                    carrierInst.start(1000);
                    sessionMgr = Manager.getInstance(carrierInst, new ManagerHandler() {
                        @Override
                        public void onSessionRequest(Carrier carrier, String from, String sdp) {
                            sessionRequestFrom = from;
                            sessionRequestSdp = sdp;
                            waitObj.wakeup();
                        }
                    });
                    waitObj.wakeup();
                    handler.synch.await();
                } catch (org.elastos.carrier.exceptions.ElastosException e) {
                    ToastUtils.shortT("Carrier初始化出错了，请退出重进");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void destroy() {
        if (Carrier.getInstance() != null) {
            if(sessionMgr != null) {
                sessionMgr.cleanup();
                sessionMgr = null;
            }
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

        @Override
        public void onStateChanged(Stream stream, StreamState state) {
            Log.i(TAG, "Stream " + stream.toString() + "'s state changed to be " + state.name());
            this.stream = stream;
            this.state = state;
            synch.wakeup();
            try {
                switch (state) {
                    case Initialized:
                        activeSession.replyRequest(0, null);
                        break;
                    case TransportReady:
                        activeSession.start(sessionRequestSdp);
                        break;
                    case Connected:
                        waitObj.wakeup();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] receivedData = new byte[4096 * 2048];
        int receivedLen = 0;
        int toReceiveLen = 0;
        int receiveStage = 0; // 0 - 获取4字节header阶段，1 - 接收body阶段

        @Override
        public void onStreamData(Stream stream, byte[] data) {
            // 需要处理粘包
            this.stream = stream;
            int len = receivedLen + data.length;
            if (receiveStage == 0) {
                if (len >= 4) {
                    System.arraycopy(data, 0, this.receivedData, receivedLen, 4 - receivedLen);
                    toReceiveLen = bytesToInt(this.receivedData, 0);
                    receiveStage = 1;
                    if (len > 4) {
                        System.arraycopy(data, 4 - receivedLen, this.receivedData, 0, len - 4);
                    }
                    receivedLen = len - 4;
                } else {
                    System.arraycopy(data, 0, this.receivedData, receivedLen, data.length);
                    receivedLen += data.length;
                }
            } else {
                if (len >= toReceiveLen) {
                    int copyLen = 0, restLen = 0;
                    copyLen = toReceiveLen - receivedLen;
                    restLen = len - toReceiveLen;
                    System.arraycopy(data, 0, this.receivedData, receivedLen, copyLen);
                    // todo：body数据接收完整，处理数据，然后重置标记
                    receivedLen = 0;
                    toReceiveLen = 0;
                    receiveStage = 0;
                    if (restLen > 4) {
                        receivedLen = restLen - 4;
                        toReceiveLen = bytesToInt(data, copyLen);
                        receiveStage = 1;
                        System.arraycopy(data, data.length - restLen + 4, this.receivedData, 0, restLen - 4);
                    } else {
                        receivedLen = restLen;
                        if (restLen > 0) {
                            System.arraycopy(data, data.length - restLen, this.receivedData, 0, restLen);
                        }
                    }
                } else {
                    System.arraycopy(data, 0, this.receivedData, receivedLen, data.length);
                    receivedLen += data.length;
                }
            }
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

    public static boolean sendDataWithSession(String toUid, byte[] data) {
        try {
            Session session = sessionMgr.newSession(toUid);
            TestStreamHandler streamHandler = new TestStreamHandler();
            Stream stream = session.addStream(StreamType.Text, Stream.PROPERTY_RELIABLE, streamHandler);
            streamHandler.synch.await();
            TestSessionRequestCompleteHandler reqCompleteHandler = new TestSessionRequestCompleteHandler();
            session.request(reqCompleteHandler);
            streamHandler.synch.await();
            waitObj.await();
            Carrier.getInstance().sendFriendMessage(toUid, "MSG_REPLY_SESSION_REQUST_AND_START");
            reqCompleteHandler.synch.await();
            session.start(reqCompleteHandler.sdp);
            streamHandler.synch.await();
            streamHandler.synch.await();

            int chunkSize = 2048;
            byte toSendBytes[] = new byte[chunkSize];
            int totalBytes = data.length;
            int bytesSent = 0;
            int rows = (int)Math.ceil((double)totalBytes / chunkSize);
            ToastUtils.shortT("即将发送" + String.valueOf(totalBytes) + "字节，分" + String.valueOf(rows) + "次");
            stream.writeData(intToBytes(totalBytes));
            for (int i = 0; i < rows; i++) {
                int toSendBytesCount = Math.min(chunkSize, totalBytes - i * chunkSize);
                if (toSendBytesCount == chunkSize) {
                    System.arraycopy(data, i * chunkSize, toSendBytes, 0, chunkSize);
                    stream.writeData(toSendBytes);
                } else {
                    byte toSendRestBytes[] = new byte[toSendBytesCount];
                    System.arraycopy(data, i * chunkSize, toSendRestBytes, 0, toSendBytesCount);
                    stream.writeData(toSendRestBytes);
                }
                bytesSent += toSendBytesCount;
                ToastUtils.shortT("已发送" + String.valueOf(bytesSent) + "字节");
            }

            session.removeStream(stream);
            session.close();
            return true;
        } catch (org.elastos.carrier.exceptions.ElastosException e) {
            Log.e(TAG, "error: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public static void replySessionRequestAndStart() {
        try {
            activeSession = sessionMgr.newSession(sessionRequestFrom);
            TestStreamHandler streamHandler = new TestStreamHandler();
            activeStream = activeSession.addStream(StreamType.Text, Stream.PROPERTY_RELIABLE, streamHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getAppPath() {
        return MyApplication.getContext().getFilesDir().getAbsolutePath();
    }

    public static byte[] intToBytes(int value)
    {
        byte[] byte_src = new byte[4];
        byte_src[3] = (byte) ((value & 0xFF000000)>>24);
        byte_src[2] = (byte) ((value & 0x00FF0000)>>16);
        byte_src[1] = (byte) ((value & 0x0000FF00)>>8);
        byte_src[0] = (byte) ((value & 0x000000FF));
        return byte_src;
    }

    public static int bytesToInt(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset]&0xFF)
                | ((ary[offset+1]<<8) & 0xFF00)
                | ((ary[offset+2]<<16)& 0xFF0000)
                | ((ary[offset+3]<<24) & 0xFF000000));
        return value;
    }
}
