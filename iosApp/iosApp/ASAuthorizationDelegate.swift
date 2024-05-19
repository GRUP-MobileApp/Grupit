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
        SettingsManager.LoginSettings.shared.isAppleSignInSuccess = false
    }
    
    public func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        switch authorization.credential {
            case let appleIDCredential as ASAuthorizationAppleIDCredential:
                if let idToken = appleIDCredential.identityToken {
                    SettingsManager.LoginSettings.shared.isAppleSignInSuccess = true
                    SettingsManager.LoginSettings.shared.appleToken = String(data: idToken, encoding: .utf8)
                } else {
                    SettingsManager.LoginSettings.shared.isAppleSignInSuccess = false
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
