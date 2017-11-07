package io.gifto.wallet.networking.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class GetGiftoBalanceRequest {

    @SerializedName("walletAddress")
    private String walletAddress;

    public GetGiftoBalanceRequest(String walletAddress)
    {
        this.walletAddress = walletAddress;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
