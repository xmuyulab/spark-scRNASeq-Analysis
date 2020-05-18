#简单介绍怎么开发jni

1.看star有哪些需要的函数<br>
2.在java中编写java层的调用<br>
3.javac xxx.java<br>
4.javah xxx<br>
5.编写c++层代码<br>
6.jni.h,jni_md.h,xxx.h,xxx.cpp<br>
7.g++ -dynamiclib -I . Native.cpp Parameters.cpp -o xxx.so

#Resolving Native Method Names
Dynamic linkers resolve entires based on their names.A native method name is concatenated from the following components:<br>
1.the prefix Java_<br>
2.a mangled fully-qualified class name<br>
3.an underscore("\_") separator<br>
4.a mangled method name<br>
5.for overloaded native methods,two underscores("\_") followed by the mangled argument signature

The VM checks for a method name match for methods that reside in the native library.<br>
The VM looks first for the short name;that is,the name without the argument signature.<br>
It the looks for the long name,which is the name with the argument signature.<br>
Programmers need to use the same name as a nonnative method.A nonnative method(a Java method) does not reside in the nature library.<br>

#Native Method Arguments
1.The JNI interface pointer is the first argument to native methods.The JNI interface pointer is of type JNIEnv.<br>
2.The second argument differs depending on whether the native method is static or nonstatic.<br>
3.The second argument to a nonstatic native method is a reference to the object.The second argument to a static method is a reference to its java class.

#Global and Local References
1.Local references are valid for the duration of a native method call,and are automatically freed after the native method call returns<br>
2.Global references remain valid until they are explicitly freed.<br>

#Reference
1.http://icejoywoo.github.io/2018/07/25/spark-jni.html
