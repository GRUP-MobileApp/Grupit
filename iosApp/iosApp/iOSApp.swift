import SwiftUI
import FirebaseCore
import FirebaseMessaging
import AuthenticationServices
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
                    appleSignInManager: AppleSignInManager(
                        getAuthorizationController: {
                            let appleIdProvider = ASAuthorizationAppleIDProvider()
                            let request = appleIdProvider.createRequest()
                            request.requestedScopes = [.fullName, .email]

                            let authorizationController = ASAuthorizationController(authorizationRequests: [request])
                            if let viewController = UIApplication.shared.windows.first?.rootViewController {
                                authorizationController.delegate = viewController
                                authorizationController.presentationContextProvider = viewController
                            }
                            return authorizationController
                        }
                    )
                ),
                notificationManager: NotificationManager(
                    getMessaging: { return Messaging.messaging() }
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
