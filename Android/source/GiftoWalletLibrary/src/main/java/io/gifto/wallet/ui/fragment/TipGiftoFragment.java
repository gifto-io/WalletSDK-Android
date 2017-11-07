package io.gifto.wallet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import de.greenrobot.event.EventBus;
import io.gifto.wallet.R;
import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.event.OnRefreshGiftoTransactionEvent;
import io.gifto.wallet.networking.RestClient;
import io.gifto.wallet.networking.WSConstants;
import io.gifto.wallet.networking.WalletApiResponseCallback;
import io.gifto.wallet.networking.models.request.TipGiftoRequest;
import io.gifto.wallet.networking.models.response.NoResponse;
import io.gifto.wallet.networking.models.response.WalletApiError;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.dialog.PassphraseRequirePopup;
import io.gifto.wallet.ui.interfaces.OnDialogTouchOutsideListener;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.SweetAlertDialogUtils;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.Logger;
import io.gifto.wallet.utils.common.MyHandler;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class TipGiftoFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = "TipGiftoFragment";

    EditText etTo;

    EditText etAmount;

    EditText etPassword;

    RelativeLayout btnTransfer;

    RelativeLayout rlPassword;

    private String toWalletAddress; // identity data
    private String amountHex;
    private String password;

    private boolean isTransferComplete;

    private MyHandler myHandler;

    private Runnable handleAfterTransferTask;

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
    public void onDestroy() {
        if (myHandler != null && handleAfterTransferTask != null)
            myHandler.removeCallbacks(handleAfterTransferTask);
        super.onDestroy();
    }

    @Override
    protected View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(!isInDialog)
            setHasOptionsMenu(true);
        if (GiftoActivity.getInstance() != null)
            GiftoActivity.getInstance().SetActivityTitle("Tip Gifto");

        return inflater.inflate(R.layout.fragment_tip_coin, null);
    }

    @Override
    protected void BindView(View rootView)
    {
        etTo = (EditText) rootView.findViewById(R.id.et_to);

        etAmount = (EditText) rootView.findViewById(R.id.et_amount);

        etPassword = (EditText) rootView.findViewById(R.id.et_password);

        btnTransfer = (RelativeLayout) rootView.findViewById(R.id.btn_transfer);

        rlPassword = (RelativeLayout) rootView.findViewById(R.id.rl_password);

    }

    @Override
    protected void ViewInjectionSuccess()
    {
        myHandler = new MyHandler(GiftoActivity.getInstance());

        handleAfterTransferTask = new Runnable() {
            @Override
            public void run() {
                if (!isTransferComplete)
                {
                    SweetAlertDialogUtils.ShowMessageDialog(GiftoActivity.getInstance(), getString(R.string.your_request_being_processed), new ICallback() {
                        @Override
                        public Object doIt(Object... params) {
                            GiftoActivity.getInstance().onBackPressed();
                            return null;
                        }
                    });
                }
            }
        };

        if (GiftoWalletManager.isUsingStorePassphrase())
            rlPassword.setVisibility(View.GONE);
        else rlPassword.setVisibility(View.VISIBLE);

        btnTransfer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.btn_transfer) {
            TransferV2();
        }
    }

    /**
     * Transfer coin - api v2
     */
    public void TransferV2()
    {
        if (!ValidateInput())
            return;

        if (GiftoWalletManager.isUsingStorePassphrase()) {
            password = GiftoWalletManager.getUserSecurePassphrase();

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
                            GiftoWalletManager.setUserSecurePassphrase(PassphraseRequirePopup.passpharse);
                            TransferV2();
                        }
                    }
                });
                passphraseRequirePopup.setAnimationStyle(R.style.GiftoFadePopupAnimation);
                passphraseRequirePopup.show(btnTransfer);
                return;
            }
        }

        if (myHandler != null && handleAfterTransferTask != null)
            myHandler.removeCallbacks(handleAfterTransferTask);

        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance(), getString(R.string.processing));

        final ICallback callback = new ICallback() {
            @Override
            public Object doIt(final Object... params) {
                isTransferComplete = true;
                if (myHandler != null && handleAfterTransferTask != null)
                    myHandler.removeCallbacks(handleAfterTransferTask);
                if (GiftoActivity.getInstance() != null)
                {
                    SweetAlertDialogUtils.DismissSweetDialog();
                    final boolean result = (boolean) params[0];
                    if (result) {
                        Logger.e(TAG, "Transfer successful");
                        EventBus.getDefault().post(new OnRefreshGiftoTransactionEvent());
                        SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getString(R.string.title_success), "Tipping coins is successful. Your transaction is being processed.", true, new ICallback() {
                            @Override
                            public Object doIt(Object... params) {
                                GiftoActivity.getInstance().onBackPressed();
                                return null;
                            }
                        });
                    } else {
                        String msg = (String) params[1];
                        Logger.e(TAG, "Transfer error: " + msg);
                        SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getString(R.string.title_error), msg, false, null);
                    }
                }
                return null;
            }
        };

        toWalletAddress = etTo.getText().toString().trim().toLowerCase();
        amountHex = Utils.FormatAmount(etAmount.getText().toString().trim());

        if (!GiftoWalletManager.isUsingStorePassphrase())
            password = etPassword.getText().toString();

        isTransferComplete = false;
        final TipGiftoRequest tipGiftoRequest = new TipGiftoRequest(GiftoWalletManager.getUserIdentityData(), toWalletAddress, amountHex, password, Constants.CURRENCY_CODE);

        RestClient.SendGift(tipGiftoRequest, new WalletApiResponseCallback<NoResponse>() {
            int mSession = GiftoActivity.getInstance().getSession();
            ICallback mCallback = new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    String code = (String) params[0];
                    switch (code)
                    {
                        case WSConstants.RESPONSE_TRANSFER_ACCEPTED:
                            callback.doIt(true);
                            break;
                        case WSConstants.RESPONSE_BALANCE_NOT_ENOUGH:
                            callback.doIt(false, getString(R.string.balance_not_enough_transfer));
                            break;
                        case WSConstants.RESPONSE_PASSPHRASE_INCORRECT:
                            callback.doIt(false, getString(R.string.password_incorrect));
                            break;
                        case WSConstants.RESPONSE_RECEIVER_NOT_FOUND:
                            callback.doIt(false, getString(R.string.msg_receiver_not_found));
                            break;
                        case WSConstants.RESPONSE_CONFLICT:
                            callback.doIt(false, getString(R.string.another_request_being_processed));
                            break;
                        default:
                            if (params.length > 1)
                            {
                                callback.doIt(false, (String) params[1]);
                            }
                            else callback.doIt(false, getString(R.string.unknown_error));
                            break;
                    }
                    return null;
                }
            };
            @Override
            public void success(String statusCode, NoResponse responseData) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && mCallback != null)
                {
                    mCallback.doIt(statusCode);
                }
            }

            @Override
            public void failed(WalletApiError error) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && mCallback != null)
                {
                    mCallback.doIt(error.getCode(), error.getMsg());
                }
            }
        });

        myHandler.postDelayed(handleAfterTransferTask, 20000);
    }

    /**
     * Validate input
     *
     * @return true if all of inputs are correct
     *         false else
     */
    public boolean ValidateInput()
    {
        if (!Utils.isStringValid(etTo.getText().toString()) ||
                !Utils.isStringValid(etAmount.getText().toString()) ||
                (!GiftoWalletManager.isUsingStorePassphrase() && !Utils.isStringValid(etPassword.getText().toString())))
        {
            SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getString(R.string.title_error), getString(R.string.please_input_all_require_field));
            return false;
        }

        try {
            double amount = Double.valueOf(Utils.FormatAmount(etAmount.getText().toString().trim()));
            if (amount < Constants.MIN_TRANSFER_AMOUNT)
            {
                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getString(R.string.title_error), getString(R.string.amount_condition));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getString(R.string.title_error), getString(R.string.amount_invalid_format));
            return false;
        }

        return true;
    }
}
