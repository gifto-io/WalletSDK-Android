package io.gifto.wallet.utils;

/**
 * Created by ThongNguyen on 9/16/17.
 */
public class Constants
{
    /**
     * Wallet Constant
     */

    /**
     * Types of transaction
     */
    public static final String TRANSACTION_TYPE_ALL = "all";
    public static final String TRANSACTION_TYPE_TRANSFER = "transfer";
    public static final String TRANSACTION_TYPE_MOVE = "move";
    public static final String TRANSACTION_TYPE_TIP = "tip";
    public static final String TRANSACTION_TYPE_UPDATE = "update";

    /**
     * Modes of transaction
     */
    public static final String TRANSACTION_MODE_SENDING = "send";
    public static final String TRANSACTION_MODE_RECEIVING = "receive";

    /**
     * Minimum amount for transferring
     */
    public static final double MIN_TRANSFER_AMOUNT = 0.00001;

    /**
     *  Gifto currency code
     */
    public static final String CURRENCY_CODE = "RSC";

    /**
     * Kryptor currency code
     */
//    public static final String CURRENCY_CODE = "KRYPTOR";

    public static final int USER_NAME_LIMIT_CHARACTER = 15;

    public static final boolean DISPLAY_MULTIPLE_COIN = false;
    /***************************/
}
