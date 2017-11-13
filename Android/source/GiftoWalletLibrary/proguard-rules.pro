# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/thongnguyen/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile



#==========================================
-printmapping /app/build/outputs/mapping/release/mapping.txt
-renamesourcefileattribute ProGuard
-keepattributes SourceFile,LineNumberTable
#==========================================

-dontwarn **

# #--------------------- RETROFIT -----------------------
-dontwarn retrofit.*
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
#--------------------------------------------------------

#----------------------- EVENTBUS -----------------------
-keepclassmembers class ** {
    public void onEvent*(***);
}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#--------------------------------------------------------

#------------------ SWEET ALERT DIALOG ------------------
-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
  public <init>(...);
}
#--------------------------------------------------------

##---------------Begin: ProGuard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
#-keepattributes Signature

# For using GSON @Expose annotation + ACRA needs "annotations" so add this...
# Note: This may already be defined in the default "proguard-android-optimize.txt"
# file in the SDK. If it is, then you don't need to duplicate it. See your
# "project.properties" file to get the path to the default "proguard-android-optimize.txt".
-keepattributes *Annotation*,Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
##---------------End: ProGuard configuration for Gson  ------------


#--------------------- DISABLE LOGS ---------------------
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
#--------------------------------------------------------

-dontwarn org.json.*
-dontwarn com.google.**
-dontwarn org.bouncycastle.**

# Keep HttpClient
-dontwarn org.apache.**
-keep class org.apache.** { *; }

# Keep all the ACRA classes
-dontwarn org.acra.**
-keep class org.acra.** { *; }


# ---------------- Gifto Wallet SDK ----------------------------------
-keepparameternames
-keep class io.gifto.wallet.networking.models.** { *; }

-keepparameternames
-keep class io.gifto.wallet.interfaces.* { *; }

-keepparameternames
-keep class io.gifto.wallet.model.* { *; }

-keepparameternames
-keep class io.gifto.wallet.ui.base.* { *; }

-keepparameternames
-keep class io.gifto.wallet.ui.manager.* { *; }

-keepparameternames
-keep class io.gifto.wallet.networking.RestClient {
    public static *** *(...);
}

-keep class io.gifto.wallet.networking.WSConstants { *; }

-keepparameternames
-keep public interface io.gifto.wallet.networking.GiftoApiResponse { *; }

-keepparameternames
-keep public interface io.gifto.wallet.networking.WalletApiResponseCallback { *; }

-keepparameternames
-keep class io.gifto.wallet.GiftoWalletManager {
    public static java.lang.String getAuthorization(...);
    public static java.lang.String getUserIdentityData(...);
    public static boolean isUsingStorePassphrase(...);
    public static void ClearData ();
}

-keep class io.gifto.wallet.ui.activity.GiftoActivity { *; }

-keepparameternames
-keep class io.gifto.wallet.WalletBuildInGUIBuilder { *; }

-keepparameternames
-keep class io.gifto.wallet.GiftoWalletManager$Builder { *; }
# -----------------------------------------------------------------
