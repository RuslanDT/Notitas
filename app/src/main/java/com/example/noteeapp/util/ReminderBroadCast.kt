package com.example.noteeapp.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.noteeapp.R

class ReminderBroadCast() : BroadcastReceiver(){
    var title: String = ""
    var body: String = ""
    var idTask: Int = -1
    constructor(title: String, body: String, idTask: Int) : this() {
        this.title = title
        this.body = body
        this.idTask = idTask
    }
    override fun onReceive(context: Context, intent: Intent?) {
        val builder =  NotificationCompat.Builder(context, "notifications")
            .setSmallIcon(androidx.core.R.drawable.notify_panel_notification_icon_bg)
            .setContentTitle(this.title)
            .setContentText(this.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(idTask, builder.build())
    }

}