package com.maxfin.phoenixapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.maxfin.phoenixapp.managers.DialogManager;
import com.maxfin.phoenixapp.managers.StateManager;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.User;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;


import java.io.IOException;

public class XMPPServerConnection implements ConnectionListener, ReconnectionListener {
    private static final String TAG = "XMPPServerConnection";
    private static final int NOTIFY_ID = 42;
    private static final String CHANEL_ID = "new message";
    private final Context mApplicationContext;
    private final Context mContext;
    private final String mUsername;
    private final String mPassword;
    private final String mServiceName;
    private User mUser;
    private XMPPTCPConnection mConnection;
    private BroadcastReceiver uiThreadMessageReceiver;
    private StateManager mStateManager;


    public enum ConnectionXMPPState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }

    public enum LoggedInXMPPState {
        LOGGED_IN, LOGGED_OUT
    }


    public XMPPServerConnection(Context context) {
        Log.d(TAG, "XMPPServerConnection CONSTRUCTOR CALLED");
        mStateManager = StateManager.getStateManager();
        mApplicationContext = context.getApplicationContext();
        mContext = context;
//        String jid = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
//                .getString("xmpp_jid", null);
//        mPassword = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
//                .getString("xmpp_password", null);

        mUser = new User("maxfin@jabber.ru", "maxim4232", "44", "44", false);   // ВРЕМЕННЫЙ ПОЛЬЗОВАТЕЛЬ!!!!
        String jid = mUser.getJId();
        mPassword = mUser.getJPassword();


        if (jid != null) {
            mUsername = jid.split("@")[0];
            mServiceName = jid.split("@")[1];
        } else {
            mUsername = "";
            mServiceName = "";
        }
    }


    void connect() throws IOException, XMPPException, SmackException {
        Log.d(TAG, "CONNECTION TO SERVER");
        final XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setXmppDomain(mServiceName);
        builder.setUsernameAndPassword(mUsername, mPassword);
        //builder.setHostAddress()
        builder.setResource("Resource");
        builder.setKeystorePath("");

        setUiThreadMessageReceiver();

        mConnection = new XMPPTCPConnection(builder.build());
        mConnection.addConnectionListener(this);
        try {
            Log.d(TAG, "TRY CONNECTING");
            mConnection.connect();
            mConnection.login();
            Log.d(TAG, "LOG IN, YAY");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(mApplicationContext, "DISCONNECT", Toast.LENGTH_SHORT).show();
        }

        ChatManager.getInstanceFor(mConnection).addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

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

                DialogManager dialogManager = DialogManager.getDialogManager(mContext);
                dialogManager.addMessage(message.getBody(), true, contactJid);
                Contact contact = dialogManager.getContact(contactJid);


                createNotificationChannel(contactJid, "test");


                createNotification(contactJid, message.getBody(), contact.getName(), contact.getPhoto());


                Intent intent = new Intent(XMPPConnectionService.NEW_MESSAGE);
                intent.setPackage(mContext.getPackageName());
//                intent.putExtra(XMPPConnectionService.BUNDLE_FROM_JID,contactJid);
//                intent.putExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY,message.getBody());
                mContext.sendBroadcast(intent);


                Log.d(TAG, "RECEIVED MESSAGE FROM :" + contactJid + " BROADCAST SENT");


            }
        });

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        ReconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }

    private void createNotification(String jId, String messageBody, String name, String photo) {

        Intent dialogIntent = new Intent(mContext, DialogActivity.class);
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        dialogIntent.setAction("NEW_MESSAGE");

        dialogIntent.putExtra("EXTRA_CONTACT_JID", jId);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                0, dialogIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext, CHANEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_message_notification)
                .setContentTitle("Новое сообщение от " + name)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(5)
                .setContentIntent(pendingIntent).build();


        //  ? notification.contentView.setImageViewUri(android.R.id.icon,Uri.parse(photo));

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
            mConnection.disconnect();
        }

        mConnection = null;
    }


    private void setUiThreadMessageReceiver() {
        uiThreadMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(XMPPConnectionService.SEND_MESSAGE)) {

                    sendMessage(intent.getStringExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY),
                            intent.getStringExtra(XMPPConnectionService.BUNDLE_TO));
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(XMPPConnectionService.SEND_MESSAGE);
        mApplicationContext.registerReceiver(uiThreadMessageReceiver, filter);

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
            Message message = new Message(jid, Message.Type.chat);
            message.setBody(stringBody);
            chat.send(message);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void connected(XMPPConnection connection) {
        mStateManager.setConnectionXMPPState(ConnectionXMPPState.CONNECTED);
        Log.d(TAG, "CONNECTED SUCCESSFULLY");

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        mStateManager.setConnectionXMPPState(ConnectionXMPPState.CONNECTED);
        Log.d(TAG, "AUTHENTICATED SUCCESSFULLY");


    }

    @Override
    public void connectionClosed() {
        mStateManager.setConnectionXMPPState(ConnectionXMPPState.DISCONNECTED);
        Log.d(TAG, "CONNECTION CLOSED");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        mStateManager.setConnectionXMPPState(ConnectionXMPPState.DISCONNECTED);
        Log.d(TAG, "CONNECTION CLOSED ON ERROR" + e.toString());

    }


    @Override
    public void reconnectingIn(int seconds) {
        mStateManager.setConnectionXMPPState(ConnectionXMPPState.CONNECTING);
        Log.d(TAG, "RECONNECTION");

    }


    @Override
    public void reconnectionFailed(Exception e) {
        mStateManager.setConnectionXMPPState(ConnectionXMPPState.DISCONNECTED);
        Log.d(TAG, "RECONNECTION FAILED");

    }


}
