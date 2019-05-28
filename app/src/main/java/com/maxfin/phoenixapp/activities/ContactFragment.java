package com.maxfin.phoenixapp.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.Utils;
import com.maxfin.phoenixapp.managers.BlackListManagers;
import com.maxfin.phoenixapp.managers.ContactManager;
import com.maxfin.phoenixapp.managers.JournalManager;
import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactFragment extends Fragment {
    private static final String REFRESH_STATE = "refresh state";

    private RecyclerView mContactsRecyclerView;
    private ContactAdapter mAdapter;
    private List<Contact> mContactList;
    private boolean isRefreshing = false;
    private ContactManager mContactManager;
    private ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContactsRecyclerView = view.findViewById(R.id.contact_recycler_view);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressBar = view.findViewById(R.id.progress_contact_bar);

        if (savedInstanceState != null)
            isRefreshing = savedInstanceState.getBoolean(REFRESH_STATE);


        EditText searchContactsEditText = view.findViewById(R.id.search_contact_edit);
        searchContactsEditText.addTextChangedListener(new TextWatcher() {
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

        FloatingActionButton addContactButton = view.findViewById(R.id.add_contact_in_book_button);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRefreshing = true;
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivity(intent);
            }
        });

        mContactManager = ContactManager.get(getContext());


        //
        return view;
    }


    private void updateUi() {
//        if (isRefreshing) {
//            mContactManager.uploadContacts(getContext());
//            isRefreshing = false;
//        }
//        mContactList = mContactManager.getSortedContactList();
        if (mAdapter == null) {
            mAdapter = new ContactAdapter(mContactList);
            mContactsRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setContacts(mContactList);
            mAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        LoadContactsTask task = new LoadContactsTask();
        task.execute();

        //   updateUi();
    }


    private void filter(String text) {
        List<Contact> filteredList = new ArrayList<>();

        for (Contact item : mContactList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getNumber().contains(text)) {
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(REFRESH_STATE, isRefreshing);
    }

    private class ContactHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView mNameContactTextView;
        private TextView mNumberContactTextView;
        private CircleImageView mPhotoContactImageView;
        private Contact mContact;

        ContactHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_call, parent, false));
            itemView.setOnCreateContextMenuListener(this);
            mNameContactTextView = itemView.findViewById(R.id.name_contact_item);
            mNumberContactTextView = itemView.findViewById(R.id.number_contact_item);
            mPhotoContactImageView = itemView.findViewById(R.id.image_contact_item);

        }

        void bind(Contact contact) {
            mContact = contact;
            mNameContactTextView.setText(contact.getName());
            mNumberContactTextView.setText(contact.getNumber());
            mPhotoContactImageView.setImageURI(Uri.parse(contact.getPhoto()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
            inflater.inflate(R.menu.contex_contact_menu, contextMenu);
            MenuItem call = contextMenu.getItem(0);
            MenuItem edit = contextMenu.getItem(1);
            MenuItem block = contextMenu.getItem(2);
            call.setOnMenuItemClickListener(mOnMenuItemClickListener);
            edit.setOnMenuItemClickListener(mOnMenuItemClickListener);
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
                                "15:45",
                                mContact.getPhoto(),
                                mContact.getContactId()
                        );
                        JournalManager.getJournalManager().addCall(call);


                        Intent makeCallIntent = new Intent(getActivity(), OutgoingCallActivity.class);
                        makeCallIntent.putExtra(Utils.NAME_KEY, mContact.getName());
                        makeCallIntent.putExtra(Utils.NUMBER_KEY, mContact.getNumber());
                        makeCallIntent.putExtra(Utils.PHOTO_KEY, mContact.getPhoto());
                        makeCallIntent.putExtra(Utils.ID_KEY, call.getId());
                        startActivity(makeCallIntent);
                        break;
                    case R.id.block_contact_context_menu:
                        BlackListManagers blackListManagers = BlackListManagers.getBlackListManagers();
                        if (blackListManagers.addToBlackList(mContact))
                            Toast.makeText(getContext(), "Контакт добавлен в черный список", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), "Контакт уже добавлен в черный список", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.edit_dialog_context_menu:

                        isRefreshing = true;
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(mContact.getContactId()));
                        intent.setData(contactUri);
                        intent.putExtra("finishActivityOnSaveCompleted", true);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        };


    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {

        private List<Contact> mContactList;

        ContactAdapter(List<Contact> contacts) {
            mContactList = contacts;
        }

        void setContacts(List<Contact> contacts) {
            mContactList = contacts;
        }

        @NonNull
        @Override
        public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ContactHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactHolder contactHolder, int position) {
            Contact contact = mContactList.get(position);
            contactHolder.bind(contact);

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


    private class LoadContactsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            super.onProgressUpdate(voids);
            mProgressBar.setMax(1);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isRefreshing) {
                mContactManager.uploadContacts(getContext());
                isRefreshing = false;
            }
            mContactList = mContactManager.getSortedContactList();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.GONE);
            updateUi();
        }


    }
}
