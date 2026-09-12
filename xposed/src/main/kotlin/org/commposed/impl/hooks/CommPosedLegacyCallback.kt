package org.commposed.impl.hooks

import java.lang.reflect.Executable

/**
 * Adapter for backward compatibility with the legacy hook round. Contains state mutations
 * strictly for legacy module support.
 */
class CommPosedLegacyCallback<T : Executable>(
    val method: T,
    var thisObject: Any?,
    var args: Array<Any?>,
) {
    var result: Any? = null
        private set

    var throwable: Throwable? = null
        private set

    var isSkipped = false
        private set

    fun setResult(res: Any?) {
        result = res
        throwable = null
        isSkipped = true
    }

    fun setThrowable(t: Throwable?) {
        result = null
        throwable = t
        isSkipped = true
    }
}
