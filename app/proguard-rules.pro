# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep data classes
-keepclassmembers class com.zekaoformani.macera.** {
    <init>(...);
}

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# AndroidX DataStore
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# Navigation Compose
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.** { *; }

# Keep Kotlin metadata for reflection
-keepattributes RuntimeVisibleAnnotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

# Keep game model data classes
-keep class com.zekaoformani.macera.data.models.** { *; }

# Keep ViewModel factory
-keep class com.zekaoformani.macera.ui.viewmodel.** { *; }
