package io.gifto.wallet.networking;

/**
 * Created by thongnguyen on 07/09/17.
 */
public class WSConstants
{
    /** Http Response code Wallet API v2
    400 bad request
    400001 json is not valid
    400002 missing required data
    400003 not support currencyCode
    400004 invalid max length data ( plus body for fields )
    400005 minimum coin required ( >= 0.00001)
    400006 balance is not enough
    400007 invalid amount
    400008 transferFeeType is not support
    400009 transfer type is not support

    401 Unauthorized
    401001 invalid token
    401002 passphrase is not correct

    404 not found
    404001 identityData is not found
    404002 receiver not found

    405 Method Not Allowed
    409 Conflict (tao lan 2 ==> n)

    500 Internal Server Error
    * */

    /**
     * Bad request
     */
    public static final String RESPONSE_BAD_REQUEST = "400";
    /**
     * Json format invalid
     */
    public static final String RESPONSE_JSON_NOT_VALID = "400001";
    /**
     * Missing required data
     */
    public static final String RESPONSE_MISSING_REQUIRED_DATA = "400002";
    /**
     * Currency code is not supported
     */
    public static final String RESPONSE_CURRENCY_CODE_NOT_SUPPORT = "400003";
    /**
     * Invalid max data length
     */
    public static final String RESPONSE_INVALID_MAX_LENGTH_DATA = "400004";
    /**
     * Minimum coin required, using for transferring or tipping
     */
    public static final String RESPONSE_MINIMUM_COIN_REQUIRED = "400005";
    /**
     * Balance is not enough to transfer or tip
     */
    public static final String RESPONSE_BALANCE_NOT_ENOUGH = "400006";
    /**
     * Invalid amount format
     */
    public static final String RESPONSE_INVALID_AMOUNT = "400007";
    /**
     * Transfer fee type is not supported. Supported transfer fee type: {sender, receiver}
     */
    public static final String RESPONSE_TRANSFER_FEE_TYPE_NOT_SUPPORT = "400008";
    /**
     * Transfer type is not supported. Supported transfer type: {transfer, tip}
     */
    public static final String RESPONSE_TRANSFER_TYPE_NOT_SUPPORT = "400009";
    /**
     * Unauthorized
     */
    public static final String RESPONSE_UNAUTHORIZED = "401";
    /**
     * Invalid authorization token
     */
    public static final String RESPONSE_INVALID_TOKEN = "401001";
    /**
     * Passphrase is incorrect
     */
    public static final String RESPONSE_PASSPHRASE_INCORRECT = "401002";
    /**
     * Forbidden
     */
    public static final String RESPONSE_FORBIDDEN = "403";
    /**
     * Not found any information
     */
    public static final String RESPONSE_NOT_FOUND = "404";
    /**
     * Not found identity data
     */
    public static final String RESPONSE_IDENTITY_DATA_NOT_FOUND = "404001";
    /**
     * Receiver not found
     */
    public static final String RESPONSE_RECEIVER_NOT_FOUND = "404002";
    /**
     * Method is not allowed
     */
    public static final String RESPONSE_METHOD_NOT_ALLOWED = "405";
    /**
     * Conflict request (create new wallet when already has a wallet)
     */
    public static final String RESPONSE_CONFLICT = "409";
    /**
     * Internal server error
     */
    public static final String RESPONSE_INTERNAL_SERVER_ERROR = "500";
    /**
     * Service is unavailable
     */
    public static final String RESPONSE_SERVICE_UNAVAILABLE = "503";

    /**
     * Request successful - using for general request
     */
    public static final int     RESPONSE_SUCCESS = 200;
    public static final String  RESPONSE_SUCCESS_STR = "200";
    /**
     * Creating wallet successful
     */
    public static final String RESPONSE_WALLET_CREATED = "201";
    /**
     * Transferring request is accepted
     */
    public static final String RESPONSE_TRANSFER_ACCEPTED = "202";



    // GENERAL STATUS CODE - API v1
    public static final String DENY_REQUEST = "FF05";

    // --------------------------------------------------------------------
    // +++++++++++++++++++++++ WALLET PAYMENT +++++++++++++++++++++++++
    public static final String GET_INTERNAL_ROSE_COIN_BALANCE_SUCCESS = "90C1";
    public static final String GET_INTERNAL_ROSE_COIN_BALANCE_ADDRESS_NOT_EXISTS = "90C2";

    public static final String GET_BLOCKCHAIN_ROSE_COIN_BALANCE_SUCCESS = "90D1";
    public static final String GET_BLOCKCHAIN_ROSE_COIN_BALANCE_ADDRESS_NOT_EXISTS = "90D2";

    public static final String TRANSFER_ROSE_COIN_SUCCESS = "91A1";
    public static final String TRANSFER_ROSE_COIN_BALANCE_NOT_ENOUGH = "91A3";
    public static final String TRANSFER_ROSE_COIN_INCORRECT_PASSPHRASE = "91A4";

    public static final String MOVE_ROSE_COIN_SUCCESS = "91B1";
    public static final String MOVE_ROSE_COIN_BALANCE_NOT_ENOUGH = "91B3";
    public static final String MOVE_ROSE_COIN_INCORRECT_PASSPHRASE = "91B4";

    public static final String TIP_ROSE_COIN_SUCCESS = "91E1";
    public static final String TIP_ROSE_COIN_BALANCE_NOT_ENOUGH = "91E3";
    public static final String TIP_ROSE_COIN_INCORRECT_PASSPHRASE = "91E4";

    public static final String GET_WALLET_ADDRESS_SUCCESS = "90B1";
    public static final String GET_WALLET_ADDRESS_NOT_EXISTS = "90B2";

    public static final String CREATE_WALLET_SUCCESS = "90A1";
    public static final String CREATE_WALLET_EXISTS = "90A2";
    public static final String CREATE_WALLET_ERROR = "90A3";

    public static final String GET_ROSE_COIN_TRANSACTION_SUCCESS = "91C1";
    public static final String GET_MOVE_ROSE_COIN_TRANSACTION_SUCCESS = "91D1";

    public static final String REFRESH_ROSE_COIN_SUCCESS = "91F1";
    public static final String REFRESH_ROSE_COIN_BALANCE_NOT_ENOUGH = "91F3";

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // --------------------------------------------------------------------
}
