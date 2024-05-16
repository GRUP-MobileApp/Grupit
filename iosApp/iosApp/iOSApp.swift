import SwiftUI
import FirebaseCore
import FirebaseMessaging
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        FirebaseApp.configure()
        
        ModuleKt.doInitKoin()
        
        ModuleKt.doInitDeviceManager(
            deviceManager: DeviceManager(
                authManager: AuthManager(
                    googleSignInManager: GoogleSignInManager(),
                    appleSignInManager: nil
                ),
                notificationManager: NotificationManager(
                    getMessaging: { Messaging.messaging() }
                )
            )
        )
    }
        
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
