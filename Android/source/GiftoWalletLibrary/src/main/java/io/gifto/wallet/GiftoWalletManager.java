package io.gifto.wallet;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.gifto.wallet.model.WalletBalanceDetail;
import io.gifto.wallet.model.WalletDetail;
import io.gifto.wallet.utils.AES256Cipher;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.PrefConstants;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.CustomSharedPreferences;

/**
 * Created by thongnguyen on 9/11/17.
 */

public class GiftoWalletManager {
    private String authorization = "";
    private String userIdentityData = "";
    private String userWalletAddress = "";
    private Bitmap userWalletAddressQRCode = null;

    private WalletDetail userWalletDetail;

    private static GiftoWalletManager instance;

    public static GiftoWalletManager getInstance() {
        if (instance == null)
            instance = new GiftoWalletManager();
        return instance;
    }

    private GiftoWalletManager()
    {
    }

    /**
     * Check whether should create wallet or not
     * @return  1: create wallet with passphrase
     *          2: create wallet without passphrase
     *          -1: Don't need to create wallet
     */
    public int needToAddOrCreateWallet()
    {
        // User doesn't have any wallet
        if (userWalletDetail == null || userWalletDetail.getWallets() == null || userWalletDetail.getWallets().size() <= 0)
            return 1;

        HashMap<String, List<String>> currencyMap = new HashMap<>();
        boolean hasCurrentlyWallet = false;

        for (WalletBalanceDetail walletBalanceDetail : userWalletDetail.getWallets())
        {
            if (Utils.isStringValid(walletBalanceDetail.getAddress()))
            {
                if (walletBalanceDetail.getCurrencyCode().equals(Constants.CURRENCY_CODE))
                    hasCurrentlyWallet = true;

                List<String> currencies = currencyMap.get(walletBalanceDetail.getAddress());
                if (currencies == null)
                    currencies = new ArrayList<>();

                currencies.add(walletBalanceDetail.getCurrencyCode());

                currencyMap.put(walletBalanceDetail.getAddress(), currencies);
            }
        }

        if (currencyMap.isEmpty())
            return 1;

        if (hasCurrentlyWallet)
            return -1;
        else return  2;
    }

    private static GiftoWalletManager build(String apiKey, String userIdentityData)
    {
        instance = new GiftoWalletManager();
        instance.authorization = apiKey;
        instance.userIdentityData = userIdentityData;
        return instance;
    }

    /**
     * Get authorization token
     *
     * @return authorization token
     */
    public static String getAuthorization() {
        GiftoWalletManager roseCoinManager = getInstance();
        if (roseCoinManager != null)
            return roseCoinManager.authorization;
        return "";
    }

    /**
     * Get user's identity data
     *
     * @return user's identity data
     */
    public static String getUserIdentityData() {
        GiftoWalletManager roseCoinManager = getInstance();
        if (roseCoinManager != null)
            return roseCoinManager.userIdentityData;
        return "";
    }

    /**
     * Get wallet's address
     *
     * @return wallet's address
     */
    public static String getUserWalletAddress()
    {
        GiftoWalletManager roseCoinManager = getInstance();
        if (roseCoinManager != null)
        {
            if (!Utils.isStringValid(roseCoinManager.userWalletAddress))
                roseCoinManager.userWalletAddress = CustomSharedPreferences.getPreferences(PrefConstants.PREF_USER_WALLET_ADDRESS, "");
            return roseCoinManager.userWalletAddress;

        }
        return "";
    }

    /**
     * Get QRCode of wallet's address
     *
     * @return QRCode
     */
    public static Bitmap getUserWalletAddressQRCode()
    {
        return getInstance().userWalletAddressQRCode;
    }

