package com.grup.service

internal expect object NotificationsService {
    fun subscribeGroupNotifications(groupId: String)
    fun unsubscribeGroupNotifications(groupId: String)

    fun subscribePersonalNotifications(userId: String)

    fun unsubscribeAllNotifications()
}