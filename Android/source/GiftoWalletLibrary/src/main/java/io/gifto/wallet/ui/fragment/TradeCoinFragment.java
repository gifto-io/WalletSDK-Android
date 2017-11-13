package io.gifto.wallet.ui.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.R;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.dialog.FingerprintAuthenticationDialogFragment;
import io.gifto.wallet.ui.dialog.PassphraseRequirePopup;
import io.gifto.wallet.ui.interfaces.OnDialogTouchOutsideListener;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.utils.PrefConstants;
import io.gifto.wallet.utils.SweetAlertDialogUtils;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.CustomSharedPreferences;
import io.gifto.wallet.utils.common.Logger;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class TradeCoinFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = "TradeCoinFragment";

    EditText etAmountEther;
    EditText etAmountGifto;
    EditText etGasLimit;
    EditText etGasPrice;
    TextView tvTransactionFee;
    RelativeLayout btnTrade;
    RelativeLayout btnNewExchange;

    RelativeLayout rlTradeCoin;
    RelativeLayout rlSuccess;

    @Override
    public FragmentType getType()
    {
        return FragmentType.TRANSFER_ROSE_COIN;
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onStart()
    {
        isInDialog = false;
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        GiftoActivity.getInstance().setTextViewKyber(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        GiftoActivity.getInstance().setTextViewKyber(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(!isInDialog)
            setHasOptionsMenu(true);
        if (GiftoActivity.getInstance() != null)
            GiftoActivity.getInstance().SetActivityTitle(getString(R.string.buy_rosecoin));

        return inflater.inflate(R.layout.fragment_trade_coin, null);
    }

    @Override
    protected void BindView(View rootView)
    {
        etAmountEther = (EditText) rootView.findViewById(R.id.et_amount_ether);
        etAmountGifto = (EditText) rootView.findViewById(R.id.et_amount_gifto);
        etGasLimit = (EditText) rootView.findViewById(R.id.et_gas_limit);
        etGasPrice = (EditText) rootView.findViewById(R.id.et_gas_price);
        tvTransactionFee = (TextView) rootView.findViewById(R.id.tv_transaction_fee);
        btnTrade = (RelativeLayout) rootView.findViewById(R.id.btn_trade);
        btnNewExchange = (RelativeLayout) rootView.findViewById(R.id.btn_new_exchange);
        rlTradeCoin = (RelativeLayout) rootView.findViewById(R.id.rl_trade_coin);
        rlSuccess = (RelativeLayout) rootView.findViewById(R.id.rl_success);
    }

    @Override
    protected void ViewInjectionSuccess()
    {
        etAmountEther.addTextChangedListener(new MyTextChangeListener()
        {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

                calculateAmountGifto(s.toString());
            }
        });

        etGasLimit.addTextChangedListener(new MyTextChangeListener()
        {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

                calculateTransactionFee(s.toString(), etGasPrice.getText().toString());
            }
        });

        etGasPrice.addTextChangedListener(new MyTextChangeListener()
        {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

                calculateTransactionFee(etGasLimit.getText().toString(), s.toString());
            }
        });

        etAmountEther.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    if (etAmountEther.getText().toString().equals("0"))
                        etAmountEther.setText("");
                }
                else
                {
                    if (etAmountEther.getText().toString().equals(""))
                        etAmountEther.setText("0");
                }
            }
        });

        btnTrade.setOnClickListener(this);
        btnNewExchange.setOnClickListener(this);

        switchToTradeMode();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.btn_trade)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                DoTradeAPI23();
            else DoTradeAPI21();
        }
        else if (id == R.id.btn_new_exchange)
        {
            switchToTradeMode();
        }
    }

    private void switchToTradeMode()
    {
        GiftoActivity.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                rlTradeCoin.setVisibility(View.VISIBLE);
                rlSuccess.setVisibility(View.GONE);

                etAmountEther.setText("0");
                etAmountGifto.setText("0");

                etGasLimit.setText("20000");
                etGasPrice.setText("100");
            }
        });
    }

    private void success()
    {
        GiftoActivity.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                rlTradeCoin.setVisibility(View.GONE);
                rlSuccess.setVisibility(View.VISIBLE);
                SweetAlertDialogUtils.DismissSweetDialog();
            }
        });
    }

    private void calculateAmountGifto(String amountEther)
    {
        try {
            double ether = Double.valueOf(amountEther);
            double gifto = ether * 3000;
            final String amountGifto = String.valueOf(gifto);

            GiftoActivity.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    etAmountGifto.setText(amountGifto);
                }
            });
        }
        catch (Exception e)
        {
            Logger.e(TAG, "Parse amount error: " + e.getMessage());
        }
    }

    private void calculateTransactionFee(String gasLimit, String gasPrice)
    {
        try {
            double limit = Double.valueOf(gasLimit);
            double price = Double.valueOf(gasPrice);
            double fee = limit * price / Math.pow(10, 9);
            final String transactionFee = Utils.FormatAmount(fee);
            GiftoActivity.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    tvTransactionFee.setText(transactionFee + " ETH");
                }
            });
        }
        catch (Exception e)
        {
            Logger.e(TAG, "Parse amount error: " + e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void DoTradeAPI21()
    {
        if (GiftoWalletManager.isUsingStorePassphrase()) {
            String password = GiftoWalletManager.getUserSecurePassphrase();

            if (!Utils.isStringValid(password)) {
                final PassphraseRequirePopup passphraseRequirePopup = new PassphraseRequirePopup(GiftoActivity.getInstance());
                passphraseRequirePopup.setOnDialogTouchOutsideListener(new OnDialogTouchOutsideListener() {
                    @Override
                    public void onDialogTouchOutside() {
                        passphraseRequirePopup.dismiss();
                    }
                });
                passphraseRequirePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (Utils.isStringValid(PassphraseRequirePopup.passpharse)) {
                            success();
                        }
                    }
                });
                passphraseRequirePopup.setAnimationStyle(R.style.GiftoFadePopupAnimation);
                passphraseRequirePopup.show(btnTrade);
                return;
            }
            else success();
        }
        else success();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void DoTradeAPI23()
    {
        if (GiftoWalletManager.isUsingStorePassphrase())
        {
            if (!Utils.isStringValid(CustomSharedPreferences.getPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "")) || CustomSharedPreferences.getPreferences(PrefConstants.PREF_USE_FINGERPRINT, false))
            {
                FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment();
                fragment.setCallback(new FingerprintAuthenticationDialogFragment.Callback() {
                    @Override
                    public void onPasswordAuthenticated(String passphrase, FingerprintAuthenticationDialogFragment.PassphraseSource source) {
                        success();
                    }

                    @Override
                    public void onCanceled() {
                    }
                });
                fragment.show(getActivity().getFragmentManager(), "FingerprintAuthenticationDialogFragment");
            }
            else
            {
                success();
            }
        }
        else success();
    }

    class MyTextChangeListener implements TextWatcher
    {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
