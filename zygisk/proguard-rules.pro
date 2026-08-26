-keepclasseswithmembers class org.commposed.core.Main {
    public static void forkCommon(boolean, boolean, java.lang.String, java.lang.String, android.os.IBinder);
}
-keepclasseswithmembers,includedescriptorclasses class * {
    native <methods>;
}
-keepclasseswithmembers class org.commposed.service.BridgeService {
    public static boolean *(android.os.IBinder, int, long, long, int);
}
-repackageclasses
-allowaccessmodification
