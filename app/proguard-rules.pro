# Tempo ProGuard Rules

# Keep ical4j classes
-keep class net.fortuna.ical4j.** { *; }
-dontwarn net.fortuna.ical4j.**

# Keep Room entities
-keep class com.tempo.app.data.local.db.entity.** { *; }

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Compose
-keep class androidx.compose.** { *; }

# General Android
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
