package com.grup.platform.notification

import cocoapods.FirebaseMessaging.FIRMessaging

actual class NotificationManager(private val messaging: FIRMessaging) {
    actual fun subscribeGroupNotifications(groupId: String) {
        messaging.subscribeToTopic("group_$groupId")
    }

    actual fun unsubscribeGroupNotifications(groupId: String) {
        messaging.unsubscribeFromTopic("group_$groupId")
    }

    actual fun subscribePersonalNotifications(userId: String) {
        messaging.subscribeToTopic("user_$userId")
    }

    actual fun unsubscribeAllNotifications() {
        messaging.deleteTokenWithCompletion {  }
    }
}