package com.maxfin.phoenixapp.Managers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.maxfin.phoenixapp.Models.Contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactManager {
    private final String TAG = "Загружаем контакты";
    private List<Contact> mContactList;
    private static ContactManager sContactManager;
    private Contact mContact;
    private Context mContext;

    private ContactManager(Context context) {
        mContext = context.getApplicationContext();
        uploadContacts();
    }

    public static ContactManager get(Context context) {
        if (sContactManager == null) {
            sContactManager = new ContactManager(context);
        }
        return sContactManager;
    }

    private void uploadContacts() {
        String idLast;
        String idPrev = "";
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
                Uri u = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Long.parseLong(mContact.getId()));
                mContact.setPhoto(Uri.withAppendedPath(u,ContactsContract.Contacts.Photo.DISPLAY_PHOTO));
                Log.d(TAG, "" + mContact.getPhoto());
                /*
                Ужасный костыль, из-за дублирования номеров, пофиксить потом
                */
                idLast = mContact.getId();
                if (!idPrev.equals(idLast))
                    mContactList.add(mContact);
                idPrev = idLast;
                i++;
                Log.i(TAG, mContact.getName());
            } while (cursor.moveToNext());
            Log.i(TAG, "" + mContactList.size());
            cursor.close();
        }


    }

    public class ContactNameComparator implements Comparator<Contact>
    {
        public int compare(Contact left, Contact right) {
            return left.getName().compareTo(right.getName());
        }
    }

    public List<Contact> getSortedContactList(){
        Collections.sort(mContactList,new ContactNameComparator());

       return  mContactList;
    }

    public List<Contact> getContactList() {
        return mContactList;
    }
}
