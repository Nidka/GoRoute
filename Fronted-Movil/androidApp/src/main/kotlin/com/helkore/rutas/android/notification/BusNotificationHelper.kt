package com.helkore.rutas.android.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object BusNotificationHelper {

    private const val CHANNEL_ID   = "bus_llegada"
    private const val CHANNEL_NAME = "Llegada del bus"
    private var notifId = 1

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas cuando el bus está cerca o llega a tu paradero"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 100, 300)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun notifyBusCercano(context: Context, rutaNombre: String, paraderoNombre: String, etaMin: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🚌 $rutaNombre se acerca")
            .setContentText("Llega a $paraderoNombre en $etaMin min")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 100, 300))

        try {
            NotificationManagerCompat.from(context).notify(notifId++, builder.build())
        } catch (_: SecurityException) {
            // El usuario no concedió permiso POST_NOTIFICATIONS — se ignora silenciosamente
        }
    }

    fun notifyBusLlego(context: Context, rutaNombre: String, paraderoNombre: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("✅ $rutaNombre llegó")
            .setContentText("El bus está en $paraderoNombre — ¡súbete ahora!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))

        try {
            NotificationManagerCompat.from(context).notify(notifId++, builder.build())
        } catch (_: SecurityException) {}
    }
}
