package com.ashish.adit_portal.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {
 private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        pDialog=new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setTitle("Processing....");
        final EditText enrollment=(EditText)findViewById(R.id.enrollreset);
        final EditText email=(EditText)findViewById(R.id.emailreset);
        Button reset=(Button)findViewById(R.id.btnreset);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String enroll = enrollment.getText().toString().trim();
                final String email_id = email.getText().toString().trim();
                if (!enroll.isEmpty()&&!email_id.isEmpty()){
                    if(AppController.isInternetAvailable(getApplicationContext())){
                        showDialog();
                        StringRequest stringrequest=new StringRequest(Request.Method.POST, AppConfig.URL_FORGOT_PASSWORD, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject jObj = null;
                                try {
                                    jObj = new JSONObject(response);
                                    boolean error = jObj.getBoolean("error");
                                    if(!error){
                                        String msg=jObj.getString("msg");
                                        Toast.makeText(ForgotPassword.this,msg, Toast.LENGTH_LONG).show();
                                        hideDialog();
                                    }else{
                                        hideDialog();
                                        String error_msg=jObj.getString("error_msg");
                                        Toast.makeText(ForgotPassword.this,error_msg, Toast.LENGTH_SHORT).show();
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
                                error.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Something is wrong there might be problem with your internet connection", Toast.LENGTH_LONG).show();
                            }
                        }){
                            protected Map<String,String> getParams(){
                                Map<String,String> params=new HashMap<String, String>();
                                params.put("enrollment_number",enroll);
                                params.put("email",email_id);
                                return params;
                            }
                        };AppController.getInstance().addToRequestQueue(stringrequest);
                    }else{
                        Toast.makeText(ForgotPassword.this, "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ForgotPassword.this, "Please fill the details!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
