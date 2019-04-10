package com.maxfin.phoenixapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class ContactFragment extends Fragment {

    private RecyclerView mContactsRecyclerView;
    private ContactAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContactsRecyclerView = view.findViewById(R.id.contact_recycler_view);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUi();
        return view;
    }

    private void updateUi() {
        ContactManager contactManager = new ContactManager(Objects.requireNonNull(getActivity()));
        List<Contact> contactList = contactManager.getContactList();
        if (mAdapter == null) {
            mAdapter = new ContactAdapter(contactList);
            mContactsRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(contactList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ContactHolder extends RecyclerView.ViewHolder {
        private TextView mNameContactTextView;
        private TextView mNumberContactTextView;
        private Contact mContact;

        public ContactHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recucler_call, parent, false));
            mNameContactTextView = itemView.findViewById(R.id.name_contact_item);
            mNumberContactTextView = itemView.findViewById(R.id.number_contact_item);
        }

        public void bind(Contact contact) {
            mNameContactTextView.setText(contact.getName());
            mNumberContactTextView.setText(contact.getNumber());
        }
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {

        private List<Contact> mContactList;

        public ContactAdapter(List<Contact> contacts) {
            mContactList = contacts;
        }

        public void setCrimes(List<Contact> contacts) {
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
    }
}
