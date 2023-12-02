package com.grup.platform.notification

import com.google.firebase.messaging.FirebaseMessaging

actual class NotificationManager {
    actual fun subscribeGroupNotifications(groupId: String) {
        FirebaseMessaging.getInstance().subscribeToTopic("group_$groupId")
    }

    actual fun unsubscribeGroupNotifications(groupId: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("group_$groupId")
    }

    actual fun subscribePersonalNotifications(userId: String) {
        FirebaseMessaging.getInstance().subscribeToTopic("user_$userId")
    }

    actual fun unsubscribeAllNotifications() {
        FirebaseMessaging.getInstance().deleteToken()
    }
}