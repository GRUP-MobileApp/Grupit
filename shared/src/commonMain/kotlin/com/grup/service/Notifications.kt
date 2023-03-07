package com.grup.service

expect object Notifications {
    fun subscribeGroupNotifications(groupId: String)
    fun unsubscribeGroupNotifications(groupId: String)
}