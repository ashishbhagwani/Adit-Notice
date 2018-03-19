package com.ashish.adit_portal.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "ajaymin28";

	// Login table name
	private static final String TABLE_USER = "student";
	private static final String TABLE_NAME = "Notice";
	private static final String TABLE_FORM="Forms";

    //feedback column names
    private static final String KEY_TYPE="type";
    private static final String KEY_CODE="subject_code";
    private static final String KEY_SUBJECT="subject_title";
    private static final String KEY_FACULTY="faculty";
    private static final String KEY_SERVER="server_id";

    //Notice column names
	private static final String KEY_ID = "ID";
	private static final String KEY_TITLE = "Title";
	private static final String KEY_IMAGE_NAME = "Image_Name";

	// Login Table Columns names
	private static final String KEY_ENROLL = "Enrollment_number";
	private static final String KEY_NAME = "Name";
	private static final String KEY_EMAIL = "Email";
	private static final String KEY_DEPARTMENT = "Department";
	private static final String KEY_MOBILE = "Mobile_number";
	private static final String KEY_YEAR = "Year";
	private static final String KEY_TOKEN="Token";
    private class MyAsyncTask extends AsyncTask<ArrayList<Notice>,Void,Void>{
        String selectQuery;
        SQLiteDatabase db;
        Cursor cursor;
        @Override
        protected void onPreExecute() {
            selectQuery = "SELECT  * FROM " + TABLE_NAME+" ORDER BY ID DESC";
            db = SQLiteHandler.this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        }

        @Override
        protected Void doInBackground(ArrayList<Notice>[] params) {
            params[0].clear();
            cursor.moveToFirst();
            if(cursor.getCount()>0) {
                do{
                    Notice notice=new Notice();
                    notice.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                    notice.setUrl(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_NAME)));
                    params[0].add(notice);
                }while(cursor.moveToNext());

            }
           return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cursor.close();
            db.close();
            Log.e(TAG, "Fetching notices from Sqlite");
        }
    }
    public class EmptyDatabaseException extends Exception{
		EmptyDatabaseException(String s){
			super(s);
		}
	}
	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " +
				TABLE_USER + "(" +
				KEY_ENROLL + " TEXT," +
				KEY_NAME  + " TEXT," +
				KEY_EMAIL + " TEXT," +
				KEY_MOBILE + " TEXT," +
				KEY_DEPARTMENT + " TEXT," +
				KEY_YEAR + " TEXT," +
				KEY_TOKEN + " TEXT" +
				")";
        String CREATE_FEEDBACK_TABLE = "CREATE TABLE " +
                TABLE_FORM + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_SERVER  + " TEXT," +
                KEY_CODE  + " TEXT," +
                KEY_SUBJECT + " TEXT," +
                KEY_FACULTY + " TEXT," +
                KEY_TYPE + " TEXT" +
                ")";
		String CREATE_NOTICE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "(" +
				KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				KEY_TITLE  + " TEXT UNIQUE," +
				KEY_IMAGE_NAME + " TEXT" +
				")";

		db.execSQL(CREATE_LOGIN_TABLE);
		db.execSQL(CREATE_NOTICE_TABLE);
        db.execSQL(CREATE_FEEDBACK_TABLE);
		Log.e("mesage", "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String enroll, String Name, String email, String mobile,String department,String year,String token) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ENROLL, enroll); // Enroll
		values.put(KEY_NAME, Name); // Name
		values.put(KEY_EMAIL, email); // Email
		values.put(KEY_MOBILE, mobile); // mobile
		values.put(KEY_DEPARTMENT, department); // department
		values.put(KEY_YEAR,year);
		values.put(KEY_TOKEN,token);//year

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}
    /**
     * Getting user data from database
     * */
    public HashMap<String,String> getUserDetails() throws EmptyDatabaseException {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            user.put("Name", cursor.getString(cursor.getColumnIndex("Name")));
            user.put("Enrollment_number", cursor.getString(cursor.getColumnIndex("Enrollment_number")));
            user.put("Email", cursor.getString(cursor.getColumnIndex("Email")));
            user.put("Mobile", cursor.getString(cursor.getColumnIndex("Mobile_number")));
            user.put("Department", cursor.getString(cursor.getColumnIndex("Department")));
            user.put("Year", cursor.getString(cursor.getColumnIndex("Year")));
            user.put("Token", cursor.getString(cursor.getColumnIndex("Token")));

        }
        else{
            throw new EmptyDatabaseException("Cursor is Empty");
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }



    void addForm(String server, String code, String subject, String faculty, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SERVER,server);
        values.put(KEY_CODE, code); // subject_code
        values.put(KEY_SUBJECT, subject); // subject
        values.put(KEY_FACULTY, faculty); // faculty
        values.put(KEY_TYPE, type); // type

        // Inserting Row
        long id = db.insert(TABLE_FORM, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New Form inserted into sqlite: " + id);
    }

    public ArrayList<Bundle> getAllTheoryForm() throws EmptyDatabaseException{
        ArrayList<Bundle> allfeedbacks=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FORM,new String[]{KEY_ID,KEY_SERVER,KEY_CODE,KEY_SUBJECT,KEY_FACULTY,KEY_TYPE},KEY_TYPE+"=?",new String[]{"theory"},null,null,null);
        if(cursor.getCount()>0) {
            if (cursor .moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Bundle bundle=new Bundle();
                    bundle.putString("id",cursor.getString(cursor.getColumnIndex(KEY_ID)));
                    bundle.putString("server_id",cursor.getString(cursor.getColumnIndex(KEY_SERVER)));
                    bundle.putString("subject_code", cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                    bundle.putString("subject", cursor.getString(cursor.getColumnIndex(KEY_SUBJECT)));
                    bundle.putString("faculty", cursor.getString(cursor.getColumnIndex(KEY_FACULTY)));
                    bundle.putString("type", cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
                    allfeedbacks.add(bundle);
                    cursor.moveToNext();
                    Log.e(TAG, "Fetching theoryform from Sqlite: " +bundle.get("subject_code").toString());
                }
            }
        }
        else{
            throw new EmptyDatabaseException("Cursor is Empty");
        }
        cursor.close();
        db.close();
        // return feedbacks
        return allfeedbacks;
    }

    /**
     * get all practical forms
     * @return
     * @throws EmptyDatabaseException
     */
    public ArrayList<Bundle> getAllPracticalForm() throws EmptyDatabaseException{
        ArrayList<Bundle> allfeedbacks=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FORM,new String[]{KEY_ID,KEY_SERVER,KEY_CODE,KEY_SUBJECT,KEY_FACULTY,KEY_TYPE},KEY_TYPE+"=?",new String[]{"practical"},null,null,null);
        if(cursor.getCount()>0) {
            if (cursor .moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Bundle bundle=new Bundle();
                    bundle.putString("id",cursor.getString(cursor.getColumnIndex(KEY_ID)));
                    bundle.putString("server_id",cursor.getString(cursor.getColumnIndex(KEY_SERVER)));
                    bundle.putString("subject_code", cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                    bundle.putString("subject", cursor.getString(cursor.getColumnIndex(KEY_SUBJECT)));
                    bundle.putString("faculty", cursor.getString(cursor.getColumnIndex(KEY_FACULTY)));
                    bundle.putString("type", cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
                    allfeedbacks.add(bundle);
                    cursor.moveToNext();
                    Log.e(TAG, "Fetching practicalform from Sqlite: " +bundle.get("subject_code").toString());
                }
            }
        }
        else{
            throw new EmptyDatabaseException("Cursor is Empty");
        }
        cursor.close();
        db.close();
        // return feedbacks
        return allfeedbacks;
    }
    public void removeForm(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FORM,KEY_ID+"=?",new String[]{id});
        db.close();
    }

    public void removeNotice(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_TITLE+"=?",new String[]{title});
        db.close();
    }

    public void updateUser(String enroll,String name,String year,String email,String phone){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(KEY_ENROLL, enroll); // Enroll
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_MOBILE, phone); // mobile
        //values.put(KEY_DEPARTMENT, department); // department
        values.put(KEY_YEAR,year);
        //values.put(KEY_TOKEN,token);
        db.update(TABLE_USER,values,KEY_ENROLL+"=?",new String[]{enroll});
        db.close();
    }
    /**
     * add notice
     * @param Title
     * @param img
     */
    public void addNotice(String Title,String img ){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, Title);
        values.put(KEY_IMAGE_NAME, img);
        // Inserting Row
        long code= db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close(); // Closing database connection
        Log.e(TAG, "New notice inserted into sqlite: " + code);
    }

    /**
     * get all notices
     * @param notices
     * @throws EmptyDatabaseException
     */
	public void getAllNotices(ArrayList<Notice> notices)  {
        new MyAsyncTask().execute(notices);
	}

    /**
     * add token
     * @param enroll
     * @param token
     */
    public void addToken(String enroll,String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TOKEN,token);//year
        db.update(TABLE_USER,values,KEY_ENROLL+"=?",new String[]{enroll});
        db.close(); // Closing database connection
    }

    /**
     * delete user from database
     */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}
}
