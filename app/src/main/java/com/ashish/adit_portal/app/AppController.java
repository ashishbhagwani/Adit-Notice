package com.ashish.adit_portal.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;

public class AppController extends Application{

	public static final String TAG = AppController.class.getSimpleName();

	private RequestQueue mRequestQueue;

	private static AppController mInstance;
	@Override
	public void onCreate() {

		super.onCreate();
		mInstance = this;

		// Create an InitializerBuilder
		Stetho.InitializerBuilder initializerBuilder =
				Stetho.newInitializerBuilder(this);

      // Enable Chrome DevTools
		initializerBuilder.enableWebKitInspector(
				Stetho.defaultInspectorModulesProvider(this)
		);

     // Enable command line interface
		initializerBuilder.enableDumpapp(
				Stetho.defaultDumperPluginsProvider(this)
		);

     // Use the InitializerBuilder to generate an Initializer
		Stetho.Initializer initializer = initializerBuilder.build();

     // Initialize Stetho with the Initializer
		Stetho.initialize(initializer);
	}
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(context);
		MultiDex.install(this);
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	public static   boolean isInternetAvailable(Context context) {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}