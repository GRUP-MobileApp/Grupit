package com.grup

actual fun getEnvVar(key: String): String? {
    return System.getenv(key)
}