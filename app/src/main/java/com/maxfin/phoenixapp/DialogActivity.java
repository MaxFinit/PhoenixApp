package com.maxfin.phoenixapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfin.phoenixapp.Managers.DialogManager;
import com.maxfin.phoenixapp.Models.Contact;
import com.maxfin.phoenixapp.Models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
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

                if (XMPPConnectionService.getConnectionState().equals(XMPPServerConnection.ConnectionState.CONNECTED)) {
                    Log.d(TAG, "Отправка сообщения, клиент подключен ");

                    Intent intent = new Intent(XMPPConnectionService.SEND_MESSAGE);
                    intent.putExtra(XMPPConnectionService.BUNDLE_MESSAGE_BODY, mMessageEditText.getText().toString());
                    intent.putExtra(XMPPConnectionService.BUNDLE_TO, JID);


                    Date date = new Date();
                    String formattedDate = DateFormat.format("MMM/dd hh:mm", System.currentTimeMillis()).toString();

                    Message message = new Message(mMessageEditText.getText().toString(), false, formattedDate);

                    mDialogManager.addMessage(message);

                    sendBroadcast(intent);
                    updateUi();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Client not connected to server ,Message not sent!",
                            Toast.LENGTH_LONG).show();
                }


            }
        });

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
            }
        } else {
            mMessagesRecyclerView.setVisibility(View.GONE);
            mEmptyDialogTextView.setVisibility(View.VISIBLE);
        }


    }

    private class DialogOutputHolder extends RecyclerView.ViewHolder {
        TextView mOutputMessageTextView;
        TextView mOutputTimeTextView;


        public DialogOutputHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_dialog_output, parent, false));
            mOutputMessageTextView = itemView.findViewById(R.id.text_output_message);
            mOutputTimeTextView = itemView.findViewById(R.id.time_output_message);
        }

        public void bind(Message message) {
            mOutputMessageTextView.setText(message.getTextMessage());
            mOutputTimeTextView.setText(message.getDateMessage().toString());
        }
    }


    public class DialogAdapter extends RecyclerView.Adapter<DialogOutputHolder> {

        List<Message> mMessageList;

        public DialogAdapter(List<Message> messages) {
            mMessageList = messages;
        }

        public void setContacts(List<Message> messages) {
            mMessageList = messages;
        }

        @NonNull
        @Override
        public DialogOutputHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new DialogOutputHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DialogOutputHolder dialogOutputHolder, int positions) {
            Message message = mMessageList.get(positions);
            dialogOutputHolder.bind(message);

        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }
    }


}
