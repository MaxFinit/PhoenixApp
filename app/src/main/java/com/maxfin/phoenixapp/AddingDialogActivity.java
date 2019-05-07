package com.maxfin.phoenixapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfin.phoenixapp.managers.ContactManager;
import com.maxfin.phoenixapp.managers.MessageManager;
import com.maxfin.phoenixapp.models.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddingDialogActivity extends AppCompatActivity {
    private static final String TAG = "AddingDialogActivity";
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;

    private RecyclerView mRecyclerView;
    private DialogAdapter mAdapter;
    private EditText mSearchDialogsList;
    private Toolbar mDialogToolbar;
    List<Contact> mContactList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_dialog);
        mDialogToolbar = findViewById(R.id.dialog_tool_bar);
        setSupportActionBar(mDialogToolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);




        mRecyclerView = findViewById(R.id.add_dialog_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mSearchDialogsList = findViewById(R.id.search_message_contact_edit);

        updateUi();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.add_new_dialog);

        return true;

    }

    private void updateUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationContext().
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            ContactManager contactManager = ContactManager.get(getApplicationContext());
            List<Contact> contactList = contactManager.getSortedContactList();
            contactList = updateList(contactList);
            if (mAdapter == null) {
                mAdapter = new AddingDialogActivity.DialogAdapter(contactList);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setContacts(contactList);
                mAdapter.notifyDataSetChanged();
            }
        }
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


    private List<Contact> updateList(List<Contact> contactList) {
        mContactList = contactList;
        Log.d(TAG, "Размер массива" + mContactList.size());
        for (int i = 0; i < contactList.size() - 1; i++) {
            if (mContactList.get(i).getIsLoaded())
                mContactList.remove(i);
        }
        Log.d(TAG, "Размер массива" + mContactList.size());
        return mContactList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateUi();
            } else {
                Toast.makeText(this, "Пока вы не приймите запрос мы не можем показать вам список контактов", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class DialogHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameContactTextView;
        private TextView mNumberContactTextView;
        private ImageView mNumberContactImageView;
        private Contact mContact;

        public DialogHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_call, parent, false));
            itemView.setOnClickListener(this);
            mNameContactTextView = itemView.findViewById(R.id.name_contact_item);
            mNumberContactTextView = itemView.findViewById(R.id.number_contact_item);
            mNumberContactImageView = itemView.findViewById(R.id.image_contact_item);

        }

        public void bind(Contact contact) {
            mContact = contact;
            mNameContactTextView.setText(contact.getName());
            mNumberContactTextView.setText(contact.getNumber());
            mNumberContactImageView.setImageURI(Uri.parse(contact.getPhoto()));


        }

        @Override
        public void onClick(View view) {
            MessageManager messageManager = MessageManager.get(getApplicationContext());
            mContact.setJId(mContact.getName() + "@jabber.ru");
            mContact.setIsLoaded(true);
            messageManager.uploadMessageList(mContact);
            Intent intent = new Intent(AddingDialogActivity.this, DialogActivity.class);
            intent.putExtra("EXTRA_CONTACT_JID", mContact.getJId());
            startActivity(intent);


        }
    }

    private class DialogAdapter extends RecyclerView.Adapter<DialogHolder> {

        private List<Contact> mContactList;

        DialogAdapter(List<Contact> contacts) {
            mContactList = contacts;
        }

        void setContacts(List<Contact> contacts) {
            mContactList = contacts;
        }

        @NonNull
        @Override
        public DialogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new DialogHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DialogHolder dialogHolder, int position) {
            Contact contact = mContactList.get(position);
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
