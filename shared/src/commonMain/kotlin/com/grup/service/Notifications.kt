package com.grup.service

internal expect object Notifications {
    fun subscribeGroupNotifications(groupId: String)
    fun unsubscribeGroupNotifications(groupId: String)

    fun subscribePersonalNotifications(userId: String)
}