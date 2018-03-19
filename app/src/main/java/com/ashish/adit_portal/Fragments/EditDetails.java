package com.ashish.adit_portal.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;
import com.ashish.adit_portal.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ashish on 2/8/2017.
 */

public class EditDetails extends Fragment {
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        db=new SQLiteHandler(getContext());
        final View rootView = inflater.inflate(R.layout.editdetails, container, false);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Processing....");
        pDialog.setCancelable(false);
        final Bundle bundle=getArguments();
        final TextView txtName = (TextView)rootView.findViewById(R.id.name);
        final TextView txtEmail = (TextView)rootView.findViewById(R.id.email_id);
        final Spinner year=(Spinner) rootView.findViewById(R.id.yearofstudy);

        final TextView txtMobile = (TextView)rootView.findViewById(R.id.Mobile_No);
        //Button btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        Button edit=(Button) rootView.findViewById(R.id.btnRegister);
        txtName.setText(bundle.getString("Name"));
        txtMobile.setText(bundle.getString("Mobile"));
        txtEmail.setText(bundle.getString("Email"));
        switch (bundle.getString("Year")) {
            case "First":year.setSelection(1);break;
            case "Second":year.setSelection(2);break;
            case "Third":year.setSelection(3);break;
            case "Fourth":year.setSelection(4);break;
        }
        edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (AppController.isInternetAvailable(getContext())) {
                            if (!year.getSelectedItem().toString().equals("Year of Study")) {
                                showDialog();
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_EDIT_DETAILS, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject mainObject=new JSONObject(response);
                                            boolean error=mainObject.getBoolean("error");
                                            if(!error){
                                                String msg=mainObject.getString("msg");
                                                hideDialog();
                                                db.updateUser(bundle.getString("Enrollment"),txtName.getText().toString(),year.getSelectedItem().toString(),txtEmail.getText().toString(), txtMobile.getText().toString());
                                                Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
                                            }else{
                                                String errorMsg = mainObject.getString("error_msg");
                                                Log.e("errorMsg",errorMsg);
                                                hideDialog();
                                                Toast.makeText(getContext(),errorMsg, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            hideDialog();
                                            Toast.makeText(getContext(), "Json Error"+""+e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        hideDialog();
                                        Toast.makeText(getContext(),
                                                "Something is wrong there might be problem with your internet connection", Toast.LENGTH_LONG).show();
                                    }
                                }) {
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("Name", txtName.getText().toString());
                                        params.put("Year", year.getSelectedItem().toString());
                                        params.put("Email", txtEmail.getText().toString());
                                        params.put("Mobile", txtMobile.getText().toString());
                                        params.put("Enrollment",bundle.getString("Enrollment"));
                                        return params;
                                    }

                                };
                                AppController.getInstance().addToRequestQueue(stringRequest, "editdetails");
                            } else {
                                Toast.makeText(getContext(), "Please select year of study", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getContext(), "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ftransaction = getFragmentManager().beginTransaction();
                        Fragment fragment =new Account_Info();
                        ftransaction.replace(R.id.framelayout, fragment);
                        ftransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ftransaction.commit();
                        return true;
                    }
                }
                return false;
            }
        });



        return rootView;
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
