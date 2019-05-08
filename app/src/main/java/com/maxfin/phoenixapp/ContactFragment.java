package com.maxfin.phoenixapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfin.phoenixapp.managers.ContactManager;
import com.maxfin.phoenixapp.models.Contact;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactFragment extends Fragment {
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;

    private EditText mSearchContactsEditText;
    private RecyclerView mContactsRecyclerView;
    private ContactAdapter mAdapter;
    private List<Contact> contactList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContactsRecyclerView = view.findViewById(R.id.contact_recycler_view);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchContactsEditText = view.findViewById(R.id.search_contact_edit);


        updateUi();

        mSearchContactsEditText.addTextChangedListener(new TextWatcher() {
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


        return view;
    }


    private void updateUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Objects.requireNonNull(getContext()).
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            ContactManager contactManager = ContactManager.get(getContext());
            contactList = contactManager.getSortedContactList();
            if (mAdapter == null) {
                mAdapter = new ContactAdapter(contactList);
                mContactsRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setContacts(contactList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateUi();
            } else {
                Toast.makeText(getActivity(), "Пока вы не приймите запрос мы не можем показать вам список контактов", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void filter(String text) {
        List<Contact> filteredList = new ArrayList<>();

        for (Contact item : contactList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getNumber().contains(text)) {
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
    }


    private class ContactHolder extends RecyclerView.ViewHolder {
        private TextView mNameContactTextView;
        private TextView mNumberContactTextView;
        private CircleImageView mPhotoContactImageView;

        ContactHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_call, parent, false));
            mNameContactTextView = itemView.findViewById(R.id.name_contact_item);
            mNumberContactTextView = itemView.findViewById(R.id.number_contact_item);
            mPhotoContactImageView = itemView.findViewById(R.id.image_contact_item);

        }

        void bind(Contact contact) {
            mNameContactTextView.setText(contact.getName());
            mNumberContactTextView.setText(contact.getNumber());
            mPhotoContactImageView.setImageURI(Uri.parse(contact.getPhoto()));
        }
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

        public void filterList(List<Contact> filteredList) {
            mContactList = filteredList;
            notifyDataSetChanged();
        }

    }
}
