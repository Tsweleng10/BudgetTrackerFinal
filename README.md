# BudgetTrackerFinal

BudgetTrackerFinal is an Android personal finance app that helps users track expenses, set monthly spending goals, and visualise their spending habits. It was developed as the final POE for a mobile development course.

## Purpose

The app enables users to:
- Log in securely (hardcoded for demonstration – username: `user`, password: `pass`).
- Create custom spending categories.
- Record expenses with date, start/end time, description, amount, and an optional photo.
- Set monthly minimum and maximum spending goals.
- View a list of expenses for any selected date period.
- View total spending per category for any selected date period.
- **View a text‑based bar graph** comparing spending per category (visual requirement).
- **See a visual indicator** of how current month spending compares to min/max goals.
- **Earn gamification rewards** (points, streak, badges) for consistent logging and staying within goals.
- **Toggle dark mode** (own feature 1).
- **Export all expenses to a CSV file** (own feature 2).

## Design Considerations

The app was designed with simplicity and reliability in mind, based on research into existing South African budgeting apps (22Seven, Wallet by BudgetBakers, Goodbudget). Key design decisions:

- **Local SQLite database** (no internet dependency) for data privacy and offline use.
- **Envelope budgeting inspired** by Goodbudget – users allocate income to categories.
- **Automatic transaction import** is not implemented due to time, but manual entry encourages mindful spending.
- **Text‑based graph** chosen over external chart libraries to avoid dependency issues and ensure stable performance.
- **Material Design** with support for dark/light theme.

## GitHub & GitHub Actions

This project uses **Git** for version control. The repository is hosted on GitHub.  
A **GitHub Actions** workflow is configured to automatically build the Android project and run tests on every push, ensuring the code remains compilable.  
The workflow uses the official `actions/checkout` and `actions/setup-java` together with the `gradle-build-action` to assemble the debug APK.

## Features (Detailed)

### Core Features (as per assignment)
- **Login** – Simple authentication screen.
- **Category Management** – Add any number of categories.
- **Expense Entry** – Record amount, description, date, start/end time, category, optional camera photo.
- **Monthly Goals** – Set min and max spending targets.
- **View Expenses by Period** – Choose start/end date, see all matching expenses (shows if a photo was taken).
- **View Totals by Category** – See sum spent per category for a date range.
- **Graph** – Text‑based bar chart (asterisks) showing relative spending per category for a selected period.
- **Visual Goal Indicator** – A coloured text bar showing how current month spending compares to min/max.
- **Gamification** – Points (+10 per expense), streak (consecutive logs), badges (“Consistent Logger” after 5 logs, “Goal Keeper” when staying within goals).

### Own Features (from design document)
1. **Dark Mode** – Toggle between light and dark themes. The setting persists across app restarts.
2. **Export to CSV** – Exports all expense records to a CSV file saved in the app’s external files directory. Useful for backup or further analysis in Excel.

## Video Demonstration

[Watch the full demonstration on YouTube](https://youtu.be/ehe5fCKN5ug)  


## Installation

### Option 1: Install APK directly
1. Download the `app-debug.apk` from the [Releases](../../releases) section or from the root of this repository.
2. On your Android device, enable **Install from unknown sources** (Settings → Security → Unknown sources).
3. Open the APK file and install.

### Option 2: Build from source
1. Clone the repository:  
   `git clone https://github.com/yourusername/BudgetTrackerFinal.git`
2. Open the project in Android Studio.
3. Build → Build Bundle(s) / APK(s) → Build APK(s).
4. The APK will be generated in `app/build/outputs/apk/debug/`.

## Permissions

The app requests the following permissions at runtime:
- **Camera** – to take photos for expense entries (optional, you can save expenses without granting it).
- **Write external storage** – needed only for exporting CSV (Android 10 and below; on newer versions scoped storage is used).

## How to Use

1. Launch the app. Log in with `user` / `pass`.
2. On the Dashboard:
   - Tap **Add Category** to create a new category.
   - Tap **Add Expense** – fill in the details. Optionally tap “Take Photo” to attach a picture.
   - Tap **Set Monthly Goals** – enter your desired min and max spending amounts for the month.
   - Tap **View Expenses by Period** – enter start and end dates (YYYY-MM-DD) to see a list.
   - Tap **View Totals per Category** – enter dates to see aggregated spending.
   - Tap **Show Graph** – enter dates to see a text‑based bar graph.
   - Tap **Toggle Dark Mode** – switch theme instantly.
   - Tap **Export to CSV** – saves all expenses to a CSV file in `Android/data/com.example.budgettrackerfinal/files/exports/`.
3. The **gamification display** shows your points, current streak, and earned badges.
4. The **goal visual** updates automatically as you add expenses.

## Known Issues

- Photo capture requires a physical device with a camera (emulator may not work).
- Date and time format must be exactly `YYYY-MM-DD` and `HH:MM` (24‑hour). Future versions could include date pickers.
- CSV export on Android 11+ uses scoped storage – the file is saved in the app’s private external directory. You can access it via file manager or by connecting to a PC.

## Future Improvements

- Automatic bank synchronisation with South African banks (inspired by 22Seven).
- Receipt OCR for automatic data extraction.
- More sophisticated charts (using MPAndroidChart if time allows).
- Cloud backup and multi‑device sync.

## Technologies Used

- Language: Java
- Database: SQLite (custom `DatabaseHelper`)
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34
- Build System: Gradle (Groovy)
- Version Control: Git + GitHub Actions

## License

This project is submitted for academic purposes as part of a software development course.

## Author

St10451745 Joshua Tsweleng – [GitHub Profile]([https://github.com/Tsweleng10])
