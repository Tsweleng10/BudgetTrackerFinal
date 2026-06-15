package com.example.budgettrackerfinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvOutput, tvGoals, tvGamification;
    private LinearLayout goalVisualLayout;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private String currentPhotoPath;
    private long currentExpenseCategoryId = -1;
    private double currentExpenseAmount;
    private String currentExpenseDesc;
    private String currentExpenseDate;
    private String currentExpenseStartTime;
    private String currentExpenseEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        tvOutput = findViewById(R.id.tvOutput);
        tvGoals = findViewById(R.id.tvGoals);
        tvGamification = findViewById(R.id.tvGamification);
        goalVisualLayout = findViewById(R.id.goalVisualLayout);

        Button btnAddCategory = findViewById(R.id.btnAddCategory);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        Button btnSetGoals = findViewById(R.id.btnSetGoals);
        Button btnViewExpenses = findViewById(R.id.btnViewExpenses);
        Button btnViewTotals = findViewById(R.id.btnViewTotals);
        Button btnShowGraph = findViewById(R.id.btnShowGraph);
        Button btnDarkMode = findViewById(R.id.btnDarkMode);
        Button btnExportCSV = findViewById(R.id.btnExportCSV);

        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
        btnAddExpense.setOnClickListener(v -> showAddExpenseDialog());
        btnSetGoals.setOnClickListener(v -> showSetGoalsDialog());
        btnViewExpenses.setOnClickListener(v -> showDateRangeDialog(true));
        btnViewTotals.setOnClickListener(v -> showDateRangeDialog(false));
        btnShowGraph.setOnClickListener(v -> showGraphDialog());
        btnDarkMode.setOnClickListener(v -> toggleDarkMode());
        btnExportCSV.setOnClickListener(v -> exportToCSV());

        refreshGoalsDisplay();
        refreshGamificationDisplay();
        updateGoalVisual();
    }

    // --- Add Category ---
    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Category");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                dbHelper.addCategory(name);
                Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Enter category name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // --- Add Expense ---
    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Expense");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        final android.widget.Spinner categorySpinner = new android.widget.Spinner(this);
        List<String> categories = dbHelper.getAllCategories();
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);

        final EditText amountInput = new EditText(this);
        amountInput.setHint("Amount (e.g., 199.99)");
        amountInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final EditText descInput = new EditText(this);
        descInput.setHint("Description");
        final EditText dateInput = new EditText(this);
        dateInput.setHint("Date (YYYY-MM-DD)");
        final EditText startTimeInput = new EditText(this);
        startTimeInput.setHint("Start time (HH:MM)");
        final EditText endTimeInput = new EditText(this);
        endTimeInput.setHint("End time (HH:MM)");

        androidx.appcompat.widget.AppCompatTextView categoryLabel = new androidx.appcompat.widget.AppCompatTextView(this);
        categoryLabel.setText("Category:");
        layout.addView(categoryLabel);
        layout.addView(categorySpinner);
        layout.addView(amountInput);
        layout.addView(descInput);
        layout.addView(dateInput);
        layout.addView(startTimeInput);
        layout.addView(endTimeInput);

        builder.setView(layout);

        builder.setPositiveButton("Save without Photo", (dialog, which) -> {
            String category = categorySpinner.getSelectedItem().toString();
            long catId = dbHelper.getCategoryId(category);
            double amount = Double.parseDouble(amountInput.getText().toString().isEmpty() ? "0" : amountInput.getText().toString());
            String desc = descInput.getText().toString();
            String date = dateInput.getText().toString();
            String start = startTimeInput.getText().toString();
            String end = endTimeInput.getText().toString();

            dbHelper.addExpense(catId, amount, desc, date, start, end, null);
            Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show();
        });

        builder.setNeutralButton("Take Photo", (dialog, which) -> {
            currentExpenseCategoryId = dbHelper.getCategoryId(categorySpinner.getSelectedItem().toString());
            currentExpenseAmount = Double.parseDouble(amountInput.getText().toString().isEmpty() ? "0" : amountInput.getText().toString());
            currentExpenseDesc = descInput.getText().toString();
            currentExpenseDate = dateInput.getText().toString();
            currentExpenseStartTime = startTimeInput.getText().toString();
            currentExpenseEndTime = endTimeInput.getText().toString();

            if (checkCameraPermission()) {
                dispatchTakePictureIntent();
            } else {
                requestCameraPermission();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                dbHelper.addExpense(currentExpenseCategoryId, currentExpenseAmount, currentExpenseDesc,
                        currentExpenseDate, currentExpenseStartTime, currentExpenseEndTime, null);
                Toast.makeText(this, "Expense saved without photo", Toast.LENGTH_SHORT).show();
                currentExpenseCategoryId = -1;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating photo file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            dbHelper.addExpense(currentExpenseCategoryId, currentExpenseAmount, currentExpenseDesc,
                    currentExpenseDate, currentExpenseStartTime, currentExpenseEndTime, currentPhotoPath);
            Toast.makeText(this, "Expense saved with photo", Toast.LENGTH_SHORT).show();
            currentExpenseCategoryId = -1;
        }
    }

    // --- Set Goals ---
    private void showSetGoalsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Monthly Spending Goals");
        final EditText minInput = new EditText(this);
        minInput.setHint("Minimum amount (e.g., 1000)");
        final EditText maxInput = new EditText(this);
        maxInput.setHint("Maximum amount (e.g., 5000)");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(minInput);
        layout.addView(maxInput);
        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            double min = Double.parseDouble(minInput.getText().toString().isEmpty() ? "0" : minInput.getText().toString());
            double max = Double.parseDouble(maxInput.getText().toString().isEmpty() ? "0" : maxInput.getText().toString());
            dbHelper.saveGoal(min, max);
            refreshGoalsDisplay();
            updateGoalVisual();
            Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // --- View Expenses / Totals by Period ---
    private void showDateRangeDialog(final boolean isExpenseList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Period");
        final EditText startDate = new EditText(this);
        startDate.setHint("Start date (YYYY-MM-DD)");
        final EditText endDate = new EditText(this);
        endDate.setHint("End date (YYYY-MM-DD)");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(startDate);
        layout.addView(endDate);
        builder.setView(layout);
        builder.setPositiveButton("Show", (dialog, which) -> {
            String start = startDate.getText().toString();
            String end = endDate.getText().toString();
            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Enter both dates", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isExpenseList) {
                showExpensesList(start, end);
            } else {
                showTotalsPerCategory(start, end);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showExpensesList(String startDate, String endDate) {
        Cursor cursor = dbHelper.getExpensesBetween(startDate, endDate);
        StringBuilder sb = new StringBuilder();
        sb.append("Expenses from ").append(startDate).append(" to ").append(endDate).append(":\n\n");
        if (cursor.getCount() == 0) {
            sb.append("No expenses found.");
        } else {
            while (cursor.moveToNext()) {
                String cat = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
                String photo = cursor.getString(cursor.getColumnIndexOrThrow("photo_path"));
                sb.append(date).append(" ").append(startTime).append("-").append(endTime).append("\n");
                sb.append("Category: ").append(cat).append("\n");
                sb.append("Amount: R").append(amount).append("\n");
                sb.append("Description: ").append(desc).append("\n");
                if (photo != null) sb.append("Has photo\n");
                sb.append("-------------------\n");
            }
        }
        tvOutput.setText(sb.toString());
        cursor.close();
    }

    private void showTotalsPerCategory(String startDate, String endDate) {
        Cursor cursor = dbHelper.getTotalsByCategory(startDate, endDate);
        StringBuilder sb = new StringBuilder();
        sb.append("Total spent per category from ").append(startDate).append(" to ").append(endDate).append(":\n\n");
        if (cursor.getCount() == 0) {
            sb.append("No data found.");
        } else {
            while (cursor.moveToNext()) {
                String cat = cursor.getString(0);
                double total = cursor.getDouble(1);
                sb.append(cat).append(": R").append(total).append("\n");
            }
        }
        tvOutput.setText(sb.toString());
        cursor.close();
    }

    // --- Graph (text-based) ---
    private void showGraphDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select period for graph");
        final EditText startDate = new EditText(this);
        startDate.setHint("Start (YYYY-MM-DD)");
        final EditText endDate = new EditText(this);
        endDate.setHint("End (YYYY-MM-DD)");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(startDate);
        layout.addView(endDate);
        builder.setView(layout);
        builder.setPositiveButton("Show Graph", (dialog, which) -> {
            String start = startDate.getText().toString();
            String end = endDate.getText().toString();
            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Enter both dates", Toast.LENGTH_SHORT).show();
                return;
            }
            displayGraph(start, end);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void displayGraph(String startDate, String endDate) {
        Cursor cursor = dbHelper.getTotalsByCategory(startDate, endDate);
        StringBuilder sb = new StringBuilder();
        sb.append("Spending per category from ").append(startDate).append(" to ").append(endDate).append(":\n\n");
        double max = 0;
        if (cursor.moveToFirst()) {
            do {
                double total = cursor.getDouble(1);
                if (total > max) max = total;
            } while (cursor.moveToNext());
            cursor.moveToFirst();
        }
        if (max == 0) {
            sb.append("No expenses found.");
        } else {
            while (cursor.moveToNext()) {
                String cat = cursor.getString(0);
                double total = cursor.getDouble(1);
                int barLength = (int) ((total / max) * 20); // max 20 asterisks
                sb.append(cat).append(": ");
                for (int i = 0; i < barLength; i++) sb.append("*");
                sb.append(" R").append(total).append("\n");
            }
        }
        tvOutput.setText(sb.toString());
        cursor.close();
    }

    // --- Gamification & Goals UI updates ---
    private void refreshGoalsDisplay() {
        double[] goals = dbHelper.getGoals();
        if (goals[0] == 0 && goals[1] == 0)
            tvGoals.setText("Goals: Not set");
        else
            tvGoals.setText(String.format(Locale.US, "Monthly goals - Min: %.2f, Max: %.2f", goals[0], goals[1]));
    }

    private void refreshGamificationDisplay() {
        Cursor c = dbHelper.getGamificationData();
        if (c.moveToFirst()) {
            int points = c.getInt(0);
            int streak = c.getInt(1);
            String badges = c.getString(2);
            tvGamification.setText("Points: " + points + " | Streak: " + streak + " logs\nBadges: " + (badges.isEmpty() ? "none" : badges));
        }
        c.close();
    }

    private void updateGoalVisual() {
        double[] goals = dbHelper.getGoals();
        double currentTotal = dbHelper.getCurrentMonthTotalSpent();
        goalVisualLayout.removeAllViews();
        if (goals[0] == 0 && goals[1] == 0) {
            TextView tv = new TextView(this);
            tv.setText("Set monthly goals to see progress.");
            goalVisualLayout.addView(tv);
            return;
        }
        TextView progressText = new TextView(this);
        progressText.setText("Current month spending: R" + currentTotal + " / Goal range: R" + goals[0] + " - R" + goals[1]);
        progressText.setPadding(16,16,16,16);
        progressText.setBackgroundColor(0xFFE0E0E0);
        goalVisualLayout.addView(progressText);
    }

    // --- Dark Mode ---
    private void toggleDarkMode() {
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        recreate();
    }

    // --- Export to CSV ---
    private void exportToCSV() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
            return;
        }
        try {
            File exportDir = new File(getExternalFilesDir(null), "exports");
            if (!exportDir.exists()) exportDir.mkdirs();
            File file = new File(exportDir, "budget_data_" + System.currentTimeMillis() + ".csv");
            FileWriter writer = new FileWriter(file);
            writer.append("Category,Amount,Description,Date,Start Time,End Time\n");
            Cursor c = dbHelper.getExpensesBetween("1900-01-01", "3000-12-31");
            while (c.moveToNext()) {
                String cat = c.getString(c.getColumnIndexOrThrow("name"));
                double amt = c.getDouble(c.getColumnIndexOrThrow("amount"));
                String desc = c.getString(c.getColumnIndexOrThrow("description"));
                String date = c.getString(c.getColumnIndexOrThrow("date"));
                String st = c.getString(c.getColumnIndexOrThrow("start_time"));
                String et = c.getString(c.getColumnIndexOrThrow("end_time"));
                writer.append(cat).append(",").append(String.valueOf(amt)).append(",").append(desc).append(",").append(date).append(",").append(st).append(",").append(et).append("\n");
            }
            c.close();
            writer.flush();
            writer.close();
            Toast.makeText(this, "Exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}