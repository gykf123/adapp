# 保留 kotlinx.serialization 生成的序列化器
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class gay.fesui.adapp.data.model.** {
    *** Companion;
}
-keepclasseswithmembers class gay.fesui.adapp.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# OkHttp 平台相关
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
