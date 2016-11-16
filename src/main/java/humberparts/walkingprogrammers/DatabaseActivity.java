/**
 * Team Name: The Walking Programmers
 * Team Members: Rafil Yashooa, Masoud Rahguzar, Divesh Oree
 * Date: Oct/17th/2016
 * Project Name: Humber Parts (HP)
 */
package humberparts.walkingprogrammers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Menu;
import android.view.MenuItem;

import java.sql.PreparedStatement;

/**
 * Created by RAF on 2016-11-12.
 */

public class DatabaseActivity extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "humber_parts.db";
    public static final String TABLE_NAME = "student_holds";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "student_id";
    public static final String COL_3 = "DATE";
    public static final String COL_4 = "PART_NUMBER";

    public DatabaseActivity(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,student_id TEXT,DATE TEXT,PART_NUMBER TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String student_id ,String date,String PART_NUMBER) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,student_id);
        contentValues.put(COL_3,date);
        contentValues.put(COL_4,PART_NUMBER);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor search(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME + " WHERE student_id = '"+id+"'",null);
        return res;
    }

    public Cursor databaseViewer() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "student_id = ?",new String[] {id});
    }

}
