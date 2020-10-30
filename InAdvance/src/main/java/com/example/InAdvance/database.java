package com.example.InAdvance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class database extends SQLiteOpenHelper {
    public static final String databasename = "signup.db";
    public static final String tablename = "signupuser";
    public static final String col1 = "ID";
    public static final String col2 = "username";
    public static final String col3 = "email";
    public static final String col4 = "password";

    public database(@Nullable Context context) {
        super(context, databasename, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE signupuser (ID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + tablename);
        onCreate(sqLiteDatabase);
    }

    public long newUser(String user, String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("email", email);
        contentValues.put("password", password);
        long res = db.insert("signupuser", null, contentValues);
        db.close();
        return res;
    }

    public boolean checkUser(String user, String password){
        String[] cols = {col1};
        SQLiteDatabase db = getReadableDatabase();
        String selection = col2 + "=? AND " + col4 + "=?";
        String[] selectionArgs = {user, password};
        Cursor cursor = db.query(tablename, cols, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        if(count > 0)
            return true;
        else
            return false;
    }
}
