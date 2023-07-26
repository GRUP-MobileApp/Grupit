package com.grup.service

import com.google.firebase.messaging.FirebaseMessaging

internal actual object NotificationsService {
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