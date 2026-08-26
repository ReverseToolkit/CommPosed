package org.commposed.impl.core

import java.lang.reflect.Executable
import org.commposed.util.Log
import org.commposed.util.Utils
import org.commposed.nativebridge.HookBridge

/**
 * Engine responsible for scanning and deoptimizing prebuilt framework methods. Ensures that hooks
 * placed on heavily inlined methods are respected by the ART runtime.
 */
object CommPosedDeopter {

    private const val TAG = "CommPosedDeopter"

    @JvmStatic
    fun deoptMethods(where: String, cl: ClassLoader?) {
        val targets = CommPosedInlinedCallers.get(where)
        if (targets.isEmpty()) return

        val searchClassLoader = cl ?: ClassLoader.getSystemClassLoader()

        for (target in targets) {
            runCatching {
                    val clazz = Class.forName(target.className, false, searchClassLoader)
                    val executable: Executable =
                        if (target.isConstructor) {
                            clazz.getDeclaredConstructor(*target.params)
                        } else {
                            clazz.getDeclaredMethod(target.methodName, *target.params)
                        }

                    // Allow access if restricted and pass to the native bridge
                    executable.isAccessible = true
                    HookBridge.deoptimizeMethod(executable)
                }
                .onFailure {
                    Log.v(
                        TAG,
                        "Skipping deopt for ${target.className}#${target.methodName}: ${it.message}",
                    )
                }
        }
    }

    fun deoptBootMethods() {
        deoptMethods(CommPosedInlinedCallers.KEY_BOOT_IMAGE, null)
    }

    @JvmStatic
    fun deoptResourceMethods() {
        if (Utils.isMIUI) {
            deoptMethods(CommPosedInlinedCallers.KEY_BOOT_IMAGE_MIUI_RES, null)
        }
    }

    fun deoptSystemServerMethods(sysCL: ClassLoader) {
        deoptMethods(CommPosedInlinedCallers.KEY_SYSTEM_SERVER, sysCL)
    }
}
