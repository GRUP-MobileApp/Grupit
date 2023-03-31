package com.grup.android

import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.grup.APIServer
import java.text.NumberFormat

// Money
fun getCurrencySymbol(): String =
    NumberFormat.getCurrencyInstance().currency!!.symbol

fun Double.asMoneyAmount(): String =
    NumberFormat.getCurrencyInstance().format(this)

fun isoDate(date: String) = date.substring(5, 10)
fun isoFullDate(date: String) = date.substring(0, 10)

// Image
fun getProfilePictureURI(userId: String) = APIServer.Images.getProfilePictureURI(userId)

fun (ImageRequest.Builder).applyCachingAndBuild(key: String) = this
    .memoryCachePolicy(CachePolicy.ENABLED)
    .diskCachePolicy(CachePolicy.ENABLED)
    .allowHardware(true)
    .diskCacheKey(key)
    .memoryCacheKey(key)
    .transformations(CircleCropTransformation())
    .build()
