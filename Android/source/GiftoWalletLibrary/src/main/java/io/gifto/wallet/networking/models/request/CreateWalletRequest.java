package io.gifto.wallet.networking.models.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 8/4/17.
 *
 * Request to create wallet
 */

public class CreateWalletRequest {

    @SerializedName("identityData")
    private String identityData;

    @SerializedName("passphrase")
    private String passphrase;

    //API V2
    @SerializedName("firstname")
    private String firstName;

    @SerializedName("lastname")
    private String lastName;

    @SerializedName("currencyCode")
    private String currencyCode;

    public CreateWalletRequest(String identityData, String passphrase)
    {
        this.identityData = identityData;
        this.passphrase = passphrase;
    }

    public CreateWalletRequest(String identityData, String passphrase, String firstName, String lastName, String currencyCode)
    {
        this.identityData = identityData;
        this.passphrase = passphrase;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currencyCode = currencyCode;
    }

    public String getIdentityData() {
        return identityData;
    }

    public void setIdentityData(String identityData) {
        this.identityData = identityData;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
