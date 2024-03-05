package com.example.testblueapp.util;

import android.util.Log;

/**
 * Centralized logging mechanism for the entire application.
 */
public class LogManager {
    private static boolean isLoggingEnabled = true; // This can be controlled dynamically

    public static void logDebug(String tag, String message) {
        if (isLoggingEnabled) {
            Log.d(tag, message);
        }
    }

    public static void logWarning(String tag, String message) {
        if (isLoggingEnabled) {
            Log.w(tag, message);
        }
    }

    public static void logError(String tag, String message) {
        if (isLoggingEnabled) {
            Log.e(tag, message);
        }
    }

    public static void setLoggingEnabled(boolean enabled) {
        isLoggingEnabled = enabled;
    }
}
