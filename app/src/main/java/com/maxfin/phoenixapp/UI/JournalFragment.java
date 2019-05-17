package com.maxfin.phoenixapp.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.managers.ContactManager;
import com.maxfin.phoenixapp.managers.JournalManager;
import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class JournalFragment extends Fragment {
    private FloatingActionButton mFloatingActionButton;
    private EditText mSearchJournalEditText;
    private RecyclerView mJournalRecyclerView;
    private JournalAdapter mAdapter;
    private JournalManager mJournalManager;
    private List<Call> mCallList;

    private Contact contact;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
        mJournalRecyclerView = view.findViewById(R.id.journal_recycler_view);
        mJournalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));




        contact = new Contact();
        contact.setJId("maxfin2@jabber.ru");
        contact.setName("Max");
        contact.setNumber("+8945554");
        Uri path = Uri.parse("android.resource://com.maxfin.phoenixapp/" + R.drawable.ic_contact_circle_api2);
        contact.setPhoto(path.toString());



        mFloatingActionButton = view.findViewById(R.id.call_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                Call call = new Call();
                call.setCallType((byte) 1);
                call.setContactId(contact.getContactId());
                call.setData("15:54");
                mJournalManager.addCall(call);
















                Intent intent = new Intent(getActivity(), OutgoingCallActivity.class);
                startActivity(intent);

            }
        });

        updateUi();

        mSearchJournalEditText = view.findViewById(R.id.search_journal_edit);
        mSearchJournalEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void updateUi() {
        mJournalManager = JournalManager.getJournalManager();
        mCallList = mJournalManager.getCalls();
        if (mAdapter == null) {
            mAdapter = new JournalFragment.JournalAdapter(mCallList);
            mJournalRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCalls(mCallList);
            mAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    private void filter(String text) {
        List<Call> filteredList = new ArrayList<>();

//        for (Call item : mCallList) {
//            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getNumber().contains(text)) {
//                filteredList.add(item);
//            }
//        }
//
//        mAdapter.filterList(filteredList);
    }


    private class JournalHolder extends RecyclerView.ViewHolder {
        private TextView mNameJournalTextView;
        private TextView mNumberJournalTextView;
        private TextView mDataJournalTextView;
        private CircleImageView mPhotoJournalImageView;
        private ImageView mCallTypeImageView;


        JournalHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_journal, parent,
                    false));
            mNameJournalTextView = itemView.findViewById(R.id.journal_name_text_view);
            mNumberJournalTextView = itemView.findViewById(R.id.journal_number_text_view);
            mPhotoJournalImageView = itemView.findViewById(R.id.journal_photo_image_view);
            mCallTypeImageView = itemView.findViewById(R.id.journal_type_call_image_view);
            mDataJournalTextView = itemView.findViewById(R.id.journal_data_text_view);

        }


        void bind(Call call) {
            //Contact contact = mJournalManager.getContact(call.getContactId());
            mNameJournalTextView.setText(contact.getName());
            mNumberJournalTextView.setText(contact.getNumber());
            mPhotoJournalImageView.setImageURI(Uri.parse(contact.getPhoto()));
            mDataJournalTextView.setText(call.getData());

            switch (call.getCallType()) {

                case 0:
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_made);
                    break;
                case 1:
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_received);
                    break;
                case 2:
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_missed);
                    break;
            }


        }
    }

    private class JournalAdapter extends RecyclerView.Adapter<JournalHolder> {

        private List<Call> mCallList;

        JournalAdapter(List<Call> calls) {
            mCallList = calls;
        }

        void setCalls(List<Call> calls) {
            mCallList = calls;
        }


        @NonNull
        @Override
        public JournalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new JournalHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull JournalHolder contactHolder, int position) {
            Call call = mCallList.get(position);
            contactHolder.bind(call);

        }

        @Override
        public int getItemCount() {
            return mCallList.size();
        }

        public void filterList(List<Contact> filteredList) {
        //    mCallList = filteredList;
            notifyDataSetChanged();
        }


    }

}