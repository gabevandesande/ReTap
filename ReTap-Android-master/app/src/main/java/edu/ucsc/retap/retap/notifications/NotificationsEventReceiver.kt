package edu.ucsc.retap.retap.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.preference.PreferenceManager
import android.provider.Telephony
import edu.ucsc.retap.retap.common.Constants
import edu.ucsc.retap.retap.contacts.interactor.ContactsHelper
import edu.ucsc.retap.retap.messages.model.Message
import edu.ucsc.retap.retap.morse.MorseInteractor

/**
 * BroadcastReceiver responsible for intercepting SMS notifications and vibrating them if the user has enabled
 * the notification vibration setting.
 */
class NotificationsEventReceiver : BroadcastReceiver() {
    companion object {
        private const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (!sharedPreferences.getBoolean(Constants.PREF_VIBRATE_ON_RECEIVE, false)) {
            return
        }

        when (intent.action) {
            SMS_RECEIVED -> {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val vibrationInteractor = MorseInteractor(vibrator)
                val smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent).first()
                val sender = smsMessage.originatingAddress
                val body = smsMessage.messageBody
                val date = smsMessage.timestampMillis
                val contact = ContactsHelper.getContact(context, sender)
                val message = Message(
                        contact,
                        body,
                        date
                )
                vibrationInteractor.vibrate(message)
            }
        }
    }
}
