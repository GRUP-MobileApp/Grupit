package com.grup.service

import cocoapods.FirebaseMessaging.FIRMessaging


internal actual object NotificationsService {
    actual fun subscribeGroupNotifications(groupId: String) {
        FIRMessaging.messaging().subscribeToTopic("group_$groupId")
    }

    actual fun unsubscribeGroupNotifications(groupId: String) {
        FIRMessaging.messaging().unsubscribeFromTopic("group_$groupId")
    }

    actual fun subscribePersonalNotifications(userId: String) {
        FIRMessaging.messaging().subscribeToTopic("user_$userId")
    }

    actual fun unsubscribeAllNotifications() {
        FIRMessaging.messaging().deleteTokenWithCompletion {  }
    }
}