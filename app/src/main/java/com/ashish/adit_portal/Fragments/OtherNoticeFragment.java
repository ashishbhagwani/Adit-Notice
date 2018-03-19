package com.ashish.adit_portal.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.activity.OtherNoticeImage;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;
import com.ashish.adit_portal.helper.Notice;
import com.ashish.adit_portal.helper.OtherDataAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OtherNoticeFragment extends Fragment {

    private ProgressDialog pDialog;

    protected static final String TAG = "RecyclerViewFragment";
    private ArrayList<Notice> country = new ArrayList<>();
    private OtherDataAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        final String dept=bundle.getString("department");
        final String year=bundle.getString("year");
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);

        pDialog.setMessage("Getting Notices...");
        showDialog();
        if(AppController.isInternetAvailable(getContext())) {
            StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.URL_GETNOTICE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideDialog();
                    try {
                        JSONObject inputobj = new JSONObject(response);
                        boolean error = inputobj.getBoolean("error");
                        if (!error) {
                            JSONArray student = inputobj.getJSONArray("Notice");
                            for (int i = 0; i < student.length(); i++) {
                                JSONObject jsonObject = student.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                String img_path = jsonObject.getString("img_path");
                                if (name != null) {
                                    Notice notice = new Notice();
                                    notice.setTitle(name);
                                    notice.setUrl(img_path);
                                    country.add(notice);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), inputobj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideDialog();
                    Toast.makeText(getContext(), "Something is wrong there might be problem with your internet connection", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Department", dept);
                    params.put("Year", year);
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(sr, TAG);
        }else{
            Toast.makeText(getContext(), "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.framelayout, container, false);
        rootView.setTag(TAG);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ftransaction = getFragmentManager().beginTransaction();
                        Fragment fragment = new OtherDepartment();
                        ftransaction.replace(R.id.framelayout, fragment);
                        ftransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ftransaction.commit();
                        return true;
                    }
                }
                return false;
            }
        });
       RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        recyclerView.setLayoutManager(layoutManager);
        adapter = new OtherDataAdapter(country);
        adapter.setListener(new OtherDataAdapter.Listener() {
            @Override
            public void onClick(int position) {
                if(AppController.isInternetAvailable(getContext())) {
                    String imagename = country.get(position).getUrl();
                    Intent intent = new Intent(getContext(), OtherNoticeImage.class);
                    intent.putExtra("image_name", imagename);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set CustomAdapter as the adapter for RecyclerView.
        recyclerView.setAdapter(adapter);


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