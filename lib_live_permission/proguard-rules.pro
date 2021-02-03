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
#---------------------------------基本指令区----------------------------------
# 混淆的压缩比例，0-7
-optimizationpasses 5
# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose
# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers
# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses
#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
# 避免混淆泛型
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
# 指定混淆是采用的算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#序列化和反序列化的类不能被混淆

# 指定外部模糊字典 proguard-chinese.txt 改为混淆文件名，下同
-obfuscationdictionary ../proguard-file/proguard-o0O.txt
# 指定class模糊字典
-classobfuscationdictionary ../proguard-file/proguard-o0O.txt
# 指定package模糊字典
-packageobfuscationdictionary ../proguard-file/proguard-o0O.txt

# 移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用
# 记得proguard-android.txt中一定不要加-dontoptimize才起作用
# 另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
-keep public class * extends androidx.annotation.**

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**




#---------------------- 自定义 -------------------
-keep public class io.dushu.permission.livedata.LiveDataPermission{
  *;
}
-keepclassmembers public class io.dushu.permission.livedata.LiveDataPermission{
  public <init>();
}
-keep public class io.dushu.permission.livedata.PermissionResult{*;}
-keep public class * extends androidx.fragment.app.Fragment