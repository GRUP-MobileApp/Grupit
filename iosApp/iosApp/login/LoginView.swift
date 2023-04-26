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
import shared

struct DebugLoginView: View {
    private struct ComposeView: UIViewControllerRepresentable {
        func makeUIViewController(context: Context) -> UIViewController {
            let loginViewModel = LoginViewModel()
            return DebugLoginControllerKt.DebugLoginController(
                loginViewModel: loginViewModel,
                googleLoginOnClick: nil,
                loginOnClick: {}
            )
        }
        
        func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
    }
    
    var body: some View {
        ComposeView()
    }
}

struct DebugLoginView_Previews: PreviewProvider {
    static var previews: some View {
        DebugLoginView()
    }
}
