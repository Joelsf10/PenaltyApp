package com.curso.penaltyapp.nfc


import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Gestiona totes les operacions NFC de l'app per a la validació de pagaments.
class NfcManager(private val activity: Activity) {

    companion object {
        private const val TAG = "NfcManager"
        const val MIME_TYPE = "application/com.penalty.app"
        const val PAYLOAD_PAYMENT_PREFIX = "PENALTY_PAYMENT:"
    }

    // `getDefaultAdapter` retorna null si el dispositiu no té NFC
    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)

    // Estat intern del NFC exposat com a StateFlow de només lectura
    private val _nfcState = MutableStateFlow<NfcState>(NfcState.Idle)
    val nfcState: StateFlow<NfcState> = _nfcState.asStateFlow()

    val isNfcAvailable: Boolean get() = nfcAdapter != null
    val isNfcEnabled: Boolean get() = nfcAdapter?.isEnabled == true

    // ─── ACTIVACIÓ I DESACTIVACIÓ ─────────────────────────────────────────────

    fun enableForegroundDispatch() {
        if (!isNfcAvailable) {
            _nfcState.value = NfcState.NotAvailable
            return
        }
        if (!isNfcEnabled) {
            _nfcState.value = NfcState.Disabled
            return
        }
        try {
            nfcAdapter?.enableReaderMode(
                activity,
                { tag -> handleTag(tag) },
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null
            )
            _nfcState.value = NfcState.Scanning
        } catch (e: Exception) {
            Log.e(TAG, "Error enabling NFC reader mode", e)
            _nfcState.value = NfcState.Error("Error activant NFC: ${e.message}")
        }
    }

    fun disableForegroundDispatch() {
        try {
            nfcAdapter?.disableReaderMode(activity)
        } catch (e: Exception) {
            Log.e(TAG, "Error disabling NFC reader mode", e)
        }
    }

    // ─── PROCESSAMENT D'INTENTS NFC ───────────────────────────────────────────

    fun processIntent(intent: Intent): NfcPaymentResult? {
        if (intent.action != NfcAdapter.ACTION_NDEF_DISCOVERED &&
            intent.action != NfcAdapter.ACTION_TAG_DISCOVERED) return null

        val messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (messages.isNullOrEmpty()) {
            return NfcPaymentResult.SimulatedPayment
        }

        return try {
            val ndefMessage = messages[0] as NdefMessage
            val payload = String(ndefMessage.records[0].payload)
            if (payload.startsWith(PAYLOAD_PAYMENT_PREFIX)) {
                val fineId = payload.removePrefix(PAYLOAD_PAYMENT_PREFIX)
                NfcPaymentResult.Success(fineId)
            } else {
                NfcPaymentResult.InvalidTag
            }
        } catch (e: Exception) {
            NfcPaymentResult.Error(e.message ?: "Error desconegut")
        }
    }

    // ─── ESCRIPTURA DE TAGS NFC ───────────────────────────────────────────────

    fun writePaymentTag(tag: Tag, fineId: String): Boolean {
        return try {
            val ndef = Ndef.get(tag) ?: return false
            val payload = "$PAYLOAD_PAYMENT_PREFIX$fineId"
            val record = NdefRecord.createMime(MIME_TYPE, payload.toByteArray())
            val message = NdefMessage(arrayOf(record))
            ndef.connect()
            ndef.writeNdefMessage(message)
            ndef.close()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing NFC tag", e)
            false
        }
    }

    // ─── SIMULACIÓ PER A PROVES ───────────────────────────────────────────────

    fun simulateNfcPayment(fineId: String) {
        _nfcState.value = NfcState.PaymentDetected(fineId)
    }

    private fun handleTag(tag: Tag) {
        Log.d(TAG, "NFC Tag detected: ${tag.id}")
        // In skeleton mode, simulate successful payment
        _nfcState.value = NfcState.PaymentDetected("simulated_${tag.id.contentToString()}")
    }
}

// ─── SEALED CLASSES D'ESTAT I RESULTAT ───────────────────────────────────────

sealed class NfcState {
    data object Idle : NfcState()                          // Estat inicial, cap operació activa
    data object Scanning : NfcState()                      // Esperant un tag NFC
    data object NotAvailable : NfcState()                  // El dispositiu no té hardware NFC
    data object Disabled : NfcState()                      // NFC desactivat a la configuració
    data class PaymentDetected(val fineId: String) : NfcState() // Tag llegit amb èxit
    data class Error(val message: String) : NfcState()     // Error durant l'operació
}

sealed class NfcPaymentResult {
    data class Success(val fineId: String) : NfcPaymentResult()  // Tag vàlid amb fineId
    data object SimulatedPayment : NfcPaymentResult()             // Fallback sense tag real
    data object InvalidTag : NfcPaymentResult()                   // Tag sense payload vàlid
    data class Error(val message: String) : NfcPaymentResult()    // Error de lectura
}