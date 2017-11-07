package io.gifto.wallet.networking;

import com.google.gson.JsonElement;

import java.util.List;

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
import io.gifto.wallet.networking.models.response.TransferGiftoResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by thongnguyen on 8/4/17.
 */

public interface RestGiftoWalletService {

    // +++++++++++++++++++++++++++++++++++++++++++++++++++
    // ------------------- WALLET PAYMENT APIs V1 -----------

    /**
     * This api will create a new wallet for an account if account has not had a wallet yet.
     * @param token Company's API Key
     * @param createWalletRequest CreateWalletRequest
     * @param callback Callback
     */
    @POST("/api/v1/wallets/create")
    public void CreateWallet(@Header("Authorization") String token, @Body CreateWalletRequest createWalletRequest, Callback<DataResponse<GetWalletAddressResponse>> callback);

    /**
     * This api will return the current internal Gifto balance of the wallet address
     * @param token Company's API Key
     * @param getGiftoBalanceRequest GetGiftoBalanceRequest
     * @param callback Callback
     */
    @POST("/api/v1/wallets/rosecoin/balance/internal")
    public void GetInternalGiftoBalance(@Header("Authorization") String token, @Body GetGiftoBalanceRequest getGiftoBalanceRequest, Callback<DataResponse<GetGiftoBalanceResponse>> callback);

    /**
     * This api will return the current amount Gifto which the wallet has in blockchain
     * @param token Company's API Key
     * @param getGiftoBalanceRequest GetGiftoBalanceRequest
     * @param callback Callback
     */
    @POST("/api/v1/wallets/rosecoin/balance/blockchain")
    public void GetBlockchainGiftoBalance(@Header("Authorization") String token, @Body GetGiftoBalanceRequest getGiftoBalanceRequest, Callback<DataResponse<GetGiftoBalanceResponse>> callback);

    /**
     * This api will transfer Gifto from a wallet to another wallet.
     * @param token Company's API Key
     * @param transferGiftoRequest TransferGiftoRequest
     * @param callback Callback
     */
    @POST("/api/v1/payments/rosecoin/transfer")
    public void TransferGiftoBalance(@Header("Authorization") String token, @Body TransferGiftoRequest transferGiftoRequest, Callback<DataResponse<TransferGiftoResponse>> callback);

    /**
     * This api will return the wallet address of an account in system
     * @param token Company's API Key
     * @param getWalletAddressRequest GetWalletAddressRequest
     * @param callback Callback
     */
    @POST("/api/v1/wallets/getaddress")
    public void GetWalletAddress(@Header("Authorization") String token, @Body GetWalletAddressRequest getWalletAddressRequest, Callback<DataResponse<GetWalletAddressResponse>> callback);

    /**
     * This api will list all transfer transactions belong to account based on parameter that client submit to server
     * @param token Company's API Key
     * @param getGiftoTransactionRequest GetGiftoTransactionRequest
     * @param callback Callback
     */
    @POST("/api/v1/payments/transactions/transfer/list")
    public void GetGiftoTransactionList(@Header("Authorization") String token, @Body GetGiftoTransactionRequest getGiftoTransactionRequest, Callback<DataResponse<List<GetGiftoTransactionListResponse>>> callback);

    /**
     * This api will move Gifto from internal balance to blockchain
     * @param token Company's API Key
     * @param moveGiftoRequest MoveGiftoRequest
     * @param callback Callback
     */
    @POST("/api/v1/payments/rosecoin/move")
    public void MoveGifto(@Header("Authorization") String token, @Body MoveGiftoRequest moveGiftoRequest, Callback<DataResponse<GetGiftoTransactionListResponse>> callback);

    /**
     * This api will list all move Gifto transactions belong to account based on parameter that client submit to server
     * @param token Company's API Key
     * @param getGiftoTransactionRequest GetGiftoTransactionRequest
     * @param callback Callback
     */
    @POST("/api/v1/payments/transactions/move/list")
    public void GetMoveGiftoTransactionList(@Header("Authorization") String token, @Body GetGiftoTransactionRequest getGiftoTransactionRequest, Callback<DataResponse<List<GetGiftoTransactionListResponse>>> callback);

    /**
     * This api will send Gifto tipping from a wallet to another wallet.
     * @param token Company's API Key
     * @param tipGiftoRequest TipGiftoRequest
     * @param callback Callback
     */
    @POST("/api/v1/payments/rosecoin/tip")
    public void TippingGifto(@Header("Authorization") String token, @Body TipGiftoRequest tipGiftoRequest, Callback<DataResponse<GetGiftoTransactionListResponse>> callback);


    /**
     * Move all Gifto in Internal balance to blockchain balance
     * @param token Company's API Key
     * @param refreshGiftoRequest RefreshGiftoRequest
     * @param callback Callback
     */
    @POST("/api/v1/payments/rosecoin/update")
    public void RefreshGiftoOnBlockChain(@Header("Authorization") String token, @Body RefreshGiftoRequest refreshGiftoRequest, Callback<DataResponse<GetGiftoTransactionListResponse>> callback);

    // ---------------------------------------------------
    // +++++++++++++++++++++++++++++++++++++++++++++++++++


    // +++++++++++++++++++++++++++++++++++++++++++++++++++
    // ------------------- WALLET PAYMENT APIs V2 --------
    /**
     * Create wallet api
     *
     * @param token authorization token
     * @param createWalletRequest request for creating wall
     * @param callback callback for response
     */
    @POST("/apiv2/v2/wallets/create")
    public void CreateNewWallet(@Header("Authorization") String token, @Body CreateWalletRequest createWalletRequest, Callback<JsonElement> callback);

    /**
     * Get wallet's detail
     *
     * @param token authorization token
     * @param getWalletDetailRequest request for getting wallet's detail
     * @param callback callback for response
     */
    @POST("/apiv2/v2/wallets/detail")
    public void GetWalletDetail(@Header("Authorization") String token, @Body GetWalletDetailRequest getWalletDetailRequest, Callback<JsonElement> callback);

    /**
     * Transfer coin
     *
     * @param token authorization token
     * @param transferGiftoRequest request for transferring coin
     * @param callback callback for response
     */
    @POST("/apiv2/v2/payments/transfer")
    public void TransferCoin(@Header("Authorization") String token, @Body TransferGiftoRequest transferGiftoRequest, Callback<JsonElement> callback);

    /**
     * Tip coin
     *
     * @param token authorization token
     * @param tipGiftoRequest request for tipping coin
     * @param callback callback for response
     */
    @POST("/apiv2/v2/payments/tip")
    public void SendGift(@Header("Authorization") String token, @Body TipGiftoRequest tipGiftoRequest, Callback<JsonElement> callback);

    /**
     * Get transaction list
     *
     * @param token authorization token
     * @param getGiftoTransactionRequest request for getting transaction list
     * @param callback callback for response
     */
    @POST("/apiv2/v2/payments/transactions/transfer/list")
    public void GetTransactionList(@Header("Authorization") String token, @Body GetGiftoTransactionRequest getGiftoTransactionRequest, Callback<JsonElement> callback);

    // ---------------------------------------------------
    // +++++++++++++++++++++++++++++++++++++++++++++++++++
}
