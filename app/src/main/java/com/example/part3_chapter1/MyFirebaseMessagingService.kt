package com.example.part3_chapter1

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        createNotificationChannel()

        val type = remoteMessage.data["type"]?.let {
        try {
            NotificationType.valueOf(it)
        }catch (e: IllegalArgumentException){
            NotificationType.valueOf(NotificationType.NORMAL.toString())
        }


        }
        val title =remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        type ?: return
        Log.d("messageType", type.toString())
        NotificationManagerCompat.from(this)
            .notify(type.id,notification(type,title,message))
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description= CHANNEL_DESCRIPTION

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }



    @SuppressLint("UnspecifiedImmutableFlag")
    private fun notification(type: NotificationType, title: String?, message:String?):Notification{
        val intent = Intent(this,MainActivity::class.java).apply {
            putExtra("notificationType","${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this,type.id,intent, FLAG_UPDATE_CURRENT)

        // TODO: 2022-01-19   // 수정해야될 부분 // id, flag수정

       val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
           .setSmallIcon(R.drawable.ic_notification)
           .setContentTitle(title)
           .setContentText(message)
           .setPriority(NotificationCompat.PRIORITY_DEFAULT)
           .setContentIntent(pendingIntent)
           .setAutoCancel(true)


        when(type){
            NotificationType.NORMAL ->Unit

            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("\uD83D\uDE00\uD83D\uDE03\uD83D\uDE04\uD83D\uDE01\uD83D\uDE06" +
                                "\uD83D\uDE05\uD83D\uDE02\uD83E\uDD23☺\uD83D\uDE0A")
                )
            }
            NotificationType.CUSTOM -> {
                notificationBuilder.setStyle(
                    NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(packageName,R.layout.view_custom_notification)
                            .apply{
                            setTextViewText(R.id.title,title)
                            setTextViewText(R.id.message,message)
                        }
                    )
                    }
            }


        return notificationBuilder.build()

    }

    companion object{
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION ="Emoji Party를 위한 채널"
        private const val CHANNEL_ID = "Channel Id"
    }

}