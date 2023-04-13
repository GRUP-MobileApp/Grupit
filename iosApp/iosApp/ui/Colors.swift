//
//  Colors.swift
//  iosApp
//
//  Created by Justin Xu on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

extension Color {
    init(hex: UInt, alpha: Double = 1) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 08) & 0xff) / 255,
            blue: Double((hex >> 00) & 0xff) / 255,
            opacity: alpha
        )
    }
}

struct Colors {
    static let dark_grey = Color(hex: 0xFF1F1F1F)
    static let grey = Color(hex: 0xFF292929)
    static let light_grey = Color(hex: 0xFF3D3D3D)
    static let white = Color(hex: 0xFFFFFFFF)
    static let off_white = Color(hex: 0xBBF5F5F4)
    static let green = Color(hex: 0xFF65B540)
    static let red = Color(hex: 0xFFEF1A1A)
    static let red_error = Color(hex: 0xffff0033)
    
    let primary: Color = dark_grey
    let secondary: Color = grey
    let onPrimary: Color = off_white
    let onSecondary: Color = white
    let confirm: Color = green
    let deny: Color = red
    let caption: Color = light_grey
    let error: Color = red_error
}
