//
//  LoggedInViewModel.swift
//  iosApp
//
//  Created by Justin Xu on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class LoggedInViewModel: ObservableObject {
    private let STOP_TIMEOUT_MILLIS = 5000
    private static var _apiServer: APIServer? = nil

    static func injectApiServer(apiServer: APIServer) {
        _apiServer = apiServer
    }
    
    var apiServer: APIServer {
        get { LoggedInViewModel._apiServer! }
    }
    
    var userObject: User {
        get { apiServer.user }
    }
    
    func closeApiServer() async throws {
        try await LoggedInViewModel._apiServer?.logOut()
        LoggedInViewModel._apiServer = nil
    }
}
