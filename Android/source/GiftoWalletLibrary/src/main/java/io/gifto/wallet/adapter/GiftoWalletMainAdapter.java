package io.gifto.wallet.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import io.gifto.wallet.R;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.fragment.GiftoWalletFragment;
import io.gifto.wallet.ui.fragment.TransferGiftoHistoryFragment;
import io.gifto.wallet.utils.Constants;

/**
 * Created by ThongNguyen on 8/21/17.
 *
 * An adapter for displaying three main screen: Home, Sent Records and Received Records
 */
public class GiftoWalletMainAdapter extends FragmentStatePagerAdapter
{
    private static final String TAG = GiftoWalletMainAdapter.class.getName();

    private Context mContext;
    private SparseArray<BaseFragment> registeredFragments = new SparseArray<BaseFragment>();

    private boolean isInDialog = false;

    public void setInDialog(boolean inDialog) {
        isInDialog = inDialog;
    }

    public GiftoWalletMainAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        this.mContext = context;
    }

    /**
     * Get Fragment by position
     *
     * @param position position of page
     * @return Fragment of each page
     */
    public BaseFragment getItem(int position)
    {
        if(getRegisteredFragment(position) != null)
            return getRegisteredFragment(position);

        BaseFragment selectedFragment = null;

        switch (position)
        {
            case 0:
                selectedFragment = new GiftoWalletFragment();
                break;
            case 1:
                selectedFragment = new TransferGiftoHistoryFragment();
                ((TransferGiftoHistoryFragment) selectedFragment).setTransactionMode(Constants.TRANSACTION_MODE_SENDING);
                break;
            case 2:
                selectedFragment = new TransferGiftoHistoryFragment();
                ((TransferGiftoHistoryFragment) selectedFragment).setTransactionMode(Constants.TRANSACTION_MODE_RECEIVING);
                break;
            default:
                break;
        }

        return selectedFragment;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String pageTitle = "";
        switch (position)
        {
            case 0:
                pageTitle = mContext.getString(R.string.home).toUpperCase();
                break;
            case 1:
                pageTitle = mContext.getString(R.string.sent_record).toUpperCase();
                break;
            case 2:
                pageTitle = mContext.getString(R.string.received_record).toUpperCase();
                break;
            default:
                break;
        }

        return pageTitle;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseFragment baseFragment = (BaseFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, baseFragment);
        return baseFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    /**
     * Get registered fragment by position
     *
     * @param position position of page
     * @return Fragment of at position #position
     */
    private BaseFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}