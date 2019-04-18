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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfin.phoenixapp.managers.DialogManager;
import com.maxfin.phoenixapp.models.Message;

import java.util.List;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        mEmptyDialogTextView = findViewById(R.id.empty_dialog);
        mMessageEditText = findViewById(R.id.input_text_message);
        mSendMessageButton = findViewById(R.id.send_message_button);
        mMessagesRecyclerView = findViewById(R.id.messages_recycler_view);
        mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessagesRecyclerView.setHasFixedSize(true);

        updateUi();

        Intent intent = getIntent();
        JID = intent.getStringExtra("EXTRA_CONTACT_JID");
        setTitle(JID);

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mMessageEditText.getText().toString().equals("")) {

                    if (XMPPConnectionService.getConnectionState().equals(XMPPServerConnection.ConnectionState.CONNECTED)) {
                        Log.d(TAG, "Отправка сообщения, клиент подключен ");

                        Intent intent = new Intent(XMPPConnectionService.SEND_MESSAGE);
                        intent.putExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY, mMessageEditText.getText().toString());
                        intent.putExtra(XMPPConnectionService.BUNDLE_TO, JID);

                        mDialogManager.addMessage(mMessageEditText.getText().toString(), false);
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
                        mDialogManager.addMessage(intent.getStringExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY), true);
                        updateUi();
                        break;
                }


            }
        };
        IntentFilter filter = new IntentFilter(XMPPConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, filter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void updateUi() {
        mDialogManager = DialogManager.getDialogManager(getApplicationContext());
        List<Message> mMessageList = mDialogManager.getMessageList();



        if (mMessageList.size() > 0) {
            mMessagesRecyclerView.setVisibility(View.VISIBLE);
            mEmptyDialogTextView.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new DialogActivity.DialogAdapter(mMessageList);
                mMessagesRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setContacts(mMessageList);
                mAdapter.notifyDataSetChanged();
                mMessagesRecyclerView.smoothScrollToPosition(mMessagesRecyclerView.getAdapter().getItemCount()-1);
            }
        } else {
            mMessagesRecyclerView.setVisibility(View.GONE);
            mEmptyDialogTextView.setVisibility(View.VISIBLE);
        }


    }

    private class DialogOutputHolder extends RecyclerView.ViewHolder {
        TextView mOutputMessageTextView;
        TextView mOutputTimeTextView;


        public DialogOutputHolder(View view) {
            super(view);
            mOutputMessageTextView = itemView.findViewById(R.id.text_output_message);
            mOutputTimeTextView = itemView.findViewById(R.id.time_output_message);
        }

        public void bind(Message message) {
            mOutputMessageTextView.setText(message.getTextMessage());
            mOutputTimeTextView.setText(message.getDateMessage());
        }
    }

    private class DialogInputHolder extends RecyclerView.ViewHolder {
        TextView mInputMessageTextView;
        TextView mInputTimeTextView;

        public DialogInputHolder(View view) {
            super(view);
            mInputMessageTextView = itemView.findViewById(R.id.text_input_message);
            mInputTimeTextView = itemView.findViewById(R.id.time_input_message);
        }

        public void bind(Message message) {
            mInputMessageTextView.setText(message.getTextMessage());
            mInputTimeTextView.setText(message.getDateMessage());
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
