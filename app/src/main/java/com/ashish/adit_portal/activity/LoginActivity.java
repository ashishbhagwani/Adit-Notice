/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.ashish.adit_portal.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;
import com.ashish.adit_portal.helper.SQLiteHandler;
import com.ashish.adit_portal.helper.SessionManager;
import com.ashish.adit_portal.helper.TokenSend;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    private final int REQUEST_PERMISSION_External_STORAGE=3;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputEnroll;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private String year;
    private String dept;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputEnroll = (EditText) findViewById(R.id.enroll);
        inputPassword = (EditText) findViewById(R.id.password);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        Button forgot=(Button) findViewById(R.id.forgotpassword);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(intent);
            }
        });
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());
        isGooglePlayServicesAvailable(this);
        //asking permission for marshmallow
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_External_STORAGE);
            }
        }

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, Navigation_Main.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String enroll = inputEnroll.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!enroll.isEmpty() && !password.isEmpty()) {
                    if(enroll.trim().length()<=12){
                    // login user
                    if(AppController.isInternetAvailable(LoginActivity.this)) {
                        checkLogin(enroll, password);
                    }else{
                        Toast.makeText(getApplicationContext(), "Please Check your Internet Connection!", Toast.LENGTH_LONG)
                                .show();
                    }
                }else{
                        Toast.makeText(LoginActivity.this, "Enrollment No. is incorrect", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String enroll, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";


        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        JSONObject user = jObj.getJSONObject("student");
                        String name = user.getString("name");
                        String enroll = user.getString("enroll");
                        year= user.getString("year");
                        dept=user.getString("department");
                        String mobile=user.getString("mobile");
                        String email=user.getString("email");
                        String token= FirebaseInstanceId.getInstance().getToken();
                        Log.e("token login",token);
                        // Inserting row in users table
                        db.addUser(enroll,name,email,mobile,dept,year,token);
                        TokenSend.SendToken(token,enroll,LoginActivity.this);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this, Navigation_Main.class);
                        intent.putExtra("activity","login");
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("Json error: ",e.getMessage());

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(LoginActivity.this,"Something is wrong there might be problem with your internet connection",Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("Enrollment_number", enroll);
                params.put("Password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_External_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoginActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Permission Denied The App May Misbehave! You can change it by going to App Permissions", Toast.LENGTH_SHORT).show();
                }
        }
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}
