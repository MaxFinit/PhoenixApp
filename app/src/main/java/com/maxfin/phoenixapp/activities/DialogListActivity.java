package com.maxfin.phoenixapp.activities;

import android.app.ActivityManager;
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
import android.util.Log;
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
import com.maxfin.phoenixapp.XMPPConnectionService;
import com.maxfin.phoenixapp.XMPPServerConnection;
import com.maxfin.phoenixapp.managers.ContactManager;
import com.maxfin.phoenixapp.managers.DialogManager;
import com.maxfin.phoenixapp.managers.MessageManager;
import com.maxfin.phoenixapp.managers.StateManager;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DialogListActivity extends AppCompatActivity {
    private static final String TAG = "DialogListActivity";

    private TextView mEmptyDialogsList;
    private EditText mSearchDialogsList;
    private TextView mToolBarStateText;
    private ImageButton mRefreshConnectButton;
    private RecyclerView mDialogsRecyclerView;
    private DialogsAdapter mAdapter;
    private List<Contact> dialogList;
    private BroadcastReceiver mBroadcastReceiver;
    private XMPPServerConnection mXMPPServerConnection;
    private Toolbar mDialogListToolbar;
    private StateManager mStateManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_list);
        mSearchDialogsList = findViewById(R.id.search_message_edit);
        mEmptyDialogsList = findViewById(R.id.empty_list_item);
        mDialogsRecyclerView = findViewById(R.id.message_recycler_view);
        mDialogListToolbar = findViewById(R.id.dialog_list_tool_bar);
        setSupportActionBar(mDialogListToolbar);
        mToolBarStateText = findViewById(R.id.dialog_list_state_toolbar);
        mRefreshConnectButton = findViewById(R.id.dialog_list_refresh_connection_toolbar);

        mStateManager = StateManager.getStateManager();
        mDialogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mXMPPServerConnection = XMPPServerConnection.getXMPPServerConnection(this);


        updateUI();

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

        OnStateCallback callbacck;


        mXMPPServerConnection.onXMPPStateConnectionChanged(callbacck = new OnStateCallback() {
            @Override
            public void onStateChanged() {
                updateState();
            }
        });

        mStateManager.setEventListener(callbacck);


        mRefreshConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMyServiceRunning()) {
                    Intent intent = new Intent(getApplicationContext(), XMPPConnectionService.class);
                    startService(intent);

                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();


        if (!mXMPPServerConnection.isAlive()) {
            Log.d(TAG,"RESTART CONNECTION");
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
                        updateUI();
                        break;
                    default:
                        updateUI();
                        break;
                }


            }
        };


        IntentFilter filter = new IntentFilter(XMPPConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, filter);
        updateUI();

    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void updateUI() {
        MessageManager messageManager = MessageManager.get();
        dialogList = messageManager.getContactList();
        if (dialogList.size() > 0) {
            mDialogsRecyclerView.setVisibility(View.VISIBLE);
            mSearchDialogsList.setVisibility(View.VISIBLE);
            mEmptyDialogsList.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new DialogsAdapter(dialogList);
                mDialogsRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setContacts(dialogList);
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

        for (Contact item : dialogList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
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
                messageManager.updateConact(contact);
                messageManager.deleteFromMessageList(contact);
                ContactManager.get(getApplicationContext()).returnToChekedList(contact);
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
