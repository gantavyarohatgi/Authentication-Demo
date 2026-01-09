package com.example.authdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions

class OtpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions
    private lateinit var userEmail: String
    private lateinit var etOtp: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance()

        try {
            auth.useEmulator("10.0.2.2", 9099)
            functions.useEmulator("10.0.2.2", 5001)
        } catch (e: Exception) {
            Log.d("Emulator Use Failed", "Cant use the emulator", e)
        }

        // Get the email passed from the previous page
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        // Setup UI
        etOtp = findViewById(R.id.etOtp)
        progressBar = findViewById(R.id.progressBar)
        findViewById<TextView>(R.id.tvEmailLabel).text = "Code sent to $userEmail"

        findViewById<Button>(R.id.btnVerify).setOnClickListener {
            val code = etOtp.text.toString().trim()
            if (code.length == 6) {
                verifyCodeAndLogin(code)
            } else {
                etOtp.error = "Enter 6 digits"
            }
        }
    }

    private fun verifyCodeAndLogin(code: String) {
        showLoading(true)

        val data = hashMapOf(
            "email" to userEmail,
            "code" to code
        )

        // 1. Call Backend to Verify Code
        functions.getHttpsCallable("verifyEmailOTP")
            .call(data)
            .addOnSuccessListener { result ->

                // 2. Extract Token
                val resultData = result.getData() as Map<*, *>
                val token = resultData["token"] as String

                // 3. Sign in to Firebase
                signInWithToken(token)
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signInWithToken(token: String) {
        showLoading(true)
        auth.signInWithCustomToken(token)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    // 4. Navigate to Home
                    navigateToHome()
                } else {
                    Toast.makeText(this, "Authentication Error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeActivity::class.java)
        // Clear back stack so they can't go back to Login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btnVerify).isEnabled = !isLoading
    }
}