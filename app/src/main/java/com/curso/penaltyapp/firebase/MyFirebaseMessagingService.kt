package com.curso.penaltyapp.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    companion object {

        fun subscribeToTeam(teamId: String) {

            FirebaseMessaging.getInstance()
                .subscribeToTopic("team_$teamId")
                .addOnCompleteListener {
                    Log.d("FCM", "Suscrito a team_$teamId")
                }
        }
    }
}