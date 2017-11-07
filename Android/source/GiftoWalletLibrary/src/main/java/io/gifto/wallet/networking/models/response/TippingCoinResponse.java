package io.gifto.wallet.networking.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 10/16/17.
 */

public class TippingCoinResponse {

    @SerializedName("fromIdentityData")
    private String from;

    @SerializedName("toIdentityData")
    private String to;

    @SerializedName("currencyCode")
    private String currencyCode;

    @SerializedName("amount")
    private String amount;

    public TippingCoinResponse(String from, String to, String currencyCode, String amount)
    {
        this.setFrom(from);
        this.setTo(to);
        this.setCurrencyCode(currencyCode);
        this.setAmount(amount);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
