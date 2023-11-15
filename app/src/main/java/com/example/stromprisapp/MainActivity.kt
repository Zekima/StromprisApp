package com.example.stromprisapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.stromprisapp.ui.MainScreen
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Hovedklassen skal starte applikasjonen og fetche FMC token fra firebase
 */
class MainActivity : ComponentActivity() {
    /**
     * blir kalt på ved oppsatart og henter FMC token og setter content til mainscreen
     * @param savedInstanceState Husker dette til neste gang man starter appen fordi det blir bundlet
     * og lagret
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d(TAG, "DET FUNKET")
            Toast.makeText(baseContext, token.toString(), Toast.LENGTH_SHORT).show()
        })
        super.onCreate(savedInstanceState)
        setContent{
            MainScreen()
        }
    }
}