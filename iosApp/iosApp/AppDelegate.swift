//
//  AppDelegate.swift
//  iosApp
//
//  Created by Justin Xu on 4/12/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import UserNotifications
import GoogleSignIn
import FirebaseCore
import FirebaseMessaging
import shared

class AppDelegate: UIResponder, UIApplicationDelegate, MessagingDelegate {
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
    
    // On startup
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Connect to FCM
        Messaging.messaging().delegate = self
        
        // Connect to APNs
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(
            options: [.alert, .sound, .badge],
            completionHandler: { _, _ in }
        )
        application.registerForRemoteNotifications()
        
        if let window = UIApplication.shared.windows.first {
            window.isOpaque = false
            window.backgroundColor = .clear
        }
        
        return true
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        let dataDict: [String: String] = ["token": fcmToken ?? ""]
        NotificationCenter.default.post(
            name: Notification.Name("FCMToken"),
            object: nil,
            userInfo: dataDict
        )
        // TODO: If necessary send token to application server.
        // Note: This callback is fired at each app startup and whenever a new token is generated.
    }
    
    // onMessageReceived for FCM
    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any]
    ) async -> UIBackgroundFetchResult {
        Messaging.messaging().appDidReceiveMessage(userInfo)
        if (allowNotification(data: userInfo)) {
            let content = UNMutableNotificationContent()
            content.title = userInfo["title"] as! String
            content.body = userInfo["body"] as! String
            
            let request = UNNotificationRequest(
                identifier: UUID().uuidString,
                content: content,
                trigger: nil
            )
            
            do {
                try await UNUserNotificationCenter.current().add(request)
            } catch {
                // TODO: Catch exception
            }
        }
        
        print(userInfo)
        return UIBackgroundFetchResult.newData
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
}

private func allowNotification(data: [AnyHashable: Any]) -> Bool {
    let notificationType: String = data["type"] as! String
    let isToggled: Bool = SettingsManager.AccountSettings.shared.getGroupNotificationType(notificationType: notificationType)
    
    return switch(notificationType) {
        case SettingsManager.AccountSettingsGroupNotificationType.newsettleaction.name:
            SettingsManager.LoginSettings.shared.userId != (data["userId"] as! String) && isToggled
        default: isToggled
    }
}
