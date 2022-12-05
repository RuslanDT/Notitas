package com.example.noteeapp.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.noteeapp.R

class ReminderBroadCast() : BroadcastReceiver(){

    var id = 1
    var title = "Terea"
    var body = "Tarea pendiente"

//    constructor(title: String, body: String, idTask: Int) : this() {
//        this.title = title
//        this.body = body
//        this.idTask = idTask
//    }
    override fun onReceive(context: Context, intent: Intent?) {
    //title = intent?.getStringExtra("title").toString()
    //body = intent?.getStringExtra("body").toString()
    intent?.action?.let { data(it) }
    getResultExtras(true)
    val builder = NotificationCompat.Builder(context, "notifications")
        .setSmallIcon(androidx.core.R.drawable.notify_panel_notification_icon_bg)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(id++, builder.build())
}
    private fun data(information: String){
        val info = information.split(" ")
        this.id = info[0].toInt()
        this.title = info[1]
        this.body = info[2]

    }

}