package com.ashish.adit_portal.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ashish.adit_portal.R;
import com.ashish.adit_portal.app.AppController;

/**
 * Created by Ashish on 9/22/2016.
 */

public class OtherDepartment extends Fragment {
    Spinner department_spinner;
    Spinner year_spinner;
    Button show;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.department_other, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ftransaction = getFragmentManager().beginTransaction();
                        Fragment fragment = new RecycleFragment();
                        ftransaction.replace(R.id.framelayout, fragment);
                        ftransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ftransaction.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        department_spinner=(Spinner) rootView.findViewById(R.id.department_spinner);
        year_spinner=(Spinner)rootView.findViewById(R.id.year_spinner);
        show=(Button)rootView.findViewById(R.id.department_select);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppController.isInternetAvailable(getContext())) {
                    String dept = department_spinner.getSelectedItem().toString();
                    String year = year_spinner.getSelectedItem().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("department", dept);
                    bundle.putString("year", year);
                    Fragment fragment = new OtherNoticeFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.framelayout, fragment);
                    ft.commit();
                }else{
                    Toast.makeText(getContext(), "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }
}
