package org.commposed.ui

internal fun logW(tag: String, msg: String, tr: Throwable? = null) {
    if (!BuildConfig.DEBUG) return
    if (tr != null) android.util.Log.w(tag, msg, tr) else android.util.Log.w(tag, msg)
}

internal fun logE(tag: String, msg: String, tr: Throwable? = null) {
    if (!BuildConfig.DEBUG) return
    if (tr != null) android.util.Log.e(tag, msg, tr) else android.util.Log.e(tag, msg)
}
