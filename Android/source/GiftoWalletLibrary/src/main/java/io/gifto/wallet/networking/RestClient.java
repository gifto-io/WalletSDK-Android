package io.gifto.wallet.networking;

import android.app.Activity;
import android.widget.PopupWindow;

import com.google.gson.JsonElement;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.gifto.wallet.R;
import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.model.WalletDetail;
import io.gifto.wallet.networking.models.request.CreateWalletRequest;
import io.gifto.wallet.networking.models.request.GetGiftoBalanceRequest;
import io.gifto.wallet.networking.models.request.GetGiftoTransactionRequest;
import io.gifto.wallet.networking.models.request.GetWalletAddressRequest;
import io.gifto.wallet.networking.models.request.GetWalletDetailRequest;
import io.gifto.wallet.networking.models.request.MoveGiftoRequest;
import io.gifto.wallet.networking.models.request.RefreshGiftoRequest;
import io.gifto.wallet.networking.models.request.TipGiftoRequest;
import io.gifto.wallet.networking.models.request.TransferGiftoRequest;
import io.gifto.wallet.networking.models.response.DataResponse;
import io.gifto.wallet.networking.models.response.GetGiftoBalanceResponse;
import io.gifto.wallet.networking.models.response.GetGiftoTransactionListResponse;
import io.gifto.wallet.networking.models.response.GetWalletAddressResponse;
import io.gifto.wallet.networking.models.response.NoResponse;
import io.gifto.wallet.networking.models.response.TippingCoinResponse;
import io.gifto.wallet.networking.models.response.TransferGiftoResponse;
import io.gifto.wallet.networking.models.response.WalletApiError;
import io.gifto.wallet.ui.dialog.TippingGiftoPopup;
import io.gifto.wallet.ui.interfaces.OnDialogTouchOutsideListener;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.JSonUtils;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.Logger;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by thongnguyen on 07/09/17.
 */
public class RestClient
{
    private static final String TAG = "GiftoRestClient";

    /**
     * time out for retrofit
     */
    private static final int ROSECOIN_CONNECTION_TIMEOUT = 150; //second
    private static final int ROSECOIN_READ_WRITE_TIMEOUT = 150; //second

    private static final String BASE_WALLET_URL = "https://wallet.upliveapps.com/"; //"https://wallet.gifto.io/";

    private RestGiftoWalletService restGiftoWalletService;

    private static RestClient mInstance;

    public static RestClient instance()
    {
        if (mInstance == null)
            mInstance = new RestClient();

        return mInstance;
    }

