//
//  LoginView.swift
//  iosApp
//
//  Created by Justin Xu on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import GoogleSignIn
import GoogleSignInSwift

struct LoginView: View {
    @StateObject private var loginViewModel = LoginViewModel()
    
    @State var email: String = ""
    @State var password: String = ""
    
    var body: some View {
        ZStack {
            AppTheme.colors.primary.edgesIgnoringSafeArea(.all)

            VStack(spacing: 20) {
                H1Text(text: "GRUP", color: AppTheme.colors.onSecondary, fontSize: 40)
                Spacer().frame(height: 50)

                TextField("Username", text: $email)
                    .keyboardType(.emailAddress)
                    .background(AppTheme.colors.secondary)
                    .foregroundColor(AppTheme.colors.onSecondary)

                SecureField("Password", text: $password)
                    .keyboardType(.default)
                    .background(AppTheme.colors.secondary)
                    .foregroundColor(AppTheme.colors.onSecondary)
                
                if case .Error(let exception) = loginViewModel.loginResult {
                    if let message = exception.userInfo["error"] as? String {
                        Text(message)
                            .foregroundColor(AppTheme.colors.onSecondary)
                    }
                }

                HStack {
                    Button(action: {
                        if loginViewModel.loginResult != .PendingRegister {
                            Task {
                                await loginViewModel.registerEmailPassword(email: email, password: password)
                            }
                        }
                    }) {
                        if loginViewModel.loginResult == .PendingRegister {
                            LoadingSpinner()
                        } else {
                            H1Text(text: "Sign Up", color: AppTheme.colors.onSecondary, fontSize: 18)
                        }
                    }
                    .frame(width: 130, height: 50)
                    .background(AppTheme.colors.secondary)
                    .clipShape(Circle())

                    Spacer().frame(width: 20)

                    Button(action: {
                        if loginViewModel.loginResult != .PendingLogin {
                            Task {
                                await loginViewModel.loginEmailPassword(email: email, password: password)
                            }
                        }
                    }) {
                        if loginViewModel.loginResult == .PendingLogin {
                            LoadingSpinner()
                        } else {
                            H1Text(text: "Login", color: AppTheme.colors.onSecondary, fontSize: 18)
                        }
                    }
                    .frame(width: 130, height: 50)
                    .background(AppTheme.colors.confirm)
                    .clipShape(Circle())
                }
                .padding(.vertical, 20)

                Button(action: {
                    if loginViewModel.loginResult != .PendingGoogleLogin {
                        // Login wit google
                    }
                }) {
                    if loginViewModel.loginResult == .PendingGoogleLogin {
                        LoadingSpinner()
                    } else {
                        H1Text(text: "Login with Google", color: AppTheme.colors.onSecondary, fontSize: 18)
                    }
                }
                .frame(width: 200, height: 50)
                .background(Color.white)
                .foregroundColor(AppTheme.colors.onSecondary)
                .clipShape(Capsule())
                .padding(.top, 20)

                Spacer()
            }
            .padding(20)
        }
        .onOpenURL { url in
          GIDSignIn.sharedInstance.handle(url)
        }
        .onAppear {
          GIDSignIn.sharedInstance.restorePreviousSignIn { user, error in
            // Check if `user` exists; otherwise, do something with `error`
          }
        }
        .onChange(of: loginViewModel.loginResult) { newValue in
            if newValue == .Success {
                // Navigate to main screen
            }
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
