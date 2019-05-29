package com.maxfin.phoenixapp.managers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.models.Contact;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactManager {
    private static final String TAG = "Загружаем контакты";
    private static ContactManager sContactManager;
    private List<Contact> mContactList;
    private List<Contact> mDialogContactList;
    private Contact mContact;
    private MessageManager messageManager;

    private ContactManager(Context context) {
        uploadContacts(context);
    }

    public static ContactManager get(Context context) {
        if (sContactManager == null) {
            sContactManager = new ContactManager(context);
        }
        return sContactManager;
    }

    public void uploadContacts(Context mContext) {
        String idLast;
        String idPrev = "";

        mDialogContactList = new ArrayList<>();
        mContactList = new ArrayList<>();
        messageManager = MessageManager.get();

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.
                query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        assert cursor != null;

        if (cursor.moveToFirst()) {
            do {
                mContact = new Contact();
                mContact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                mContact.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                mContact.setContactId(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                mContact.setPhoto(uploadImage(mContext));
                mContact.setIsLoaded(false);
                /*
                Ужасный костыль, из-за дублирования номеров, пофиксить потом
                */
                idLast = mContact.getContactId();
                if (!idPrev.equals(idLast)) {
                    mContactList.add(mContact);
                    uploadDialogContacts(mContact);
                }
                idPrev = idLast;
                Log.i(TAG, mContact.getName());
            } while (cursor.moveToNext());
            Log.i(TAG, "" + mContactList.size());
            cursor.close();
        }


    }


    private String uploadImage(Context mContext) {
        String imageUri;
        Uri u = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(mContact.getContactId()));
        imageUri = Uri.withAppendedPath(u, ContactsContract.Contacts.Photo.DISPLAY_PHOTO).toString();
        try {
            AssetFileDescriptor fd = mContext.getContentResolver().
                    openAssetFileDescriptor(Uri.parse(imageUri), "r");
        } catch (FileNotFoundException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageUri = Uri.parse("android.resource://com.maxfin.phoenixapp/" + R.drawable.ic_avatar)
                        .toString();
            } else {
                imageUri = Uri.parse("android.resource://com.maxfin.phoenixapp/" + R.drawable.ic_contact_circle_api2)
                        .toString();
            }
            e.printStackTrace();
        }
        return imageUri;
    }


    private void uploadDialogContacts(Contact contact) {
        boolean isAdd = false;

        for (Contact item : messageManager.getContactList()) {
            if (item.getNumber().equals(contact.getNumber())) {
                isAdd = true;
                break;
            }
        }

        if (!isAdd) {
            mDialogContactList.add(contact);
        }

    }


    public void returnToCheckedList(Contact contact) {
        mDialogContactList.add(contact);
    }


    public List<Contact> getCheckedLoadList() {

        Collections.sort(mDialogContactList, new ContactNameComparator());

        return mDialogContactList;


    }


    public class ContactNameComparator implements Comparator<Contact> {
        public int compare(Contact left, Contact right) {
            return left.getName().compareTo(right.getName());
        }
    }

    public List<Contact> getSortedContactList() {
        Collections.sort(mContactList, new ContactNameComparator());

        return mContactList;
    }

}
