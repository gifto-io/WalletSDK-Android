package io.gifto.wallet.utils.common;

import android.util.Log;

/**
 * Created by ThangPM on 9/17/15.
 */
public class Logger
{
    private static final boolean USE_ANDROID_LOGCAT = true;
    private static final boolean USE_MINT = true;
    private static final boolean USE_GA = true;

    /**
     * Debug Log
     *
     * @author Winny Pham
     * @param tag
     * @param log
     */
    public static void d(String tag, String log)
    {
        if (USE_ANDROID_LOGCAT)
            Log.d(tag, log);
    }

    /**
     * Information Log
     *
     * @author Winny Pham
     * @param tag
     * @param log
     */
    public static void i(String tag, String log)
    {
        if (USE_ANDROID_LOGCAT)
            Log.i(tag, log);
    }

    /**
     * Warning Log
     *
     * @author Winny Pham
     * @param tag
     * @param log
     */
    public static void w(String tag, String log)
    {
        if (USE_ANDROID_LOGCAT)
            Log.w(tag, log);
    }

    /**
     * Error Log
     *
     * @author Winny Pham
     * @param tag
     * @param log
     */
    public static void e(String tag, String log)
    {
        if (USE_ANDROID_LOGCAT)
            Log.e(tag, log);
    }

    /**
     * Process Exceptions.
     *
     * @author Winny Pham
     * @param tag TAG
     * @param e Exception object
     */
    public static void Exception(String tag, Exception e)
    {
        if (USE_ANDROID_LOGCAT)
        {
            Log.d(tag, e.toString());
            e.printStackTrace();
        }

        if (USE_MINT)
        {
//            Mint.logException(e);
        }

        if(USE_GA)
        {
//			EasyTracker tracker = EasyTracker.getInstance(App.getContext());
//			tracker.send(MapBuilder.createException(new StandardExceptionParser(App.getContext(), null)
//								   .getDescription(Thread.currentThread().getName(), e), false)
//								   .build());
        }
    }
}
