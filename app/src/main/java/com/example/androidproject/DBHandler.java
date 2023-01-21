package com.example.androidproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "notesdb";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "mynotes";

    private static final String ID_COL = "id";

    private static final String TITLE_COL = "title";
    private static final String DESCRIPTION_COL = "description";
    private static final String LOCATION_COL = "location";


    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + LOCATION_COL + " TEXT)";
        db.execSQL(query);
    }

    public void insert(Note note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TITLE_COL, note.getTitle());
        values.put(DESCRIPTION_COL, note.getDescription());
        values.put(LOCATION_COL, note.getLocation());
        db.insert(TABLE_NAME, null, values);

        db.close();

    }

    public ArrayList<Note> read() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<Note> courseModalArrayList = new ArrayList<>();

        if (cursorCourses.moveToFirst()) {
            do {
                courseModalArrayList.add(
                        new Note(
                                cursorCourses.getString(1),
                                cursorCourses.getString(2),
                                cursorCourses.getString(3)));
            } while (cursorCourses.moveToNext());
        }

        cursorCourses.close();
        return courseModalArrayList;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
