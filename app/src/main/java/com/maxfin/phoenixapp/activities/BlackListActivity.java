package com.maxfin.phoenixapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.managers.BlackListManagers;
import com.maxfin.phoenixapp.models.BlockContact;
import com.maxfin.phoenixapp.models.Contact;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlackListActivity extends AppCompatActivity {
    private RecyclerView mBlackListRecyclerView;
    private BlackListAdapter mBlackListAdapter;
    private List<BlockContact> mBlockContacts;
    private BlackListManagers mBlackListManagers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        Toolbar toolbar = findViewById(R.id.black_list_tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mBlackListRecyclerView = findViewById(R.id.black_list_recycler_view);
        mBlackListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        updateUi();


    }

    private void updateUi() {
        mBlackListManagers = BlackListManagers.getBlackListManagers();
        mBlockContacts = mBlackListManagers.getBlackList();
        if (mBlackListAdapter == null) {
            mBlackListAdapter = new BlackListAdapter(mBlockContacts);
            mBlackListRecyclerView.setAdapter(mBlackListAdapter);
        } else {
            mBlackListAdapter.setContacts(mBlockContacts);
            mBlackListAdapter.notifyDataSetChanged();
        }
    }


    private class BlackListHolder extends RecyclerView.ViewHolder {
        private TextView mNameBlockContact;
        private TextView mNumberBlockContact;
        private CircleImageView mCircleImageView;
        private ImageButton mImageButton;
        private BlockContact mBlockContact;


        BlackListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_black_list, parent, false));
            //   itemView.setOnClickListener(this);
            mNameBlockContact = itemView.findViewById(R.id.black_list_name_text_view);
            mNumberBlockContact = itemView.findViewById(R.id.black_list_data_text_view);
            mCircleImageView = itemView.findViewById(R.id.black_list_avatar_image_view);
            mImageButton = itemView.findViewById(R.id.delete_from_black_list_button);
        }

        void bind(BlockContact contact) {
            mBlockContact = contact;
            mNameBlockContact.setText(contact.getName());
            mNumberBlockContact.setText(contact.getNumber());
            mCircleImageView.setImageURI(Uri.parse(contact.getPhoto()));
            mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBlackListManagers.deleteFromBlackList(mBlockContact);
                    updateUi();
                }
            });
        }
    }


    private class BlackListAdapter extends RecyclerView.Adapter<BlackListHolder> {

        private List<BlockContact> mBlackList;

        BlackListAdapter(List<BlockContact> contacts) {
            mBlackList = contacts;
        }

        void setContacts(List<BlockContact> contacts) {
            mBlackList = contacts;
        }

        @NonNull
        @Override
        public BlackListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new BlackListHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BlackListHolder blackListHolder, int position) {
            BlockContact contact = mBlackList.get(position);
            blackListHolder.bind(contact);
        }

        @Override
        public int getItemCount() {
            return mBlackList.size();
        }

        void filterList(List<Contact> filteredList) {
            //   mBlackList = filteredList;
            notifyDataSetChanged();
        }
    }


}
