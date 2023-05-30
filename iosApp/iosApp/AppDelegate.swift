//
//  AppDelegate.swift
//  iosApp
//
//  Created by Justin Xu on 4/12/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import GoogleSignIn
import FirebaseCore

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
    
    func application(
        _application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        FirebaseApp.configure()
        GIDSignIn.sharedInstance.restorePreviousSignIn { user, error in
            if error != nil || user == nil {
            // Show the app's signed-out state.
            } else {
                // Show the app's signed-in state.
            }
        }
        return true
    }
}
