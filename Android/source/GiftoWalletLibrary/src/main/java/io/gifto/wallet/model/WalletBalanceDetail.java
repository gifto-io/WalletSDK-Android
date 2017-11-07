package io.gifto.wallet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 10/14/17.
 *
 * Balance's detail of wallet
 */
public class WalletBalanceDetail {
    @SerializedName("currencyCode")
    private String currencyCode;

    @SerializedName("address")
    private String address;

    @SerializedName("onChained")
    private String onChanined;

    @SerializedName("offChained")
    private String offChanined;

    @SerializedName("blocked")
    private String blocked;


    /**
     * Get currency code
     *
     * @return currencyCode
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Set currency code
     *
     * @param currencyCode currencyCode
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Get wallet's address
     *
     * @return wallet's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set wallet's address
     *
     * @param address wallet's address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get on-chained balance
     *
     * @return on-chained balance
     */
    public String getOnChanined() {
        return onChanined;
    }

    /**
     * Set on-chained balance
     *
     * @param onChanined on-chained balance
     */
    public void setOnChanined(String onChanined) {
        this.onChanined = onChanined;
    }

    /**
     * Get off-chained balance
     *
     * @return off-chained balance
     */
    public String getOffChanined() {
        return offChanined;
    }

    /**
     * Set off-chained balance
     * @param offChanined off-chained balance
     */
    public void setOffChanined(String offChanined) {
        this.offChanined = offChanined;
    }

    /**
     * Get blocked balance
     *
     * @return blocked balance
     */
    public String getBlocked() {
        return blocked;
    }

    /**
     * Set blocked balance
     * @param blocked blocked balance
     */
    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    /**
     * Get currency Name
     *
     * @return currency Name
     */
    public String getCurrencyName()
    {
        return WalletCurrency.GetNameByCode(currencyCode);
    }
}
