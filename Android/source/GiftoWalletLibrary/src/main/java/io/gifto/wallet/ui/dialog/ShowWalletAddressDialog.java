package io.gifto.wallet.ui.dialog;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.gifto.wallet.R;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.base.WrapperDialogFragment;
import io.gifto.wallet.ui.fragment.ShowWalletAddressFragment;
import io.gifto.wallet.utils.ScreenUtils;
import io.gifto.wallet.utils.common.ICallback;


/**
 * Created by thongnguyen on 8/4/17.
 *
 * A dialog for showing wallet's address
 */

public class ShowWalletAddressDialog extends WrapperDialogFragment
{
    private static ShowWalletAddressDialog instance = null;
    ShowWalletAddressFragment showWalletAddressFragment;
    private ImageView btnClose;

    public static ShowWalletAddressDialog getNewInstance()
    {
        instance = new ShowWalletAddressDialog();
        return instance;
    }
    public static ShowWalletAddressDialog getInstance()
    {
        return instance;
    }

    /**
     * Set wallet's address
     *
     * @param walletAddress wallet's address
     * @return this dialog
     */
    public ShowWalletAddressDialog setWalletAddress(String walletAddress)
    {
        if (showWalletAddressFragment != null)
            showWalletAddressFragment.setWalletAddress(walletAddress);
        return this;
    }

    /**
     * Set QRCode of wallet's address
     *
     * @param qrCode qrcode as bitmap
     * @return this dialog
     */
    public ShowWalletAddressDialog setWalletAddressQRCode(Bitmap qrCode)
    {
        if (showWalletAddressFragment != null)
            showWalletAddressFragment.setQrCode(qrCode);
        return this;
    }

    /**
     * Remove all button in header
     */
    public void RemoveAllButtons()
    {
        if(llOptionsTopLeft != null)
            llOptionsTopLeft.removeAllViews();

        if(llOptionsTopRight != null)
            llOptionsTopRight.removeAllViews();
    }

    /**
     * Add close button
     *
     * @param callback callback to run when close button is clicked
     */
    public void AddCloseButton(final ICallback callback)
    {
        btnClose = new ImageView(getContext());
        btnClose.setImageResource(R.drawable.close);
        btnClose.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        btnClose.setBackgroundResource(R.drawable.btn_white_grey_background_selector);
        btnClose.setColorFilter(ContextCompat.getColor(GiftoActivity.getInstance(), R.color.black));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.doIt();
            }
        });

        llOptionsTopRight.addView(btnClose);
        llOptionsTopRight.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnClose.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        btnClose.setLayoutParams(params);

        // Adjust
        btnClose.getLayoutParams().width = (int) (ScreenUtils.ACTION_BAR_HEIGHT);
        btnClose.getLayoutParams().height = (int) (ScreenUtils.ACTION_BAR_HEIGHT);
    }

    public ShowWalletAddressDialog()
    {
        super("");

        showWalletAddressFragment = new ShowWalletAddressFragment();

        isCancelledOnTouchOutside = true;

        width = (int) ScreenUtils.SCREEN_WIDTH;
        height = (int) ScreenUtils.SCREEN_HEIGHT;
    }

    public void ShowDialog(FragmentManager manager)
    {
        ShowDialog(manager, "");
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        showWalletAddressFragment = null;
        instance = null;

        super.onDismiss(dialog);
    }

    @Override
    protected void onPostViewCreated(LayoutInflater inflater)
    {
        llOptionsTopLeft.setVisibility(View.GONE);
        llOptionsTopRight.setVisibility(View.GONE);

        rlFooter.setVisibility(View.GONE);
        llOptionsBottomLeft.setVisibility(View.GONE);
        llOptionsBottomRight.setVisibility(View.GONE);
    }

    @Override
    protected BaseFragment getMainFragment()
    {
        return showWalletAddressFragment;
    }
}
