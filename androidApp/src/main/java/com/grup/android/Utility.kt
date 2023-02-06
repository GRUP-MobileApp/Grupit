package com.grup.android

import java.text.NumberFormat

fun Double.asMoneyAmount(): String =
    NumberFormat.getCurrencyInstance().format(this)