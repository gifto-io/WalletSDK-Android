package io.gifto.wallet.networking.models.response;

import com.google.gson.annotations.SerializedName;

import io.gifto.wallet.model.TransactionUserInfo;

/**
 * Created by thongnguyen on 8/21/17.
 */

public class GetGiftoTransactionListResponse {

    /**
     "transactionId": "599a6aa1e4b023ebc8ec9dbe",
     "amount": "1000000.00000",
     "fromWalletAddress": "0xb04ace0720bd9c08a320d43d408d8cce2d321d37",
     "toWalletAddress": "0xa44e95c960041b9edcc87247b8d86260086ae735",
     "currencyCode": "RSC",
     "transactionAddress": "0xe670ec64341771606e55d6b4ca35a1a6b75ee3d5145a99d05921026d1527331",
     "note": "Send 1 million Rosecoin",
     "status": "2",
     "transactionType": "1",
     "createdAt": "1503294470740"
     */

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("amount")
    private String amount;

    @SerializedName("fromWalletAddress")
    private String fromWalletAddress;

    @SerializedName("toWalletAddress")
    private String toWalletAddress;

    @SerializedName("currencyCode")
    private String currencyCode;

    @SerializedName("transactionAddress")
    private String transactionAddress;

    @SerializedName("note")
    private String note;

    @SerializedName("status")
    private String status;

    @SerializedName("transactionType")
    private String transactionType;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("transferFee")
    private String transferFee;

    // API V2
    @SerializedName("from")
    private TransactionUserInfo from;

    @SerializedName("to")
    private TransactionUserInfo to;


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFromWalletAddress() {
        return fromWalletAddress;
    }

    public void setFromWalletAddress(String fromWalletAddress) {
        this.fromWalletAddress = fromWalletAddress;
    }

    public String getToWalletAddress() {
        return toWalletAddress;
    }

    public void setToWalletAddress(String toWalletAddress) {
        this.toWalletAddress = toWalletAddress;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getTransactionAddress() {
        return transactionAddress;
    }

    public void setTransactionAddress(String transactionAddress) {
        this.transactionAddress = transactionAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(String transferFee) {
        this.transferFee = transferFee;
    }

    public TransactionUserInfo getFrom() {
        return from;
    }

    public void setFrom(TransactionUserInfo from) {
        this.from = from;
    }

    public TransactionUserInfo getTo() {
        return to;
    }

    public void setTo(TransactionUserInfo to) {
        this.to = to;
    }
}
