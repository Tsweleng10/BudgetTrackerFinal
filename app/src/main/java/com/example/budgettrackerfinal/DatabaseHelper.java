package com.example.budgettrackerfinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "budget.db";
    private static final int DB_VERSION = 1;

    // Categories table
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COL_CAT_ID = "id";
    private static final String COL_CAT_NAME = "name";

    // Expenses table
    private static final String TABLE_EXPENSES = "expenses";
    private static final String COL_EXP_ID = "id";
    private static final String COL_EXP_CAT_ID = "cat_id";
    private static final String COL_EXP_AMOUNT = "amount";
    private static final String COL_EXP_DESC = "description";
    private static final String COL_EXP_DATE = "date";
    private static final String COL_EXP_START_TIME = "start_time";
    private static final String COL_EXP_END_TIME = "end_time";
    private static final String COL_EXP_PHOTO = "photo_path";

    // Goals table (single row)
    private static final String TABLE_GOALS = "goals";
    private static final String COL_GOAL_ID = "id";
    private static final String COL_GOAL_MIN = "min_goal";
    private static final String COL_GOAL_MAX = "max_goal";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCategories = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COL_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CAT_NAME + " TEXT UNIQUE)";
        db.execSQL(createCategories);

        String createExpenses = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COL_EXP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EXP_CAT_ID + " INTEGER, " +
                COL_EXP_AMOUNT + " REAL, " +
                COL_EXP_DESC + " TEXT, " +
                COL_EXP_DATE + " TEXT, " +
                COL_EXP_START_TIME + " TEXT, " +
                COL_EXP_END_TIME + " TEXT, " +
                COL_EXP_PHOTO + " TEXT)";
        db.execSQL(createExpenses);

        String createGoals = "CREATE TABLE " + TABLE_GOALS + " (" +
                COL_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_GOAL_MIN + " REAL, " +
                COL_GOAL_MAX + " REAL)";
        db.execSQL(createGoals);

        // Insert default categories
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + COL_CAT_NAME + ") VALUES ('Food'), ('Transport'), ('Entertainment'), ('Bills'), ('Shopping')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        onCreate(db);
    }

    // --- Category methods ---
    public List<String> getAllCategories() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_CAT_NAME + " FROM " + TABLE_CATEGORIES + " ORDER BY " + COL_CAT_NAME, null);
        while (c.moveToNext()) list.add(c.getString(0));
        c.close();
        return list;
    }

    public long getCategoryId(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_CAT_ID + " FROM " + TABLE_CATEGORIES + " WHERE " + COL_CAT_NAME + "=?", new String[]{name});
        long id = c.moveToFirst() ? c.getLong(0) : -1;
        c.close();
        return id;
    }

    public void addCategory(String name) {
        ContentValues cv = new ContentValues();
        cv.put(COL_CAT_NAME, name);
        getWritableDatabase().insert(TABLE_CATEGORIES, null, cv);
    }

    // --- Expense methods ---
    public void addExpense(long catId, double amount, String desc, String date, String startTime, String endTime, String photoPath) {
        ContentValues cv = new ContentValues();
        cv.put(COL_EXP_CAT_ID, catId);
        cv.put(COL_EXP_AMOUNT, amount);
        cv.put(COL_EXP_DESC, desc);
        cv.put(COL_EXP_DATE, date);
        cv.put(COL_EXP_START_TIME, startTime);
        cv.put(COL_EXP_END_TIME, endTime);
        cv.put(COL_EXP_PHOTO, photoPath);
        getWritableDatabase().insert(TABLE_EXPENSES, null, cv);
    }

    public Cursor getExpensesBetween(String startDate, String endDate) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT e.*, c." + COL_CAT_NAME + " FROM " + TABLE_EXPENSES + " e " +
                "JOIN " + TABLE_CATEGORIES + " c ON e." + COL_EXP_CAT_ID + "=c." + COL_CAT_ID +
                " WHERE e." + COL_EXP_DATE + " BETWEEN ? AND ? ORDER BY e." + COL_EXP_DATE, new String[]{startDate, endDate});
    }

    public Cursor getTotalsByCategory(String startDate, String endDate) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT c." + COL_CAT_NAME + ", SUM(e." + COL_EXP_AMOUNT + ") as total " +
                "FROM " + TABLE_EXPENSES + " e JOIN " + TABLE_CATEGORIES + " c ON e." + COL_EXP_CAT_ID + "=c." + COL_CAT_ID +
                " WHERE e." + COL_EXP_DATE + " BETWEEN ? AND ? GROUP BY c." + COL_CAT_NAME, new String[]{startDate, endDate});
    }

    // --- Goal methods ---
    public void saveGoal(double minGoal, double maxGoal) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GOALS);
        ContentValues cv = new ContentValues();
        cv.put(COL_GOAL_MIN, minGoal);
        cv.put(COL_GOAL_MAX, maxGoal);
        db.insert(TABLE_GOALS, null, cv);
    }

    public double[] getGoals() {
        double[] goals = new double[]{0, 0};
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_GOAL_MIN + ", " + COL_GOAL_MAX + " FROM " + TABLE_GOALS, null);
        if (c.moveToFirst()) {
            goals[0] = c.getDouble(0);
            goals[1] = c.getDouble(1);
        }
        c.close();
        return goals;
    }
}