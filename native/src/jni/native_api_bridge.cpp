#include "core/native_api.h"
#include "jni/jni_bridge.h"
#include "jni/jni_hooks.h"

namespace commposed::native::jni {
COMMPOSED_DEF_NATIVE_METHOD(void, NativeAPI, recordNativeEntrypoint, jstring jstr) {
    commplant::JUTFString str(env, jstr);
    commposed::native::RegisterNativeLib(str);
}

static JNINativeMethod gMethods[] = {
    COMMPOSED_NATIVE_METHOD(NativeAPI, recordNativeEntrypoint, "(Ljava/lang/String;)V")};

void RegisterNativeApiBridge(JNIEnv *env) { REGISTER_COMMPOSED_NATIVE_METHODS(NativeAPI); }

}  // namespace commposed::native::jni