    /**
     * Set wallet's address
     *
     * @param walletAddress wallet's address
     */
    public static void setUserWalletAddress(String walletAddress)
    {
        GiftoWalletManager roseCoinManager = getInstance();
        if (roseCoinManager != null)
        {
            roseCoinManager.userWalletAddress = walletAddress;
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_WALLET_ADDRESS, walletAddress);
        }
    }

    /**
     * Set QRCode for wallet's address
     *
     * @param qrCode bitmap of qrcode
     */
    public static void setUserWalletAddressQRCode(Bitmap qrCode)
    {
        getInstance().userWalletAddressQRCode = qrCode;
    }

    /**
     * Get user's passphrase
     *
     * @return user's passphrase
     */
    public static String getUserSecurePassphrase()
    {
        return AES256Cipher.Decrypt(getAuthorization(), getUserIdentityData(), CustomSharedPreferences.getPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, ""));
    }

    /**
     * Set user's passphrase
     *
     * @param passphrase passphrase
     */
    public static void setUserSecurePassphrase(@NonNull  String passphrase)
    {
        if (Utils.isStringValid(passphrase))
        {
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, AES256Cipher.Encrypt(getAuthorization(), getUserIdentityData(), passphrase));
        }
        else CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
    }

    /**
     * Check whether SDK store user's passphrase or not
     *
     * @return true if SDK store user's passphrase
     *          false else
     */
    public static boolean isUsingStorePassphrase()
    {
        return CustomSharedPreferences.getPreferences(PrefConstants.PREF_IS_STORE_PASSPHRASE, false);
    }

    /**
     * Clear QRCode data memory
     */
    public static void RecycleQRCode()
    {
        try {
            if (getInstance().userWalletAddressQRCode != null)
            {
                getInstance().userWalletAddressQRCode.recycle();
                getInstance().userWalletAddressQRCode = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            getInstance().userWalletAddressQRCode = null;
        }
    }

    /**
     * Get user's wallet detail
     *
     * @return wallet's detail
     */
    public static WalletDetail getUserWalletDetail()
    {
        if (getInstance() != null)
            return getInstance().userWalletDetail;
        return null;
    }

    /**
     * Get balance detail by currencyCode
     *
     * @param currencyCode currencyCode
     * @return balance detail
     */
    public static WalletBalanceDetail getWalletBalanceDetail(String currencyCode)
    {
        if (getInstance() != null && getInstance().userWalletDetail != null)
        {
            if (getInstance().userWalletDetail.getWallets() != null)
            {
                for (WalletBalanceDetail walletBalanceDetail : getInstance().userWalletDetail.getWallets())
                {
                    if (walletBalanceDetail.getCurrencyCode().equals(currencyCode))
                        return walletBalanceDetail;
                }
            }
        }
        return null;
    }

    /**
     * Set user wallet detial
     *
     * @param userWalletDetail wallet detail
     */
    public static void setUserWalletDetail(WalletDetail userWalletDetail)
    {
        if (getInstance() != null)
            getInstance().userWalletDetail = userWalletDetail;
    }

    public static void ClearData()
    {
        if (getInstance() != null)
        {
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_AUTHEN_TOKEN, "");
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_ENCRYPTION_IV, "");
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_IS_STORE_PASSPHRASE, false);
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, false);
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_IDENTITY_DATA, "");
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_WALLET_ADDRESS, "");
        }
    }

    /**
     * Gifto manager's builder
     */
    public static class Builder
    {
        Context mContext;
        String apiKey;
        String userIdentityData;
        boolean isStorePassphrase = false;

        public Builder(@NonNull Context context)
        {
            mContext = context;
        }

        /**
         * Set api key
         *
         * @param apiKey apikey
         * @return this
         */
        public Builder setApiKey(@NonNull String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Set user's identity data
         *
         * @param userIdentityData identity data
         * @return this
         */
        public Builder setUserIdentityData(@NonNull String userIdentityData) {
            this.userIdentityData = userIdentityData;
            return this;
        }

        /**
         * Allow SDK store user's passphrase or not
         *
         * @param isStorePassphrase true if allow
         * @return this
         */
        public Builder setUsingStoringPassphrase(boolean isStorePassphrase)
        {
            this.isStorePassphrase = isStorePassphrase;
            return this;
        }

        /**
         * Build Gifto manager
         *
         * @return GiftoWalletManager instance
         */
        public GiftoWalletManager build()
        {
            if (mContext == null)
                throw new NullPointerException("Context must not be null");
            if (!Utils.isStringValid(apiKey))
                throw new NullPointerException("API key must not be null or empty");
            if (!Utils.isStringValid(userIdentityData))
                throw new NullPointerException("Identity-data must not be null or empty");

            CustomSharedPreferences.Init(mContext);

            GiftoWalletManager.build(apiKey, userIdentityData);

            String packageID = CustomSharedPreferences.getPreferences(PrefConstants.PREF_PACKAGE_ID, "");
            if (!Utils.isStringValid(packageID) || !packageID.equals(getClass().getPackage().toString()))
            {
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_AUTHEN_TOKEN, "");
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_ENCRYPTION_IV, "");
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_IS_STORE_PASSPHRASE, false);
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, false);
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_IDENTITY_DATA, "");
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_WALLET_ADDRESS, "");
            }

            CustomSharedPreferences.setPreferences(PrefConstants.PREF_PACKAGE_ID, getClass().getPackage().toString());

            CustomSharedPreferences.setPreferences(PrefConstants.PREF_IS_STORE_PASSPHRASE, isStorePassphrase);

            if (isStorePassphrase)
            {
                String preIdentityData = AES256Cipher.Decrypt(apiKey, userIdentityData, CustomSharedPreferences.getPreferences(PrefConstants.PREF_USER_IDENTITY_DATA, ""));
                if (preIdentityData == null || !preIdentityData.equals(userIdentityData))
                {
                    CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
                    CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, false);
                    CustomSharedPreferences.setPreferences(PrefConstants.PREF_ENCRYPTION_IV, "");
                }
            }
            else
            {
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, false);
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_ENCRYPTION_IV, "");
            }

            CustomSharedPreferences.setPreferences(PrefConstants.PREF_AUTHEN_TOKEN, AES256Cipher.Encrypt(apiKey, userIdentityData, apiKey));
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_IDENTITY_DATA, AES256Cipher.Encrypt(apiKey, userIdentityData, userIdentityData));

            return GiftoWalletManager.getInstance();
        }
    }
}
