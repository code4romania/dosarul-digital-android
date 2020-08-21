package ro.code4.casefile.services.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ro.code4.casefile.App

class NotificationService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message)
    }

    private fun showNotification(message: RemoteMessage) {
        message.notification?.let {
            App.instance.currentActivity?.runOnUiThread {
                App.instance.currentActivity?.showPushNotification(it.title.orEmpty(), it.body.orEmpty())
            }
        }
    }
}
