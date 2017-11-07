package io.gifto.wallet.ui.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.gifto.wallet.R;
import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.model.WalletCurrency;
import io.gifto.wallet.networking.RestClient;
import io.gifto.wallet.networking.GiftoApiResponse;
import io.gifto.wallet.networking.WSConstants;
import io.gifto.wallet.networking.WalletApiResponseCallback;
import io.gifto.wallet.networking.models.request.TipGiftoRequest;
import io.gifto.wallet.networking.models.response.DataResponse;
import io.gifto.wallet.networking.models.response.GetGiftoTransactionListResponse;
import io.gifto.wallet.networking.models.response.NoResponse;
import io.gifto.wallet.networking.models.response.TippingCoinResponse;
import io.gifto.wallet.networking.models.response.WalletApiError;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.PrefConstants;
import io.gifto.wallet.utils.ScreenUtils;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.CustomSharedPreferences;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.Logger;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by thongnguyen on 7/18/17.
 *
 * A popup for tipping
 */

public class TippingGiftoPopup extends DialogMenu implements View.OnClickListener
{
    private static final String TAG = "TippingGiftoPopup";

    EditText etAmount;
    EditText etPassword;
    RelativeLayout rlPassword;
    RelativeLayout btnTip;
    TextView tvIntro;
    TextView tvAmountNoti;
    ImageView iconGifto;

    private String fromIdentityData;
    private String toIdentityData;
    private String amount;
    private GiftoApiResponse<GetGiftoTransactionListResponse> callback;
    private WalletApiResponseCallback<TippingCoinResponse> callback2;

    private int xPos = 0;
    private int yPos = 0;

    private Activity mContext;

    private long lastClickTime = 0;

    /**
     * Set sender's identity data
     *
     * @param fromIdentityData identity data
     */
    public void setFromIdentityData(String fromIdentityData) {
        this.fromIdentityData = fromIdentityData;
    }

    /**
     * Set receiver's identity data
     *
     * @param toIdentityData identity data
     */
    public void setToIdentityData(String toIdentityData) {
        this.toIdentityData = toIdentityData;
    }

    /**
     * Set amount
     * @param amount amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * Set callback for response - using for api v1
     *
     * @param callback callback for response
     */
    public void setCallback(GiftoApiResponse<GetGiftoTransactionListResponse> callback) {
        this.callback = callback;
    }

    /**
     * Set callback for response - using for api v2
     *
     * @param callback callback for response
     */
    public void setResponseCallback(WalletApiResponseCallback<TippingCoinResponse> callback) {
        this.callback2 = callback;
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
    }

