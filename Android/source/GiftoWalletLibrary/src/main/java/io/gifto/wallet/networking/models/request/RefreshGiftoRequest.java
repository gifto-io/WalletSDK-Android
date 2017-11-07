package io.gifto.wallet.networking.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class RefreshGiftoRequest {

    @SerializedName("identityData")
    private String identityData;

    public RefreshGiftoRequest(String identityData) {
        this.identityData = identityData;
    }

    public String getIdentityData() {
        return identityData;
    }

    public void setIdentityData(String identityData) {
        this.identityData = identityData;
    }
}
