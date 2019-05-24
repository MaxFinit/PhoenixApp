package com.maxfin.phoenixapp.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfin.phoenixapp.OnStateCallback;
import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.XMPPConnectionService;
import com.maxfin.phoenixapp.XMPPServerConnection;
import com.maxfin.phoenixapp.managers.DialogManager;
import com.maxfin.phoenixapp.managers.StateManager;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DialogActivity extends AppCompatActivity {
    private static final String TAG = "DialogActivity";

    private TextView mEmptyDialogTextView;
    private TextView mToolbarStateTextView;
    private EditText mMessageEditText;
    private ImageButton mRefreshConnectionButton;
    private RecyclerView mMessagesRecyclerView;


    private DialogAdapter mAdapter;
    private DialogManager mDialogManager;
    private StateManager mStateManager;
    private XMPPServerConnection mXMPPServerConnection;

    private BroadcastReceiver mBroadcastReceiver;

    private String contactJID;
    private Contact mContact;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Toolbar dialogToolbar = findViewById(R.id.dialog_tool_bar);
        setSupportActionBar(dialogToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mEmptyDialogTextView = findViewById(R.id.empty_dialog);
        mMessageEditText = findViewById(R.id.input_text_message);
        mToolbarStateTextView = findViewById(R.id.connect_status_toolbar);


        mXMPPServerConnection = XMPPServerConnection.getXMPPServerConnection(this);
        mStateManager = StateManager.getStateManager();


        Intent intent = getIntent();
        contactJID = intent.getStringExtra("EXTRA_CONTACT_JID");
        mDialogManager = DialogManager.getDialogManager();
        mContact = mDialogManager.getContact(contactJID);


        Button sendMessageButton = findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mMessageEditText.getText().toString().equals("")) {

                    if (mStateManager.getConnectionXMPPState().equals(XMPPServerConnection.ConnectionXMPPState.CONNECTED)) {
                        Log.d(TAG, "SEND MESSAGE, CLIENT IS CONNECTING");

                        Intent intent = new Intent(XMPPConnectionService.SEND_MESSAGE);
                        intent.putExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY, mMessageEditText.getText().toString());
                        intent.putExtra(XMPPConnectionService.BUNDLE_TO, contactJID);

                        mDialogManager.addMessage(mMessageEditText.getText().toString(), false, contactJID);
                        mMessageEditText.setText("");

                        sendBroadcast(intent);
                        updateUi();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "CLIENT NOT CONNECTED TO SERVER, MESSAGE NOT SENT!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); // Отображает список с конца.
        mMessagesRecyclerView = findViewById(R.id.messages_recycler_view);
        mMessagesRecyclerView.setLayoutManager(linearLayoutManager);
        mMessagesRecyclerView.setHasFixedSize(true);
        mMessagesRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mMessagesRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mMessagesRecyclerView.scrollToPosition((Objects.requireNonNull(mMessagesRecyclerView.getAdapter()).
                                    getItemCount() - 1));

                        }
                    });
                }

            }
        });


        mRefreshConnectionButton = findViewById(R.id.dialog_refresh_connection_toolbar);
        mRefreshConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), XMPPConnectionService.class);
                startService(intent);

            }


        });


        mXMPPServerConnection.onXMPPStateConnectionChanged(new OnStateCallback() {
            @Override
            public void onStateChanged() {
                updateState();
            }
        });


        updateUi();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dialog_tool_bar_menu, menu);
        CircleImageView imageView = findViewById(R.id.contact_photo_toolbar);
        TextView textView = findViewById(R.id.contact_name_toolbar);


        imageView.setImageURI(Uri.parse(mContact.getPhoto()));
        textView.setText(mContact.getName());

        return true;
    }


    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (XMPPConnectionService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateState() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (mStateManager.getConnectionXMPPState()) {

                    case CONNECTED:
                        mToolbarStateTextView.setText("Сообщения");
                        mRefreshConnectionButton.setVisibility(View.GONE);
                        break;
                    case CONNECTING:
                        mToolbarStateTextView.setText("Подключение");
                        mRefreshConnectionButton.setVisibility(View.GONE);
                        break;
                    case DISCONNECTED:
                        mToolbarStateTextView.setText("Потеря соединения");
                        mRefreshConnectionButton.setVisibility(View.VISIBLE);
                        break;
                    case AUTHENTICATED:
                        mToolbarStateTextView.setText("Аутификация");
                        mRefreshConnectionButton.setVisibility(View.GONE);
                        break;
                    case DISCONNECTING:
                        mToolbarStateTextView.setText("Ожидания подключения");
                        mRefreshConnectionButton.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_history_dialog_menu:
                showDialog();
                break;
            case android.R.id.home:
                Intent toDialogList = new Intent(DialogActivity.this, DialogListActivity.class);
                startActivity(toDialogList);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mXMPPServerConnection.isAlive()) {
            Intent intent = new Intent(getApplicationContext(), XMPPConnectionService.class);
            startService(intent);
            updateState();
        }


        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (Objects.requireNonNull(action)) {
                    case XMPPConnectionService.NEW_MESSAGE:
                        //    mDialogManager.addMessage(intent.getStringExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY), true,contactJID);
                        updateUi();
                        break;
                    default:
                        updateUi();
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter(XMPPConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, filter);
        updateUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }


    private void updateUi() {
        List<Message> mMessageList = mDialogManager.getMessageList(contactJID);

        if (mMessageList.size() > 0) {
            mMessagesRecyclerView.setVisibility(View.VISIBLE);
            mEmptyDialogTextView.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new DialogActivity.DialogAdapter(mMessageList);
                mMessagesRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setContacts(mMessageList);
                mAdapter.notifyDataSetChanged();
                mMessagesRecyclerView.scrollToPosition(Objects.requireNonNull(mMessagesRecyclerView.getAdapter())
                        .getItemCount() - 1);//Прокрутка списка в конец
                Log.d(TAG, mMessageList.size() + "" + (mMessagesRecyclerView.getAdapter().getItemCount() - 1));
            }
        } else {
            mMessagesRecyclerView.setVisibility(View.GONE);
            mEmptyDialogTextView.setVisibility(View.VISIBLE);
        }


    }


    private void showDialog() {
        AlertDialog.Builder alertDialog;

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Удалить историю");
        alertDialog.setMessage("Все сообщения пропадут.Вы уверены?");
        alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialogManager.deleteMessageList(contactJID);
                updateUi();
            }
        });
        alertDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }


    private class DialogOutputHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView mOutputMessageTextView;
        TextView mOutputTimeTextView;
        Message mMessage;


        DialogOutputHolder(View view) {
            super(view);
            view.setOnCreateContextMenuListener(this);
            mOutputMessageTextView = itemView.findViewById(R.id.text_output_message);
            mOutputTimeTextView = itemView.findViewById(R.id.time_output_message);
        }

        void bind(Message message) {
            mMessage = message;
            mOutputMessageTextView.setText(message.getTextMessage());
            mOutputTimeTextView.setText(message.getDateMessage());
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_dialog_menu, contextMenu);
            MenuItem delete = contextMenu.getItem(0);
            MenuItem copy = contextMenu.getItem(1);
            delete.setOnMenuItemClickListener(mOnMenuItemClickListener);
            copy.setOnMenuItemClickListener(mOnMenuItemClickListener);
        }

        private final MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete_message_context_menu:
                        mDialogManager.deleteMessage(mMessage);
                        Log.d(TAG, "DELETE MESSAGE");
                        updateUi();
                        break;
                    case R.id.copy_to_buffer_context_menu:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", mOutputMessageTextView.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "Сообщение скопированно", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        };


    }

    private class DialogInputHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView mInputMessageTextView;
        TextView mInputTimeTextView;
        Message mMessage;

        DialogInputHolder(View view) {
            super(view);
            view.setOnCreateContextMenuListener(this);
            mInputMessageTextView = itemView.findViewById(R.id.text_input_message);
            mInputTimeTextView = itemView.findViewById(R.id.time_input_message);
        }

        void bind(Message message) {
            mMessage = message;
            mInputMessageTextView.setText(message.getTextMessage());
            mInputTimeTextView.setText(message.getDateMessage());
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_dialog_menu, contextMenu);
            MenuItem delete = contextMenu.getItem(0);
            MenuItem copy = contextMenu.getItem(1);
            delete.setOnMenuItemClickListener(mOnMenuItemClickListener);
            copy.setOnMenuItemClickListener(mOnMenuItemClickListener);
        }

        private final MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete_message_context_menu:
                        mDialogManager.deleteMessage(mMessage);
                        Log.d(TAG, "DELETE MESSAGE");
                        updateUi();
                        break;
                    case R.id.copy_to_buffer_context_menu:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", mInputMessageTextView.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "Сообщение скопированно", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        };
    }


    public class DialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int INPUT = 0;
        private final int OUTPUT = 1;

        List<Message> mMessageList;

        DialogAdapter(List<Message> messages) {
            mMessageList = messages;
        }

        void setContacts(List<Message> messages) {
            mMessageList = messages;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {

                case INPUT:
                    View inputView = inflater.inflate(R.layout.item_recycler_dialog_input, parent, false);
                    viewHolder = new DialogInputHolder(inputView);
                    break;
                case OUTPUT:
                    View outView = inflater.inflate(R.layout.item_recycler_dialog_output, parent, false);
                    viewHolder = new DialogOutputHolder(outView);
                    break;

                default:
                    View view = inflater.inflate(R.layout.item_recycler_dialog_input, parent, false);
                    viewHolder = new DialogInputHolder(view);
                    break;
            }

            return viewHolder;


        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            Message message = mMessageList.get(position);
            switch (viewHolder.getItemViewType()) {
                case INPUT:
                    DialogInputHolder dialogInputHolder = (DialogInputHolder) viewHolder;
                    dialogInputHolder.bind(message);
                    break;
                case OUTPUT:
                    DialogOutputHolder dialogOutputHolder = (DialogOutputHolder) viewHolder;
                    dialogOutputHolder.bind(message);
                    break;


            }

        }

        @Override
        public int getItemViewType(int position) {
            if (mMessageList.get(position).isTypeMessage()) {
                return INPUT;
            } else {
                return OUTPUT;
            }
        }


        @Override
        public int getItemCount() {
            return mMessageList.size();
        }
    }


}