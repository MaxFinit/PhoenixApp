package com.maxfin.phoenixapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.Utils;
import com.maxfin.phoenixapp.managers.JournalManager;
import com.maxfin.phoenixapp.models.Call;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class JournalFragment extends Fragment {
    // private static final String TAG = "JournalFragment";
    private RecyclerView mJournalRecyclerView;
    private TextView mEmptyJournalTextView;
    private ProgressBar mProgressBar;
    private JournalAdapter mAdapter;
    private JournalManager mJournalManager;
    private InputNumberFragment mInputNumberFragment;
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;
    private List<Call> mCallList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
        mJournalRecyclerView = view.findViewById(R.id.journal_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mJournalRecyclerView.setLayoutManager(linearLayoutManager);
        mEmptyJournalTextView = view.findViewById(R.id.empty_journal_text_view);
        mFragmentManager = getFragmentManager();
        mInputNumberFragment = new InputNumberFragment();
        mProgressBar = view.findViewById(R.id.journal_progress_bar);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.custom_number_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFragmentTransaction = mFragmentManager.beginTransaction();
                if (!mInputNumberFragment.isAdded()) {
                    mFragmentTransaction.add(R.id.input_number_container, mInputNumberFragment);
                    mFragmentTransaction.addToBackStack(null);

                } else {
                    mFragmentTransaction.remove(mInputNumberFragment);
                }


                mFragmentTransaction.commit();


/////////////////////////////////////временный код
//                Contact contact = new Contact();
//                contact.setJId("maxfin2@jabber.ru");
//                contact.setName("Max");
//                contact.setNumber("+380713222303");
//                Uri path = Uri.parse("android.resource://com.maxfin.phoenixapp/" + R.drawable.ic_contact_circle_api2);
//                contact.setPhoto(path.toString());
//
//                Call call = new Call("Max", contact.getNumber(), (byte) 0, "15-00", path.toString(), "23");
//                mJournalManager.addCall(call);
//
//                Intent intent = new Intent(getActivity(), OutgoingCallActivity.class);
//                startActivity(intent);
////////////////////////////////////////////////////

            }
        });

        EditText searchJournalEditText = view.findViewById(R.id.search_journal_edit);
        searchJournalEditText.addTextChangedListener(new TextWatcher() {
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

        // updateUi();


        return view;
    }

    private void updateUi() {
        mJournalManager = JournalManager.getJournalManager();
        mCallList = mJournalManager.getCalls();

        if (mCallList.size() == 0)
            mEmptyJournalTextView.setVisibility(View.VISIBLE);
        else
            mEmptyJournalTextView.setVisibility(View.GONE);


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
        LoadJournalTask task = new LoadJournalTask(this);
        task.execute();
    }


    private void filter(String text) {
        List<Call> filteredList = new ArrayList<>();

        for (Call item : mCallList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getNumber().contains(text)) {
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
    }


    private class JournalHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView mNameJournalTextView;
        private TextView mNumberJournalTextView;
        private TextView mDataJournalTextView;
        private CircleImageView mPhotoJournalImageView;
        private ImageView mCallTypeImageView;
        private Call mCall;


        JournalHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_recycler_journal, parent,
                    false));
            itemView.setOnCreateContextMenuListener(this);
            mNameJournalTextView = itemView.findViewById(R.id.journal_name_text_view);
            mNumberJournalTextView = itemView.findViewById(R.id.journal_number_text_view);
            mPhotoJournalImageView = itemView.findViewById(R.id.journal_photo_image_view);
            mCallTypeImageView = itemView.findViewById(R.id.journal_type_call_image_view);
            mDataJournalTextView = itemView.findViewById(R.id.journal_data_text_view);

        }


        void bind(Call call) {
            mCall = call;
            mNameJournalTextView.setText(call.getName());
            mNumberJournalTextView.setText(call.getNumber());
            mPhotoJournalImageView.setImageURI(Uri.parse(call.getPhoto()));
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

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
            inflater.inflate(R.menu.context_journal_menu, contextMenu);
            MenuItem call = contextMenu.getItem(0);
            MenuItem edit = contextMenu.getItem(1);
            MenuItem block = contextMenu.getItem(2);
            MenuItem delete = contextMenu.getItem(3);
            MenuItem deleteAll = contextMenu.getItem(4);
            call.setOnMenuItemClickListener(mOnMenuItemClickListener);
            edit.setOnMenuItemClickListener(mOnMenuItemClickListener);
            block.setOnMenuItemClickListener(mOnMenuItemClickListener);
            delete.setOnMenuItemClickListener(mOnMenuItemClickListener);
            deleteAll.setOnMenuItemClickListener(mOnMenuItemClickListener);

        }


        private final MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.make_call_context_menu:
                        Call call = new Call(
                                mCall.getName(),
                                mCall.getNumber(),
                                (byte) 0,
                                mCall.getPhoto(),
                                mCall.getContactId());
                        mJournalManager.addCall(call);
                        Intent makeCallIntent = new Intent(getActivity(), OutgoingCallActivity.class);
                        makeCallIntent.putExtra(Utils.NAME_KEY, call.getName());
                        makeCallIntent.putExtra(Utils.NUMBER_KEY, call.getNumber());
                        makeCallIntent.putExtra(Utils.PHOTO_KEY, call.getPhoto());
                        startActivity(makeCallIntent);
                        break;
                    case R.id.block_contact_context_menu:
                        break;
                    case R.id.edit_dialog_context_menu:
                        break;
                    case R.id.delete_journal_context_menu:
                        mJournalManager.clearCall(mCall);
                        updateUi();
                        break;
                    case R.id.delete_all_contact_context_menu:
                        mJournalManager.clearJournal();
                        updateUi();
                        break;
                }
                return true;
            }
        };

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

        void filterList(List<Call> filteredList) {
            mCallList = filteredList;
            notifyDataSetChanged();
        }


    }

    private static class LoadJournalTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<JournalFragment> activityReference;

        LoadJournalTask(JournalFragment context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JournalFragment activity = activityReference.get();
            activity.mJournalManager = JournalManager.getJournalManager();
            activity.mCallList = activity.mJournalManager.getCalls();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            JournalFragment activity = activityReference.get();
            super.onPostExecute(aVoid);
            activity.mProgressBar.setVisibility(View.GONE);
            activity.updateUi();
        }


    }

}
