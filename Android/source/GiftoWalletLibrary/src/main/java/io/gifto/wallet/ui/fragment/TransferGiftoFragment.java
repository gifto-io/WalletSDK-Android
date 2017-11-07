package io.gifto.wallet.ui.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import de.greenrobot.event.EventBus;
import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.R;
import io.gifto.wallet.event.OnQRCodeDetectedEvent;
import io.gifto.wallet.event.OnRefreshGiftoTransactionEvent;
import io.gifto.wallet.model.TransferFeeType;
import io.gifto.wallet.model.TransferType;
import io.gifto.wallet.model.WalletBalanceDetail;
import io.gifto.wallet.model.WalletCurrency;
import io.gifto.wallet.networking.RestClient;
import io.gifto.wallet.networking.WSConstants;
import io.gifto.wallet.networking.WalletApiResponseCallback;
import io.gifto.wallet.networking.models.request.TransferGiftoRequest;
import io.gifto.wallet.networking.models.response.DataResponse;
import io.gifto.wallet.networking.models.response.NoResponse;
import io.gifto.wallet.networking.models.response.TransferGiftoResponse;
import io.gifto.wallet.networking.models.response.WalletApiError;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.dialog.FingerprintAuthenticationDialogFragment;
import io.gifto.wallet.ui.dialog.PassphraseRequirePopup;
import io.gifto.wallet.ui.interfaces.OnDialogTouchOutsideListener;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.PrefConstants;
import io.gifto.wallet.utils.SweetAlertDialogUtils;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.CustomSharedPreferences;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.Logger;
import io.gifto.wallet.utils.common.MyHandler;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class TransferGiftoFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = "TransferGiftoFragment";

    EditText etFrom;

    EditText etTo;

    EditText etAmount;

    EditText etPassword;

    EditText etNote;

    ImageView btnQRCode;

    RelativeLayout btnTransfer;

    RelativeLayout rlPassword;

    Spinner spFee;

    ImageView iconAmount;

    private String fromWalletAddress;
    private String toWalletAddress;
    private String amountHex;
    private String password;
    private String note;

    private boolean isTransferComplete;

    private EventBus eventBus = EventBus.getDefault();
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
        if (!eventBus.isRegistered(this))
            eventBus.register(this);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (eventBus.isRegistered(this))
            eventBus.unregister(this);
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
            GiftoActivity.getInstance().SetActivityTitle(getString(R.string.transfer_rosecoin));

        return inflater.inflate(R.layout.fragment_transfer_rose_coin, null);
    }

    @Override
    protected void BindView(View rootView)
    {
        etFrom = (EditText) rootView.findViewById(R.id.et_from);

        etTo = (EditText) rootView.findViewById(R.id.et_to);

        etAmount = (EditText) rootView.findViewById(R.id.et_amount);

        etPassword = (EditText) rootView.findViewById(R.id.et_password);

        etNote = (EditText) rootView.findViewById(R.id.et_note);

        btnQRCode = (ImageView) rootView.findViewById(R.id.btn_qr_code);

        btnTransfer = (RelativeLayout) rootView.findViewById(R.id.btn_transfer);

        rlPassword = (RelativeLayout) rootView.findViewById(R.id.rl_password);

        spFee = (Spinner) rootView.findViewById(R.id.sp_fee_type);

        iconAmount = (ImageView) rootView.findViewById(R.id.icon_amount);
    }

    @Override
    protected void ViewInjectionSuccess()
    {
        myHandler = new MyHandler(GiftoActivity.getInstance());

        iconAmount.setImageResource(WalletCurrency.getIconResourceByCode(Constants.CURRENCY_CODE));

        WalletBalanceDetail walletBalanceDetail = GiftoWalletManager.getWalletBalanceDetail(Constants.CURRENCY_CODE);
        fromWalletAddress = walletBalanceDetail == null? "" : walletBalanceDetail.getAddress();

        handleAfterTransferTask = new Runnable() {
            @Override
            public void run() {
                if (!isTransferComplete)
                {
                    isTransferComplete = true;
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

        etFrom.setEnabled(false);
        etFrom.setText(Utils.isStringValid(fromWalletAddress)? fromWalletAddress : "");

        btnQRCode.setOnClickListener(this);
        btnTransfer.setOnClickListener(this);

        etNote.setText("");

        ArrayAdapter feeAdapter = new ArrayAdapter(getContext(), R.layout.spinner_textview, new String[] {TransferFeeType.SENDER.getName().toUpperCase(), TransferFeeType.RECEIVER.getName().toUpperCase()});
        feeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFee.setAdapter(feeAdapter);
        spFee.setSelection(0);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.btn_transfer) {
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    TransferV3();
                else TransferV2();
            }
        }
        else if (id == R.id.btn_qr_code)
        {
            ScanQRCode();
        }
    }

    /**
     * Start QRCode Scanner
     */
    public void ScanQRCode()
    {
        GiftoActivity.scanQRCode();
    }

    /**
     * Transfer coin - api v1
     */
    private void Transfer()
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
                            Transfer();
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
                        SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getString(R.string.title_success), getString(R.string.transfer_coin_success), true, new ICallback() {
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

        fromWalletAddress = etFrom.getText().toString().trim().toLowerCase();
        toWalletAddress = etTo.getText().toString().trim().toLowerCase();
        amountHex = Utils.FormatAmount(etAmount.getText().toString().trim());
        note = etNote.getText().toString().trim();

        if (!GiftoWalletManager.isUsingStorePassphrase())
            password = etPassword.getText().toString();

        TransferGiftoRequest transferGiftoRequest = new TransferGiftoRequest(GiftoWalletManager.getUserIdentityData(), fromWalletAddress, toWalletAddress, amountHex, password, note);

        isTransferComplete = false;
        RestClient.instance().getRestGiftoWalletService().TransferGiftoBalance(GiftoWalletManager.getAuthorization(), transferGiftoRequest, new Callback<DataResponse<TransferGiftoResponse>>() {
            @Override
            public void success(DataResponse<TransferGiftoResponse> transferGiftoResponseDataResponse, Response response) {
                if (transferGiftoResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "TransferGiftoBalance Unknown Error");
                    callback.doIt(false, getString(R.string.unknown_error));
                }
                else
                {
                    switch (transferGiftoResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.TRANSFER_ROSE_COIN_SUCCESS:
                            callback.doIt(true);
                            break;
                        case WSConstants.TRANSFER_ROSE_COIN_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "TransferGiftoBalance Balance not enough");
                            callback.doIt(false, getString(R.string.balance_not_enough_transfer));
                            break;
                        case WSConstants.TRANSFER_ROSE_COIN_INCORRECT_PASSPHRASE:
                            Logger.e(TAG, "TransferGiftoBalance Incorrect passphrase");
                            if (GiftoWalletManager.isUsingStorePassphrase())
                                GiftoWalletManager.setUserSecurePassphrase("");
                            callback.doIt(false, getString(R.string.password_incorrect));
                            break;
                        case WSConstants.DENY_REQUEST:
                            Logger.e(TAG, "TransferGiftoBalance: The another request is being processed. Please request again later.");
                            callback.doIt(false, getString(R.string.another_request_being_processed));
                            break;
                        default:
                            Logger.e(TAG, "TransferGiftoBalance Unknown Error");
                            callback.doIt(false, getString(R.string.unknown_error));
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "TransferGiftoBalance Error: " + error.getMessage());
                callback.doIt(false, getString(R.string.error_due_to_network));
            }
        });

        myHandler.postDelayed(handleAfterTransferTask, 20000);
    }

    /**
     * Transfer coin - api v2
     */
    private void TransferV2()
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
                if (isTransferComplete)
                    return null;

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
                        SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getString(R.string.title_success), getString(R.string.transfer_coin_success), true, new ICallback() {
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

        fromWalletAddress = etFrom.getText().toString().trim().toLowerCase();
        toWalletAddress = etTo.getText().toString().trim().toLowerCase();
        amountHex = Utils.FormatAmount(etAmount.getText().toString().trim());
        note = etNote.getText().toString().trim();

        if (!GiftoWalletManager.isUsingStorePassphrase())
            password = etPassword.getText().toString();

        String feeType = ((String)spFee.getSelectedItem()).toLowerCase();
        String type = TransferType.TRANSFER.getName();
        final String refId = "";

        TransferGiftoRequest transferGiftoRequest = new TransferGiftoRequest(GiftoWalletManager.getUserIdentityData(), toWalletAddress, amountHex, password, note, refId, feeType, type, Constants.CURRENCY_CODE);

        isTransferComplete = false;
        RestClient.TransferCoin(transferGiftoRequest, new WalletApiResponseCallback<NoResponse>() {
            int mSession = GiftoActivity.getInstance().getSession();
            @Override
            public void success(String statusCode, NoResponse responseData) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                    callback.doIt(true);
            }

            @Override
            public void failed(WalletApiError error) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                {
                    switch (error.getCode())
                    {
                        case WSConstants.RESPONSE_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "TransferGiftoBalance Balance not enough");
                            callback.doIt(false, getString(R.string.balance_not_enough_transfer));
                            break;
                        case WSConstants.RESPONSE_PASSPHRASE_INCORRECT:
                            Logger.e(TAG, "TransferGiftoBalance Incorrect passphrase");
                            if (GiftoWalletManager.isUsingStorePassphrase())
                                GiftoWalletManager.setUserSecurePassphrase("");
                            callback.doIt(false, getString(R.string.password_incorrect));
                            break;
                        case WSConstants.RESPONSE_RECEIVER_NOT_FOUND:
                            Logger.e(TAG, "TransferGiftoBalance Receiver not found");
                            callback.doIt(false, getString(R.string.msg_receiver_not_found));
                            break;
                        default:
                            Logger.e(TAG, "TransferGiftoBalance Unknown Error");
                            callback.doIt(false, getString(R.string.unknown_error));
                            break;
                    }
                }
            }
        });

        myHandler.postDelayed(handleAfterTransferTask, 20000);
    }

    /**
     * Transfer coin V3 - using fingerprint to authorize
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void TransferV3()
    {
        if (!ValidateInput())
            return;

        if (GiftoWalletManager.isUsingStorePassphrase())
        {
            if (!Utils.isStringValid(CustomSharedPreferences.getPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "")) || CustomSharedPreferences.getPreferences(PrefConstants.PREF_USE_FINGERPRINT, false))
            {
                FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment();
                fragment.setCallback(new FingerprintAuthenticationDialogFragment.Callback() {
                    @Override
                    public void onPasswordAuthenticated(String passphrase, FingerprintAuthenticationDialogFragment.PassphraseSource source) {
                        DoTransfer(passphrase, source);
                    }

                    @Override
                    public void onCanceled() {
                    }
                });
                fragment.show(getActivity().getFragmentManager(), "FingerprintAuthenticationDialogFragment");
            }
            else
            {
                DoTransfer(GiftoWalletManager.getUserSecurePassphrase(), FingerprintAuthenticationDialogFragment.PassphraseSource.STORE_PASSPHRASE);
            }
        }
        else DoTransfer(etPassword.getText().toString(), FingerprintAuthenticationDialogFragment.PassphraseSource.USER_INPUT_PASSPHRASE);
    }

    /**
     * Transfer coin with password
     *
     * @param password password
     * @param source source of password: from user's input or stored password
     */
    public void DoTransfer(String password, final FingerprintAuthenticationDialogFragment.PassphraseSource source)
    {
        if (myHandler != null && handleAfterTransferTask != null)
            myHandler.removeCallbacks(handleAfterTransferTask);

        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance(), getString(R.string.processing));

        final ICallback callback = new ICallback() {
            @Override
            public Object doIt(final Object... params) {
                if (isTransferComplete)
                    return null;

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
                        SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getString(R.string.title_success), getString(R.string.transfer_coin_success), true, new ICallback() {
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

        fromWalletAddress = etFrom.getText().toString().trim().toLowerCase();
        toWalletAddress = etTo.getText().toString().trim().toLowerCase();
        amountHex = Utils.FormatAmount(etAmount.getText().toString().trim());
        note = etNote.getText().toString().trim();

        String feeType = ((String)spFee.getSelectedItem()).toLowerCase();
        String type = TransferType.TRANSFER.getName();
        final String refId = "";

        TransferGiftoRequest transferGiftoRequest = new TransferGiftoRequest(GiftoWalletManager.getUserIdentityData(), toWalletAddress, amountHex, password, note, refId, feeType, type, Constants.CURRENCY_CODE);

        isTransferComplete = false;
        RestClient.TransferCoin(transferGiftoRequest, new WalletApiResponseCallback<NoResponse>() {
            int mSession = GiftoActivity.getInstance().getSession();
            @Override
            public void success(String statusCode, NoResponse responseData) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                    callback.doIt(true);
            }

            @Override
            public void failed(WalletApiError error) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                {
                    switch (error.getCode())
                    {
                        case WSConstants.RESPONSE_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "TransferGiftoBalance Balance not enough");
                            callback.doIt(false, getString(R.string.balance_not_enough_transfer));
                            break;
                        case WSConstants.RESPONSE_PASSPHRASE_INCORRECT:
                            Logger.e(TAG, "TransferGiftoBalance Incorrect passphrase");
                            if (source == FingerprintAuthenticationDialogFragment.PassphraseSource.STORE_PASSPHRASE)
                                GiftoWalletManager.setUserSecurePassphrase("");
                            callback.doIt(false, getString(R.string.password_incorrect));
                            break;
                        case WSConstants.RESPONSE_RECEIVER_NOT_FOUND:
                            Logger.e(TAG, "TransferGiftoBalance Receiver not found");
                            callback.doIt(false, getString(R.string.msg_receiver_not_found));
                            break;
                        default:
                            Logger.e(TAG, "TransferGiftoBalance Unknown Error");
                            callback.doIt(false, getString(R.string.unknown_error));
                            break;
                    }
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

    /**
     * On QRCode detected event
     *
     * @param event event data contain QRCode
     */
    public void onEvent(final OnQRCodeDetectedEvent event)
    {
        GiftoActivity.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (Utils.isStringValid(event.getData()))
                    etTo.setText(event.getData());
            }
        });

    }
}
