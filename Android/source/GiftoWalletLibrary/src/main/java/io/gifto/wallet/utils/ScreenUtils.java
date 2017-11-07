package io.gifto.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by ThangPM on 9/19/15.
 */
public class ScreenUtils
{
    public static int PORTRAIT  = 0;
    public static int LANDSCAPE = 1;

    public static float SCREEN_DENSITY = 1;
    public static float SCREEN_HEIGHT = 0;
    public static float SCREEN_WIDTH = 0;
    public static float ACTION_BAR_HEIGHT = 0;
    public static float STATUS_BAR_HEIGHT = 0;

    public static int CELL_HEIGHT_LISTVIEW = 0;
    public static int LINE_HEIGHT_LISTVIEW = 2;

    public static Typeface light_font;
    public static Typeface regular_font;
    public static Typeface semibold_font;

    public static boolean isTablet = false;
    public static boolean isTablet()
    {
        //!important
        return isTablet;
    }

    /**
     * Initialize screen's params
     *
     * @param context context
     * @param resources resource
     * @param isTablet true if device is tablet
     *                 false if phone
     */
    public static void InitialScreenParams(Context context, Resources resources, boolean isTablet)
    {
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        SCREEN_WIDTH = (float) (size.x);
        SCREEN_HEIGHT = (float) (size.y);
        SCREEN_DENSITY = resources.getDisplayMetrics().density * 1920f / ScreenUtils.SCREEN_HEIGHT;

        int cellHeight = (int) (0.08f * SCREEN_HEIGHT);
        if(isTablet)
            cellHeight = (int) (0.055f * SCREEN_HEIGHT);

        CELL_HEIGHT_LISTVIEW = cellHeight;

        // UPDATE REAL ACTIONBAR
        if(ACTION_BAR_HEIGHT == 0)
        {
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if(context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                ACTION_BAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, resources.getDisplayMetrics());
            }
        }

        // UPDATE REAL STATUSBAR
        if(STATUS_BAR_HEIGHT == 0)
        {
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if(resourceId > 0)
            {
                STATUS_BAR_HEIGHT = resources.getDimensionPixelSize(resourceId);
            }
        }

        ScreenUtils.isTablet = isTablet;
    }

    /**
     * Calculate screen's size after rotate orientation
     *
     * @param context context
     */
    public static void CalculateScreenSizeAfterOrientationChanged(Context context)
    {
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        SCREEN_WIDTH = (float) (size.x);
        SCREEN_HEIGHT = (float) (size.y);
    }
}
