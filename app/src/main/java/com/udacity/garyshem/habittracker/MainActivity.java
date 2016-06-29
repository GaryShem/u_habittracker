package com.udacity.garyshem.habittracker;

import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private HabitOpenHelper mHabitBase;
    private TextView mMainTextView;
    private EditText mEditName;
    private EditText mEditCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make a variable for out helper for easier access
        mHabitBase = HabitOpenHelper.getInstance(this);
        // Same for other view on tha main screen
        mMainTextView = (TextView) findViewById(R.id.main_text_view);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditCount = (EditText) findViewById(R.id.edit_count);
    }

    public void readHabits(View view) {
        // Read the database
        // It would probably make more sense to return an ArrayList of
        // custom objects (custom Habit class maybe), but the task explicitly says
        // it has to return a Cursor object, so there it is
        Cursor results = mHabitBase.readHabits();

        // Since we'll be putting the results into a simple TextView this time,
        // we should empty it first. And we should do it before checking if we have any results
        // so the user can see that the command at least went through
        mMainTextView.setText("");

        // Check whether we actually have any entries
        if (results.moveToFirst() == false) {
            Toast.makeText(MainActivity.this, "There are no habits in the base", Toast.LENGTH_SHORT).show();
            Log.d(getClass().getName(), "Read attempt on empty base");
            results.close();
            return;
        }

        // Get the indexes (indices?) of the columns by names
        int habitNameIndex = results.getColumnIndex(HabitContract.TableHabits.COLUMN_NAME);
        int habitCountIndex = results.getColumnIndex(HabitContract.TableHabits.COLUMN_COUNT);

        String habitName = results.getString(habitNameIndex);
        int habitCount = results.getInt(habitCountIndex);

        // Make a StringBuilder for assembling the database entries
        StringBuilder resultingString = new StringBuilder();
        resultingString.append(String.format(Locale.getDefault(),
                "%s: %d%s", habitName, habitCount, System.getProperty("line.separator")));

        // Iterate through the remaining entries, if any
        while (results.moveToNext()) {
            habitName = results.getString(habitNameIndex);
            habitCount = results.getInt(habitCountIndex);

            resultingString.append(String.format(Locale.getDefault(),
                    "%s: %d%s", habitName, habitCount, System.getProperty("line.separator")));
        }
        // Close the Cursor object
        results.close();
        mMainTextView.setText(resultingString.toString());
    }

    public void addHabit(View view) {
        // First, parse our inputs
        String habitName = mEditName.getText().toString();
        if (habitName.trim().equals("")) {
            Toast.makeText(MainActivity.this, "Habit name must not be empty", Toast.LENGTH_SHORT).show();
            Log.d(getClass().getName(), "Empty habit name on creation");
            return;
        }
        int habitCount = 0;
        try {
            habitCount = Integer.parseInt(mEditCount.getText().toString());
        } catch (NumberFormatException nfe) {
            Toast.makeText(MainActivity.this, "Count must be a number", Toast.LENGTH_SHORT).show();
            Log.d(getClass().getName(), "Count format error on creation");
            return;
        }

        // Now add them to the database
        try {
            mHabitBase.addHabit(habitName, habitCount);
            Toast.makeText(MainActivity.this, "Click \"READ\" to see the result", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(MainActivity.this, "Habit already exists, use update", Toast.LENGTH_LONG).show();
            Log.d(getClass().getName(), "Trying to add already existing habit");
        }
    }

    public void deleteHabits(View view) {
        mHabitBase.clearHabits();
    }

    public void updateHabit(View view) {
        // First, parse our inputs
        String habitName = mEditName.getText().toString();
        if (habitName.trim().equals("")) {
            Toast.makeText(MainActivity.this, "Habit name must not be empty", Toast.LENGTH_SHORT).show();
            Log.d(getClass().getName(), "Empty habit name on creation");
            return;
        }
        int habitCount = 0;
        try {
            habitCount = Integer.parseInt(mEditCount.getText().toString());
        } catch (NumberFormatException nfe) {
            Toast.makeText(MainActivity.this, "Count must be a number", Toast.LENGTH_SHORT).show();
            Log.d(getClass().getName(), "Count format error on creation");
            return;
        }

        // Now add them to the database
        try {
            mHabitBase.updateHabit(habitName, habitCount);
            Toast.makeText(MainActivity.this, "Click \"READ\" to see the result", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(MainActivity.this, "Habit does not exist, use create", Toast.LENGTH_LONG).show();
            Log.d(getClass().getName(), "Trying to update nonexistent habit");
        }
    }
}
