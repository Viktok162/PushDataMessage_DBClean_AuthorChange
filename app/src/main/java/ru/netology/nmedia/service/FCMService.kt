package ru.netology.nmedia.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlin.random.Random
import ru.netology.nmedia.R

class FCMService : FirebaseMessagingService() {

    private val action = "action"
    private val content = "content"
    private val gson = Gson()
    private val channelId = "remote"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply{
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        Log.i("FCM_token", "New token: $token")

    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM_message", "New message:${message.data}")
        message.data[action]?.let {
            try {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(
                        gson.fromJson(message.data[content],
                            ActionLike::class.java)
                    )
                }
            } catch (e: IllegalArgumentException) {
                Log.e("FCM_exception", "Unknown action:$it", e)
        }
      }
    }

    @SuppressLint("StringFormatMatches")
    private fun handleLike(like: ActionLike) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    like.userName,
                    like.postId,   // add postId
                    like.postAuthor
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("FCM_permission", "No permission to push")
            return
        }

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    enum class Action {
        LIKE
    }

    data class ActionLike (
        val userId: Int,
        val userName: String,
        val postId: Int,
        val postAuthor: String
    )
}