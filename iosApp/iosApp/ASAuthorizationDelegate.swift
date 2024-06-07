//
//  ASAuthorizationDelegate.swift
//  Grupit
//
//  Created by Justin Xu on 5/18/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import AuthenticationServices
import shared

extension UIViewController: ASAuthorizationControllerDelegate {
    public func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: any Error) {
        // Sign in failed, updating sign in status value in local settings
        SettingsManager.LoginSettings.shared.appleSignInStatus = AppleSignInResult.Failed()
    }
    
    public func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        switch authorization.credential {
            case let appleIDCredential as ASAuthorizationAppleIDCredential:
                if let idToken = appleIDCredential.identityToken {
                    // Sign in succeeded, updating sign in status and appleToken values in local settings
                    if #available(iOS 15.0, *) {
                        SettingsManager.LoginSettings.shared.appleSignInStatus = AppleSignInResult.Success(
                            appleToken: String(data: idToken, encoding: .utf8)!, fullName: appleIDCredential.fullName?.formatted())
                    } else {
                        SettingsManager.LoginSettings.shared.appleSignInStatus = AppleSignInResult.Success(
                            appleToken: String(data: idToken, encoding: .utf8)!, fullName: appleIDCredential.fullName?.givenName)
                    }
                } else {
                    SettingsManager.LoginSettings.shared.appleSignInStatus = AppleSignInResult.Failed()
                }
            default:
                break
        }
    }
}

extension UIViewController: ASAuthorizationControllerPresentationContextProviding {
    public func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return view.window!
    }
}
