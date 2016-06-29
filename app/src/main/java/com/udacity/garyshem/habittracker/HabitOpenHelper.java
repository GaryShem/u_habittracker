package com.udacity.garyshem.habittracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

public class HabitOpenHelper extends SQLiteOpenHelper {

    // We only ever want one database copy,
    // so let's prevent the app from making several instances
    private static HabitOpenHelper sInstance;

    public static synchronized HabitOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that we
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new HabitOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * All callers should use the getInstance() method
     */
    private HabitOpenHelper(Context context) {
        super(context, HabitContract.DATABASE_NAME, null, HabitContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Setting habit name as primary key makes validation easier
        String CREATE_HABITS_TABLE = "CREATE TABLE " + HabitContract.TableHabits.TABLE_NAME +
                "(" +
                HabitContract.TableHabits.COLUMN_NAME + " TEXT PRIMARY KEY NOT NULL, " +  // Define field for habit name
                HabitContract.TableHabits.COLUMN_COUNT + " INTEGER NOT NULL" + // Define field for habit count
                ")";

        db.execSQL(CREATE_HABITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + HabitContract.TableHabits.TABLE_NAME);
            onCreate(db);
        }
    }

    public Cursor readHabits() {
        // Get the info needed for query
        // In our case, since we have to use query() and not rawQuery(),
        // we need to get the columns that we'd like returned
        String[] columns = {HabitContract.TableHabits.COLUMN_NAME, HabitContract.TableHabits.COLUMN_COUNT};
        // Get our database object
        SQLiteDatabase db = getReadableDatabase();
        // Execute and return the query
        Cursor result = db.query(HabitContract.TableHabits.TABLE_NAME,
                null, null, null, null, null, null);
        return result;
    }

    public void addHabit(String habitName, int habitCount) {
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(HabitContract.TableHabits.COLUMN_NAME, habitName);
            values.put(HabitContract.TableHabits.COLUMN_COUNT, habitCount);

            db.insertOrThrow(HabitContract.TableHabits.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    public void updateHabit(String habitName, int newHabitCount) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(HabitContract.TableHabits.COLUMN_NAME, habitName);
        values.put(HabitContract.TableHabits.COLUMN_COUNT, newHabitCount);

        // Try updating the habit
        int rows = db.update(HabitContract.TableHabits.TABLE_NAME,
                values,
                HabitContract.TableHabits.COLUMN_NAME + "= ?",
                new String[]{habitName});
        if (rows == 1) {
            db.setTransactionSuccessful();
            db.endTransaction();
        } else {
            db.endTransaction();
            throw new SQLException("Trying to update non-existent habit");
        }
    }

    public void clearHabits() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + HabitContract.TableHabits.TABLE_NAME);
        onCreate(db);
    }

}
