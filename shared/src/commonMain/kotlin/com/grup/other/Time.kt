package com.grup.other

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getCurrentTime(): String = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()