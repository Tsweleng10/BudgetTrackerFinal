# BudgetTrackerFinal

BudgetTrackerFinal is an Android personal finance app that helps users track expenses, set monthly spending goals, and view reports by category and date range. It supports adding categories, recording expenses with optional photos, and storing all data locally in an SQLite database.

## Features

- **Login** – Simple authentication (default username: `user`, password: `pass`).
- **Category Management** – Add custom spending categories.
- **Expense Entry** – Record expenses with amount, description, date, start/end time, and optional photo (camera capture).
- **Monthly Goals** – Set minimum and maximum monthly spending targets.
- **Expense List** – View all expenses for a selected date period, including any attached photos.
- **Category Totals** – See total money spent per category for a selected date range.
- **Local Database** – All data stored using SQLite (via custom `DatabaseHelper`).

## Technology Stack

- Language: Java
- Database: SQLite (custom helper)
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34
- Build Tool: Gradle (Groovy)

## Installation

### Option 1: Install the APK (easiest)

1. Download the `app-debug.apk` from the [Releases](../../releases) section of this repository (or from the root folder if uploaded).
2. On your Android device, enable **Install from unknown sources** (Settings → Security → Unknown sources).
3. Open the APK file and install.

### Option 2: Build from source

1. Clone the repository:
   ```bash
   https://github.com/Tsweleng10/BudgetTrackerFinal.git

2. Open the project in Android Studio.
3. Build → Build Bundle(s) / APK(s) → Build APK(s).
4. The APK will be generated in app/build/outputs/apk/debug/.

## How to Use

1. Open the app. Log in with:
    - Username: user 
    - Password: pass 
2. On the Dashboard:
   - Tap Add Category to create a new spending category. 
   - Tap Add Expense to record an expense (fill all fields; optionally take a photo). 
   - Tap Set Monthly Goals to define your minimum and maximum spending targets. 
   - Tap View Expenses by Period – enter a date range (YYYY-MM-DD) to see a list of expenses. 
   - Tap View Totals per Category – enter a date range to see how much you spent in each category. 
3. The bottom text field will display the results.

## Demo Video

https://youtube.com/shorts/W3-_M0A6vfY?si=NEpOU48-IW1UTeMu

Click the link to watch the demonstration video

## Permissions
The app requests the following permissions at runtime:

- Camera – to take photos for expense entries. You can still save expenses without granting this permission.
## Known Issues

- Photo capture requires a physical device or an emulator with camera support. 
- Date and time format must be entered exactly as YYYY-MM-DD and HH:MM (24‑hour format).
## Future Improvements

- Automatic bank synchronisation (inspired by 22Seven). 
- Envelope budgeting (inspired by Goodbudget). 
- Receipt OCR and reporting enhancements. 
- Gamification (wise points and badges).
##  License

This project is submitted for academic purposes as part of a software development course.

## Author

Tsweleng Joshua ST10451745
GitHub Tsweleng10
