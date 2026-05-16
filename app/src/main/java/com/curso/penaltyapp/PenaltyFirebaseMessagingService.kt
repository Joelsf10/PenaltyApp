package com.curso.penaltyapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.curso.penaltyapp.data.repository.AuthRepository
import com.curso.penaltyapp.data.repository.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PenaltyFirebaseMessagingService : FirebaseMessagingService() {

    // Es crida quan es genera un nou token FCM
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = AuthRepository.currentFirebaseUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            FirestoreRepository.saveFcmToken(uid, token)
        }
    }

    // Es crida quan arriba una notificació amb l'app en primer pla
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}