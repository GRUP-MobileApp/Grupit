package com.grup.objects

inline fun throwIf(condition: Boolean, thr: () -> Throwable) {
    if (condition) {
        throw thr()
    }
}