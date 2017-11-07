package io.gifto.wallet.networking.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 9/8/17.
 */

public class MoveGiftoRequest
{
    @SerializedName("identityData")
    private String identityData;

    @SerializedName("amount")
    private String amount;

    @SerializedName("passphrase")
    private String passphrase;

    public MoveGiftoRequest(String identityData, String amount, String passphrase)
    {
        this.identityData = identityData;
        this.amount = amount;
        this.passphrase = passphrase;
    }

    public String getIdentityData() {
        return identityData;
    }

    public void setIdentityData(String identityData) {
        this.identityData = identityData;
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
}
