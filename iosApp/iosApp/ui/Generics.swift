//
//  Generics.swift
//  iosApp
//
//  Created by Justin Xu on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct H1Text: View {
    let text: String
    var color: Color = AppTheme.colors.onSecondary
    var fontFamily: String = "proxima_nova"
    var fontSize: CGFloat = 24
    
    var body: some View {
        Text(text).font(Font.custom(fontFamily, size: fontSize)).foregroundColor(color)
    }
}

struct LoadingSpinner: View {
    var body: some View {
        ProgressView()
    }
}
