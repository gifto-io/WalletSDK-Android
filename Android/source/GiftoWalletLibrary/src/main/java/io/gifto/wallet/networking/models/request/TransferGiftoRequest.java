package io.gifto.wallet.networking.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class TransferGiftoRequest {

    @SerializedName("identityData")
    private String identityData;

    @SerializedName("fromWalletAdress")
    private String fromWalletAdress;

    @SerializedName("toWalletAddress")
    private String toWalletAddress;

    @SerializedName("amount")
    private String amount;

    @SerializedName("passphrase")
    private String password;

    @SerializedName("note")
    private String note;

    // API v2
    @SerializedName("referenceId")
    private String referenceId;

    @SerializedName("transferFeeType")
    private String transferFeeType; //"sender/receiver"

    @SerializedName("type")
    private String type; // transfer/tip

    @SerializedName("currencyCode")
    private String currencyCode;

    public TransferGiftoRequest(String identityData, String fromWalletAdress, String toWalletAddress, String amount, String password, String note)
    {
        this.identityData = identityData;
        this.fromWalletAdress = fromWalletAdress;
        this.toWalletAddress = toWalletAddress;
        this.amount = amount;
        this.password = password;
        this.note = note;
    }

    public TransferGiftoRequest(String identityData, String toWalletAddress, String amount, String password, String note, String referenceId, String transferFeeType, String type, String currencyCode)
    {
        this.identityData = identityData;
        this.fromWalletAdress = null;
        this.toWalletAddress = toWalletAddress;
        this.amount = amount;
        this.password = password;
        this.note = note;

        this.referenceId = referenceId;
        this.transferFeeType = transferFeeType;
        this.type = type;
        this.currencyCode = currencyCode;
    }

    public String getIdentityData() {
        return identityData;
    }

    public void setIdentityData(String identityData) {
        this.identityData = identityData;
    }

    public String getFromWalletAdress() {
        return fromWalletAdress;
    }

    public void setFromWalletAdress(String fromWalletAdress) {
        this.fromWalletAdress = fromWalletAdress;
    }

    public String getToWalletAddress() {
        return toWalletAddress;
    }

    public void setToWalletAddress(String toWalletAddress) {
        this.toWalletAddress = toWalletAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTransferFeeType() {
        return transferFeeType;
    }

    public void setTransferFeeType(String transferFeeType) {
        this.transferFeeType = transferFeeType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
