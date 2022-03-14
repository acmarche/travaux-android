package be.marche.apptravaux.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import be.marche.apptravaux.R

class NotificationUtil(val context: Context) {

    private val manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(id: String, idNotify: Int, name: String, desc: String) {
        val channelId = id
        val channelName = name

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("AppTravaux")
            .setContentText(desc)
            .setSmallIcon(R.drawable.ic_outline_notifications_active_24)

        manager.notify(idNotify, builder.build())
    }

}