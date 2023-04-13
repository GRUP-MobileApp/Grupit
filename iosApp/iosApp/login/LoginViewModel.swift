//
//  LoginViewModel.swift
//  iosApp
//
//  Created by Justin Xu on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

extension LoginView {
    @MainActor class LoginViewModel: ObservableObject {
        enum LoginResult: Equatable {
            case Success
            case PendingLogin
            case PendingRegister
            case PendingGoogleLogin
            case Error(exception: NSError)
            case None
        }
        
        @Published private(set) var loginResult: LoginResult = LoginResult.None
        
        func loginEmailPassword(email: String, password: String) async {
            loginResult = LoginResult.PendingLogin
            do {
                try await LoggedInViewModel.injectApiServer(
                    apiServer: APIServer.Login().loginEmailAndPassword(
                        email: email,
                        password: password
                    )
                )
                loginResult = LoginResult.Success
            } catch let error as NSError {
                loginResult = LoginResult.Error(exception: error)
            }
        }
        
        func registerEmailPassword(email: String, password: String) async {
            loginResult = LoginResult.PendingRegister
            do {
                try await LoggedInViewModel.injectApiServer(
                    apiServer: APIServer.Login().registerEmailAndPassword(
                        email: email,
                        password: password
                    )
                )
                loginResult = LoginResult.Success
            } catch let error as NSError {
                loginResult = LoginResult.Error(exception: error)
            }
        }
        
//        func loginGoogle(email: String, password: String) async {
//            loginResult = LoginResult.PendingRegister
//            do {
//                try await LoggedInViewModel.injectApiServer(
//                    apiServer: APIServer.Login().registerEmailAndPassword(
//                        email: email,
//                        password: password
//                    )
//                )
//                loginResult = LoginResult.Success
//            } catch let error as NSError {
//                loginResult = LoginResult.Error(exception: error)
//            }
//        }
    }
}
