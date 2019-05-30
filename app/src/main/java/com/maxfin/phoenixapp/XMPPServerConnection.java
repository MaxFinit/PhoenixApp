package com.maxfin.phoenixapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.maxfin.phoenixapp.activities.DialogActivity;
import com.maxfin.phoenixapp.managers.DialogManager;
import com.maxfin.phoenixapp.managers.StateManager;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.User;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.sm.StreamManagementException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Objects;

public class XMPPServerConnection implements ConnectionListener, ReconnectionListener, ReceiptReceivedListener {
    private static final String TAG = "XMPPServerConnection";
    private static final int NOTIFY_ID = 42;
    private static final String CHANEL_ID = "new message";
    private final Context mApplicationContext;
    private final Context mContext;
    private final String mUsername;
    private final String mServiceName;
    private User mUser;
    private XMPPTCPConnection mConnection;
    private StateManager mStateManager;
    private ConnectionXMPPState mConnectionXMPPState = ConnectionXMPPState.DISCONNECTED;
    private OnStateCallback mOnXMPPConnectionStateCallback;
    private static XMPPServerConnection mXMPPServerConnection;

    public enum ConnectionXMPPState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED, RECONNECTED
    }

    public enum LoggedInXMPPState {
        LOGGED_IN, LOGGED_OUT
    }

    public static XMPPServerConnection getXMPPServerConnection(Context context) {
        if (mXMPPServerConnection == null) {
            mXMPPServerConnection = new XMPPServerConnection(context);
        }
        return mXMPPServerConnection;
    }

    private XMPPServerConnection(Context context) {

//        Настройки получения статуса отправки
//        DeliveryReceiptManager.setDefaultAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
//        ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
//        ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceiptRequest.Provider());

        Log.d(TAG, "XMPPServerConnection CONSTRUCTOR CALLED");
        mStateManager = StateManager.getStateManager();
        mApplicationContext = context;
        mContext = context;

        mUser = new User("maxfin@jabber.ru", "maxim4232", "44", "44", false);   // ВРЕМЕННЫЙ ПОЛЬЗОВАТЕЛЬ!!!!

        if (mUser.getJId() != null) {
            mUsername = mUser.getJId().split("@")[0];
            mServiceName = mUser.getJId().split("@")[1];
        } else {
            mUsername = "";
            mServiceName = "";
        }

    }


    public void onXMPPStateConnectionChanged(OnStateCallback eventListener) {
        Log.d(TAG, "СМЕНА СОСТОЯНИЯ: " + mConnectionXMPPState);
        if (eventListener != null) {
            mStateManager.setConnectionXMPPState(mConnectionXMPPState);
            mOnXMPPConnectionStateCallback = eventListener;
            mOnXMPPConnectionStateCallback.onStateChanged();
        }
    }


    public void connect() throws IOException, XMPPException, SmackException {
        Log.d(TAG, "CONNECTION TO SERVER");
        final XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setXmppDomain(mServiceName);
        builder.setUsernameAndPassword(mUsername, mUser.getJPassword());
        builder.setResource("Resource");
        builder.setKeystorePath("");

        setUiThreadMessageReceiver();

        mConnection = new XMPPTCPConnection(builder.build());
        mConnection.addConnectionListener(this);


        try {
            Log.d(TAG, "TRY CONNECTING");
            mConnectionXMPPState = ConnectionXMPPState.CONNECTING;
            onXMPPStateConnectionChanged(mOnXMPPConnectionStateCallback);
            mConnection.connect();
            mConnection.login();

            DeliveryReceiptManager deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(mConnection);
            deliveryReceiptManager.addReceiptReceivedListener(this);

            Log.d(TAG, "LOG IN, YAY");

        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(mApplicationContext, "DISCONNECT", Toast.LENGTH_SHORT).show();
        }


        ChatManager.getInstanceFor(mConnection).addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

                message.addExtension(new DeliveryReceipt(message.getStanzaId()));

                Log.d(TAG, "MESSAGES FROM :" + message.getFrom());
                Log.d(TAG, "MESSAGES TEXT :" + message.getBody());

                String fromWho = message.getFrom().toString();

                String contactJid;

                if (fromWho.contains("/")) {
                    contactJid = fromWho.split("/")[0];
                    Log.d(TAG, "THE REAL JID IS :" + contactJid);
                    Log.d(TAG, "THE MESSAGE IS FROM :" + fromWho);


                } else {
                    contactJid = fromWho;
                }

                DialogManager dialogManager = DialogManager.getDialogManager();
                dialogManager.addMessage(message.getBody(), true, contactJid);
                Contact contact = dialogManager.getContact(contactJid);

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mApplicationContext.getContentResolver(), Uri.parse(contact.getPhoto()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                createNotificationChannel(contactJid, "test");
                createNotification(contactJid, message.getBody(), contact.getName(), bitmap);


                Intent intent = new Intent(XMPPConnectionService.NEW_MESSAGE);
                intent.setPackage(mContext.getPackageName());
                mContext.sendBroadcast(intent);

                Log.d(TAG, "RECEIVED MESSAGE FROM :" + contactJid + " BROADCAST SENT");
            }
        });

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        ReconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }

    private void sendMessage(String stringBody, String stringJId) {
        Log.d(TAG, "SENDING MESSAGE TO :" + stringJId);

        EntityBareJid jid = null;

        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
        try {
            jid = JidCreate.entityBareFrom(stringJId);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        Chat chat = chatManager.chatWith(jid);
        try {
            Message message;
            message = new Message(jid, Message.Type.chat);
            DeliveryReceiptRequest.addTo(message);
            message.setType(Message.Type.chat);
            message.setBody(stringBody);
            chat.send(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void isAlive(Context context) {
        Intent isAliveIntent = new Intent(XMPPConnectionService.SEND_MESSAGE);
        isAliveIntent.putExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY, "");
        isAliveIntent.putExtra(XMPPConnectionService.BUNDLE_TO, "maxfin@jabber.ru");
        context.sendBroadcast(isAliveIntent);
    }


    private void createNotification(String jId, String messageBody, String name, Bitmap bitmap) {

        Intent dialogIntent = new Intent(mContext, DialogActivity.class);
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        dialogIntent.setAction("NEW_MESSAGE");

        dialogIntent.putExtra("EXTRA_CONTACT_JID", jId);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                0, dialogIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext, CHANEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_message_notification)
                .setColor(mContext.getResources().getColor(R.color.colorPurple100))
                .setLargeIcon(bitmap)
                .setContentTitle("Новое сообщение от " + name)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(NOTIFY_ID, notification);
    }


    private void createNotificationChannel(String nameD, String desc) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANEL_ID, nameD, importance);
            channel.setDescription(desc);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mApplicationContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    void disconnect() {
        Log.d(TAG, "DISCONNECTING FROM SERVER" + mServiceName);
        if (mConnection != null) {
            mStateManager.setConnectionXMPPState(ConnectionXMPPState.DISCONNECTED);
            mConnection.disconnect();
        }
        mConnection = null;
    }


    private void setUiThreadMessageReceiver() {
        BroadcastReceiver uiThreadMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (Objects.requireNonNull(intent.getAction())) {
                    case XMPPConnectionService.SEND_MESSAGE:
                        sendMessage(intent.getStringExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY),
                                intent.getStringExtra(XMPPConnectionService.BUNDLE_TO));
                        break;
                    case XMPPConnectionService.RECEIVED_MESSAGE:
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(XMPPConnectionService.SEND_MESSAGE);
        filter.addAction(XMPPConnectionService.RECEIVED_MESSAGE);
        mApplicationContext.registerReceiver(uiThreadMessageReceiver, filter);
    }

    @Override
    public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
        Log.d(TAG, "RECEIVER MESSAGE");
        Intent intent = new Intent(XMPPConnectionService.RECEIVED_MESSAGE);
        intent.setPackage(mContext.getPackageName());
//                intent.putExtra(XMPPConnectionService.BUNDLE_FROM_JID,contactJid);
//                intent.putExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY,message.getBody());
        mContext.sendBroadcast(intent);

    }

    private void acknowledgementFromServer(final Message message) throws StreamManagementException.StreamManagementNotEnabledException {
        if (mConnection != null && mConnection.isSmEnabled()) {
            mConnection.addStanzaIdAcknowledgedListener(message.getStanzaId(), new StanzaListener() {
                @Override
                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {


                }
            });
        }
    }


    @Override
    public void connected(XMPPConnection connection) {
        mConnectionXMPPState = ConnectionXMPPState.CONNECTED;
        onXMPPStateConnectionChanged(mOnXMPPConnectionStateCallback);
        Log.d(TAG, "CONNECTED SUCCESSFULLY");

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        mConnectionXMPPState = ConnectionXMPPState.CONNECTED;
        onXMPPStateConnectionChanged(mOnXMPPConnectionStateCallback);
        Log.d(TAG, "AUTHENTICATED SUCCESSFULLY");


    }

    @Override
    public void connectionClosed() {
        mConnectionXMPPState = ConnectionXMPPState.DISCONNECTED;
        onXMPPStateConnectionChanged(mOnXMPPConnectionStateCallback);
        Log.d(TAG, "CONNECTION CLOSED");
        Intent intent = new Intent(mContext.getApplicationContext(), XMPPConnectionService.class);
        mApplicationContext.stopService(intent);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        mConnectionXMPPState = ConnectionXMPPState.DISCONNECTED;
        onXMPPStateConnectionChanged(mOnXMPPConnectionStateCallback);
        Log.d(TAG, "CONNECTION CLOSED ON ERROR" + e.toString());


    }


    @Override
    public void reconnectingIn(int seconds) {
        mConnectionXMPPState = ConnectionXMPPState.RECONNECTED;
        onXMPPStateConnectionChanged(mOnXMPPConnectionStateCallback);
        Log.d(TAG, "RECONNECTION");

    }


    @Override
    public void reconnectionFailed(Exception e) {
        mConnectionXMPPState = ConnectionXMPPState.DISCONNECTED;
        onXMPPStateConnectionChanged(mOnXMPPConnectionStateCallback);
        Log.d(TAG, "RECONNECTION FAILED");

    }


}
