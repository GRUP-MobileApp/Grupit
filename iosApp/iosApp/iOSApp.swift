import SwiftUI
import FirebaseMessaging
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        ModuleKt.doInitKoin()
        
        ModuleKt.doInitAuthManager(
            authManager: AuthManager(
                googleSignInManager: GoogleSignInManager(),
                appleSignInManager: nil
            )
        )
        ModuleKt.doInitNotificationManager(
            notificationManager: NotificationManager(
                messaging: Messaging.messaging()
            )
        )
    }
        
        var body: some Scene {
            WindowGroup {
                ContentView()
            }
        }
    }
