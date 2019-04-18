package com.maxfin.phoenixapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
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
import android.widget.Toast;

import com.maxfin.phoenixapp.managers.ContactManager;
import com.maxfin.phoenixapp.models.Contact;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactFragment extends Fragment {
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Objects.requireNonNull(getContext()).
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            ContactManager contactManager = ContactManager.get(getContext());
            List<Contact> contactList = contactManager.getSortedContactList();
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

    private class ContactHolder extends RecyclerView.ViewHolder {
        private TextView mNameContactTextView;
        private TextView mNumberContactTextView;
        private CircleImageView mPhotoContactImageView;
        private Contact mContact;

        public ContactHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_call, parent, false));
            mNameContactTextView = itemView.findViewById(R.id.name_contact_item);
            mNumberContactTextView = itemView.findViewById(R.id.number_contact_item);
            mPhotoContactImageView = itemView.findViewById(R.id.image_contact_item);

        }

        public void bind(Contact contact) {
            mNameContactTextView.setText(contact.getName());
            mNumberContactTextView.setText(contact.getNumber());
            try {
                AssetFileDescriptor fd = Objects.requireNonNull(getContext()).getContentResolver().
                        openAssetFileDescriptor(contact.getPhoto(), "r");
                mPhotoContactImageView.setImageURI(contact.getPhoto());
            } catch (FileNotFoundException e) {
                mPhotoContactImageView.setImageResource(R.drawable.ic_contact_circle);
                e.printStackTrace();
            }

        }
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {

        private List<Contact> mContactList;

        public ContactAdapter(List<Contact> contacts) {
            mContactList = contacts;
        }

        public void setContacts(List<Contact> contacts) {
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
