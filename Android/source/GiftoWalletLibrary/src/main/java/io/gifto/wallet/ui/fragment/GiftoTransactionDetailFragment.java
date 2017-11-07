package io.gifto.wallet.ui.fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import io.gifto.wallet.R;
import io.gifto.wallet.model.TransactionUserInfo;
import io.gifto.wallet.networking.models.response.GetGiftoTransactionListResponse;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.dialog.GiftoTransactionDetailDialog;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.Utils;

/**
 * Created by thongnguyen on 8/22/17.
 *
 * Displaying detail of transaction
 */

public class GiftoTransactionDetailFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = "GiftoTransactionDetailFragment";

    TextView tvTransactionId;

    TextView tvCreateTime;

    TextView tvFromToLabel;

    TextView tvFromTo;

    TextView tvAmount;

    TextView tvNote;

    ImageView btnClose;

    LinearLayout llFromTo;

    LinearLayout llTransactionFee;
    TextView tvTransactionFee;

    private GetGiftoTransactionListResponse transactionDetail;
    private String transactionMode = Constants.TRANSACTION_MODE_SENDING;

    /**
     * Set transaction's detail
     *
     * @param transactionDetail detail of transaction
     */
    public void setTransactionDetail(GetGiftoTransactionListResponse transactionDetail) {
        this.transactionDetail = transactionDetail;
    }

    /**
     * Set transaction's mode
     *
     * @param transactionMode transaction's mode {send, receive}
     */
    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    @Override
    public FragmentType getType()
    {
        return FragmentType.HISTORY_DETAIL;
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onStart()
    {
        isInDialog = true;
        super.onStart();
    }

    @Override
    protected View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(!isInDialog)
            setHasOptionsMenu(true);

        // Change Header GUIif(HistoryDetailDialog.getInstance() != null)
        if(GiftoTransactionDetailDialog.getInstance() != null) {
            GiftoTransactionDetailDialog.getInstance().setHeader(View.GONE, R.color.edoopad_white);
            GiftoTransactionDetailDialog.getInstance().setFooter(View.GONE, R.color.edoopad_white);
        }

        return inflater.inflate(R.layout.fragment_rosecoin_transaction_detail, null);
    }

    @Override
    protected void BindView(View rootView)
    {
        tvTransactionId = (TextView) rootView.findViewById(R.id.tv_transaction_id);

        tvCreateTime = (TextView) rootView.findViewById(R.id.tv_create_time);

        tvFromToLabel = (TextView) rootView.findViewById(R.id.tv_from_to_label);

        tvFromTo = (TextView) rootView.findViewById(R.id.tv_from_to);

        tvAmount = (TextView) rootView.findViewById(R.id.tv_amount);

        tvNote = (TextView) rootView.findViewById(R.id.tv_note);

        btnClose = (ImageView) rootView.findViewById(R.id.btn_close);

        llFromTo = (LinearLayout) rootView.findViewById(R.id.ll_from_to);

        llTransactionFee = (LinearLayout) rootView.findViewById(R.id.ll_transaction_fee);

        tvTransactionFee = (TextView) rootView.findViewById(R.id.tv_transaction_fee);
    }

    @Override
    protected void ViewInjectionSuccess()
    {
        tvTransactionId.setText(transactionDetail.getTransactionId());
        if(Utils.isStringValid(transactionDetail.getCreatedAt()))
        {
            long dateTimeInMiliSecond = Long.parseLong(transactionDetail.getCreatedAt());
            String dateString = DateFormat.format(Utils.getFormatDate(false), new Date(dateTimeInMiliSecond)).toString();

            tvCreateTime.setText(dateString);
        }
        else tvCreateTime.setText("#");
        tvAmount.setText(transactionDetail.getAmount());

        String type = transactionDetail.getTransactionType();
        if (Constants.TRANSACTION_TYPE_TIP.equals(type))
            setUpGUIForTip();
        else if (Constants.TRANSACTION_TYPE_MOVE.equals(type) || Constants.TRANSACTION_TYPE_UPDATE.equals(type))
            setUpGUIForMove();
        else setUpGUIForTransfer();

        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.btn_close)
        {
            GiftoActivity.getInstance().onBackPressed();
        }
    }

    /**
     * Display transaction detail for transferring transaction
     */
    private void setUpGUIForTransfer()
    {
        tvNote.setText(Utils.isStringValid(transactionDetail.getNote())? transactionDetail.getNote() : getString(R.string.no_note));
        if (transactionMode.equals(Constants.TRANSACTION_MODE_SENDING))
        {
            tvFromToLabel.setText(getString(R.string.to));
            TransactionUserInfo info = transactionDetail.getTo();
            if (Utils.isStringValid(info.getName()))
                tvFromTo.setText(info.getName());
            else tvFromTo.setText(info.getIdentityData());
            tvTransactionFee.setText(transactionDetail.getTransferFee());
        }
        else
        {
            tvFromToLabel.setText(getString(R.string.from));
            TransactionUserInfo info = transactionDetail.getFrom();
            if (Utils.isStringValid(info.getName()))
                tvFromTo.setText(info.getName());
            else tvFromTo.setText(info.getIdentityData());
            llTransactionFee.setVisibility(View.GONE);
        }
    }

    /**
     * Display transaction detail for updating transaction
     */
    private void setUpGUIForMove()
    {
        tvNote.setText(getString(R.string.refresh_rosecoin_on_blockchain));
        llFromTo.setVisibility(View.GONE);
        tvTransactionFee.setText(transactionDetail.getTransferFee());
    }

    /**
     * Display transaction detail for tipping transaction
     */
    private void setUpGUIForTip()
    {
        tvNote.setText(getString(R.string.tipping));
        if (transactionMode.equals(Constants.TRANSACTION_MODE_SENDING))
        {
            tvFromToLabel.setText(getString(R.string.to));
            TransactionUserInfo info = transactionDetail.getTo();
            if (Utils.isStringValid(info.getName()))
                tvFromTo.setText(info.getName());
            else tvFromTo.setText(info.getIdentityData());
            tvTransactionFee.setText(transactionDetail.getTransferFee());
        }
        else
        {
            tvFromToLabel.setText(getString(R.string.from));
            TransactionUserInfo info = transactionDetail.getFrom();
            if (Utils.isStringValid(info.getName()))
                tvFromTo.setText(info.getName());
            else tvFromTo.setText(info.getIdentityData());
            llTransactionFee.setVisibility(View.GONE);
        }
    }
}
