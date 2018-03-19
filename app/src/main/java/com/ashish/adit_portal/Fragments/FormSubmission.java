package com.ashish.adit_portal.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.activity.Navigation_Main;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;
import com.ashish.adit_portal.helper.FormResponse;
import com.ashish.adit_portal.helper.SQLiteHandler;
import com.ashish.adit_portal.theory_question_fragments.Question1;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormSubmission extends Fragment {

 private ProgressDialog pDialog;
    public FormSubmission() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView= inflater.inflate(R.layout.fragment_form_submission, container, false);
        final Bundle argument=getArguments();
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ftransaction = getFragmentManager().beginTransaction();
                        Fragment fragment = new FeedbackForm();
                        fragment.setArguments(getArguments());
                        ftransaction.replace(R.id.framelayout, fragment);
                        ftransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ftransaction.commit();
                        return true;
                    }
                }
                return false;
            }
        });

        final FormResponse response=(FormResponse) argument.get("object");
        pDialog=new ProgressDialog(getContext());
        pDialog.setCancelable(true);
        pDialog.setMessage("Sending Your Response...");
        TextView submission_box= (TextView) rootView.findViewById(R.id.submission_text);
        final String type=argument.getString("type");
        Log.e("trying",argument.getString("server_id")+argument.getString("enrollment_number")+argument.getString("type"));
        String answer="Ans:  ";
        String string1;
        if(type.equals("theory")) {
            string1 =   getString(R.string.question1)+"\n"+answer+response.getResponseText(1)+"\n\n"+
                        getString(R.string.question2)+"\n"+answer+response.getResponseText(2)+"\n\n"+
                        getString(R.string.question3)+"\n"+answer+response.getResponseText(3)+"\n\n"+
                        getString(R.string.question4)+"\n"+answer+response.getResponseText(4)+"\n\n"+
                        getString(R.string.question5)+"\n"+answer+response.getResponseText(5)+"\n\n"+
                        getString(R.string.question6)+"\n"+answer+response.getResponseText(6)+"\n\n"+
                        getString(R.string.question7)+"\n"+answer+response.getResponseText(7)+"\n\n"+
                        getString(R.string.question8)+"\n"+answer+response.getResponseText(8)+"\n\n"+
                        getString(R.string.question9)+"\n"+answer+response.getResponseText(9)+"\n\n"+
                        getString(R.string.question10)+"\n"+answer+response.getResponseText(10)+"\n\n"+
                        getString(R.string.question11)+"\n"+answer+response.getResponseText(11)+"\n\n"+
                        getString(R.string.question12)+"\n"+answer+response.getResponseText(12)+"\n\n"+
                        getString(R.string.question13)+"\n"+answer+response.getResponseText(13)+"\n\n"+
                        getString(R.string.question14)+"\n"+answer+response.getResponseText(14)+"\n\n"+
                        getString(R.string.question15)+"\n"+answer+response.getResponseText(15)+"\n\n"+
                        getString(R.string.question16)+"\n"+answer+response.getResponseText(16)+"\n\n"+
                        getString(R.string.question17)+"\n"+answer+response.getResponseText(17)+"\n\n"+
                        getString(R.string.question18)+"\n"+answer+response.getResponseText(18)+"\n\n"+
                        getString(R.string.question19)+"\n"+answer+response.getResponseText(19)+"\n\n"+
                        "Additional Comment:"+"\n"+response.getResponseText(20);

        }else{
            string1=getString(R.string.pquestion1)+"\n"+answer+response.getResponseText(1)+"\n\n"+
                    getString(R.string.pquestion2)+"\n"+answer+response.getResponseText(2)+"\n\n"+
                    getString(R.string.pquestion3)+"\n"+answer+response.getResponseText(3)+"\n\n"+
                    getString(R.string.pquestion4)+"\n"+answer+response.getResponseText(4)+"\n\n"+
                    getString(R.string.pquestion5)+"\n"+answer+response.getResponseText(5)+"\n\n"+
                    getString(R.string.pquestion6)+"\n"+answer+response.getResponseText(6)+"\n\n"+
                    getString(R.string.pquestion7)+"\n"+answer+response.getResponseText(7)+"\n\n"+
                    getString(R.string.pquestion8)+"\n"+answer+response.getResponseText(8)+"\n\n"+
                    getString(R.string.pquestion9)+"\n"+answer+response.getResponseText(9)+"\n\n"+
                    getString(R.string.pquestion10)+"\n"+answer+response.getResponseText(10)+"\n\n"+
                    "Additional Comment:"+"\n"+response.getResponseText(20);
        }
        submission_box.setText(string1);
        Button submit=(Button)rootView.findViewById(R.id.submission_button);
        Button refill=(Button)rootView.findViewById(R.id.refill_feedback);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                if(AppController.isInternetAvailable(getContext())) {
                    StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.URL_SEND_REPONSE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null && response.length() > 0) {
                                Log.e("response", response);
                            } else {
                                Log.e("failed", "onResponse:failed ");
                            }
                            try {

                                JSONObject inputobj = new JSONObject(response);
                                boolean error = inputobj.getBoolean("error");
                                if (!error) {
                                    SQLiteHandler db = new SQLiteHandler(getContext());
                                    db.removeForm(argument.getString("database_id"));
                                    if (argument.getString("type").equals("theory")) {
                                        ((Navigation_Main) getActivity()).removetheoryform(argument.getString("item_id"));
                                    } else {
                                        ((Navigation_Main) getActivity()).removepracticalform(argument.getString("item_id"));
                                    }
                                    hideDialog();
                                    Toast.makeText(getContext(), "Your Response has been submitted", Toast.LENGTH_SHORT).show();
                                    android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ftransaction = fm.beginTransaction();
                                    Fragment fragment = new RecycleFragment();
                                    ftransaction.replace(R.id.framelayout, fragment);
                                    ftransaction.commit();
                                } else {
                                    hideDialog();
                                    Toast.makeText(getContext(), inputobj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                hideDialog();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideDialog();
                            Log.e("error", error.toString());
                            Toast.makeText(getContext(), "Something is wrong there might be problem with your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("server_id", argument.getString("server_id"));
                            params.put("enrollment_number", argument.getString("enrollment_number"));
                            params.put("type", argument.getString("type"));
                            params.put("visibility", response.getResponseText(0));
                            params.put("question1", response.getResponseText(1));
                            params.put("question2", response.getResponseText(2));
                            params.put("question3", response.getResponseText(3));
                            params.put("question4", response.getResponseText(4));
                            params.put("question5", response.getResponseText(5));
                            params.put("question6", response.getResponseText(6));
                            params.put("question7", response.getResponseText(7));
                            params.put("question8", response.getResponseText(8));
                            params.put("question9", response.getResponseText(9));
                            params.put("question10", response.getResponseText(10));
                            if (argument.getString("type").equals("theory")) {
                                params.put("question11", response.getResponseText(11));
                                params.put("question12", response.getResponseText(12));
                                params.put("question13", response.getResponseText(13));
                                params.put("question14", response.getResponseText(14));
                                params.put("question15", response.getResponseText(15));
                                params.put("question16", response.getResponseText(16));
                                params.put("question17", response.getResponseText(17));
                                params.put("question18", response.getResponseText(18));
                                params.put("question19", response.getResponseText(19));
                            }
                            params.put("comment", response.getResponseText(20));
                            return params;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(sr, "submit_form");
                }else{
                    Toast.makeText(getContext(), "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        refill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fm=getActivity().getSupportFragmentManager();
                FragmentTransaction ftransaction = fm.beginTransaction();
                Fragment fragment = new Question1();
                fragment.setArguments(argument);
                ftransaction.replace(R.id.framelayout, fragment);
                ftransaction.commit();
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