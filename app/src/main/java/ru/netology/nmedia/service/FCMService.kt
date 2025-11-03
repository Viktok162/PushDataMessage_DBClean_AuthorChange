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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import android.database.sqlite.SQLiteException
import com.google.gson.JsonSyntaxException

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
        message.data[action]?.let { actionValue ->
            try {                               // try-catch для LIKE
                val like = try {                // try-catch для парсинга JSON
                    gson.fromJson(message.data[content], ActionLike::class.java)
                } catch (e: JsonSyntaxException) {
                    Log.e("FCM_json_error", "Failed to parse FCM message content: ${e.message}. Message: ${message.data[content]}", e)
                    return@let  // Выход из блока
                }
                // Если парсинг успешен, продолжить
                when (Action.valueOf(actionValue)) {
                    Action.LIKE -> handleLike(like)
                }
            } catch (e: IllegalArgumentException) {
                Log.e("FCM_exception", "Unknown action: $actionValue", e)
            }
        }
    }


    @SuppressLint("StringFormatMatches")
    private fun handleLike(like: ActionLike) {
        // Увеличение likes
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDb.getInstance(this@FCMService)   // Получаем DAO из БД
                val postIdLong = like.postId.toLong()  // конвертация типа Int to Long

                // Проверяем, существует ли пост
                if (db.postDao().postExistsByIdAndAuthor(postIdLong, like.postAuthor)) {
                    db.postDao().incrementLikeById(postIdLong)// add likes if post exist
                    Log.d("FCM_save", "Likes incremented for post ${like.postId} by author ${like.postAuthor}")
                } else {
                    Log.e("FCM_postId_author_error", "Failed to save like: Post with id ${like.postId} " +
                            "and author '${like.postAuthor}' not found")
                }

            } catch (e: SQLiteException) {
                Log.e("FCM_BD_error", "Failed to save like due to DB error: ${e.message}", e)
            }
        }


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
