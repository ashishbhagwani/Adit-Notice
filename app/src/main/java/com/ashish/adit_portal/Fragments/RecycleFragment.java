package com.ashish.adit_portal.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ashish.adit_portal.R;
import com.ashish.adit_portal.activity.Navigation_Main;
import com.ashish.adit_portal.activity.NoticeImage;
import com.ashish.adit_portal.helper.DataAdapter;
import com.ashish.adit_portal.helper.ImageStorage;
import com.ashish.adit_portal.helper.Notice;
import com.ashish.adit_portal.helper.SQLiteHandler;

import java.util.ArrayList;

public class RecycleFragment extends Fragment{
    protected static final String TAG = "RecyclerViewFragment";
    private DataAdapter adapter;
    private ActionMode mActionMode;
    boolean isMultiSelect = false;
    private ArrayList<Notice> country=new ArrayList<>();
    private ArrayList<Notice> selectedItems=new ArrayList<>();
    private SQLiteHandler database;

    private ActionMode.Callback mCallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            ((Navigation_Main)getActivity()).getToolbar().setVisibility(View.GONE);
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
                    builder.setTitle("Delete");
                    //Add Message
                    builder.setMessage("Do you want to delete selected notices?");
                    // Add the buttons
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            for(Notice a:selectedItems) {
                                database.removeNotice(a.getTitle());
                            }
                            country.removeAll(selectedItems);
                            selectedItems.clear();
                            refreshAdapter();
                            mActionMode.finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mActionMode.finish();
                            dialog.cancel();
                            // User cancelled the dialog
                        }
                    });
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            selectedItems = new ArrayList<>();
            ((Navigation_Main)getActivity()).getToolbar().setVisibility(View.VISIBLE);
            refreshAdapter();
        }
    };
    private final BroadcastReceiver Updated= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            database.getAllNotices(country);
            //Log.e("Arraylistno",country.get(0).getTitle());
            adapter.notifyDataSetChanged();
        }
    };
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=new SQLiteHandler(getContext());
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(Updated, new IntentFilter("NOTICE_INSERTED"));
        database.getAllNotices(country);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.framelayout, container, false);
        rootView.setTag(TAG);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        adapter = new DataAdapter(country,selectedItems,getContext());
        adapter.setOnLongClickListenre(new DataAdapter.LongClickListener() {
            @Override
            public boolean onLongClick(View v,int position) {
                if (!isMultiSelect) {
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = ((Navigation_Main)getActivity()).startSupportActionMode(mCallback);//startActionMode(mActionModeCallback);
                    }
                }
                multi_select(position);
                //Toast.makeText(getContext(), "Item is long clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        adapter.setListener(new DataAdapter.Listener() {
            @Override
            public void onClick(int position,View v) {
                if (isMultiSelect) {
                    multi_select(position);
                    if(selectedItems.isEmpty()){
                        mActionMode.finish();
                    }
                }
                else {
                    Notice n = country.get(position);
                    String imagename = n.getUrl();
                    if (ImageStorage.checkifImageExists(imagename)) {
                        Intent intent = new Intent(getContext(), NoticeImage.class);
                        intent.putExtra("image_name", imagename);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Image does not exist click the download button to download it", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return rootView;
    }
    public void refreshAdapter()
    {
        adapter.selectedItems=this.selectedItems;
        adapter.countries=country;
        adapter.notifyDataSetChanged();
    }
    public void multi_select(int position) {
        if (mActionMode != null) {
            if (selectedItems.contains(country.get(position)))
                selectedItems.remove(country.get(position));
            else
                selectedItems.add(country.get(position));
            if (selectedItems.size() > 0)
                mActionMode.setTitle("" + selectedItems.size());
            else
                mActionMode.setTitle("");
            refreshAdapter();
        }
    }
}
