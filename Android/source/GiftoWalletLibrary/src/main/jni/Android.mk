LOCAL_PATH := $(call my-dir)

#gifto-native-utils module
include $(CLEAR_VARS)

LOCAL_MODULE    := gifto-native-utils
LOCAL_SRC_FILES := utils.c \
				utilsInterface.c

LOCAL_LDLIBS := -llog
				
include $(BUILD_SHARED_LIBRARY)
#if you need to add more module, do the same as the one we started with (the one with the CLEAR_VARS)
