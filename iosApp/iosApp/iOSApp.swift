import SwiftUI
import GoogleSignIn
import FirebaseCore
import FirebaseAuth
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
                        guard error == nil else {
                            // Handle error
                        }
                        guard let user = signInResult?.user,
                            let idToken = user.idToken?.tokenString
                        else {
                            // Handle null user
                        }
                        let credential = GoogleAuthProvider.credential(
                            withIDToken: idToken,
                            accessToken: user.accessToken.tokenString
                        )
                        Auth.auth().signIn(with: credential) { result, error in
                            signInCallback(user.accessToken.tokenString)
                        }
                    }
                }
            },
            signOutClosure: {
                GIDSignIn.sharedInstance.signOut()
                do {
                    try Auth.auth().signOut()
                } catch let signOutError as NSError {
                    print("Error signing out: %@", signOutError)
                }
                
            },
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
