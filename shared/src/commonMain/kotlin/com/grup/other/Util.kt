package com.grup.other

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal fun getCurrentTime(): String = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
