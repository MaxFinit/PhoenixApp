package com.maxfin.phoenixapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.maxfin.phoenixapp.managers.DialogManager;
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
    private final Context mApplicationContext;
    private final String mUsername;
    private final String mPassword;
    private final String mServiceName;
    private User mUser;
    private XMPPTCPConnection mConnection;
    private BroadcastReceiver uiThreadMessageReceiver;


    public static enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }

    public static enum LoggedInState {
        LOGGED_IN, LOGGED_OUT
    }


    public XMPPServerConnection(Context context) {
        Log.d(TAG, "XMPPServerConnection constructor вызван");
        mApplicationContext = context.getApplicationContext();
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


    public void connect() throws IOException, XMPPException, SmackException {
        Log.d(TAG, "Connection to server");
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setXmppDomain(mServiceName);
        builder.setUsernameAndPassword(mUsername, mPassword);
        //builder.setHostAddress()
        builder.setResource("Resource");
        builder.setKeystorePath("");

        setUiThreadMessageReceiver();

        mConnection = new XMPPTCPConnection(builder.build());
        mConnection.addConnectionListener(this);
        try {
            Log.d(TAG, "Попытка подключения");
            mConnection.connect();
            mConnection.login();
            Log.d(TAG, "Залогинились, ура");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(mApplicationContext,"Дисконект",Toast.LENGTH_SHORT).show();
        }

        ChatManager.getInstanceFor(mConnection).addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

                Log.d(TAG,"message.getBody() :"+message.getBody());
                Log.d(TAG,"message.getFrom() :"+message.getFrom());

                String fromWho = message.getFrom().toString();

                String contactJid="";

                if (fromWho.contains("/")){
                    contactJid = fromWho.split("/")[0];
                    Log.d(TAG,"The real jid is :" +contactJid);
                    Log.d(TAG,"The message is from :" +fromWho);


                } else {
                    contactJid=fromWho;
                }

                DialogManager dialogManager = DialogManager.getDialogManager(mApplicationContext);
                dialogManager.addMessage(message.getBody(),true,contactJid);


                Intent intent = new Intent(XMPPConnectionService.NEW_MESSAGE);
                intent.setPackage(mApplicationContext.getPackageName());
//                intent.putExtra(XMPPConnectionService.BUNDLE_FROM_JID,contactJid);
//                intent.putExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY,message.getBody());
                mApplicationContext.sendBroadcast(intent);





                Log.d(TAG,"Received message from :"+contactJid+" broadcast sent.");






            }
        });

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        ReconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }


    public void disconnect() {
        Log.d(TAG, "Disconnecting from server " + mServiceName);
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
        mApplicationContext.registerReceiver(uiThreadMessageReceiver,filter);
        
    }

    private void sendMessage(String stringBody, String stringJId) {
        Log.d(TAG,"Sending message to :"+ stringJId);

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
        XMPPConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG, "Connected Successfully");

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        XMPPConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG, "Authenticated Successfully");


    }

    @Override
    public void connectionClosed() {
        XMPPConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "Connectionclosed()");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        XMPPConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "ConnectionClosedOnError, error " + e.toString());

    }


    @Override
    public void reconnectingIn(int seconds) {
        XMPPConnectionService.sConnectionState = ConnectionState.CONNECTING;
        Log.d(TAG, "ReconnectingIn() ");

    }


    @Override
    public void reconnectionFailed(Exception e) {
        XMPPConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "ReconnectionFailed()");

    }


}
