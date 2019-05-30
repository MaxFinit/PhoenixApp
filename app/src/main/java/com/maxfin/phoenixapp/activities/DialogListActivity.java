package com.maxfin.phoenixapp.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.maxfin.phoenixapp.OnStateCallback;
import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.Utils;
import com.maxfin.phoenixapp.XMPPConnectionService;
import com.maxfin.phoenixapp.XMPPServerConnection;
import com.maxfin.phoenixapp.managers.ContactManager;
import com.maxfin.phoenixapp.managers.DialogManager;
import com.maxfin.phoenixapp.managers.JournalManager;
import com.maxfin.phoenixapp.managers.MessageManager;
import com.maxfin.phoenixapp.managers.StateManager;
import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DialogListActivity extends AppCompatActivity {

    private TextView mEmptyDialogsList;
    private EditText mSearchDialogsList;
    private TextView mToolBarStateText;
    private ImageButton mRefreshConnectButton;
    private RecyclerView mDialogsRecyclerView;
    private DialogsAdapter mAdapter;
    private List<Contact> mContactList;
    private BroadcastReceiver mBroadcastReceiver;
    private XMPPServerConnection mXMPPServerConnection;
    private StateManager mStateManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_list);

        Toolbar dialogListToolbar = findViewById(R.id.dialog_list_tool_bar);
        setSupportActionBar(dialogListToolbar);
        mEmptyDialogsList = findViewById(R.id.empty_list_item);
        mDialogsRecyclerView = findViewById(R.id.message_recycler_view);
        mToolBarStateText = findViewById(R.id.dialog_list_state_toolbar);


        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        Intent homeIntent = new Intent(DialogListActivity.this, MainActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        break;
                    case R.id.menu_call:
                        Intent callIntent = new Intent(DialogListActivity.this, CallActivity.class);
                        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(callIntent);
                        break;
                }
                return false;
            }
        });

        FloatingActionButton mFloatingActionButton = findViewById(R.id.add_dialog_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DialogListActivity.this, AddDialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);

            }
        });

        mRefreshConnectButton = findViewById(R.id.dialog_list_refresh_connection_toolbar);
        mRefreshConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent startXMPPConnectService = new Intent(getApplicationContext(), XMPPConnectionService.class);
                    startService(startXMPPConnectService);
            }
        });


        mSearchDialogsList = findViewById(R.id.search_message_edit);
        mSearchDialogsList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });



        mStateManager = StateManager.getStateManager();
        mDialogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mXMPPServerConnection = XMPPServerConnection.getXMPPServerConnection(getApplicationContext());
        mXMPPServerConnection.onXMPPStateConnectionChanged(new OnStateCallback() {
            @Override
            public void onStateChanged() {
                updateState();
            }
        });



        mXMPPServerConnection.isAlive(getApplicationContext());

        updateUI();
    }


    @Override
    protected void onResume() {
        super.onResume();

        mXMPPServerConnection.isAlive(getApplicationContext());
            updateState();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (XMPPConnectionService.NEW_MESSAGE.equals(Objects.requireNonNull(intent.getAction()))) {
                    updateUI();
                } else {
                    updateUI();
                }


            }
        };
        IntentFilter newMesssageFilter = new IntentFilter(XMPPConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, newMesssageFilter);
        updateUI();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void updateUI() {
        MessageManager messageManager = MessageManager.get();
        mContactList = messageManager.getContactList();
        if (mContactList.size() > 0) {
            mDialogsRecyclerView.setVisibility(View.VISIBLE);
            mSearchDialogsList.setVisibility(View.VISIBLE);
            mEmptyDialogsList.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new DialogsAdapter(mContactList);
                mDialogsRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setContacts(mContactList);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mDialogsRecyclerView.setVisibility(View.GONE);
            mSearchDialogsList.setVisibility(View.GONE);
            mEmptyDialogsList.setVisibility(View.VISIBLE);
        }
    }




    private void updateState() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (mStateManager.getConnectionXMPPState()) {

                    case CONNECTED:
                        mToolBarStateText.setText("Сообщения");
                        mRefreshConnectButton.setVisibility(View.GONE);
                        break;
                    case CONNECTING:
                        mToolBarStateText.setText("Подключение");
                        mRefreshConnectButton.setVisibility(View.GONE);
                        break;
                    case DISCONNECTED:
                        mToolBarStateText.setText("Потеря соединения");
                        mRefreshConnectButton.setVisibility(View.VISIBLE);
                        break;
                    case AUTHENTICATED:
                        mToolBarStateText.setText("Аутификация");
                        mRefreshConnectButton.setVisibility(View.GONE);
                        break;
                    case DISCONNECTING:
                        mToolBarStateText.setText("Ожидания подключения");
                        mRefreshConnectButton.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });


    }


    private void filter(String text) {
        List<Contact> filteredList = new ArrayList<>();
        for (Contact item : mContactList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        mAdapter.filterList(filteredList);
    }




    private void showDialog(final Contact contact) {
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Удалить диалог");
        alertDialog.setMessage("Вы уверены?");
        alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MessageManager messageManager = MessageManager.get();
                contact.setIsLoaded(false);
                messageManager.updateContact(contact);
                messageManager.deleteFromMessageList(contact);
                ContactManager.get(getApplicationContext()).returnToCheckedList(contact);
                updateUI();
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


    private class DialogsHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        ImageView mDialogImageView;
        TextView mDialogNameTextView;
        TextView mDialogPreviewTextView;
        TextView mDialogTimeTextView;
        Contact mContact;


        DialogsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_dialog_list, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            mDialogImageView = itemView.findViewById(R.id.image_dialog_item);
            mDialogNameTextView = itemView.findViewById(R.id.name_dialog_item);
            mDialogPreviewTextView = itemView.findViewById(R.id.preview_dialog_item);
            mDialogTimeTextView = itemView.findViewById(R.id.time_dialog_item);
        }

        void bind(Contact contact) {
            mContact = contact;
            mDialogNameTextView.setText(contact.getName());
            mDialogImageView.setImageURI(Uri.parse(contact.getPhoto()));

            try {
                Message lastMessage = DialogManager.getDialogManager().
                        getLastMessage(mContact.getJId());
                mDialogPreviewTextView.setText(lastMessage.getTextMessage());
                mDialogTimeTextView.setText(lastMessage.getDateMessage());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_dalog_list_menu, contextMenu);
            MenuItem call = contextMenu.getItem(0);
            MenuItem delete = contextMenu.getItem(1);
            MenuItem block = contextMenu.getItem(2);
            call.setOnMenuItemClickListener(mOnMenuItemClickListener);
            delete.setOnMenuItemClickListener(mOnMenuItemClickListener);
            block.setOnMenuItemClickListener(mOnMenuItemClickListener);
        }

        private final MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.make_call_context_menu:
                        Call call = new Call(
                                mContact.getName(),
                                mContact.getNumber(),
                                (byte) 0,
                                mContact.getPhoto(),
                                mContact.getContactId()
                        );
                        JournalManager.getJournalManager().addCall(call);


                        Intent makeCallIntent = new Intent(getApplicationContext(), OutgoingCallActivity.class);
                        makeCallIntent.putExtra(Utils.NAME_KEY, mContact.getName());
                        makeCallIntent.putExtra(Utils.NUMBER_KEY, mContact.getNumber());
                        makeCallIntent.putExtra(Utils.PHOTO_KEY, mContact.getPhoto());
                        makeCallIntent.putExtra(Utils.ID_KEY, call.getId());
                        startActivity(makeCallIntent);
                        break;
                    case R.id.block_contact_context_menu:
                        break;
                    case R.id.delete_dialog_context_menu:
                        showDialog(mContact);
                        break;
                }
                return true;
            }
        };


        @Override
        public void onClick(View view) {

            Intent intent = new Intent(DialogListActivity.this, DialogActivity.class);
            intent.putExtra("EXTRA_CONTACT_JID", mContact.getJId());

            startActivity(intent);

        }
    }


    public class DialogsAdapter extends RecyclerView.Adapter<DialogsHolder> {
        private List<Contact> mContactList;

        DialogsAdapter(List<Contact> contacts) {
            mContactList = contacts;
        }

        void setContacts(List<Contact> contacts) {
            mContactList = contacts;
        }


        @NonNull
        @Override
        public DialogsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new DialogsHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DialogsHolder dialogHolder, int positions) {
            Contact contact = mContactList.get(positions);
            dialogHolder.bind(contact);
        }

        @Override
        public int getItemCount() {
            return mContactList.size();
        }

        void filterList(List<Contact> filteredList) {
            mContactList = filteredList;
            notifyDataSetChanged();
        }


    }
}
