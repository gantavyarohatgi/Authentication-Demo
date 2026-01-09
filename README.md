# Authentication Demo (OTP & Google Sign-In)

This is an Android application demonstrating a secure authentication flow using **Firebase Authentication**, **Cloud Functions**, and **Firestore**.

It features two distinct login methods:
1.  **Google Sign-In**: Native Google authentication.
2.  **Passwordless Email OTP**: A custom implementation using Cloud Functions to send a 6-digit code via email (with expiration logic).

**Note:** This project is configured to run on the **Firebase Local Emulator Suite** to avoid costs and ensure safe testing.

Link for demo video: https://drive.google.com/file/d/1rZZLuOH7wvkzAXuzlsWTvQFLMqieX-OV/view?usp=sharing

---

## 🛠 Tech Stack
* **Android:** Kotlin, XML, Coroutines
* **Backend:** Node.js (Firebase Cloud Functions)
* **Database:** Cloud Firestore
* **Email Service:** Nodemailer (Gmail SMTP)

---

## 📋 Prerequisites
Before running this project, ensure you have the following installed:
1.  **Android Studio** (Latest version)
2.  **Node.js** (LTS Version) - [Download](https://nodejs.org/)
3.  **Firebase CLI**:
    ```bash
    npm install -g firebase-tools
    ```

---

## ⚙️ Setup Instructions

**Important:** This repository excludes sensitive API keys for security. You must configure them manually after cloning.

### 1. Firebase Project Setup
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Create a new project.
3.  Enable **Authentication**:
    * Turn on **Google Sign-In**.
4.  Enable **Firestore Database** (Start in Test Mode).

### 2. Android Configuration
1.  In the Firebase Console, add an Android App with your package name (e.g., `com.example.authdemo`).
2.  **Crucial:** Add your debug **SHA-1 Fingerprint**.
    * *In Android Studio: Gradle Tab > Tasks > android > signingReport*
3.  Download `google-services.json`.
4.  Place the file in the `app/` directory of this project.

### 3. Backend Configuration
The backend requires a `secrets.js` file to send emails. This file is ignored by Git.

1.  Navigate to the functions folder:
    ```bash
    cd functions
    npm install
    ```
2.  Create a file named `secrets.js` inside the `functions/` folder.
3.  Create a 16 digit app password of your gmail account using the following link: (https://myaccount.google.com/apppasswords). These are confidential information and not to be share with anyone.
4.  Paste the following content:
    ```javascript
    module.exports = {
        email: "YOUR_GMAIL@gmail.com",
        password: "YOUR_16_DIGIT_APP_PASSWORD"
    };
    ```

---

## 🚀 How to Run

### Step 1: Start the Backend (Emulator)
You must start the local server before running the app. The Android app is configured to talk to the emulator at `10.0.2.2`.

1.  Open your terminal in the project root.
2.  Run the emulators:
    ```bash
    firebase emulators:start
    ```
3.  Wait until you see `✔ functions: functions initialized`.

### Step 2: Run the Android App
1.  Open the project in **Android Studio**.
2.  Select an **Android Emulator** (e.g., Pixel 4 API 30+).
    * *Note: If using a physical device, you must update the IP address in `LoginActivity.kt` from `10.0.2.2` to your laptop's local IP (e.g., `192.168.1.5`) and run emulators with `--host 0.0.0.0`.*
3.  Click **Run**.

---

## ⚠️ Troubleshooting

**1. "INTERNAL" Error when sending OTP**
* Check the terminal running the emulators for red error logs.
* Ensure `secrets.js` exists and has valid credentials.
* Ensure you are using an **App Password** for Gmail, not your login password.

**2. Google Sign-In Fails (Error 10 or 12500)**
* You are likely missing the **SHA-1** fingerprint in the Firebase Console.
* If testing Google Sign-In, ensure the `auth.useEmulator` line in `LoginActivity.kt` is commented out (Google Sign-In usually requires the real Firebase Auth server).

**3. Network Error / Silent Failure**
* Ensure `android:usesCleartextTraffic="true"` is in your `AndroidManifest.xml`.
* Ensure the Emulator is running (`firebase emulators:start`).
