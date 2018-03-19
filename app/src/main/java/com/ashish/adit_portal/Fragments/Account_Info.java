package com.ashish.adit_portal.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashish.adit_portal.R;
import com.ashish.adit_portal.helper.SQLiteHandler;

import java.util.HashMap;

public class Account_Info extends Fragment {
	//private SessionManager session;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.accountinfo, container, false);
		TextView txtName = (TextView)rootView.findViewById(R.id.namecardcontent);
		TextView txtEnroll = (TextView)rootView.findViewById(R.id.enrollcardcontent);
		TextView txtEmail = (TextView)rootView.findViewById(R.id.emailcardcontent);
		TextView txtMobile = (TextView)rootView.findViewById(R.id.phonecardcontent);
		TextView txtDept = (TextView)rootView.findViewById(R.id.departmentcardcontent);
		TextView txtYear = (TextView)rootView.findViewById(R.id.yearcardcontent);
		//Button btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
		ImageView edit=(ImageView) rootView.findViewById(R.id.editaccount);
		FrameLayout frameLayout=(FrameLayout) rootView.findViewById(R.id.planeLayout);

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

		// SqLite database handler
		SQLiteHandler db = new SQLiteHandler(getContext());

		// Fetching user details from SQLite
		HashMap<String, String> user = null;
		try {
			user = db.getUserDetails();
		} catch (SQLiteHandler.EmptyDatabaseException e) {
			e.printStackTrace();
		}


		final String name = user.get("Name");
		final String enroll = user.get("Enrollment_number");
		final String mobile = user.get("Mobile");
		final String email = user.get("Email");
		final String department = user.get("Department");
		final String year = user.get("Year");
		if(department.equals("Computer Engineering")){
			frameLayout.setBackground(getResources().getDrawable(R.drawable.cp));
		}else if(department.equals("Mechanical Engineering")){
			frameLayout.setBackground(getResources().getDrawable(R.drawable.mechanical));
		}else if(department.equals("Information Technology")){
			frameLayout.setBackground(getResources().getDrawable(R.drawable.it));
		}else if(department.equals("Civil Engineering")){
			frameLayout.setBackground(getResources().getDrawable(R.drawable.civil));
		}else if(department.equals("Food Processing & Technology")){
			frameLayout.setBackground(getResources().getDrawable(R.drawable.ft));
		}else if(department.equals("Electronics & Communication Engineering")){
			frameLayout.setBackground(getResources().getDrawable(R.drawable.ec));
		}else if(department.equals("Electrical Engineering")){
			frameLayout.setBackground(getResources().getDrawable(R.drawable.electrical));
		}else if(department.equals("Automobile Engineering")){
			frameLayout.setBackground(getResources().getDrawable(R.drawable.automobile));
		}

		// Displaying the user details on the screen
		txtName.setText(name);
		txtEnroll.setText(enroll);
		txtDept.setText(department);
		txtYear.setText(year);
		txtEmail.setText(email);
		txtMobile.setText(mobile);
        final Bundle bundle=new Bundle();
        bundle.putString("Name",name);
        bundle.putString("Year",year);
        bundle.putString("Email",email);
        bundle.putString("Mobile",mobile);
		bundle.putString("Enrollment",enroll);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ftransaction = getFragmentManager().beginTransaction();
                Fragment fragment = new EditDetails();
                fragment.setArguments(bundle);
                ftransaction.replace(R.id.framelayout, fragment);
                ftransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftransaction.commit();
            }
        });
		// Logout button click event


		return rootView;
	}
}
