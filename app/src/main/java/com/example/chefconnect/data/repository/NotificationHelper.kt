package com.example.chefconnect.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.chefconnect.R

/**
 * Helper to create and show notifications when a meal is added to favorites.
 * Includes a deep link to navigate directly to the meal detail screen.
 */
object NotificationHelper {

    private const val CHANNEL_ID = "chefconnect_favorites"
    private const val CHANNEL_NAME = "Favorites"
    private const val CHANNEL_DESC = "Notifications when you add a meal to favorites"

    /** Create the notification channel (safe to call multiple times). */
    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESC
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    /**
     * Show a notification that the meal was added to favorites.
     * Tapping the notification deep-links to the meal detail screen.
     */
    fun showFavoriteNotification(context: Context, mealId: String, mealName: String) {
        // Deep link URI matching the NavHost route
        val deepLinkUri = Uri.parse("chefconnect://detail/$mealId")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            setPackage(context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, mealId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Added to Favorites ❤️")
            .setContentText("$mealName has been saved to your favorites!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(mealId.hashCode(), notification)
    }
}
