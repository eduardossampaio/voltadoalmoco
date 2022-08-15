package apps.esampaio.com.voltadoalmoco.services.notifications;


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import apps.esampaio.com.voltadoalmoco.R
import apps.esampaio.com.voltadoalmoco.services.workers.CountdownWorker
import apps.esampaio.com.voltadoalmoco.views.main.MainActivity

class Notifications {

    companion object{
        private const val COUNTDOWN_NOTIFICATION_ID = "apps.esampaio.com.voltadoalmoco.COUNTDOWN_NOTIFICATION_ID"
        private const val CHANNEL_ID = "DefaultChannel"

        fun createCountdownProgressNotification(context: Context, remainingMinutes: Int): Notification{
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, COUNTDOWN_NOTIFICATION_ID)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setTicker(context.getString(R.string.app_name))
                    .setContentText("Faltam $remainingMinutes minutos para o retorno ao trabalho")
                    .setSmallIcon(R.drawable.ic_arrow_start)
                    .setContentIntent(
                        PendingIntent.getActivity(
                            context, 123, Intent(
                                context,
                                MainActivity::class.java
                            ), 0
                        )
                    )
                    .setOngoing(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(createChannel(context))
            }
            val cancelIntent = Intent("apps.esampaio.com.voltadoalmoco.CancelCountdown").setPackage(
                context.packageName
            )
            val action = NotificationCompat.Action.Builder(
                R.drawable.ic_arrow_start,
                "Cancelar",
                PendingIntent.getBroadcast(context, 0, cancelIntent, 0)
            )
                .build()
            builder.addAction(action)
            return builder.build()

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun createChannel(context: Context): String {
            val name: CharSequence = "Default Channel"
            val description = "Notificações de volta do almoço"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager: NotificationManager =
               context.getSystemService(NotificationManager::class.java )
            notificationManager.createNotificationChannel(channel)
            return CHANNEL_ID
        }
    }
}