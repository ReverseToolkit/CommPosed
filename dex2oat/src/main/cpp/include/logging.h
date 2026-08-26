#pragma once

#include <android/log.h>
#include <errno.h>

#ifndef LOG_TAG
#define LOG_TAG "CommPosedDex2Oat"
#endif

#if defined(LOG_DISABLED) || defined(NDEBUG)
#define LOGD(...) ((void)0)
#define LOGV(...) ((void)0)
#define LOGI(...) ((void)0)
#define LOGW(...) ((void)0)
#define LOGE(...) ((void)0)
#define LOGF(...) ((void)0)
#define PLOGE(...) ((void)0)
#else
#define LOGD(fmt, ...)                                                                             \
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,                                                \
                        "%s:%d#%s"                                                                 \
                        ": " fmt,                                                                  \
                        __FILE_NAME__, __LINE__, __PRETTY_FUNCTION__ __VA_OPT__(, ) __VA_ARGS__)
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL, LOG_TAG, __VA_ARGS__)
#define PLOGE(fmt, args...) LOGE(fmt " failed with %d: %s", ##args, errno, strerror(errno))
#endif
