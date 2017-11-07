package io.gifto.wallet.utils.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ThangPM on 7/16/15.
 */
public class CustomSharedPreferences
{
    public static final String CUSTOM_SHARED_PREFERNCES = "rosecoin_data_shared_pref";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;
    private static Context appContext;

    /**
     * Constructor. Please create new object before using setter and getter
     * @param context Input from Activity
     */
    public static void Init(final Context context)
    {
        appContext = context;
        if(context != null)
        {
            sharedPref = context.getSharedPreferences(CUSTOM_SHARED_PREFERNCES, 0);
            editor = sharedPref.edit();
        }
        else
        {
            editor = null;
            sharedPref = null;
        }
    }

    private static void Refresh()
    {
        if(null != appContext)
        {
            sharedPref = appContext.getSharedPreferences(CUSTOM_SHARED_PREFERNCES, 0);
            editor = sharedPref.edit();
        }
        else
        {
            editor = null;
            sharedPref = null;
        }
    }

    /**
     * Set data for String
     * @param prefName Preferences name
     * @param value String input
     */
    public synchronized static void setPreferences(final String prefName, final String value)
    {
        Refresh();
        if(null != editor)
        {
            editor.putString(prefName, value);
            editor.commit();
        }
    }

    /**
     * Get data for String
     * @param prefName Preferences name
     * @param defaultValue
     * @return String or 0 if Name not existed
     */
    public static String getPreferences(final String prefName, final String defaultValue)
    {
        Refresh();
        if(null != sharedPref)
        {
            return sharedPref.getString(prefName, defaultValue);
        }
        else
        {
            return null;
        }
    }

    /**
     * Set data for boolean
     * @param prefName Preferences name
     * @param value boolean input
     */
    public synchronized static void setPreferences(final String prefName, final boolean value)
    {
        if(null != editor)
        {
            editor.putBoolean(prefName, value);
            editor.commit();
        }
    }

    /**
     * Get data for boolean
     * @param prefName Preferences name
     * @param defaultValue
     * @return boolean or 0 if Name not existed
     */
    public static boolean getPreferences(final String prefName, final boolean defaultValue)
    {
        if(null != sharedPref)
        {
            return sharedPref.getBoolean(prefName, defaultValue);
        }
        else
        {
            return false;
        }
    }

    /**
     * Set data for Integer
     * @param prefName Preferences name
     * @param value Integer input
     */
    public synchronized static void setPreferences(final String prefName, final int value)
    {
        if(null != editor)
        {
            editor.putInt(prefName, value);
            editor.commit();
        }
    }

    /**
     * Get data for Integer
     * @param prefName Preferences name
     * @param defaultValue
     * @return Integer or 0 if Name not existed
     */
    public static int getPreferences(final String prefName, final int defaultValue)
    {
        if(null != sharedPref)
        {
            return sharedPref.getInt(prefName, defaultValue);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Set data for Long
     * @param prefName Preferences name
     * @param value Long input
     */
    public synchronized static void setPreferences(final String prefName, final long value)
    {
        if(null != editor)
        {
            editor.putLong(prefName, value);
            editor.commit();
        }
    }

    /**
     * Get data for Long
     * @param prefName Preferences name
     * @param defaultValue
     * @return Long or 0 if Name not existed
     */
    public static long getPreferences(final String prefName, final long defaultValue)
    {
        if(null != sharedPref)
        {
            return sharedPref.getLong(prefName, defaultValue);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Set data for Float
     * @param prefName Preferences name
     * @param value Float input
     */
    public synchronized static void setPreferences(final String prefName, final float value)
    {
        if(null != editor)
        {
            editor.putFloat(prefName, value);
            editor.commit();
        }
    }

    /**
     * Get data for Float
     * @param prefName Preferences name
     * @param defaultValue
     * @return Float or 0 if Name not existed
     */
    public static float getPreferences(final String prefName, final float defaultValue)
    {
        if(null != sharedPref)
        {
            return sharedPref.getFloat(prefName, defaultValue);
        }
        else
        {
            return 0;
        }

    }

    /**
     * remove data in preferences by key name
     * @param prefName Preferences name
     */
    public synchronized static void removeKey(final String prefName)
    {
        if(null != editor)
        {
            editor.remove(prefName);
            editor.commit();
        }
    }
}
