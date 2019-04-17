package com.maxfin.phoenixapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfin.phoenixapp.Managers.ContactManager;
import com.maxfin.phoenixapp.Managers.MessageManager;
import com.maxfin.phoenixapp.Models.Contact;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

public class AddingDialogActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;

    private RecyclerView mRecyclerView;
    private DialogAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_dialog);
        mRecyclerView = findViewById(R.id.add_dialog_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        updateUi();
    }

    private void updateUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationContext().
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            ContactManager contactManager = ContactManager.get(getApplicationContext());
            List<Contact> contactList = contactManager.getSortedContactList();
            if (mAdapter == null) {
                mAdapter = new AddingDialogActivity.DialogAdapter(contactList);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setContacts(contactList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                updateUi();
            }else {
                Toast.makeText(this, "Пока вы не приймите запрос мы не можем показать вам список контактов", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private class DialogHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameContactTextView;
        private TextView mNumberContactTextView;
        private ImageView mNumberContactImageView;
        private Contact mContact;

        public DialogHolder(LayoutInflater inflater, ViewGroup parent)  {
            super(inflater.inflate(R.layout.item_recycler_call, parent, false));
            itemView.setOnClickListener(this);
            mNameContactTextView = itemView.findViewById(R.id.name_contact_item);
            mNumberContactTextView = itemView.findViewById(R.id.number_contact_item);
            mNumberContactImageView = itemView.findViewById(R.id.image_contact_item);
        }

        public void bind(Contact contact) {
            mContact =contact;
            mNameContactTextView.setText(contact.getName());
            mNumberContactTextView.setText(contact.getNumber());
            try {
                AssetFileDescriptor fd = getContentResolver().
                        openAssetFileDescriptor(contact.getPhoto(), "r");
                mNumberContactImageView.setImageURI(contact.getPhoto());
            } catch (FileNotFoundException e) {
                mNumberContactImageView.setImageResource(R.drawable.ic_contact_circle);
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            MessageManager messageManager = MessageManager.get(getApplicationContext());
            messageManager.uploadMessageList(mContact);



        }
    }

  private class DialogAdapter extends RecyclerView.Adapter<DialogHolder>{

      private List<Contact> mContactList;

      public DialogAdapter(List<Contact> contacts) {
          mContactList = contacts;
      }

      public void setContacts(List<Contact> contacts) {
          mContactList = contacts;
      }

      @NonNull
      @Override
      public DialogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
          return new DialogHolder(layoutInflater,parent);
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
  }



}
