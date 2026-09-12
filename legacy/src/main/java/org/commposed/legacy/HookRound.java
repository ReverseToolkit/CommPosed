package org.commposed.legacy;

import org.commposed.impl.hooks.CommPosedLegacyCallback;

import java.lang.reflect.Executable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * One round of legacy before/after callbacks for a hooked executable.
 *
 * <p>Kept out of {@code de.robv.android.xposed.XposedBridge} so the simple class name is not
 * {@code XposedBridge$LegacyApiSupport}. That inner class sits on the public API type, which
 * prefix obfuscation cannot rename, and it appeared on every hooked-method stack and in every
 * throwable {@link XposedBridge#log(Throwable)} printed. This type is package-private; R8 can
 * rename it. Cross-package calls into {@link XC_MethodHook} go through
 * {@link XC_MethodHook#callBeforeHookedMethod} / {@link XC_MethodHook#callAfterHookedMethod}.
 */
final class HookRound<T extends Executable> {
    private final XC_MethodHook.MethodHookParam<T> param;
    private final CommPosedLegacyCallback<T> callback;
    private final Object[] snapshot;

    private int beforeIdx;

    HookRound(CommPosedLegacyCallback<T> callback, Object[] legacySnapshot) {
        this.param = new XC_MethodHook.MethodHookParam<>();
        this.callback = callback;
        this.snapshot = legacySnapshot;
    }

    void handleBefore() {
        syncronizeApi(param, callback, true);
        for (beforeIdx = 0; beforeIdx < snapshot.length; beforeIdx++) {
            try {
                var cb = (XC_MethodHook) snapshot[beforeIdx];
                cb.callBeforeHookedMethod(param);
            } catch (Throwable t) {
                XposedBridge.log(t);
                param.setResult(null);
                param.returnEarly = false;
            }

            if (param.returnEarly) {
                beforeIdx++;
                break;
            }
        }
        syncronizeApi(param, callback, false);
    }

    void handleAfter() {
        syncronizeApi(param, callback, true);
        for (int afterIdx = beforeIdx - 1; afterIdx >= 0; afterIdx--) {
            Object lastResult = param.getResult();
            Throwable lastThrowable = param.getThrowable();
            try {
                var cb = (XC_MethodHook) snapshot[afterIdx];
                cb.callAfterHookedMethod(param);
            } catch (Throwable t) {
                XposedBridge.log(t);
                if (lastThrowable == null) {
                    param.setResult(lastResult);
                } else {
                    param.setThrowable(lastThrowable);
                }
            }
        }
        syncronizeApi(param, callback, false);
    }

    private void syncronizeApi(XC_MethodHook.MethodHookParam<T> param, CommPosedLegacyCallback<T> callback, boolean forward) {
        if (forward) {
            param.method = callback.getMethod();
            param.thisObject = callback.getThisObject();
            param.args = callback.getArgs();
            param.result = callback.getResult();
            param.throwable = callback.getThrowable();
            param.returnEarly = callback.isSkipped();
        } else {
            callback.setThisObject(param.thisObject);
            callback.setArgs(param.args);

            // Only write the result/throwable back if the legacy module explicitly skipped execution
            if (param.returnEarly) {
                if (param.throwable != null) {
                    callback.setThrowable(param.throwable);
                } else {
                    callback.setResult(param.result);
                }
            }
        }
    }
}
