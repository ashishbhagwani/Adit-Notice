package com.ashish.adit_portal.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ashish on 12/5/2016.
 */

public class TokenSend {
    public static void SendToken(final String Token, final String enroll, final Context context) {
        if (AppController.isInternetAvailable(context)) {
            StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.URL_TOKEN_REFRESH, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(context,response, Toast.LENGTH_SHORT).show();
                    Log.e("php response", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed to Send token", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<>();
                    params.put("token", Token);
                    params.put("mobile", enroll);
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(sr, "tokenrefresh");
        }else{
            Toast.makeText(context, "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }
}
