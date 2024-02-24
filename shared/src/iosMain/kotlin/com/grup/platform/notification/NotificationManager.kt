package com.grup.platform.notification

import cocoapods.FirebaseMessaging.FIRMessaging
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class NotificationManager(getMessaging: () -> FIRMessaging) {
    private val messaging by lazy { getMessaging() }
    actual fun subscribeGroupNotifications(groupId: String) {
        messaging.subscribeToTopic("group_$groupId") { error ->
            error?.let { println(it.localizedDescription) }
        }
    }

    actual fun unsubscribeGroupNotifications(groupId: String) {
        messaging.unsubscribeFromTopic("group_$groupId") { error ->
            error?.let { println(it.localizedDescription) }
        }
    }

    actual fun subscribePersonalNotifications(userId: String) {
        messaging.subscribeToTopic("user_$userId") { error ->
            error?.let { println(it.localizedDescription) }
        }
    }

    actual fun unsubscribeAllNotifications() {
        messaging.deleteTokenWithCompletion {  }
    }
}