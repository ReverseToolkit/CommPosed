#pragma once

#include <android/log.h>
#include <fmt/format.h>

#include <array>

/**
 * @file logging.h
 * @brief Provides a lightweight logging framework using fmt.
 *
 */

/// The tag used for all log messages from this library.
#ifndef LOG_TAG
#define LOG_TAG "CommPosedNative"
#endif

/**
 * @def LOGV(fmt, ...)
 * @brief Logs a verbose message. Compiled out in release builds.
 */

/**
 * @def LOGD(fmt, ...)
 * @brief Logs a debug message. Compiled out in release builds.
 */

/**
 * @def LOGI(fmt, ...)
 * @brief Logs an informational message. Compiled out in release builds.
 */

/**
 * @def LOGW(fmt, ...)
 * @brief Logs a warning message. Compiled out in release builds.
 */

/**
 * @def LOGE(fmt, ...)
 * @brief Logs an error message. Compiled out in release builds.
 */

/**
 * @def LOGF(fmt, ...)
 * @brief Logs a fatal error message. Compiled out in release builds.
 */

/**
 * @def PLOGE(fmt, ...)
 * @brief Logs an error message and appends `errno`. Compiled out in release builds.
 */

#if defined(LOG_DISABLED) || defined(NDEBUG)
#define LOGV(...) ((void)0)
#define LOGD(...) ((void)0)
#define LOGI(...) ((void)0)
#define LOGW(...) ((void)0)
#define LOGE(...) ((void)0)
#define LOGF(...) ((void)0)
#define PLOGE(...) ((void)0)
#else

namespace commposed::native::detail {
template <typename... T>
inline void LogToAndroid(int prio, const char *tag, fmt::format_string<T...> fmt, T &&...args) {
    // Using a stack-allocated buffer for performance.
    std::array<char, 1024> buf{};
    // format_to_n is safe against buffer overflows.
    auto result = fmt::format_to_n(buf.data(), buf.size() - 1, fmt, std::forward<T>(args)...);
    buf[result.size] = '\0';
    __android_log_write(prio, tag, buf.data());
}
}  // namespace commposed::native::detail

#define LOGD(fmt, ...)                                                                             \
    ::commposed::native::detail::LogToAndroid(ANDROID_LOG_DEBUG, LOG_TAG, "{}:{} ({}): " fmt,         \
                                           __FILE_NAME__, __LINE__,                                \
                                           __PRETTY_FUNCTION__ __VA_OPT__(, ) __VA_ARGS__)
#define LOGV(fmt, ...)                                                                             \
    ::commposed::native::detail::LogToAndroid(ANDROID_LOG_VERBOSE, LOG_TAG,                           \
                                           fmt __VA_OPT__(, ) __VA_ARGS__)
#define LOGI(fmt, ...)                                                                             \
    ::commposed::native::detail::LogToAndroid(ANDROID_LOG_INFO, LOG_TAG,                              \
                                           fmt __VA_OPT__(, ) __VA_ARGS__)
#define LOGW(fmt, ...)                                                                             \
    ::commposed::native::detail::LogToAndroid(ANDROID_LOG_WARN, LOG_TAG,                              \
                                           fmt __VA_OPT__(, ) __VA_ARGS__)
#define LOGE(fmt, ...)                                                                             \
    ::commposed::native::detail::LogToAndroid(ANDROID_LOG_ERROR, LOG_TAG,                             \
                                           fmt __VA_OPT__(, ) __VA_ARGS__)
#define LOGF(fmt, ...)                                                                             \
    ::commposed::native::detail::LogToAndroid(ANDROID_LOG_FATAL, LOG_TAG,                             \
                                           fmt __VA_OPT__(, ) __VA_ARGS__)
#define PLOGE(fmt, ...) LOGE(fmt " failed with error {}: {}", ##__VA_ARGS__, errno, strerror(errno))

#endif  // LOG_DISABLED || NDEBUG
