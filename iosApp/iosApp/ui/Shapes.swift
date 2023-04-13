//
//  Shapes.swift
//  iosApp
//
//  Created by Justin Xu on 4/12/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct Shapes {
    let small: any Shape = RoundedRectangle(cornerRadius: 4)
    let medium: any Shape = RoundedRectangle(cornerRadius: 8)
    let large: any Shape = RoundedRectangle(cornerRadius: 16)
    let extraLarge: any Shape = RoundedRectangle(cornerRadius: 24)

    let circleShape: any Shape = Circle()
}
