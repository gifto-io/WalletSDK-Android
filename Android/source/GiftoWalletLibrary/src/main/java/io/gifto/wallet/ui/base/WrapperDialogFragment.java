package io.gifto.wallet.ui.base;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import io.gifto.wallet.R;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.utils.ScreenUtils;

/**
 * Created by ThangPM on 9/19/15.
 */
public abstract class WrapperDialogFragment extends DialogFragment
{
    String headerTitle = "Dialog";
    String footerTitle = "";
    boolean isShowing = false;
    boolean isReady = false;
    protected boolean isCancelledOnTouchOutside = true;

    protected int width = LayoutParams.MATCH_PARENT;
    protected int height = LayoutParams.MATCH_PARENT;

    protected TextView tvTitleHeader;

    protected LinearLayout llOptionsTopLeft;

    protected LinearLayout llOptionsTopRight;

    protected TextView tvTitleFooter;

    protected LinearLayout llOptionsBottomLeft;

    protected LinearLayout llOptionsBottomRight;

    // Header
    protected RelativeLayout rlHeader;

    // Footer
    protected RelativeLayout rlFooter;

    // Main Fragments
    protected RelativeLayout mainContainer;

    private View rootView;

    public WrapperDialogFragment(String title)
    {
        this.headerTitle = title;
    }

    protected abstract void onPostViewCreated(LayoutInflater inflater);
    protected abstract BaseFragment getMainFragment();

    /**
     * Set text and color for header title
     * @param title title
     * @param color color
     */
    public void setHeaderTitle(String title, int color)
    {
        this.headerTitle = title;
        tvTitleHeader.setText(title);

        if(color != -1)
            tvTitleHeader.setTextColor(getResources().getColor(color));
    }

    /**
     * Set text and color for footer title
     * @param title title
     * @param color color
     */
    public void setFooterTitle(String title, int color)
    {
        this.footerTitle = title;
        tvTitleFooter.setText(title);

        if(color != -1)
            tvTitleFooter.setTextColor(getResources().getColor(color));
    }

    /**
     * Set visibility and background color for header
     * @param visibility visibility
     * @param id background resource color
     */
    public void setHeader(int visibility, int id)
    {
        rlHeader.setBackgroundColor(getResources().getColor(id));
        rlHeader.setVisibility(visibility);
    }

    /**
     * Set visibility and background color for footer
     * @param visibility visibility
     * @param id background resource color
     */
    public void setFooter(int visibility, int id)
    {
        rlFooter.setBackgroundColor(getResources().getColor(id));
        rlFooter.setVisibility(visibility);
    }

    @Override
    public void onStart()
    {
        // Adjust
        rlHeader.getLayoutParams().height = (int) ScreenUtils.ACTION_BAR_HEIGHT;
        rlFooter.getLayoutParams().height = (int) ScreenUtils.ACTION_BAR_HEIGHT;

        if(!ScreenUtils.isTablet())
        {
            //rootView.setBackgroundResource(R.drawable.rounded_no_corners_2);
        }
        else
        {
            tvTitleHeader.setTextSize(40f / ScreenUtils.SCREEN_DENSITY);
        }

        Window window = getDialog().getWindow();

        window.setLayout(width, height);
        //window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setBackgroundDrawableResource(R.drawable.white_rounded_background);
        window.getAttributes().windowAnimations = R.style.DialogSlideAnimation;

        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.dialog_fragment_layout, container);

        BindView(rootView);

        getDialog().setCanceledOnTouchOutside(isCancelledOnTouchOutside);
        getDialog().setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                {
                    GiftoActivity.getInstance().onBackPressed();
                    return true;
                }

                return false;
            }
        });

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        //ft.setCustomAnimations not working
        ft.replace(R.id.rl_main_container_dialog, getMainFragment(), getMainFragment().getType().getName());
        ft.addToBackStack(getMainFragment().getType().getName());
        ft.commitAllowingStateLoss();

        // Init UI
        tvTitleHeader.setText(headerTitle);

        // Adjust
        if(ScreenUtils.isTablet())
        {
            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) mainContainer.getLayoutParams();
            lParams.setMargins(0, 0, 0, 0);
            mainContainer.setLayoutParams(lParams);
            //mainContainer.setBackgroundResource(R.drawable.rounded_corners_bot_gray);
        }

        // For inheritanced class
        onPostViewCreated(inflater);

        isReady = true;

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        isShowing = false;
    }
    @Override
    public void onDetach()
    {
        super.onDetach();

        try
        {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        }
        catch(NoSuchFieldException e)
        {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

    /**
     * Initialize all view
     *
     * @param rootView root view
     */
    private void BindView(View rootView)
    {
        tvTitleHeader = (TextView) rootView.findViewById(R.id.tv_title_header);

        llOptionsTopLeft = (LinearLayout) rootView.findViewById(R.id.ll_options_top_left);

        llOptionsTopRight = (LinearLayout) rootView.findViewById(R.id.ll_options_top_right);

        tvTitleFooter = (TextView) rootView.findViewById(R.id.tv_title_footer);

        llOptionsBottomLeft = (LinearLayout) rootView.findViewById(R.id.ll_options_bottom_left);

        llOptionsBottomRight = (LinearLayout) rootView.findViewById(R.id.ll_options_bottom_right);

        // Header
        rlHeader = (RelativeLayout) rootView.findViewById(R.id.rl_header);

        // Footer
        rlFooter = (RelativeLayout) rootView.findViewById(R.id.rl_footer);

        // Main Fragments
        mainContainer = (RelativeLayout) rootView.findViewById(R.id.rl_main_container_dialog);
    }

    /**
     * Show this dialog
     *
     * @param manager fragment manager
     * @param tag tag
     */
    public void ShowDialog(FragmentManager manager, String tag)
    {
        this.show(manager, tag);
        isShowing = true;
    }

    /**
     * Dismiss this dialog
     */
    public void DismissDialog()
    {
        this.dismiss();
        isShowing = false;
    }
    public void ShowDialog()
    {
        getDialog().show();
        isShowing = true;
    }
    public void HideDialog()
    {
        getDialog().hide();
        isShowing = false;
    }

    /**
     * Check showing state of dialog
     * @return true is dialog is showing, else false
     */
    public boolean isShowing()
    {
        return isShowing;
    }

    public FragmentManager getChildFragmentManagerForDialog()
    {
        return getChildFragmentManager();
    }

    public void UpdateDialogSizeAfterOrientationChanged()
    {
        width = (int) ScreenUtils.SCREEN_WIDTH;
        height = (int) ScreenUtils.SCREEN_HEIGHT;

        getDialog().getWindow().setLayout(width, height);
    }
}
