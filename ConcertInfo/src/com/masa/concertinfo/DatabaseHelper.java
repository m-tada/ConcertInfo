package com.masa.concertinfo;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "consertdata.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "consertdata";
    public static final String DAY = "day";
    public static final String LOCATION = "location";
    public static final String TITLE = "title";
    public static final String WEB = "web";
    public static final String DETAIL = "detail";
    public static final String[] COL_ARR = {TITLE,DAY,LOCATION,WEB,DETAIL};

    DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    String query = "create table " + TABLE_NAME + "(" + TITLE
    + " INTEGER PRIMARY KEY," + DAY + " TEXT," +
   LOCATION + " TEXT," + WEB + " TEXT," + DETAIL +  " TEXT);";
    db.execSQL(query);
    }

     @Override
     public void onOpen(SQLiteDatabase db) {
     super.onOpen(db);
    }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     db.execSQL("drop table if exists " + TABLE_NAME);
     onCreate(db);
     }
}
