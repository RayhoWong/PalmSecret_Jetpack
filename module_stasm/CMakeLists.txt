cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)
set(ocvlibs "${CMAKE_SOURCE_DIR}/src/main/jniLibs")
include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/include)
add_library(des SHARED IMPORTED)
#add_library(opencv_java3 SHARED IMPORTED)

set_target_properties(des
        PROPERTIES IMPORTED_LOCATION "${ocvlibs}/${ANDROID_ABI}/libdes.so")

#set_target_properties(opencv_java3 PROPERTIES
#        IMPORTED_LOCATION "${ocvlibs}/${ANDROID_ABI}/libopencv_java3.so")

#add_library(stasm-face-detection-jni
#        SHARED
#        src/main/cpp/opencv_utility.cpp
#        src/main/cpp/stasm-face-detection-jni.cpp)
add_library(desJni
        SHARED
        src/main/cpp/des_jni.cpp src/main/cpp/checkApp.cpp)
#add_library(mls-jni
#        SHARED
#        src/main/cpp/mls-jni.cpp)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log android)

find_library(GLESv2-lib
        GLESv2)

#target_link_libraries(mls-jni
#        opencv_java3
#        ${log-lib})

#target_link_libraries(stasm-face-detection-jni
#        opencv_java3
#        ${log-lib}
#        ${GLESv2-lib})

target_link_libraries( # Specifies the target library.
        desJni des android

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})