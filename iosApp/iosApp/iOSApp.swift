import SwiftUI
import GoogleSignIn
import FirebaseAuth
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        ModuleKt.doInitKoin()
        
        let googleSignInManager = GoogleSignInManager(
            signInClosure: { signInCallback in
                if let rootViewController = UIApplication.shared.windows.first?.rootViewController {
                    GIDSignIn.sharedInstance.signIn(
                        withPresenting: rootViewController
                    ) { signInResult, error in
                        guard error == nil else {
                            // Handle error
                            return
                        }
                        guard let user = signInResult?.user,
                            let idToken = user.idToken?.tokenString
                        else {
                            // Handle null user
                            return
                        }
                        let credential = GoogleAuthProvider.credential(
                            withIDToken: idToken,
                            accessToken: user.accessToken.tokenString
                        )
                        Auth.auth().signIn(with: credential) { result, error in
                            _ = signInCallback(user.accessToken.tokenString)
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
        
        ModuleKt.doInitAuthManager(
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
