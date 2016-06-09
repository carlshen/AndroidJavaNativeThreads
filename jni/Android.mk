LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := Threads
LOCAL_SRC_FILES := com_apress_threads_MainActivity.cpp

include $(BUILD_SHARED_LIBRARY)
