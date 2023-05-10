//
//  DebugApplication.swift
//  iosApp
//
//  Created by Justin Xu on 5/2/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ContentView: View {
    private struct ComposeView: UIViewControllerRepresentable {
        func makeUIViewController(context: Context) -> UIViewController {
            return DebugApplicationControllerKt.DebugApplicationController(googleSignInManager: nil)
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
