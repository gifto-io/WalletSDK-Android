package io.gifto.wallet.networking.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 9/8/17.
 */

public class GetGiftoTransactionRequest {

    @SerializedName("identityData")
    private String identityData;

    @SerializedName("offset")
    private String offset;

    @SerializedName("limit")
    private String limit;

    @SerializedName("transactionType")
    private String transactionType;

    @SerializedName("transactionMode")
    private String transactionMode;

    public GetGiftoTransactionRequest(String identityData, String offset, String limit, String transactionType, String transactionMode)
    {
        this.setIdentityData(identityData);
        this.setOffset(offset);
        this.setLimit(limit);
        this.setTransactionType(transactionType);
        this.setTransactionMode(transactionMode);
    }

    public String getIdentityData() {
        return identityData;
    }

    public void setIdentityData(String identityData) {
        this.identityData = identityData;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }
}
