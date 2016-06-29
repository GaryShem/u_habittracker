package com.udacity.garyshem.habittracker;

import android.content.ContentResolver;
import android.net.Uri;

public class HabitContract {
    public static final String CONTENT_AUTHORITY =
            "com.udacity.garyshem.habittracker";
    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + CONTENT_AUTHORITY);

    // Database Info
    public static final String DATABASE_NAME = "habitsDatabase";
    public static final int DATABASE_VERSION = 1;


    public static final String PATH_HABITS = "habits";

    public static class TableHabits {
        public static final String TABLE_NAME = "habits";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COUNT = "count";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HABITS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HABITS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HABITS;
    }
}
