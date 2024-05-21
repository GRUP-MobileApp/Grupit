package com.grup.platform.notification

import cocoapods.FirebaseMessaging.FIRMessaging
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class NotificationManager(getMessaging: () -> FIRMessaging) {
    private val messaging by lazy { getMessaging() }
    actual fun subscribeGroupNotifications(groupId: String) {
        messaging.subscribeToTopic("group_$groupId") { }
    }

    actual fun unsubscribeGroupNotifications(groupId: String) {
        messaging.unsubscribeFromTopic("group_$groupId") { }
    }

    actual fun subscribePersonalNotifications(userId: String) {
        messaging.subscribeToTopic("user_$userId") { }
    }

    actual fun unsubscribeAllNotifications() {
        messaging.deleteTokenWithCompletion { }
    }
}