package io.gifto.wallet.networking.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 9/10/17.
 */

public class TipGiftoRequest {
    /**
     * {
     "fromIdentityData": "exampleuser1@gmail.com",
     "toIdentityData": "exampleuser2@gmail.com",
     "amount": "10",
     "passphrase": "p@Ssw#!@!#@!d"
     }
     */

    @SerializedName("fromIdentityData")
    private String fromIdentityData;

    @SerializedName("toIdentityData")
    private String toIdentityData;

    @SerializedName("amount")
    private String amount;

    @SerializedName("passphrase")
    private String passphrase;

    @SerializedName("currencyCode")
    private String currencyCode;

    public TipGiftoRequest(String fromIdentityData, String toIdentityData, String amount, String passphrase, String currencyCode)
    {
        this.fromIdentityData = fromIdentityData;
        this.toIdentityData = toIdentityData;
        this.amount = amount;
        this.passphrase = passphrase;
        this.currencyCode = currencyCode;
    }

    public String getFromIdentityData() {
        return fromIdentityData;
    }

    public void setFromIdentityData(String fromIdentityData) {
        this.fromIdentityData = fromIdentityData;
    }

    public String getToIdentityData() {
        return toIdentityData;
    }

    public void setToIdentityData(String toIdentityData) {
        this.toIdentityData = toIdentityData;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
