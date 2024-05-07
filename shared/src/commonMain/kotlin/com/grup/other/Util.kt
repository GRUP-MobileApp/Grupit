package com.grup.other

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal fun getCurrentTime(): Instant = Clock.System.now()

internal expect val platform: Platform

enum class Platform {
    Android, IOS
}
