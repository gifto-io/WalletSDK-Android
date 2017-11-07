package io.gifto.wallet.adapter;

import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.gifto.wallet.R;
import io.gifto.wallet.model.WalletBalanceDetail;
import io.gifto.wallet.model.WalletCurrency;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.ICallback;

/**
 * Created by ThongNguyen on 10/14/17.
 *
 * An adapter for displaying wallet's balance
 */
public class WalletBalanceListAdapter extends RecyclerView.Adapter<WalletBalanceListAdapter.ViewHolder>
{
    public static final String TAG = "WalletBalanceListAdapter";

    private List<WalletBalanceDetail> walletBalanceDetailList;
    private ICallback onItemClickListener;

    private long lastClick = 0;

    /**
     * Holder of GUI
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        TextView tvLabel;
        TextView tvBalance;
        ImageView icon;

        public ViewHolder(View view)
        {
            super(view);
            rootView = view;
            tvLabel = (TextView) view.findViewById(R.id.tv_rosecoin_label);
            tvBalance = (TextView) view.findViewById(R.id.tv_balance);
            icon = (ImageView) view.findViewById(R.id.icon_balance);
        }
    }

    public WalletBalanceListAdapter(List<WalletBalanceDetail> walletBalanceDetailList) {
        this.walletBalanceDetailList = walletBalanceDetailList;
        if(this.walletBalanceDetailList == null)
            this.walletBalanceDetailList = new ArrayList<>();
    }

    /**
     * Set data for adapter
     *
     * @param walletBalanceDetailList A list of wallet detail
     */
    public void setWalletBalanceDetailList(List<WalletBalanceDetail> walletBalanceDetailList)
    {
        this.walletBalanceDetailList = walletBalanceDetailList;
        if(this.walletBalanceDetailList == null)
            this.walletBalanceDetailList = new ArrayList<>();

        notifyDataSetChanged();
    }

    /**
     * Set callback for item click listener
     *
     * @param onItemClickListener callback
     */
    public void setOnItemClickListener(ICallback onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_wallet_balance, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        final WalletBalanceDetail walletBalanceDetail = getItem(position);
        if (walletBalanceDetail != null)
        {
            double total = 0;
            try {
                total = Double.parseDouble(walletBalanceDetail.getOnChanined()) + Double.parseDouble(walletBalanceDetail.getOffChanined());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            holder.tvBalance.setText(Utils.FormatAmount(total));
            holder.tvLabel.setText(holder.rootView.getContext().getString(R.string.coin_balance).replace("%s", walletBalanceDetail.getCurrencyName()));
            holder.icon.setImageResource(WalletCurrency.getIconResourceByCode(walletBalanceDetail.getCurrencyCode()));
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    synchronized (this)
                    {
                        if (SystemClock.elapsedRealtime() - lastClick < 1000)
                            return;
                        lastClick = SystemClock.elapsedRealtime();
                    }

                    if (onItemClickListener != null)
                        onItemClickListener.doIt(walletBalanceDetail);
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        if (Constants.DISPLAY_MULTIPLE_COIN)
            return walletBalanceDetailList.size();
        else
        {
            for (int i = 0; i < walletBalanceDetailList.size(); ++i)
                if (walletBalanceDetailList.get(i).getCurrencyCode().equals(Constants.CURRENCY_CODE))
                    return 1;
        }
        return 0;
    }

    /**
     * Get Wallet's balance detail by position
     *
     * @param position position
     * @return Wallet's balance detail at position #position
     */
    private WalletBalanceDetail getItem(int position)
    {
        if (Constants.DISPLAY_MULTIPLE_COIN)
            return walletBalanceDetailList.get(position);
        else
        {
            for (int i = 0; i < walletBalanceDetailList.size(); ++i)
                if (walletBalanceDetailList.get(i).getCurrencyCode().equals(Constants.CURRENCY_CODE))
                    return walletBalanceDetailList.get(i);
        }
        return null;
    }
}