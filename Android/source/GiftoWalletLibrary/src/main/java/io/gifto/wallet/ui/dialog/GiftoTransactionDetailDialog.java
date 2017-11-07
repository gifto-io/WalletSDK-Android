package io.gifto.wallet.ui.dialog;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;

import io.gifto.wallet.networking.models.response.GetGiftoTransactionListResponse;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.base.WrapperDialogFragment;
import io.gifto.wallet.ui.fragment.GiftoTransactionDetailFragment;
import io.gifto.wallet.utils.ScreenUtils;


/**
 * Created by thongnguyen on 8/22/17.
 *
 * A dialog to display detail of transaction
 */

public class GiftoTransactionDetailDialog extends WrapperDialogFragment
{
    private static GiftoTransactionDetailDialog instance = null;

    GiftoTransactionDetailFragment historyDetailFragment;

    public static GiftoTransactionDetailDialog getNewInstance()
    {
        instance = new GiftoTransactionDetailDialog();
        return instance;
    }
    public static GiftoTransactionDetailDialog getInstance()
    {
        return instance;
    }

    /**
     * Set transaction detail for displaying
     *
     * @param transactionDetail detail of transaction
     * @return this dialog
     */
    public GiftoTransactionDetailDialog setTransactionDetail(GetGiftoTransactionListResponse transactionDetail)
    {
        if (historyDetailFragment != null)
            historyDetailFragment.setTransactionDetail(transactionDetail);
        return this;
    }

    /**
     * Set transaction mode
     *
     * @param transactionMode transaction's mode {send, receive}
     * @return this dialog
     */
    public GiftoTransactionDetailDialog setTransactionMode(String transactionMode)
    {
        if (historyDetailFragment != null)
            historyDetailFragment.setTransactionMode(transactionMode);
        return this;
    }

    public static boolean checkShowingState()
    {
        if(instance != null)
            return instance.isShowing();

        return false;
    }

    public GiftoTransactionDetailDialog()
    {
        super("");

        historyDetailFragment = new GiftoTransactionDetailFragment();
        historyDetailFragment.setIsInDialog(true);

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
        historyDetailFragment = null;
        instance = null;

        super.onDismiss(dialog);
    }

    @Override
    protected void onPostViewCreated(LayoutInflater inflater)
    {
        //rlHeader.setVisibility(View.GONE);
        llOptionsTopLeft.setVisibility(View.GONE);
        llOptionsTopRight.setVisibility(View.GONE);
        rlFooter.setVisibility(View.GONE);
    }

    @Override
    public BaseFragment getMainFragment()
    {
        return historyDetailFragment;
    }
}