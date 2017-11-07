package io.gifto.wallet.networking.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class GetWalletAddressResponse {

    @SerializedName("walletAddress")
    private String walletAddress;

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
