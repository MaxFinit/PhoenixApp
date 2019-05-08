package com.maxfin.phoenixapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.maxfin.phoenixapp.managers.MessageManager;
import com.maxfin.phoenixapp.models.Contact;


import java.util.ArrayList;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

public class DialogListActivity extends AppCompatActivity {

    private TextView mEmptyDialogsList;
    private EditText mSearchDialogsList;
    private RecyclerView mDialogsRecyclerView;
    private DialogsAdapter mAdapter;
    private List<Contact> dialogList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        mSearchDialogsList = findViewById(R.id.search_message_edit);
        mEmptyDialogsList = findViewById(R.id.empty_list_item);
        mDialogsRecyclerView = findViewById(R.id.message_recycler_view);
        mDialogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


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
                Intent intent = new Intent(DialogListActivity.this, AddingDialogActivity.class);
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


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        MessageManager messageManager = MessageManager.get(this);
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


    private void filter(String text) {
        List<Contact> filteredList = new ArrayList<>();

        for (Contact item : dialogList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
    }


    private class DialogsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView mDialogImageView;
        TextView mDialogNameTextView;
        TextView mDialogPreviewTextView;
        TextView mDialogTimeTextView;
        Contact mContact;


        DialogsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_dialog, parent, false));
            itemView.setOnClickListener(this);
            mDialogImageView = itemView.findViewById(R.id.image_dialog_item);
            mDialogNameTextView = itemView.findViewById(R.id.name_dialog_item);
            mDialogPreviewTextView = itemView.findViewById(R.id.preview_dialog_item);
            mDialogTimeTextView = itemView.findViewById(R.id.time_dialog_item);
        }

        void bind(Contact contact) {
            mContact = contact;
            mDialogNameTextView.setText(contact.getName());
            mDialogImageView.setImageURI(Uri.parse(contact.getPhoto()));
        }

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

        public void filterList(List<Contact> filteredList) {
            mContactList = filteredList;
            notifyDataSetChanged();
        }


    }


}
