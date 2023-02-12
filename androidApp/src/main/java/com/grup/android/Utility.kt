package com.grup.android

import java.text.NumberFormat

fun getCurrencySymbol(): String =
    NumberFormat.getCurrencyInstance().currency!!.symbol

fun Double.asMoneyAmount(): String =
    NumberFormat.getCurrencyInstance().format(this)

fun isoDate(date: String) = date.substring(0, 10)