package com.grup.platform.notification

expect class NotificationManager {
    fun subscribeGroupNotifications(groupId: String)
    fun unsubscribeGroupNotifications(groupId: String)

    fun subscribePersonalNotifications(userId: String)

    fun unsubscribeAllNotifications()
}
