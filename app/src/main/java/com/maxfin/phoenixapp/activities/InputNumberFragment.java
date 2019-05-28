package com.maxfin.phoenixapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.Utils;
import com.maxfin.phoenixapp.managers.JournalManager;
import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;

public class InputNumberFragment extends Fragment {
    private EditText mInputNumberEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_input_number, container, false);
        mInputNumberEditText = view.findViewById(R.id.input_number_edit_text);

        FloatingActionButton customCallButton = view.findViewById(R.id.custom_call_button);
        customCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mInputNumberEditText.getText().toString();
                if (!s.equals("")) {
                    Contact contact = new Contact();
                    contact.setJId(s);
                    contact.setName(s);
                    contact.setNumber(s);
                    Uri path = Uri.parse("android.resource://com.maxfin.phoenixapp/" + R.drawable.ic_contact_circle_api2);
                    contact.setPhoto(path.toString());

                    Call call = new Call(
                            contact.getName(),
                            contact.getNumber(),
                            (byte) 0,
                            "15:45",
                            contact.getPhoto(),
                            contact.getContactId()
                    );
                    JournalManager.getJournalManager().addCall(call);
                    Intent makeCallIntent = new Intent(getActivity(), OutgoingCallActivity.class);
                    makeCallIntent.putExtra(Utils.NAME_KEY, call.getName());
                    makeCallIntent.putExtra(Utils.NUMBER_KEY, call.getNumber());
                    makeCallIntent.putExtra(Utils.PHOTO_KEY, call.getPhoto());
                    makeCallIntent.putExtra(Utils.ID_KEY, call.getId());
                    startActivity(makeCallIntent);
                } else
                    Toast.makeText(getContext(), "Пустое поле", Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonOne = view.findViewById(R.id.key1);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "1");
            }
        });

        Button buttonTwo = view.findViewById(R.id.key2);
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "2");
            }
        });
        Button buttonThree = view.findViewById(R.id.key3);
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "3");
            }
        });
        Button buttonFour = view.findViewById(R.id.key4);
        buttonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "4");
            }
        });
        Button buttonFive = view.findViewById(R.id.key5);
        buttonFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "5");
            }
        });
        Button buttonSix = view.findViewById(R.id.key6);
        buttonSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "6");
            }
        });
        Button buttonSeven = view.findViewById(R.id.key7);
        buttonSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "7");
            }
        });
        Button buttonEight = view.findViewById(R.id.key8);
        buttonEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "8");
            }
        });
        Button buttonNine = view.findViewById(R.id.key9);
        buttonNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "9");
            }
        });
        Button buttonNull = view.findViewById(R.id.key0);
        buttonNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "0");
            }
        });
        Button buttonPlus = view.findViewById(R.id.keyPlus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputNumberEditText.setText(mInputNumberEditText.getText() + "+");
            }
        });
        ImageButton buttonBack = view.findViewById(R.id.keyRemove);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = mInputNumberEditText.getText().toString();
                if (string.length() > 0)
                    mInputNumberEditText.setText(string.substring(0, string.length() - 1));
            }
        });
        buttonBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mInputNumberEditText.setText("");
                return false;
            }
        });


        return view;
    }


}
