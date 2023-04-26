import SwiftUI
import GoogleSignIn
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    init() {
        InitKoinKt.doInitKoin()
    }
    
	var body: some Scene {
		WindowGroup {
			DebugLoginView()
		}
	}
}
