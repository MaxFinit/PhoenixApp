package com.maxfin.phoenixapp;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.TabItem;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ContactManager {
    private final String TAG = "Загружаем контакты";
    private List<Contact> mContactList;
    private Contact mContact;
    private Context mContext;

    public ContactManager(Context context) {
        mContext = context.getApplicationContext();
        uploadContacts();
    }

    private void uploadContacts() {
        int i = 0;

        mContactList = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                mContact = new Contact();
                mContact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                mContact.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                mContact.setId(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                mContactList.add(mContact);
                i++;
                Log.i(TAG,""+i);
            } while (cursor.moveToNext());
            cursor.close();
        }


    }


    public List<Contact> getContactList() {
        return mContactList;
    }
}
