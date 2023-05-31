//
//  DebugApplication.swift
//  iosApp
//
//  Created by Justin Xu on 5/2/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import GoogleSignIn

struct ContentView: View {
    private struct ComposeView: UIViewControllerRepresentable {
        func makeUIViewController(context: Context) -> UIViewController {
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
            
            return ReleaseApplicationControllerKt.ReleaseApplicationController(
                authManager:
                    AuthManager(
                        googleSignInManager: googleSignInManager,
                        appleSignInManager: nil
                    )
            )
        }
        
        func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
    }

    var body: some View {
        ComposeView()
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
            .edgesIgnoringSafeArea(.bottom)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
