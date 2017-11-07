package io.gifto.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by thongnguyen on 9/7/17.
 */

public class Utils {
    public static int DpToPx(Context context, float dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static boolean isStringValid(String str)
    {
        if(str != null && !str.equals(""))
            return true;

        return false;
    }

    public static String getStringById(Resources resources, int id)
    {
        String result = resources.getString(id);
        return result;
    }

    public static String getFormatDate(boolean isShowWeekDay)
    {
        Locale locale = Locale.getDefault();

        String formatDate = "";
        if(locale.equals(Locale.JAPAN) || locale.equals(Locale.JAPANESE))
        {
            if(isShowWeekDay)
                formatDate = "yyyy年MM月dd日,EEEE";
            else
                formatDate = "yyyy年MM月dd日";
        }
        else
        {
            if(isShowWeekDay)
                formatDate = "EEE, MMMM dd, yyyy";
            else
                formatDate = "MMM dd, yyyy";
        }

        return formatDate;
    }

    public static Bitmap GenerateQRCode(String content, int width, int height, int color)
    {
        if (!Utils.isStringValid(content))
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        try {
            //generating qr code.
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap =new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            BitMatrix matrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, width, height, hintMap);
            //converting bitmatrix to bitmap

            width = matrix.getWidth();
            height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //for black and white
                    //pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                    //for custom color
                    pixels[offset + x] = matrix.get(x, y) ? color : Color.WHITE;
                }
            }
            //creating bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            return bitmap;

            //getting the logo
//            Bitmap overlay = Bitmap.createBitmap(width / 5, height / 5, Bitmap.Config.ARGB_8888);
//            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            paint.setColor(Color.WHITE);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setStrokeWidth(1);
//            Canvas canvas = new Canvas(overlay);
//            canvas.drawCircle(overlay.getWidth() / 2, overlay.getHeight() / 2, overlay.getWidth() / 2, paint);
//
//            //setting bitmap to image view
//            return mergeBitmaps(overlay,bitmap);

        }catch (Exception er){
            Log.e("QrGenerate",er.getMessage());
        }
        return null;
    }

    public static String MD5_Hash(final String s)
    {
        final String MD5 = "MD5";
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for(byte aMessageDigest : messageDigest)
            {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while(h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String FormatAmount(String amount)
    {
        double a = Double.valueOf(amount);
        return new DecimalFormat("#.#####", new DecimalFormatSymbols(Locale.US)).format(a);
    }

    public static String FormatAmount(double amount)
    {
        return new DecimalFormat("#.#####", new DecimalFormatSymbols(Locale.US)).format(amount);
    }

    public static void forceShowSoftKeyboard(Context context, View view)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void forceHideSoftKeyboard(Activity contextActivity)
    {
        InputMethodManager inputManager = (InputMethodManager) contextActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = contextActivity.getCurrentFocus();
        if(v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void setUpForceHideSoftKeyboardWhenTouchOutsideEditText(final Activity activity, View rootView) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(rootView instanceof EditText)) {
            rootView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    forceHideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (rootView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                View innerView = ((ViewGroup) rootView).getChildAt(i);
                setUpForceHideSoftKeyboardWhenTouchOutsideEditText(activity, innerView);
            }
        }
    }

    public static void DisplayImage(Context context, String uri, ImageView imageAware, int width, int height)
    {
        if (!Utils.isStringValid(uri))
            return;

        Picasso.with(context).cancelRequest(imageAware);
        Picasso.with(context)
                .load(uri)
                .noPlaceholder()
                .resize(width, height) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .into(imageAware);
    }
}
