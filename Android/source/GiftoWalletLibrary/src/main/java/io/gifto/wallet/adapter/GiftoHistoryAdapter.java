package io.gifto.wallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.gifto.wallet.R;
import io.gifto.wallet.model.TransactionStatus;
import io.gifto.wallet.networking.models.response.GetGiftoTransactionListResponse;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.Utils;

/**
 * Created by ThongNguyen on 8/21/17.
 */
public class GiftoHistoryAdapter extends BaseAdapter
{
    private static final String TAG = GiftoHistoryAdapter.class.getName();

    static class ViewHolder
    {
        TextView tvCreateTime;

        TextView tvNote;

        TextView tvAmount;

        ImageView ivStatus;

        public ViewHolder(View view)
        {
            tvCreateTime = (TextView) view.findViewById(R.id.tv_create_time);
            tvNote = (TextView) view.findViewById(R.id.tv_note);
            tvAmount = (TextView) view.findViewById(R.id.tv_amount);
            ivStatus = (ImageView) view.findViewById(R.id.iv_status);
        }
    }

    private final LayoutInflater mLayoutInflater;
    private Context mContext;

    private List<GetGiftoTransactionListResponse> mGiftoTransactionListResponses;
    private String transactionMode = Constants.TRANSACTION_MODE_SENDING;

    /**
     * Constructor
     *
     * @param context Context
     * @param transactionMode transaction mode: all/send/receive
     * @param transactionListResponses list of transactions
     */
    public GiftoHistoryAdapter(@NonNull Context context, String transactionMode, List<GetGiftoTransactionListResponse> transactionListResponses)
    {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.transactionMode = transactionMode;

        UpdateTransactionList(transactionListResponses);
    }

    /**
     * Update transaction data
     *
     * @param transactionListResponses list of transactions
     */
    public void UpdateTransactionList(List<GetGiftoTransactionListResponse> transactionListResponses)
    {
        mGiftoTransactionListResponses = transactionListResponses;
        if (mGiftoTransactionListResponses == null)
            mGiftoTransactionListResponses = new ArrayList<>();
    }

    @Override
    public int getCount()
    {
        return mGiftoTransactionListResponses.size();
    }

    @Override
    public Object getItem(int position)
    {
        if(mGiftoTransactionListResponses == null || mGiftoTransactionListResponses.get(position) == null)
            return null;

        return mGiftoTransactionListResponses.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        GetGiftoTransactionListResponse creditTransaction = (GetGiftoTransactionListResponse) getItem(position);

        if (creditTransaction != null)
        {
            ViewHolder vHolder;

            convertView = mLayoutInflater.inflate(R.layout.cell_rosecoin_history_item, parent, false);
            vHolder = new ViewHolder(convertView);

            if(Utils.isStringValid(creditTransaction.getCreatedAt()))
            {
                long dateTimeInMiliSecond = Long.parseLong(creditTransaction.getCreatedAt());
                String dateString = DateFormat.format(Utils.getFormatDate(false), new Date(dateTimeInMiliSecond)).toString();

                vHolder.tvCreateTime.setText(dateString);
            }
            else vHolder.tvCreateTime.setText("#");

            if (Utils.isStringValid(creditTransaction.getStatus()))
            {
                if (TransactionStatus.SUCCESS.getName().equals(creditTransaction.getStatus()))
                    vHolder.ivStatus.setImageResource(R.drawable.success);
                else if (TransactionStatus.FAILED.getName().equals(creditTransaction.getStatus()))
                    vHolder.ivStatus.setImageResource(R.drawable.failed);
                else if (TransactionStatus.PROCESSING.getName().equals(creditTransaction.getStatus()))
                {
                    vHolder.ivStatus.setImageResource(R.drawable.failed);
                    vHolder.ivStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.processing_color));
                }
            }
            else vHolder.ivStatus.setVisibility(View.GONE);

            String type = creditTransaction.getTransactionType();
            if (Constants.TRANSACTION_TYPE_TIP.equals(type))
                setUpGUIForTip(vHolder, creditTransaction);
            else if (Constants.TRANSACTION_TYPE_MOVE.equals(type) || Constants.TRANSACTION_TYPE_UPDATE.equals(type))
                setUpGUIForMove(vHolder, creditTransaction);
            else setUpGUIForTransfer(vHolder, creditTransaction);
        }
        return convertView;
    }

    /**
     * Display GUI for Transferring Transactions
     *
     * @param vHolder Holder of GUI
     * @param creditTransaction Detail of transaction
     */
    private void setUpGUIForTransfer(ViewHolder vHolder, GetGiftoTransactionListResponse creditTransaction)
    {
        vHolder.tvNote.setText(Utils.isStringValid(creditTransaction.getNote()) ? creditTransaction.getNote() : mContext.getString(R.string.no_note));

        if (transactionMode.equals(Constants.TRANSACTION_MODE_SENDING)) {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_red));
            vHolder.tvAmount.setText("-" + creditTransaction.getAmount());
        } else if (transactionMode.equals(Constants.TRANSACTION_MODE_RECEIVING)) {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_blue));
            vHolder.tvAmount.setText("+" + creditTransaction.getAmount());
        } else {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_green));
            vHolder.tvAmount.setText(creditTransaction.getAmount());
        }
    }

    /**
     * Display GUI for Updating Transactions
     *
     * @param vHolder Holder of GUI
     * @param creditTransaction Detail of transaction
     */
    private void setUpGUIForMove(ViewHolder vHolder, GetGiftoTransactionListResponse creditTransaction)
    {
        vHolder.tvNote.setText(mContext.getString(R.string.refresh_rosecoin_on_blockchain));

        if (transactionMode.equals(Constants.TRANSACTION_MODE_SENDING)) {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_red));
            vHolder.tvAmount.setText("-" + creditTransaction.getAmount());
        } else if (transactionMode.equals(Constants.TRANSACTION_MODE_RECEIVING)) {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_blue));
            vHolder.tvAmount.setText("+" + creditTransaction.getAmount());
        } else {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_green));
            vHolder.tvAmount.setText(creditTransaction.getAmount());
        }
    }

    /**
     * Display GUI for Tipping Transactions
     *
     * @param vHolder Holder of GUI
     * @param creditTransaction Detail of transaction
     */
    private void setUpGUIForTip(ViewHolder vHolder, GetGiftoTransactionListResponse creditTransaction)
    {
        vHolder.tvNote.setText(mContext.getString(R.string.tipping));

        if (transactionMode.equals(Constants.TRANSACTION_MODE_SENDING)) {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_red));
            vHolder.tvAmount.setText("-" + creditTransaction.getAmount());
        } else if (transactionMode.equals(Constants.TRANSACTION_MODE_RECEIVING)) {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_blue));
            vHolder.tvAmount.setText("+" + creditTransaction.getAmount());
        } else {
            vHolder.tvAmount.setTextColor(ContextCompat.getColor(mContext, R.color.whispers_green));
            vHolder.tvAmount.setText(creditTransaction.getAmount());
        }
    }
}