package com.grup.android

import com.grup.APIServer.Image
import java.text.NumberFormat

// Money
fun getCurrencySymbol(): String =
    NumberFormat.getCurrencyInstance().currency!!.symbol

fun Double.asMoneyAmount(): String =
    NumberFormat.getCurrencyInstance().format(this)

fun isoDate(date: String) = date.substring(5, 10)
fun isoFullDate(date: String) = date.substring(0, 10)

// Image
fun getProfilePictureURI(userId: String) = Image.getProfilePictureURI(userId)
