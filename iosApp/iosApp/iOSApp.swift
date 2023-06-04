import SwiftUI
import GoogleSignIn
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    init() {
        InitKoinKt.doInitKoin()
        
        let signIn: (@escaping (String) -> Void) -> Void = { signInCallback in
            if let rootViewController = UIApplication.shared.windows.first?.rootViewController {
                GIDSignIn.sharedInstance.signIn(
                    withPresenting: rootViewController
                ) { signInResult, error in
                    if let googleToken = signInResult?.user.accessToken.tokenString {
                        signInCallback(googleToken)
                    }
                }
            }
        }
        
        let googleSignInManager = GoogleSignInManager(
            signInClosure: { signInCallback in
                if let rootViewController = UIApplication.shared.windows.first?.rootViewController {
                    GIDSignIn.sharedInstance.signIn(
                        withPresenting: rootViewController
                    ) { signInResult, error in
                        if let googleToken = signInResult?.user.idToken?.tokenString {
                            signInCallback(googleToken)
                        }
                    }
                }
            },
            signOutClosure: { GIDSignIn.sharedInstance.signOut() },
            disconnectClosure: { GIDSignIn.sharedInstance.disconnect() }
        )
        
        InitAuthManagerKt.doInitAuthManager(
            authManager: AuthManager(
                googleSignInManager: googleSignInManager,
                appleSignInManager: nil
            )
        )
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
