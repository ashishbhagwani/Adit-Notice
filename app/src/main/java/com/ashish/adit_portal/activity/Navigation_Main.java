package com.ashish.adit_portal.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ashish.adit_portal.Fragments.Account_Info;
import com.ashish.adit_portal.Fragments.FeedbackForm;
import com.ashish.adit_portal.Fragments.OtherDepartment;
import com.ashish.adit_portal.Fragments.RecycleFragment;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;
import com.ashish.adit_portal.helper.NetworkStateReceiver;
import com.ashish.adit_portal.helper.SQLiteHandler;
import com.ashish.adit_portal.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Navigation_Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,NetworkStateReceiver.NetworkStateReceiverListener{
    private NetworkStateReceiver networkStateReceiver;
    private SQLiteHandler db;
    private SessionManager session;
    private Fragment fragment;
    private Menu theorysubmenu;
    private Menu practicalsubmenu;
    private MenuItem theoryitem;
    private MenuItem practicalitem;
    private String enrollno;
    private Toolbar toolbar;
    private final BroadcastReceiver Updated= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            checkDatabaseForForms();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        //inititalizing database
        db = new SQLiteHandler(getApplicationContext());

        //reading username and enrollment number from database
        HashMap<String,String> userdetails= null;
        try {
            userdetails = db.getUserDetails();
        } catch (SQLiteHandler.EmptyDatabaseException e) {
            e.printStackTrace();
        }
        String name= userdetails != null ? userdetails.get("Name") : null;
        enrollno= userdetails != null ? userdetails.get("Enrollment_number") : null;

        // session manager
        session = new SessionManager(getApplicationContext());


        //registering broadcast
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(Navigation_Main.this);
        lbm.registerReceiver(Updated, new IntentFilter("FORM_INSERTED"));




        //setting up Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting up drawer layout
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
        //Setting Navigaiton view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        //setting navigation menu for feedback forms
        Menu menu= navigationView.getMenu();
        theorysubmenu=menu.addSubMenu("Theory Feedback Forms");
        practicalsubmenu=menu.addSubMenu("Practical Feedback Forms");
        checkDatabaseForForms();
        if(getIntent().getStringExtra("fragment")!=null){
            if(getIntent().getStringExtra("fragment").equals("feedback")){
                //setting up fragment transaction for notification click
                if (drawer != null) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }else if(getIntent().getStringExtra("fragment").equals("livemsg")){
                String title=getIntent().getStringExtra("title");
                String content=getIntent().getStringExtra("content");
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                builder.setTitle(title);
                //Add Message
                builder.setMessage(content);
                //can it be cancelled
                builder.setCancelable(false);
                // Add the buttons
                builder.setPositiveButton("Got It!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.cancel();
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
            //setting up default fragment transaction
            FragmentTransaction ftransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new RecycleFragment();
            ftransaction.replace(R.id.framelayout, fragment);
            ftransaction.commit();


        //Creating DialogBox for Syncing the data
        if (getIntent().getStringExtra("activity") != null) {
            if (getIntent().getStringExtra("activity").equals("login")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                builder.setTitle("Do you want to sync?");
                //Add Message
                builder.setMessage("You have just logged in it is possible that there are some messages on server which won't be shown to you unless you Sync with server");
                //can it be cancelled
                builder.setCancelable(false);
                // Add the buttons
                builder.setPositiveButton("Sync", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        if(AppController.isInternetAvailable(Navigation_Main.this)) {
                            makerequest();
                        }else{
                            Toast.makeText(Navigation_Main.this, "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        // User cancelled the dialog
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        //setting up text on username and enrollement number in navigation header
        View header = navigationView.getHeaderView(0);
        TextView username=(TextView)header.findViewById(R.id.nav_name);
        TextView enroll=(TextView)header.findViewById(R.id.nav_enroll);
        username.setText(name);
        enroll.setText(enrollno);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_, menu);
        return true;
    }

    //For handling clicks on menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_logout){

            session.setLogin(false);

            db.deleteUsers();

            // Launching the login activity
            Intent intent = new Intent(Navigation_Main.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle("ADIT PORTAL 1.0.1");
            //Add Message
            builder.setMessage("Thanks for using this app, our main aim to build this app is to solve problems related with current system of our college and make it simpler and easy.");
            //can it be cancelled
            builder.setCancelable(false);
            // Add the buttons
            builder.setPositiveButton("Cool", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.cancel();
                }
            });
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    // For handling clicks on navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_notice) {
            fragment=new RecycleFragment();
            fragmentreplace();

        }else if(id == R.id.nav_user_detail){
            fragment=new Account_Info();
            fragmentreplace();

        }
        else if(id==R.id.nav_other_department){
            fragment=new OtherDepartment();
            fragmentreplace();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    //For replacing the fragment
    private void fragmentreplace(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    //For creating feedback forms
    private void createFeedbackForm(final Bundle bundle,Menu submenu){
        String code=bundle.getString("subject_code");
        if(submenu==theorysubmenu) {
            if (theoryitem != null) {
                theorysubmenu.removeItem(theoryitem.getItemId());
            }
        }else{
            if(practicalitem!=null){
                practicalsubmenu.removeItem(practicalitem.getItemId());
            }
        }
        MenuItem item=submenu.add(code);
        bundle.putString("item_id",Integer.toString(item.getItemId()));
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fragment=new FeedbackForm();
                Log.e("working in createforms",bundle.getString("database_id")+bundle.getString("type")+bundle.getString("subject")+bundle.getString("faculty")+bundle.getString("subject_code")+bundle.getString("enrollment_number")+bundle.getString("item_id"));
                fragment.setArguments(bundle);
                fragmentreplace();
                return false;
            }
        });
    }

    //For removing theory forms
    public void removetheoryform(String id){
        theorysubmenu.removeItem(Integer.parseInt(id));
        if(!theorysubmenu.hasVisibleItems()) {
            theoryitem = theorysubmenu.add("No Feedback Forms");
            theoryitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(Navigation_Main.this, "Currently there are no theory feedback forms", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    //For removing practical forms
     public void removepracticalform(String id){
         practicalsubmenu.removeItem(Integer.parseInt(id));
         if(!practicalsubmenu.hasVisibleItems()) {
             practicalitem = practicalsubmenu.add("No Feedback Forms");
             practicalitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                 @Override
                 public boolean onMenuItemClick(MenuItem item) {
                     Toast.makeText(Navigation_Main.this, "Currently there are no practical feedback forms", Toast.LENGTH_SHORT).show();
                     return true;
                 }
             });
         }
    }

    //check Database for Forms
    private void checkDatabaseForForms(){

        //check Theory Forms
        try {
            ArrayList<Bundle> theoryforms=db.getAllTheoryForm();
            theorysubmenu.clear();
            for(Bundle a:theoryforms){
                Bundle bundle=new Bundle();
                bundle.putString("database_id",a.getString("id"));
                bundle.putString("server_id",a.getString("server_id"));
                bundle.putString("type",a.getString("type"));
                bundle.putString("subject",a.getString("subject"));
                bundle.putString("faculty",a.getString("faculty"));
                bundle.putString("subject_code",a.getString("subject_code"));
                bundle.putString("enrollment_number",enrollno);
                Log.e("working in theoryforms",a.getString("id")+a.getString("type")+a.getString("subject")+a.getString("faculty")+a.getString("subject_code"));
                createFeedbackForm(bundle,theorysubmenu);
            }
        } catch (SQLiteHandler.EmptyDatabaseException e) {
            //Toast.makeText(this, "Atleast reaching here", Toast.LENGTH_SHORT).show();
            theorysubmenu.clear();
            theoryitem = theorysubmenu.add("No Feedback Forms");
            theoryitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(Navigation_Main.this, "Currently there are no theory feedback forms", Toast.LENGTH_SHORT).show();
                        return true;
                    }
            });
        }

        //Check practical forms
        try {
            ArrayList<Bundle> pracitcalforms=db.getAllPracticalForm();
            practicalsubmenu.clear();
            for(Bundle a:pracitcalforms){
                Bundle bundle=new Bundle();
                bundle.putString("database_id",a.getString("id"));
                bundle.putString("server_id",a.getString("server_id"));
                bundle.putString("type",a.getString("type"));
                bundle.putString("subject",a.getString("subject"));
                bundle.putString("faculty",a.getString("faculty"));
                bundle.putString("subject_code",a.getString("subject_code"));
                bundle.putString("enrollment_number",enrollno);
                Log.e("workingpractialforms",a.getString("id")+a.getString("type")+a.getString("subject")+a.getString("faculty")+a.getString("subject_code"));
                createFeedbackForm(bundle,practicalsubmenu);
            }
        } catch (SQLiteHandler.EmptyDatabaseException e) {
                practicalsubmenu.clear();
                practicalitem = practicalsubmenu.add("No Feedback Forms");
                practicalitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(Navigation_Main.this, "Currently there are no practical feedback forms", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
        }
    }


    //Request for old notices
    private void makerequest(){
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.URL_GETNOTICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject inputobj = new JSONObject(response);
                    boolean error = inputobj.getBoolean("error");
                    if (!error) {
                        JSONArray student = inputobj.getJSONArray("Notice");
                        for (int i = 0; i < student.length(); i++) {
                            JSONObject jsonObject = student.getJSONObject(i);
                            final String name = jsonObject.getString("name");
                            final String img_path = jsonObject.getString("img_path");
                            if (name != null) {
                                db.addNotice(name,img_path);
                            }
                        }
                    }else{
                        Toast.makeText(Navigation_Main.this,inputobj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent("NOTICE_INSERTED");
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
                    lbm.sendBroadcast(i);
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Navigation_Main.this, "Something is wrong there might be problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("Department",db.getUserDetails().get("Department"));
                    params.put("Year",db.getUserDetails().get("Year"));
                } catch (SQLiteHandler.EmptyDatabaseException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(sr, "makerequest");
    }
    public Toolbar getToolbar(){
        return toolbar;
    }

    @Override
    public void networkAvailable() {
        //Snackbar.make(findViewById(R.id.nav_view),"Network Connected",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void networkUnavailable() {
        Snackbar.make(findViewById(R.id.nav_view),"No Network Connection",Snackbar.LENGTH_LONG).show();
    }
}



