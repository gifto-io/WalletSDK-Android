package io.gifto.wallet.utils;

import android.content.Context;
import android.content.DialogInterface;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.gifto.wallet.R;
import io.gifto.wallet.utils.common.ICallback;

/**
 * Created by ThangPM on 11/17/15.
 */
public class SweetAlertDialogUtils
{
    public static SweetAlertDialog pDialog;
    private static boolean allow_dismiss = true;

    public static void ShowLoadingPopup(Context context)
    {
        if(context == null)
            return;

        if(pDialog != null && pDialog.isShowing() && pDialog.getAlerType() == SweetAlertDialog.PROGRESS_TYPE)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.edoopad_bg_red)); //#A5DC86
        pDialog.setTitleText(Utils.getStringById(context.getResources(), R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void ShowLoadingPopup(Context context, String title)
    {
        if(context == null)
            return;

        if(pDialog != null && pDialog.isShowing() && pDialog.getAlerType() == SweetAlertDialog.PROGRESS_TYPE)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.edoopad_bg_red));
        pDialog.setTitleText(title);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void ShowErrorDialog(Context context, String title, String content)
    {
        if(context == null)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        pDialog.setTitleText(title);
        pDialog.setContentText(content);
        pDialog.show();
    }

    public static void ShowErrorDialogAndDoSomething(Context context, String title, String content, final ICallback callback)
    {
        if(context == null)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        pDialog.setTitleText(title);
        pDialog.setContentText(content);
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog)
            {
                if(callback != null)
                    callback.doIt(true);
                sDialog.cancel();
            }
        });
        pDialog.show();
    }

    public static void ShowMessageDialog(Context context, String content)
    {
        if(context == null)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText(Utils.getStringById(context.getResources(), R.string.message));
        pDialog.setContentText(content);
        pDialog.show();
    }

    public static void ShowMessageDialog(Context context, String content, final ICallback callback)
    {
        if(context == null)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText(Utils.getStringById(context.getResources(), R.string.message));
        pDialog.setContentText(content);
        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (callback != null)
                    callback.doIt();
            }
        });
        pDialog.show();
    }

    public static void ShowWarningDialog(Context context, String content, final ICallback callback)
    {
        if(context == null)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);

        pDialog.setTitleText(Utils.getStringById(context.getResources(), R.string.confirm));
        pDialog.setContentText(content);
        pDialog.setCancelText(Utils.getStringById(context.getResources(), R.string.cancel));
        pDialog.setConfirmText(Utils.getStringById(context.getResources(), R.string.yes));
        pDialog.showCancelButton(true);
        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog)
            {
                if(callback != null)
                    callback.doIt(false);
                sDialog.cancel();
            }
        });
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog)
            {
                if(callback != null)
                    callback.doIt(true);
                sDialog.cancel();
            }
        });

        pDialog.show();
    }

    public static void ShowSuccessOrFailDialog(Context context, String title, String content, boolean isSuccess, final ICallback callback)
    {
        if(context == null)
            return;

        if (!DismissSweetDialog())
            return;

        if(isSuccess)
        {
            pDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        }
        else
        {
            pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        }

        pDialog.setTitleText(title);
        pDialog.setContentText(content);
        pDialog.setConfirmText(Utils.getStringById(context.getResources(), R.string.ok));
        pDialog.showCancelButton(false);
        pDialog.setCancelClickListener(null);
        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (callback != null)
                    callback.doIt(true);
            }
        });

        pDialog.show();
    }

    public static void ShowMessageWithActionButton(Context context, String content, String actionText, final ICallback callback)
    {
        if(context == null)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText(Utils.getStringById(context.getResources(), R.string.message));
        pDialog.setContentText(content);
        pDialog.setCancelText(Utils.getStringById(context.getResources(), R.string.cancel));
        pDialog.setConfirmText(actionText);
        pDialog.showCancelButton(true);
        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog)
            {
                allow_dismiss = true;
                sDialog.cancel();
                if(callback != null)
                    callback.doIt(false);
            }
        });
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog)
            {
                allow_dismiss = true;
                sDialog.cancel();
                if(callback != null)
                    callback.doIt(true);
            }
        });
        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                allow_dismiss = true;
            }
        });
        pDialog.show();
        allow_dismiss = false;
    }

    public static void ShowMessageWithActionButton(Context context, String content, String actionText1, final int actionCode1, String actionText2, final int actionCode2, boolean isCancelOnTouchOutside, final ICallback callback)
    {
        if(context == null)
            return;

        if (!DismissSweetDialog())
            return;

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText(Utils.getStringById(context.getResources(), R.string.message));
        pDialog.setContentText(content);
        pDialog.setCancelText(actionText1);
        pDialog.setConfirmText(actionText2);
        pDialog.showCancelButton(true);
        pDialog.setCanceledOnTouchOutside(isCancelOnTouchOutside);
        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog)
            {
                allow_dismiss = true;
                sDialog.cancel();
                if(callback != null)
                    callback.doIt(actionCode1);
            }
        });
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog)
            {
                allow_dismiss = true;
                sDialog.cancel();
                if(callback != null)
                    callback.doIt(actionCode2);
            }
        });
        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                allow_dismiss = true;
            }
        });
        pDialog.show();
        allow_dismiss = false;
    }

    public static boolean DismissSweetDialog()
    {
        if (!allow_dismiss)
            return false;
        if(pDialog != null)
        {
            if(pDialog.isShowing())
                pDialog.cancel();

            pDialog = null;
        }
        return true;
    }
}
