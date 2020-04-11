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

#微服务SDK
-keep class com.base.services.**{*;}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#-dontpreverify
#-dontoptimize
-verbose
-ignorewarnings

-keepattributes Exceptions,SourceFile,LineNumberTable,InnerClasses,Signature,Deprecated,SourceFile,*Annotation*,EnclosingMethod

-dontwarn com.cs.bd.commerce.util.**

#买量sdk混淆配置===BEGIN=========
#-keep  class com.cs.bd.buychannel.BuyChannelApi{*;}
#-keep  class com.cs.bd.buychannel.BuySdkInitParams{*;}
#-keep  class com.cs.bd.buychannel.BuySdkInitParams$Builder{*;}
#-keep  interface com.cs.bd.buychannel.BuySdkInitParams$IProtocal19Handler{*;}
#-keep  class com.cs.bd.buychannel.MPSharedPreferences{*;}
#-keep  class com.cs.bd.ad.**{*;}
#-keep  class com.cs.bd.utils.**{*;}
#-keep  class com.cs.bd.buychannel.buyChannel.database.BuychannelDbHelpler.**{*;}
#买量sdk混淆配置===END===========
-keepattributes Exceptions,SourceFile,LineNumberTable,InnerClasses,Signature,Deprecated,SourceFile,*Annotation*,EnclosingMethod

-dontwarn com.cs.bd.commerce.util.**
-dontwarn com.android.installreferrer.**
#第3方sdk混淆配置===BEGIN===========
#google play service sdk
-keep public class com.google.ads.** {*;}
-keep public class com.google.android.gms.** {*;}
#EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#ARouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
#Gson
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keep class com.palmapp.master.baselib.bean.** { *; }
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Class names are needed in reflection
-keepclassmembers enum com.amazonaws.** { *; }
-keepnames class com.amazonaws.**
-keepnames class com.amazon.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**

##---------------End: proguard configuration for Gson  ----------
#第3方sdk混淆配置===END===========

##广告SDK
##aerserv
-keep class com.aerserv.**{*;}
-keepclassmembers class com.aerserv.** {*;}
##adcolony
-keep class com.adcolony.**{*;}
# Keep ADCNative class members unobfuscated
-keepclassmembers class com.adcolony.sdk.ADCNative** {
 *;
 }
-dontwarn com.adcolony.**
-dontwarn com.aerserv.**

-keepclassmembers class * {
 @android.webkit.JavascriptInterface <methods>;
}

#inmobi===BEGIN===========
-keepattributes SourceFile,LineNumberTable
-keep class com.inmobi.** { *; }
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.picasso.**
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{
     public *;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{
     public *;
}
# skip the Picasso library classes
-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**
# skip Moat classes
-keep class com.moat.** {*;}
-dontwarn com.moat.**
# For old ads classes
-keep public class com.google.ads.**{
   public *;
}
# For mediation
-keepattributes *Annotation*
# For Google Play services
-keep public class com.google.android.gms.ads.**{
   public *;
}
#inmobi===END===========

#smaato===BEGIN===========
-keep class com.smaato.**{*;}
-dontwarn com.smaato.soma.SomaUnityPlugin*
-dontwarn com.millennialmedia**
-dontwarn com.facebook.**

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
#smaato===END===========

#doubleclick===STRAT===========
-keep class com.doubleclick.**{*;}
-keepclassmembers class com.doubleclick.** {*;}
#doubleclick===END===========

#google play service sdk
-keep public class com.google.ads.** {*;}
-keep public class com.google.android.gms.** {*;}
#facebook sdk
-keep public class com.facebook.ads.** {*;}
#Chartboost sdk
-keep class com.chartboost.** { *; }
#Vungle sdk
-dontwarn com.vungle.**
-keep class com.vungle.** { *; }
-keep class javax.inject.*

#MoPub SDK混淆
#-Keep public classes and methods.
-keepclassmembers class com.mopub.** { public *; }
-keep public class com.mopub.**
-keep public class android.webkit.JavascriptInterface {}
#-Explicitly keep any custom event classes in any package.
-keep class * extends com.mopub.mobileads.CustomEventBanner {}
-keep class * extends com.mopub.mobileads.CustomEventInterstitial {}
-keep class * extends com.mopub.nativeads.CustomEventNative {}
-keep class * extends com.mopub.nativeads.CustomEventRewardedAd {}
#-Keep methods that are accessed via reflection
-keepclassmembers class ** { @com.mopub.common.util.ReflectionTarget *; }
#Unity sdk
-keep class com.unity3d.** { *; }
#Applovin sdk
-keep class com.applovin.** { *; }
#广告sdk新增混淆配置
-keep public class com.mediation.**{*;}