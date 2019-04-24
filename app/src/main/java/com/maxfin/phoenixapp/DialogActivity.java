package com.maxfin.phoenixapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfin.phoenixapp.managers.DialogManager;
import com.maxfin.phoenixapp.models.Message;

import java.util.List;
import java.util.Objects;

public class DialogActivity extends AppCompatActivity {
    private static final String TAG = "DialogActivity";

    private TextView mEmptyDialogTextView;
    private RecyclerView mMessagesRecyclerView;
    private DialogAdapter mAdapter;
    private EditText mMessageEditText;
    private Button mSendMessageButton;
    private DialogManager mDialogManager;
    private String JID;
    private BroadcastReceiver mBroadcastReceiver;
    private Toolbar mDialogToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        mDialogToolbar = findViewById(R.id.dialog_tool_bar);
        setSupportActionBar(mDialogToolbar);

        mEmptyDialogTextView = findViewById(R.id.empty_dialog);
        mMessageEditText = findViewById(R.id.input_text_message);
        mSendMessageButton = findViewById(R.id.send_message_button);
        mMessagesRecyclerView = findViewById(R.id.messages_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); // Отображает список с конца.
        mMessagesRecyclerView.setLayoutManager(linearLayoutManager);
        mMessagesRecyclerView.setHasFixedSize(true);


        Intent intent = getIntent();

        JID = intent.getStringExtra("EXTRA_CONTACT_JID");
        updateUi();


        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!mMessageEditText.getText().toString().equals("")) {

                    if (XMPPConnectionService.getConnectionState().equals(XMPPServerConnection.ConnectionState.CONNECTED)) {
                        Log.d(TAG, "Отправка сообщения, клиент подключен ");

                        Intent intent = new Intent(XMPPConnectionService.SEND_MESSAGE);
                        intent.putExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY, mMessageEditText.getText().toString());
                        intent.putExtra(XMPPConnectionService.BUNDLE_TO, JID);

                        mDialogManager.addMessage(mMessageEditText.getText().toString(), false, JID);
                        mMessageEditText.setText("");


                        sendBroadcast(intent);
                        updateUi();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Client not connected to server ,Message not sent!",
                                Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        mMessagesRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mMessagesRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mMessagesRecyclerView.scrollToPosition((mMessagesRecyclerView.getAdapter().getItemCount() - 1));

                        }
                    });
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dialog_tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_history_dialog_menu:
                mDialogManager.deleteMessageList(JID);
                break;
                case android.R.id.home:
                    onBackPressed();
                    break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case XMPPConnectionService.NEW_MESSAGE:
                        //    mDialogManager.addMessage(intent.getStringExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY), true,JID);
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
        mDialogManager = DialogManager.getDialogManager(getApplicationContext());
        List<Message> mMessageList = mDialogManager.getMessageList(JID);


        if (mMessageList.size() > 0) {
            mMessagesRecyclerView.setVisibility(View.VISIBLE);
            mEmptyDialogTextView.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new DialogActivity.DialogAdapter(mMessageList);
                mMessagesRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setContacts(mMessageList);
                mAdapter.notifyDataSetChanged();
                mMessagesRecyclerView.scrollToPosition(mMessagesRecyclerView.getAdapter().getItemCount() - 1);//Прокрутка списка в конец
                Log.d(TAG, mMessageList.size() + "" + (mMessagesRecyclerView.getAdapter().getItemCount() - 1));
            }
        } else {
            mMessagesRecyclerView.setVisibility(View.GONE);
            mEmptyDialogTextView.setVisibility(View.VISIBLE);
        }


    }

    private class DialogOutputHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mOutputMessageTextView;
        TextView mOutputTimeTextView;
        Message mMessage;


        public DialogOutputHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
            mOutputMessageTextView = itemView.findViewById(R.id.text_output_message);
            mOutputTimeTextView = itemView.findViewById(R.id.time_output_message);
        }

        public void bind(Message message) {
            mMessage = message;
            mOutputMessageTextView.setText(message.getTextMessage());
            mOutputTimeTextView.setText(message.getDateMessage());
        }


        @Override
        public void onClick(View view) {
            mDialogManager.deleteMessage(mMessage);
            Log.d(TAG, "Delete message");
            updateUi();
        }
    }

    private class DialogInputHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mInputMessageTextView;
        TextView mInputTimeTextView;
        Message mMessage;

        public DialogInputHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
            mInputMessageTextView = itemView.findViewById(R.id.text_input_message);
            mInputTimeTextView = itemView.findViewById(R.id.time_input_message);
        }

        public void bind(Message message) {
            mMessage = message;
            mInputMessageTextView.setText(message.getTextMessage());
            mInputTimeTextView.setText(message.getDateMessage());
        }


        @Override
        public void onClick(View view) {
            mDialogManager.deleteMessage(mMessage);
            Log.d(TAG, "Delete message");
            updateUi();
        }
    }


    public class DialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int INPUT = 0;
        private final int OUTPUT = 1;

        List<Message> mMessageList;

        public DialogAdapter(List<Message> messages) {
            mMessageList = messages;
        }

        public void setContacts(List<Message> messages) {
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
