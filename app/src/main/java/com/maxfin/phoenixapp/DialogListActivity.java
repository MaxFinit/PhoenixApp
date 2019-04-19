package com.maxfin.phoenixapp;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.maxfin.phoenixapp.managers.MessageManager;
import com.maxfin.phoenixapp.models.Contact;

import java.io.FileNotFoundException;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

public class DialogListActivity extends AppCompatActivity {

    private TextView mEmptyDialogsList;
    private EditText mSearchDialogsList;
    private RecyclerView mDialogsRecyclerView;
    private DialogsAdapter mAdapter;


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
                        startActivity(homeIntent);
                        break;
                    case R.id.menu_call:
                        Intent callIntent = new Intent(DialogListActivity.this, CallActivity.class);
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
                startActivity(intent);
            }
        });

         Intent i1 = new Intent(this,XMPPConnectionService.class);
         startService(i1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        MessageManager messageManager = MessageManager.get(this);
        List<Contact> dialogList = messageManager.getContactList();
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


    private class DialogsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView mDialogImageView;
        TextView mDialogNameTextView;
        TextView mDialogPreviewTextView;
        TextView mDialogTimeTextView;
        Contact mContact;


        public DialogsHolder(LayoutInflater inflater, ViewGroup parent)  {
            super(inflater.inflate(R.layout.item_recycler_dialog, parent, false));
            itemView.setOnClickListener(this);
            mDialogImageView = itemView.findViewById(R.id.image_dialog_item);
            mDialogNameTextView = itemView.findViewById(R.id.name_dialog_item);
            mDialogPreviewTextView = itemView.findViewById(R.id.preview_dialog_item);
            mDialogTimeTextView = itemView.findViewById(R.id.time_dialog_item);
        }

        public void bind(Contact contact) {
            mContact = contact;
            mDialogNameTextView.setText(contact.getName());
            try {
                AssetFileDescriptor fd = getContentResolver().
                        openAssetFileDescriptor(Uri.parse(contact.getPhoto()), "r");
                mDialogImageView.setImageURI(Uri.parse(contact.getPhoto()));
            } catch (FileNotFoundException e) {
                mDialogImageView.setImageResource(R.drawable.ic_contact_circle);
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(DialogListActivity.this, DialogActivity.class);
            intent.putExtra("EXTRA_CONTACT_JID",mContact.getJId());
            intent.putExtra("EXTRA_CONTACT_ID",mContact.getId());
            startActivity(intent);

        }
    }

    public class DialogsAdapter extends RecyclerView.Adapter<DialogsHolder> {
        private List<Contact> mContactList;

        public DialogsAdapter(List<Contact> contacts) {
            mContactList = contacts;
        }

        public void setContacts(List<Contact> contacts) {
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
    }

}
