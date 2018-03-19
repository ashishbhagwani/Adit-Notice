/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.ashish.adit_portal.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;
import com.ashish.adit_portal.helper.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputFullName;
    private EditText inputEnroll;
    private EditText emailid;
    private EditText mobileno;
    private EditText inputPassword;
    private EditText inputPassword1;
    private ProgressDialog pDialog;
    private Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEnroll = (EditText) findViewById(R.id.enroll);
        emailid = (EditText) findViewById(R.id.email_id);
        mobileno = (EditText) findViewById(R.id.Mobile_No);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword1 = (EditText) findViewById(R.id.password1);
        spinner=(Spinner) findViewById(R.id.yearofstudy);

        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        Button btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        SessionManager session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, Navigation_Main.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String enroll = inputEnroll.getText().toString().trim();
                String email_id = emailid.getText().toString().trim();
                String mobile = mobileno.getText().toString();
                String password = inputPassword.getText().toString().trim();
                String password1 = inputPassword1.getText().toString().trim();
                String year=spinner.getSelectedItem().toString().trim();
                if (!name.isEmpty() && !enroll.isEmpty() && !email_id.isEmpty() && !password.isEmpty() && !password1.isEmpty() && !mobile.isEmpty() && !year.isEmpty()) {
                    if(!year.equals("Year of Study")) {
                        if(enroll.trim().length()<=12){
                            if (password.equals(password1)) {
                                if(AppController.isInternetAvailable(RegisterActivity.this)) {
                                    registerUser(name, enroll, email_id, mobile, password, password1, year);
                                }else{
                                    Toast.makeText(RegisterActivity.this, "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this, "Enrollment is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "You have not selected your year of study!", Toast.LENGTH_LONG).show();                    }

                }else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String enroll, final String email_id,final String mobile, final String password , final String password1, final String year) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        /*JSONObject user = jObj.getJSONObject("student");
                        String name = user.getString("name");
                        String enroll = user.getString("enroll");
                        String year= user.getString("year");
                        String dept=user.getString("department");
                        String mobile=user.getString("mobile");
                        String email=user.getString("email");
                        String token= FirebaseInstanceId.getInstance().getToken();*/
                        // Inserting row in users table
                        //db.addUser(enroll, name,email,mobile,dept,year,token);

                        Toast.makeText(RegisterActivity.this, "Registration successful!!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String s= e.toString();
                    Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Something is wrong there might be problem with your internet connection", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                String token=FirebaseInstanceId.getInstance().getToken();
                params.put("Name", name);
                params.put("Enrollment_number", enroll);
                params.put("Email", email_id);
                params.put("Mobile_number", mobile);
                params.put("Password", password);
                params.put("Password1", password1);
                params.put("Year",year);
                params.put("Token",token);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
