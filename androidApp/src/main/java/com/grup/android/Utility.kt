package com.grup.android

import java.text.NumberFormat

fun Double.asMoneyAmount(): String =
    NumberFormat.getCurrencyInstance().format(this)

fun isoDate(date: String) = date.substring(0, 10)