    private RestClient()
    {
        RestAdapter walletRestAdapter;

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(ROSECOIN_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(ROSECOIN_READ_WRITE_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(ROSECOIN_READ_WRITE_TIMEOUT, TimeUnit.SECONDS);

        walletRestAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_WALLET_URL)
                .setClient(new OkClient(okHttpClient))
                .build();

        restGiftoWalletService = walletRestAdapter.create(RestGiftoWalletService.class);
    }

    public RestGiftoWalletService getRestGiftoWalletService() {
        return restGiftoWalletService;
    }


    // API SDK V1 - deprecated
    /**
     * Create Gifto Wallet using identityData and Password
     * @param createWalletRequest CreateWalletRequest contain identityData and password
     * @param callback callback when create success or error
     */
    private static void CreateGiftoWallet(final CreateWalletRequest createWalletRequest, final GiftoApiResponse<GetWalletAddressResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().CreateWallet(GiftoWalletManager.getAuthorization(), createWalletRequest, new Callback<DataResponse<GetWalletAddressResponse>>()
        {
            @Override
            public void success(DataResponse<GetWalletAddressResponse> getWalletAddressResponseDataResponse, Response response) {
                if (getWalletAddressResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "GetWalletAddress Unknown Error");
                    if (callback != null)
                        callback.onError(getWalletAddressResponseDataResponse, "Unknown Error");
                }
                else
                {
                    switch (getWalletAddressResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.CREATE_WALLET_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(getWalletAddressResponseDataResponse);
                            break;
                        case WSConstants.CREATE_WALLET_EXISTS:
                            if (callback != null)
                                callback.onError(getWalletAddressResponseDataResponse, Message.ROSECOIN_WALLET_EXISTS);
                            break;
                        case WSConstants.DENY_REQUEST:
                            if (callback != null)
                                callback.onError(getWalletAddressResponseDataResponse, "The another request is being processed. Please request again later.");
                            break;
                        default:
                            Logger.e(TAG, "GetWalletAddress Unknown Error");
                            if (callback != null)
                                callback.onError(getWalletAddressResponseDataResponse, "Unknown Error");
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "GetWalletAddress error");
                if (callback != null)
                    callback.onError(null, error.getMessage());
            }
        });
    }

    /**
     * Get Internal Gifto balance
     * @param getGiftoBalanceRequest GetGiftoBalanceRequest
     * @param callback Callback when get balance success or error
     */
    private static void GetInternalGiftoBalance(final GetGiftoBalanceRequest getGiftoBalanceRequest, final GiftoApiResponse<GetGiftoBalanceResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().GetInternalGiftoBalance(GiftoWalletManager.getAuthorization(), getGiftoBalanceRequest, new Callback<DataResponse<GetGiftoBalanceResponse>>() {
            @Override
            public void success(DataResponse<GetGiftoBalanceResponse> getGiftoBalanceResponseDataResponse, Response response) {
                if (getGiftoBalanceResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "GetGiftoBalance Unknown Error");
                    if (callback != null)
                        callback.onError(getGiftoBalanceResponseDataResponse, "Unknown Error");
                }
                else
                {
                    switch (getGiftoBalanceResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.GET_INTERNAL_ROSE_COIN_BALANCE_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(getGiftoBalanceResponseDataResponse);
                            break;
                        default:
                            Logger.e(TAG, "GetGiftoBalance Unknown Error");
                            if (callback != null)
                                callback.onError(getGiftoBalanceResponseDataResponse, "Unknown Error");
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "GetGiftoBalance error");
                if (callback != null)
                    callback.onError(null, error.getMessage());
            }
        });
    }

    /**
     * Get External(Blockchain) Gifto balance
     * @param getGiftoBalanceRequest GetGiftoBalanceRequest
     * @param callback Callback when get balance success or error
     */
    private static void GetExternalGiftoBalance(final GetGiftoBalanceRequest getGiftoBalanceRequest, final GiftoApiResponse<GetGiftoBalanceResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().GetBlockchainGiftoBalance(GiftoWalletManager.getAuthorization(), getGiftoBalanceRequest, new Callback<DataResponse<GetGiftoBalanceResponse>>() {
            @Override
            public void success(DataResponse<GetGiftoBalanceResponse> getGiftoBalanceResponseDataResponse, Response response) {
                if (getGiftoBalanceResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "GetGiftoBalance Unknown Error");
                    if (callback != null)
                        callback.onError(getGiftoBalanceResponseDataResponse, "Unknown Error");
                }
                else
                {
                    switch (getGiftoBalanceResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.GET_INTERNAL_ROSE_COIN_BALANCE_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(getGiftoBalanceResponseDataResponse);
                            break;
                        default:
                            Logger.e(TAG, "GetGiftoBalance Unknown Error");
                            if (callback != null)
                                callback.onError(getGiftoBalanceResponseDataResponse, "Unknown Error");
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "GetGiftoBalance error");
                if (callback != null)
                    callback.onError(null, error.getMessage());
            }
        });
    }

    /**
     * Get Gifto Wallet Address by identityData
     * @param getWalletAddressRequest GetWalletAddressRequest contain identityData
     * @param callback callback when get Gifto Wallet address success or error
     */
    private static void GetGiftoWalletAddress(final GetWalletAddressRequest getWalletAddressRequest, final GiftoApiResponse<GetWalletAddressResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().GetWalletAddress(GiftoWalletManager.getAuthorization(), getWalletAddressRequest, new Callback<DataResponse<GetWalletAddressResponse>>() {
            @Override
            public void success(DataResponse<GetWalletAddressResponse> getWalletAddressResponseDataResponse, Response response) {
                if (getWalletAddressResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "GetWalletAddress Unknown Error");
                    if (callback != null)
                        callback.onError(getWalletAddressResponseDataResponse, "Unknown Error");
                }
                else
                {
                    switch (getWalletAddressResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.GET_WALLET_ADDRESS_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(getWalletAddressResponseDataResponse);
                            break;
                        case WSConstants.GET_WALLET_ADDRESS_NOT_EXISTS:
                            if (callback != null)
                                callback.onError(getWalletAddressResponseDataResponse, Message.ROSECOIN_WALLET_NOT_EXISTS);
                            break;
                        default:
                            Logger.e(TAG, "GetWalletAddress Unknown Error");
                            if (callback != null)
                                callback.onError(getWalletAddressResponseDataResponse, "Unknown Error");
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "GetWalletAddress error");
                if (callback != null)
                    callback.onError(null, error.getMessage());
            }
        });
    }

    /**
     * Transfer Gifto from an account to another account using wallet address
     * @param transferGiftoRequest TransferGiftoRequest
     * @param callback callback when transfer success or error
     */
    private static void TransferGifto(final TransferGiftoRequest transferGiftoRequest, final GiftoApiResponse<TransferGiftoResponse> callback)
    {
        try
        {
            double amount = Double.valueOf(Utils.FormatAmount(transferGiftoRequest.getAmount()));
            if (amount < Constants.MIN_TRANSFER_AMOUNT)
            {
                if (callback != null)
                    callback.onError(null, "Amount must be larger than 0.001");
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        RestClient.instance().getRestGiftoWalletService().TransferGiftoBalance(GiftoWalletManager.getAuthorization(), transferGiftoRequest, new Callback<DataResponse<TransferGiftoResponse>>() {
            @Override
            public void success(DataResponse<TransferGiftoResponse> transferGiftoResponseDataResponse, Response response) {
                if (transferGiftoResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "TransferGiftoBalance Unknown Error");
                    if (callback != null)
                        callback.onError(transferGiftoResponseDataResponse, "Unknown Error");
                }
                else
                {
                    switch (transferGiftoResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.TRANSFER_ROSE_COIN_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(transferGiftoResponseDataResponse);
                            break;
                        case WSConstants.TRANSFER_ROSE_COIN_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "TransferGiftoBalance Balance not enough");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Balance is not enough to transfer.");
                            break;
                        case WSConstants.TRANSFER_ROSE_COIN_INCORRECT_PASSPHRASE:
                            Logger.e(TAG, "TransferGiftoBalance Incorrect passphrase");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Password is incorrect");
                            break;
                        case WSConstants.DENY_REQUEST:
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "The another request is being processed. Please request again later.");
                            break;
                        default:
                            Logger.e(TAG, "TransferGiftoBalance Unknown Error");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Unknown Error");
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "TransferGiftoBalance Error: " + error.getMessage());
                if (callback != null)
                    callback.onError(null, error.getMessage());
            }
        });
    }

    /**
     * Transfer Gifto using identity data instead of wallet address, using Build-in GUI
     * @param activity Activity to control GUI
     * @param fromIdentityData Sender's identity data
     * @param toIdentityData Receiver's identity data
     * @param callback Callback when transfer success or error
     */
    private static void SendGiftWithBuildInGUI(final Activity activity, final String fromIdentityData, final String toIdentityData, final GiftoApiResponse<GetGiftoTransactionListResponse> callback)
    {
        final TippingGiftoPopup tippingGiftoPopup = new TippingGiftoPopup(activity);
        tippingGiftoPopup.setFromIdentityData(fromIdentityData);
        tippingGiftoPopup.setToIdentityData(toIdentityData);
        tippingGiftoPopup.setAmount("");
        tippingGiftoPopup.setCallback(callback);
        tippingGiftoPopup.setAnimationStyle(R.style.GiftoFadePopupAnimation);
        tippingGiftoPopup.setOnDialogTouchOutsideListener(new OnDialogTouchOutsideListener() {
            @Override
            public void onDialogTouchOutside() {
                tippingGiftoPopup.dismiss();
            }
        });
        tippingGiftoPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tippingGiftoPopup.setIsShow(false);
            }
        });
        tippingGiftoPopup.show(activity.getCurrentFocus());
    }

    /**
     * Transfer Gifto using identity data instead of wallet address
     * @param tipGiftoRequest TipGiftoRequest contains fromIdentity, toIdentity, amount and passphrase
     * @param callback Callback when transfer success or error
     */
    private static void SendGift(final TipGiftoRequest tipGiftoRequest, final GiftoApiResponse<GetGiftoTransactionListResponse> callback)
    {
        try
        {
            double amount = Double.valueOf(Utils.FormatAmount(tipGiftoRequest.getAmount()));
            if (amount < Constants.MIN_TRANSFER_AMOUNT)
            {
                if (callback != null)
                    callback.onError(null, "Amount must be larger than 0.001");
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        RestClient.instance().getRestGiftoWalletService().TippingGifto(GiftoWalletManager.getAuthorization(), tipGiftoRequest, new Callback<DataResponse<GetGiftoTransactionListResponse>>()
        {
            @Override
            public void success(DataResponse<GetGiftoTransactionListResponse> transferGiftoResponseDataResponse, Response response) {
                if (transferGiftoResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "TipGifto Unknown Error");
                    if (callback != null)
                        callback.onError(transferGiftoResponseDataResponse, "Unknown Error");
                }
                else
                {
                    switch (transferGiftoResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.TIP_ROSE_COIN_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(transferGiftoResponseDataResponse);
                            break;
                        case WSConstants.TIP_ROSE_COIN_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "TipGifto Balance not enough");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Balance is not enough to transfer.");
                            break;
                        case WSConstants.TIP_ROSE_COIN_INCORRECT_PASSPHRASE:
                            Logger.e(TAG, "TipGifto Incorrect passphrase");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Password is incorrect");
                            break;
                        case WSConstants.DENY_REQUEST:
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "The another request is being processed. Please request again later.");
                            break;
                        default:
                            Logger.e(TAG, "TipGifto Unknown Error");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Unknown Error");
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "TipGifto Error: " + error.getMessage());
                if (callback != null)
                    callback.onError(null, error.getMessage());
            }
        });
    }

    /**
     * Move Gifto balance from internal balance to blockchain balance
     * @param moveGiftoRequest MoveGiftoRequest
     * @param callback Callback when moving success or error
     */
    private static void MoveGifto(final MoveGiftoRequest moveGiftoRequest, final GiftoApiResponse<GetGiftoTransactionListResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().MoveGifto(GiftoWalletManager.getAuthorization(), moveGiftoRequest, new Callback<DataResponse<GetGiftoTransactionListResponse>>()
        {
            @Override
            public void success(DataResponse<GetGiftoTransactionListResponse> transferGiftoResponseDataResponse, Response response) {
                if (transferGiftoResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "MoveGiftoBalance Unknown Error");
                    if (callback != null)
                        callback.onError(transferGiftoResponseDataResponse, "Unknown Error");
                }
                else
                {
                    switch (transferGiftoResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.MOVE_ROSE_COIN_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(transferGiftoResponseDataResponse);
                            break;
                        case WSConstants.MOVE_ROSE_COIN_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "MoveGiftoBalance Balance not enough");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Your balance is not enough to move.");
                            break;
                        case WSConstants.MOVE_ROSE_COIN_INCORRECT_PASSPHRASE:
                            Logger.e(TAG, "MoveGiftoBalance Incorrect passphrase");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Moving coin error. Password is incorrect");
                            break;
                        case WSConstants.DENY_REQUEST:
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "The another request is being processed. Please request again later.");
                            break;
                        default:
                            Logger.e(TAG, "MoveGiftoBalance Unknown Error");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "An error occurred while moving coin.");
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "MoveGiftoBalance Error: " + error.getMessage());
                if (callback != null)
                    callback.onError(null, "An error occurred while moving coin. Please check your network connection and try again.");
            }
        });
    }

    /**
     * Move all Gifto in Internal balance to blockchain balance
     * @param refreshGiftoRequest RefreshGiftoRequest contain identityData
     * @param callback Callback when updating success or error
     */
    private static void RefreshGiftoBalanceOnBlockchain(final RefreshGiftoRequest refreshGiftoRequest, final GiftoApiResponse<GetGiftoTransactionListResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().RefreshGiftoOnBlockChain(GiftoWalletManager.getAuthorization(), refreshGiftoRequest, new Callback<DataResponse<GetGiftoTransactionListResponse>>()
        {
            @Override
            public void success(DataResponse<GetGiftoTransactionListResponse> transferGiftoResponseDataResponse, Response response) {
                if (transferGiftoResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "RefreshGiftoOnBlockChain Unknown Error");
                    if (callback != null)
                        callback.onError(transferGiftoResponseDataResponse, "Unknown Error");
                }
                else
                {
                    switch (transferGiftoResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.REFRESH_ROSE_COIN_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(transferGiftoResponseDataResponse);
                            break;
                        case WSConstants.REFRESH_ROSE_COIN_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "RefreshGiftoOnBlockChain Balance not enough");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "Your balance is not enough for updating.");
                            break;
                        case WSConstants.DENY_REQUEST:
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "The another request is being processed. Please request again later.");
                            break;
                        default:
                            Logger.e(TAG, "RefreshGiftoOnBlockChain Unknown Error");
                            if (callback != null)
                                callback.onError(transferGiftoResponseDataResponse, "An error occurred while updating.");
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "RefreshGiftoOnBlockChain Error: " + error.getMessage());
                if (callback != null)
                    callback.onError(null, "An error occurred. Please check your network connection and try again.");
            }
        });
    }

    /**
     * Get Sent Records of Gifto Transaction
     * @param offset Offset is index of start record
     * @param limit Limit is number of record you want to get
     * @param callback Callback when get success or error
     */
    private static void GetTransferHistorySentRecord(final String offset, final String limit, final GiftoApiResponse<List<GetGiftoTransactionListResponse>> callback)
    {
        final Callback<DataResponse<List<GetGiftoTransactionListResponse>>> getTransactionListCallback = new Callback<DataResponse<List<GetGiftoTransactionListResponse>>>() {
            @Override
            public void success(DataResponse<List<GetGiftoTransactionListResponse>> listDataResponse, Response response) {
                if (response.getStatus() == WSConstants.RESPONSE_SUCCESS) {
                    if (listDataResponse == null) {
                        Logger.e(TAG, "Data response is NULL.");
                        if (callback != null)
                            callback.onError(null, "Unknown Error");
                        return;
                    }

                    switch (listDataResponse.getStatusCode()) {
                        case WSConstants.GET_ROSE_COIN_TRANSACTION_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(listDataResponse);
                            break;
                        default:
                            Logger.e(TAG, "Unknown error occurred: " + response.getStatus() + " --- " + response.getReason());
                            if (callback != null)
                                callback.onError(listDataResponse, "Unknown Error");
                            break;
                    }
                } else {
                    //DO SOMETHING
                    Logger.e(TAG, "Error response: " + response.getStatus() + " --- " + response.getReason());
                    if (callback != null)
                        callback.onError(listDataResponse, "Unknown Error");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "Error failure: " + error.getMessage());
                if (callback != null)
                    callback.onError(null, error.getMessage());
            }
        };

        GetGiftoTransactionRequest getGiftoTransactionRequest = new GetGiftoTransactionRequest(GiftoWalletManager.getUserIdentityData(), String.valueOf(offset), String.valueOf(limit), Constants.TRANSACTION_TYPE_ALL, Constants.TRANSACTION_MODE_SENDING);

        RestClient.instance().getRestGiftoWalletService().GetGiftoTransactionList(GiftoWalletManager.getAuthorization(), getGiftoTransactionRequest, getTransactionListCallback);
    }

    /**
     * Get Received Records of Gifto Transaction
     * @param offset Offset is index of start record
     * @param limit Limit is number of record you want to get
     * @param callback Callback when get success or error
     */
    private static void GetTransferHistoryReceivedRecord(final String offset, final String limit, final GiftoApiResponse<List<GetGiftoTransactionListResponse>> callback)
    {
        final Callback<DataResponse<List<GetGiftoTransactionListResponse>>> getTransactionListCallback = new Callback<DataResponse<List<GetGiftoTransactionListResponse>>>() {
            @Override
            public void success(DataResponse<List<GetGiftoTransactionListResponse>> listDataResponse, Response response) {
                if (response.getStatus() == WSConstants.RESPONSE_SUCCESS) {
                    if (listDataResponse == null) {
                        Logger.e(TAG, "Data response is NULL.");
                        if (callback != null)
                            callback.onError(null, "Unknown Error");
                        return;
                    }

                    switch (listDataResponse.getStatusCode()) {
                        case WSConstants.GET_ROSE_COIN_TRANSACTION_SUCCESS:
                            if (callback != null)
                                callback.onSuccess(listDataResponse);
                            break;
                        default:
                            Logger.e(TAG, "Unknown error occurred: " + response.getStatus() + " --- " + response.getReason());
                            if (callback != null)
                                callback.onError(listDataResponse, "Unknown Error");
                            break;
                    }
                } else {
                    //DO SOMETHING
                    Logger.e(TAG, "Error response: " + response.getStatus() + " --- " + response.getReason());
                    if (callback != null)
                        callback.onError(listDataResponse, "Unknown Error");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "Error failure: " + error.getMessage());
                if (callback != null)
                    callback.onError(null, error.getMessage());
            }
        };

        GetGiftoTransactionRequest getGiftoTransactionRequest = new GetGiftoTransactionRequest(GiftoWalletManager.getUserIdentityData(), String.valueOf(offset), String.valueOf(limit), Constants.TRANSACTION_TYPE_ALL, Constants.TRANSACTION_MODE_RECEIVING);

        RestClient.instance().getRestGiftoWalletService().GetGiftoTransactionList(GiftoWalletManager.getAuthorization(), getGiftoTransactionRequest, getTransactionListCallback);
    }


    // API SDK V2
    /**
     * Create wallet
     *
     * @param createWalletRequest request for creating the wallet
     * @param callback callback for response
     */
    public static void CreateWallet(final CreateWalletRequest createWalletRequest, final WalletApiResponseCallback<NoResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().CreateNewWallet(GiftoWalletManager.getAuthorization(), createWalletRequest, new Callback<JsonElement>()
        {
            ICallback mCallback = new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    String code = (String) params[0];
                    switch (code)
                    {
                        case WSConstants.RESPONSE_WALLET_CREATED:
                            if (callback != null)
                                callback.success(code, null);
                            break;
                        default:
                            WalletApiError walletApiError = JSonUtils.parseErrorResponse(params[1]);
                            if (walletApiError != null && Utils.isStringValid(walletApiError.getCode()))
                            {
                                if (callback != null)
                                    callback.failed(walletApiError);
                            }
                            else
                            {
                                if (callback != null)
                                    callback.failed(new WalletApiError(code, (String) params[1]));
                            }
                            break;
                    }
                    return null;
                }
            };

            @Override
            public void success(JsonElement sResponse, Response response) {
                if (response != null)
                    mCallback.doIt(String.valueOf(response.getStatus()), sResponse);
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, sResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getResponse() != null)
                {
                    if (error.getResponse().getBody() != null)
                    {
                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        mCallback.doIt(String.valueOf(error.getResponse().getStatus()), json);
                    }
                    else mCallback.doIt(String.valueOf(error.getResponse().getStatus()), error.getLocalizedMessage());
                }
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, "Unknown Error");
            }
        });
    }

    /**
     * Get wallet's detail
     *
     * @param getWalletDetailRequest request for getting wallet's detail
     * @param callback callback for response
     */
    public static void GetWalletDetail(final GetWalletDetailRequest getWalletDetailRequest, final WalletApiResponseCallback<WalletDetail> callback)
    {
        RestClient.instance().getRestGiftoWalletService().GetWalletDetail(GiftoWalletManager.getAuthorization(), getWalletDetailRequest, new Callback<JsonElement>()
        {
            ICallback mCallback = new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    String code = (String) params[0];
                    switch (code)
                    {
                        case WSConstants.RESPONSE_SUCCESS_STR:
                            if (callback != null)
                                callback.success(code, JSonUtils.parseWalletDetail(params[1]));
                            break;
                        default:
                            WalletApiError walletApiError = JSonUtils.parseErrorResponse(params[1]);
                            if (walletApiError != null && Utils.isStringValid(walletApiError.getCode()))
                            {
                                if (callback != null)
                                    callback.failed(walletApiError);
                            }
                            else
                            {
                                if (callback != null)
                                    callback.failed(new WalletApiError(code, (String) params[1]));
                            }
                            break;
                    }
                    return null;
                }
            };

            @Override
            public void success(JsonElement sResponse, Response response) {
                if (response != null)
                    mCallback.doIt(String.valueOf(response.getStatus()), sResponse);
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, sResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getResponse() != null)
                {
                    if (error.getResponse().getBody() != null)
                    {
                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        mCallback.doIt(String.valueOf(error.getResponse().getStatus()), json);
                    }
                    else mCallback.doIt(String.valueOf(error.getResponse().getStatus()), error.getLocalizedMessage());
                }
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, "Unknown Error");
            }
        });
    }

    /**
     * Transfer Coin
     *
     * @param transferGiftoRequest request for transferring coin
     * @param callback callback for response
     */
    public static void TransferCoin(final TransferGiftoRequest transferGiftoRequest, final WalletApiResponseCallback<NoResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().TransferCoin(GiftoWalletManager.getAuthorization(), transferGiftoRequest, new Callback<JsonElement>() {
            ICallback mCallback = new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    String code = (String) params[0];
                    switch (code)
                    {
                        case WSConstants.RESPONSE_TRANSFER_ACCEPTED:
                            if (callback != null)
                                callback.success(code, null);
                            break;
                        default:
                            WalletApiError walletApiError = JSonUtils.parseErrorResponse(params[1]);
                            if (walletApiError != null && Utils.isStringValid(walletApiError.getCode()))
                            {
                                if (callback != null)
                                    callback.failed(walletApiError);
                            }
                            else
                            {
                                if (callback != null)
                                    callback.failed(new WalletApiError(code, (String) params[1]));
                            }
                            break;
                    }
                    return null;
                }
            };

            @Override
            public void success(JsonElement sResponse, Response response) {
                if (response != null)
                    mCallback.doIt(String.valueOf(response.getStatus()), sResponse);
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, sResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getResponse() != null)
                {
                    if (error.getResponse().getBody() != null)
                    {
                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        mCallback.doIt(String.valueOf(error.getResponse().getStatus()), json);
                    }
                    else mCallback.doIt(String.valueOf(error.getResponse().getStatus()), error.getLocalizedMessage());
                }
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, "Unknown Error");
            }
        });
    }

    /**
     * Tip Coin
     *
     * @param tipGiftoRequest request for tipping coin
     * @param callback callback for response
     */
    public static void SendGift(final TipGiftoRequest tipGiftoRequest, final WalletApiResponseCallback<NoResponse> callback)
    {
        RestClient.instance().getRestGiftoWalletService().SendGift(GiftoWalletManager.getAuthorization(), tipGiftoRequest, new Callback<JsonElement>()
        {
            ICallback mCallback = new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    String code = (String) params[0];
                    switch (code)
                    {
                        case WSConstants.RESPONSE_TRANSFER_ACCEPTED:
                            if (callback != null)
                                callback.success(code, null);
                            break;
                        default:
                            WalletApiError walletApiError = JSonUtils.parseErrorResponse(params[1]);
                            if (walletApiError != null && Utils.isStringValid(walletApiError.getCode()))
                            {
                                if (callback != null)
                                    callback.failed(walletApiError);
                            }
                            else
                            {
                                if (callback != null)
                                    callback.failed(new WalletApiError(code, (String) params[1]));
                            }
                            break;
                    }
                    return null;
                }
            };

            @Override
            public void success(JsonElement sResponse, Response response) {
                if (response != null)
                    mCallback.doIt(String.valueOf(response.getStatus()), sResponse);
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, sResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getResponse() != null)
                {
                    if (error.getResponse().getBody() != null)
                    {
                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        mCallback.doIt(String.valueOf(error.getResponse().getStatus()), json);
                    }
                    else mCallback.doIt(String.valueOf(error.getResponse().getStatus()), error.getLocalizedMessage());
                }
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, "Unknown Error");
            }
        });
    }

    /**
     * Tip coin with Build-in GUI
     *
     * @param activity parrent activity
     * @param fromIdentityData sender's identity data
     * @param toIdentityData receiver's identity data
     * @param callback callback for response
     */
    public static void SendGiftWithBuildInGUI(final Activity activity, final String fromIdentityData, final String toIdentityData, final WalletApiResponseCallback<TippingCoinResponse> callback)
    {
        final TippingGiftoPopup tippingGiftoPopup = new TippingGiftoPopup(activity);
        tippingGiftoPopup.setFromIdentityData(fromIdentityData);
        tippingGiftoPopup.setToIdentityData(toIdentityData);
        tippingGiftoPopup.setAmount("");
        tippingGiftoPopup.setResponseCallback(callback);
        tippingGiftoPopup.setAnimationStyle(R.style.GiftoFadePopupAnimation);
        tippingGiftoPopup.setOnDialogTouchOutsideListener(new OnDialogTouchOutsideListener() {
            @Override
            public void onDialogTouchOutside() {
                tippingGiftoPopup.dismiss();
            }
        });
        tippingGiftoPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tippingGiftoPopup.setIsShow(false);
            }
        });
        tippingGiftoPopup.show(activity.getCurrentFocus());
    }

    /**
     * Get transaction list
     *
     * @param getGiftoTransactionRequest request for getting transaction list
     * @param callback callback for response
     */
    public static void GetTransactionList(final GetGiftoTransactionRequest getGiftoTransactionRequest, final WalletApiResponseCallback<List<GetGiftoTransactionListResponse>> callback)
    {
        RestClient.instance().getRestGiftoWalletService().GetTransactionList(GiftoWalletManager.getAuthorization(), getGiftoTransactionRequest, new Callback<JsonElement>()
        {
            ICallback mCallback = new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    String code = (String) params[0];
                    switch (code)
                    {
                        case WSConstants.RESPONSE_SUCCESS_STR:
                            if (callback != null)
                                callback.success(code, JSonUtils.parseListTransaction(params[1]));
                            break;
                        default:
                            WalletApiError walletApiError = JSonUtils.parseErrorResponse(params[1]);
                            if (walletApiError != null && Utils.isStringValid(walletApiError.getCode()))
                            {
                                if (callback != null)
                                    callback.failed(walletApiError);
                            }
                            else
                            {
                                if (callback != null)
                                    callback.failed(new WalletApiError(code, (String) params[1]));
                            }
                            break;
                    }
                    return null;
                }
            };

            @Override
            public void success(JsonElement sResponse, Response response) {
                if (response != null)
                    mCallback.doIt(String.valueOf(response.getStatus()), sResponse);
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, sResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getResponse() != null)
                {
                    if (error.getResponse().getBody() != null)
                    {
                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        mCallback.doIt(String.valueOf(error.getResponse().getStatus()), json);
                    }
                    else mCallback.doIt(String.valueOf(error.getResponse().getStatus()), error.getLocalizedMessage());
                }
                else mCallback.doIt(WSConstants.RESPONSE_INTERNAL_SERVER_ERROR, "Unknown Error");
            }
        });
    }

    private class Message
    {
        // Gifto Wallet - Gifto
//        private static final String ROSECOIN_WALLET_EXISTS = "Gifto Wallet is exists";
//        private static final String ROSECOIN_WALLET_NOT_EXISTS = "Email is not created Gifto Wallet";

        // Gifto Wallet
        private static final String ROSECOIN_WALLET_EXISTS = "Gifto Wallet is exists";
        private static final String ROSECOIN_WALLET_NOT_EXISTS = "Email is not created Gifto Wallet";
    }
}
