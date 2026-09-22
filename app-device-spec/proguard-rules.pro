# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Keep Hilt annotations
-keep @dagger.** class *
-keep @javax.inject.** class *
-keep @hilt.** class *

# Keep Dagger generated classes
-keep class dagger.hilt.** { *; }
-keep class dagger.** { *; }
-keep class javax.** { *; }
-keep class jakarta.** { *; }

# Keep ViewModels
-keep class com.device.spec.extractor.viewmodels.** { *; }

# Keep Services
-keep class com.device.spec.extractor.services.** { *; }

# Keep Activities
-keep class com.device.spec.extractor.** { *; }

# Keep sensor data classes
-keep class com.device.spec.extractor.sensors.** { *; }

# Keep utility classes
-keep class com.device.spec.extractor.utils.** { *; }

# Keep serialization classes
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.device.spec.extractor.**$$serializer { *; }
-keepclassmembers class com.device.spec.extractor.** {
    *** Companion;
}
-keepclasseswithmembers class com.device.spec.extractor.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Gson classes
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep OkHttp classes
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Room classes
-keep class com.device.spec.extractor.database.** { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keep @androidx.room.Database class *

# Keep Compose classes
-dontwarn androidx.compose.runtime.**
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material.** { *; }

# Keep Coroutines classes
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# Keep Timber classes
-keep class timber.log.** { *; }

# Keep MultiDex classes
-keep class android.multidex.** { *; }

# Keep reflection for serialization
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes EnclosingMethod

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}