    @SuppressWarnings("deprecation")
    public TippingGiftoPopup(Activity activity)
    {
        mContext = activity;

        ScreenUtils.InitialScreenParams(activity, activity.getResources(), activity.getResources().getBoolean(R.bool.isTablet));

        this.menuWindow = new PopupWindow(activity);
        menuWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (onDialogTouchOutsideListener != null) {
                        onDialogTouchOutsideListener.onDialogTouchOutside();
                    }
                    return true;
                }
                return false;
            }
        });
        menuWindow.setOutsideTouchable(true);

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (GiftoWalletManager.isUsingStorePassphrase())
            rootView = inflater.inflate(R.layout.fragment_tip_rosecoin, null);
        else rootView = inflater.inflate(R.layout.fragment_tip_rosecoin_no_store_pass, null);

        etAmount = (EditText) rootView.findViewById(R.id.et_amount);
        etPassword = (EditText) rootView.findViewById(R.id.et_password);
        btnTip = (RelativeLayout) rootView.findViewById(R.id.btn_tip);
        rlPassword = (RelativeLayout) rootView.findViewById(R.id.rl_password);
        tvIntro = (TextView) rootView.findViewById(R.id.tv_intro);
        tvAmountNoti = (TextView) rootView.findViewById(R.id.tv_amount_noti);
        iconGifto = (ImageView) rootView.findViewById(R.id.icon_rosecoin);

        iconGifto.setImageResource(WalletCurrency.getIconResourceByCode(Constants.CURRENCY_CODE));

        tvAmountNoti.setVisibility(View.INVISIBLE);

        btnTip.setOnClickListener(this);

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double amount = Double.valueOf(Utils.FormatAmount(etAmount.getText().toString().trim()));
                    if (amount < Constants.MIN_TRANSFER_AMOUNT)
                        tvAmountNoti.setVisibility(View.VISIBLE);
                    else tvAmountNoti.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    tvAmountNoti.setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                }
            }
        });

        // ====================================== Adjust item =======================================

        menuWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        menuWindow.setFocusable(true);
        menuWindow.update();
    }

    @Override
    public void onClick(View v)
    {
        if(SystemClock.elapsedRealtime() - lastClickTime < 500)
            return;

        lastClickTime = SystemClock.elapsedRealtime();

        int id = v.getId();
        if (id == R.id.btn_tip)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                TipV3();
            else TipV2();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void show(View vc)
    {
        if (isShow)
            return;

        etAmount.setText(amount);

        int orientation = mContext.getResources().getConfiguration().orientation;
        Logger.e(TAG, "Orientation: " + orientation);

        if (GiftoWalletManager.isUsingStorePassphrase())
        {
            String passphrase = GiftoWalletManager.getUserSecurePassphrase();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || Utils.isStringValid(passphrase))
            {
                tvIntro.setText(mContext.getString(R.string.please_enter_amount_to_continue));
                etPassword.setText(passphrase);
                rlPassword.setVisibility(View.GONE);
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                {
                    windowWidth = (int) (ScreenUtils.SCREEN_WIDTH * 4 / 5);
                    windowHeight = Utils.DpToPx(mContext, 300);
                }
                else
                {
                    if (!ScreenUtils.isTablet())
                    {
                        windowWidth = (int) (ScreenUtils.SCREEN_WIDTH * 3 / 5);
                        windowHeight = (int) (ScreenUtils.SCREEN_HEIGHT - ScreenUtils.STATUS_BAR_HEIGHT);
                    }
                    else
                    {
                        windowWidth = (int) (ScreenUtils.SCREEN_WIDTH / 2);
                        windowHeight = (int) (ScreenUtils.SCREEN_HEIGHT * 2 / 3);
                    }
                }
            } else {
                tvIntro.setText(mContext.getString(R.string.please_enter_amount_password_to_continue));
                etPassword.setText("");
                rlPassword.setVisibility(View.VISIBLE);
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                {
                    windowWidth = (int) (ScreenUtils.SCREEN_WIDTH * 4 / 5);
                    windowHeight = Utils.DpToPx(mContext, 430);
                }
                else
                {
                    if (!ScreenUtils.isTablet())
                    {
                        windowWidth = (int) (ScreenUtils.SCREEN_WIDTH * 3 / 5);
                        windowHeight = (int) (ScreenUtils.SCREEN_HEIGHT - ScreenUtils.STATUS_BAR_HEIGHT);
                    }
                    else
                    {
                        windowWidth = (int) (ScreenUtils.SCREEN_WIDTH / 2);
                        windowHeight = (int) (ScreenUtils.SCREEN_HEIGHT * 2 / 3);
                    }
                }
            }
        }
        else
        {
            tvIntro.setText(mContext.getString(R.string.please_enter_amount_password_to_continue));
            etPassword.setText("");
            rlPassword.setVisibility(View.VISIBLE);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            {
                windowWidth = (int) (ScreenUtils.SCREEN_WIDTH * 4 / 5);
                windowHeight = Utils.DpToPx(mContext, 450);
            }
            else
            {
                if (!ScreenUtils.isTablet())
                {
                    windowWidth = (int) (ScreenUtils.SCREEN_WIDTH * 3 / 5);
                    windowHeight = (int) (ScreenUtils.SCREEN_HEIGHT - ScreenUtils.STATUS_BAR_HEIGHT);
                }
                else
                {
                    windowWidth = (int) (ScreenUtils.SCREEN_WIDTH / 2);
                    windowHeight = (int) (ScreenUtils.SCREEN_HEIGHT * 2 / 3);
                }
            }
        }

        menuWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuWindow.setContentView(rootView);

        xPos = (int)((ScreenUtils.SCREEN_WIDTH - windowWidth) / 2);
        yPos = (int)((ScreenUtils.SCREEN_HEIGHT - windowHeight)  /2);

        menuWindow.setWidth(windowWidth);
        menuWindow.setHeight(windowHeight);
        menuWindow.showAtLocation(mContext.getWindow().getDecorView().getRootView(), Gravity.NO_GRAVITY, xPos, yPos);

        isShow = true;
    }

    /**
     * Tipping - api v1
     */
    private void Tip()
    {
        if (!ValidateInputs())
            return;

        final String passphrase = etPassword.getText().toString();
        TipGiftoRequest tipGiftoRequest = new TipGiftoRequest(fromIdentityData, toIdentityData, Utils.FormatAmount(etAmount.getText().toString().trim()), passphrase, Constants.CURRENCY_CODE);
        RestClient.instance().getRestGiftoWalletService().TippingGifto(GiftoWalletManager.getAuthorization(), tipGiftoRequest, new Callback<DataResponse<GetGiftoTransactionListResponse>>()
        {
            @Override
            public void success(DataResponse<GetGiftoTransactionListResponse> transferGiftoResponseDataResponse, Response response) {
                if (transferGiftoResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "TransferGiftoWithoutWalletAddress Unknown Error");
                    if (callback != null)
                        callback.onError(transferGiftoResponseDataResponse, mContext.getString(R.string.unknown_error));
                }
                else
                {
                    switch (transferGiftoResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.TIP_ROSE_COIN_SUCCESS:
                            if (GiftoWalletManager.isUsingStorePassphrase() && !Utils.isStringValid(GiftoWalletManager.getUserSecurePassphrase()))
                                GiftoWalletManager.setUserSecurePassphrase(passphrase);
                            if (callback != null)
                                callback.onSuccess(transferGiftoResponseDataResponse);
                            break;
                        case WSConstants.TIP_ROSE_COIN_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "TransferGiftoWithoutWalletAddress Balance not enough");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, mContext.getString(R.string.balance_not_enough_transfer));
                            break;
                        case WSConstants.TIP_ROSE_COIN_INCORRECT_PASSPHRASE:
                            Logger.e(TAG, "TransferGiftoWithoutWalletAddress Incorrect passphrase");
                            if (GiftoWalletManager.isUsingStorePassphrase())
                                GiftoWalletManager.setUserSecurePassphrase("");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, mContext.getString(R.string.password_incorrect));
                            break;
                        case WSConstants.DENY_REQUEST:
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, mContext.getString(R.string.another_request_being_processed));
                            break;
                        default:
                            Logger.e(TAG, "TransferGiftoWithoutWalletAddress Unknown Error");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, mContext.getString(R.string.unknown_error));
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "TransferGiftoWithoutWalletAddress Error: " + error.getMessage());
                if (callback != null)
                    callback.onError(null, error.getLocalizedMessage());
            }
        });
        dismiss();
    }

    /**
     * Tipping - api v2
     */
    private void TipV2()
    {
        if (!ValidateInputs())
            return;

        final String passphrase = etPassword.getText().toString();
        final TipGiftoRequest tipGiftoRequest = new TipGiftoRequest(fromIdentityData, toIdentityData, Utils.FormatAmount(etAmount.getText().toString().trim()), passphrase, Constants.CURRENCY_CODE);

        RestClient.SendGift(tipGiftoRequest, new WalletApiResponseCallback<NoResponse>() {
            ICallback mCallback = new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    String code = (String) params[0];
                    switch (code)
                    {
                        case WSConstants.RESPONSE_TRANSFER_ACCEPTED:
                            if (GiftoWalletManager.isUsingStorePassphrase() && !Utils.isStringValid(GiftoWalletManager.getUserSecurePassphrase()))
                                GiftoWalletManager.setUserSecurePassphrase(passphrase);
                            if (callback2 != null)
                                callback2.success(WSConstants.RESPONSE_TRANSFER_ACCEPTED, new TippingCoinResponse(fromIdentityData, toIdentityData, Constants.CURRENCY_CODE, tipGiftoRequest.getAmount()));
                            break;
                        case WSConstants.RESPONSE_BALANCE_NOT_ENOUGH:
                            if (callback2 != null)
                                callback2.failed(new WalletApiError(WSConstants.RESPONSE_BALANCE_NOT_ENOUGH, mContext.getString(R.string.balance_not_enough_transfer)));
                            break;
                        case WSConstants.RESPONSE_PASSPHRASE_INCORRECT:
                            if (GiftoWalletManager.isUsingStorePassphrase())
                                GiftoWalletManager.setUserSecurePassphrase("");
                            if (callback2 != null)
                                callback2.failed(new WalletApiError(WSConstants.RESPONSE_PASSPHRASE_INCORRECT, mContext.getString(R.string.password_incorrect)));
                            break;
                        case WSConstants.RESPONSE_RECEIVER_NOT_FOUND:
                            if (callback2 != null)
                                callback2.failed(new WalletApiError(WSConstants.RESPONSE_RECEIVER_NOT_FOUND, mContext.getString(R.string.msg_receiver_not_found)));
                            break;
                        case WSConstants.RESPONSE_CONFLICT:
                            if (callback2 != null)
                                callback2.failed(new WalletApiError(WSConstants.RESPONSE_CONFLICT, mContext.getString(R.string.another_request_being_processed)));
                            break;
                        default:
                            if (params.length > 1)
                            {
                                if (callback2 != null)
                                    callback2.failed(new WalletApiError(code, (String) params[1]));
                            }
                            else if (callback2 != null)
                                callback2.failed(new WalletApiError(code, mContext.getString(R.string.unknown_error)));
                            break;
                    }
                    return null;
                }
            };
            @Override
            public void success(String statusCode, NoResponse responseData) {
                if (mCallback != null)
                    mCallback.doIt(statusCode);
            }

            @Override
            public void failed(WalletApiError error) {
                if (mCallback != null)
                    mCallback.doIt(error.getCode(), error.getMsg());
            }
        });
        dismiss();
    }

    /**
     * Tip coin V3 - using fingerprint to authorize
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void TipV3()
    {
        if (!ValidateInputs())
            return;

        if (GiftoWalletManager.isUsingStorePassphrase())
        {
            if (!Utils.isStringValid(CustomSharedPreferences.getPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "")) || CustomSharedPreferences.getPreferences(PrefConstants.PREF_USE_FINGERPRINT, false))
            {
                FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment();
                fragment.setCallback(new FingerprintAuthenticationDialogFragment.Callback() {
                    @Override
                    public void onPasswordAuthenticated(String passphrase, FingerprintAuthenticationDialogFragment.PassphraseSource source) {
                        DoTip(passphrase, source);
                    }

                    @Override
                    public void onCanceled() {
                    }
                });
                fragment.show(mContext.getFragmentManager(), "FingerprintAuthenticationDialogFragment");
            }
            else
            {
                DoTip(GiftoWalletManager.getUserSecurePassphrase(), FingerprintAuthenticationDialogFragment.PassphraseSource.STORE_PASSPHRASE);
            }
        }
        else DoTip(etPassword.getText().toString(), FingerprintAuthenticationDialogFragment.PassphraseSource.USER_INPUT_PASSPHRASE);
    }

    /**
     * Tip coin with password
     *
     * @param passphrase password
     * @param source    source of password: from user's input or stored password
     */
    private void DoTip(final String passphrase, final FingerprintAuthenticationDialogFragment.PassphraseSource source)
    {
        final TipGiftoRequest tipGiftoRequest = new TipGiftoRequest(fromIdentityData, toIdentityData, Utils.FormatAmount(etAmount.getText().toString().trim()), passphrase, Constants.CURRENCY_CODE);

        RestClient.SendGift(tipGiftoRequest, new WalletApiResponseCallback<NoResponse>() {
            ICallback mCallback = new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    String code = (String) params[0];
                    switch (code)
                    {
                        case WSConstants.RESPONSE_TRANSFER_ACCEPTED:
                            if (GiftoWalletManager.isUsingStorePassphrase() && !Utils.isStringValid(GiftoWalletManager.getUserSecurePassphrase()))
                                GiftoWalletManager.setUserSecurePassphrase(passphrase);
                            if (callback2 != null)
                                callback2.success(WSConstants.RESPONSE_TRANSFER_ACCEPTED, new TippingCoinResponse(fromIdentityData, toIdentityData, Constants.CURRENCY_CODE, tipGiftoRequest.getAmount()));
                            break;
                        case WSConstants.RESPONSE_BALANCE_NOT_ENOUGH:
                            if (callback2 != null)
                                callback2.failed(new WalletApiError(WSConstants.RESPONSE_BALANCE_NOT_ENOUGH, mContext.getString(R.string.balance_not_enough_transfer)));
                            break;
                        case WSConstants.RESPONSE_PASSPHRASE_INCORRECT:
                            if (source == FingerprintAuthenticationDialogFragment.PassphraseSource.STORE_PASSPHRASE)
                                GiftoWalletManager.setUserSecurePassphrase("");
                            if (callback2 != null)
                                callback2.failed(new WalletApiError(WSConstants.RESPONSE_PASSPHRASE_INCORRECT, mContext.getString(R.string.password_incorrect)));
                            break;
                        case WSConstants.RESPONSE_RECEIVER_NOT_FOUND:
                            if (callback2 != null)
                                callback2.failed(new WalletApiError(WSConstants.RESPONSE_RECEIVER_NOT_FOUND, mContext.getString(R.string.msg_receiver_not_found)));
                            break;
                        case WSConstants.RESPONSE_CONFLICT:
                            if (callback2 != null)
                                callback2.failed(new WalletApiError(WSConstants.RESPONSE_CONFLICT, mContext.getString(R.string.another_request_being_processed)));
                            break;
                        default:
                            if (params.length > 1)
                            {
                                if (callback2 != null)
                                    callback2.failed(new WalletApiError(code, (String) params[1]));
                            }
                            else if (callback2 != null)
                                callback2.failed(new WalletApiError(code, mContext.getString(R.string.unknown_error)));
                            break;
                    }
                    return null;
                }
            };
            @Override
            public void success(String statusCode, NoResponse responseData) {
                if (mCallback != null)
                    mCallback.doIt(statusCode);
            }

            @Override
            public void failed(WalletApiError error) {
                if (mCallback != null)
                    mCallback.doIt(error.getCode(), error.getMsg());
            }
        });
        dismiss();
    }

    /**
     * Check all input is correct or not
     * @return true if all of input is correct
     *         false else
     */
    public boolean ValidateInputs()
    {
        if(!Utils.isStringValid(etAmount.getText().toString().trim()))
        {
            return false;
        }
        else if(!GiftoWalletManager.isUsingStorePassphrase() && !Utils.isStringValid(etPassword.getText().toString()))
        {
            return false;
        }

        try {
            double amount = Double.valueOf(Utils.FormatAmount(etAmount.getText().toString().trim()));
            if (amount < Constants.MIN_TRANSFER_AMOUNT)
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
