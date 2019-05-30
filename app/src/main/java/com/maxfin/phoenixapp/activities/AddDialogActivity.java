package com.maxfin.phoenixapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.managers.ContactManager;
import com.maxfin.phoenixapp.managers.MessageManager;
import com.maxfin.phoenixapp.models.Contact;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AddDialogActivity extends AppCompatActivity {
    private static final String TAG = "AddDialogActivity";

    private RecyclerView mRecyclerView;
    private DialogAdapter mAdapter;
    private List<Contact> mContactList;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_dialog);
        Toolbar dialogToolbar = findViewById(R.id.adding_dialog_tool_bar_menu);
        setSupportActionBar(dialogToolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.add_dialog_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mProgressBar = findViewById(R.id.add_dialog_progress_barr);


        EditText searchDialogsList = findViewById(R.id.search_message_contact_edit);
        searchDialogsList.addTextChangedListener(new TextWatcher() {
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

        LoadContactsTask task = new LoadContactsTask(this);
        task.execute();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void updateUi() {

        if (mAdapter == null) {
            mAdapter = new AddDialogActivity.DialogAdapter(mContactList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setContacts(mContactList);
            mAdapter.notifyDataSetChanged();
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


    private class DialogHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameContactTextView;
        private TextView mNumberContactTextView;
        private ImageView mNumberContactImageView;
        private Contact mContact;

        DialogHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_call, parent, false));
            itemView.setOnClickListener(this);
            mNameContactTextView = itemView.findViewById(R.id.name_contact_item);
            mNumberContactTextView = itemView.findViewById(R.id.number_contact_item);
            mNumberContactImageView = itemView.findViewById(R.id.image_contact_item);
        }

        void bind(Contact contact) {
            mContact = contact;
            mNameContactTextView.setText(contact.getName());
            mNumberContactTextView.setText(contact.getNumber());
            mNumberContactImageView.setImageURI(Uri.parse(contact.getPhoto()));
        }

        @Override
        public void onClick(View view) {
            MessageManager messageManager = MessageManager.get();
            mContact.setJId(mContact.getNumber() + "@jabber.ru");
            mContact.setIsLoaded(true);
            mContactList.remove(mContact);
            messageManager.updateContact(mContact);
            messageManager.uploadMessageList(mContact);
            Intent intent = new Intent(AddDialogActivity.this, DialogActivity.class);
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

        void filterList(List<Contact> filteredList) {
            mContactList = filteredList;
            notifyDataSetChanged();
        }
    }


    private static class LoadContactsTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<AddDialogActivity> activityReference;

        LoadContactsTask(AddDialogActivity context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected Void doInBackground(Void... voids) {
            AddDialogActivity activity = activityReference.get();
            ContactManager contactManager = ContactManager.get(activity);
            activity.mContactList = contactManager.getSortedContactList();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AddDialogActivity activity = activityReference.get();
            activity.mProgressBar.setVisibility(View.GONE);
            activity.updateUi();
        }


    }


}
