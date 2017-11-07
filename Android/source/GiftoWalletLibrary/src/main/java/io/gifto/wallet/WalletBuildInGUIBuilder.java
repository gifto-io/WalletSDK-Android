package io.gifto.wallet;

import android.content.Context;
import android.content.Intent;

import io.gifto.wallet.ui.activity.GiftoActivity;

/**
 * Created by thongnguyen on 10/17/17.
 */

public class WalletBuildInGUIBuilder {

    private Context mContext;
    private Intent intent;


    private WalletBuildInGUIBuilder(Context context)
    {
        this.mContext = context;
        if (mContext != null)
            intent = new Intent(mContext, GiftoActivity.class);
    }

    /**
     * Create a builder to start Wallet GUI
     *
     * @param context Context
     * @return A builder instance
     */
    public static WalletBuildInGUIBuilder with(Context context)
    {
        GiftoActivity.onBuyCoinClickListener = null;
        return new WalletBuildInGUIBuilder(context);
    }

    /**
     * Set user's avatar. It is used for displaying in wallet address's QR Code
     *
     * @param avatar An URL of image
     * @return Builder instance
     */
    public WalletBuildInGUIBuilder setUserAvatar(String avatar)
    {
        intent.putExtra(GiftoActivity.EXTRA_AVATAR, avatar);
        return this;
    }

    /**
     * Start Wallet GUI
     */
    public void start()
    {
        if (mContext == null)
            throw new NullPointerException("Context must not be NULL");

        mContext.startActivity(intent);
    }
}
