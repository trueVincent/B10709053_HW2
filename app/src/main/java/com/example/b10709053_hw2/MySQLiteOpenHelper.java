package com.example.b10709053_hw2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyDatabase";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "WaitingList";
    private static final String COL_id = "id";
    private static final String COL_name = "name";
    private static final String COL_number = "number";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_name + " TEXT NOT NULL, " +
                    COL_number + " TEXT NOT NULL );";

    public MySQLiteOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Reservation> getList(){
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {COL_id, COL_name, COL_number};

        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        List<Reservation> reservationList = new ArrayList<>();

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String number = cursor.getString(2);
            Reservation reservation = new Reservation(id, name, number);
            reservationList.add(reservation);
        }
        cursor.close();
        return reservationList;
    }

    public long insert(Reservation reservation){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_name, reservation.getName());
        values.put(COL_number, reservation.getNumber());
        return db.insert(TABLE_NAME, null, values);
    }

    public int deleteById(int id){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }
}
