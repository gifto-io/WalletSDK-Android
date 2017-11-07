package io.gifto.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thongnguyen on 10/14/17.
 *
 * Detail of wallet
 */
public class WalletDetail {
    @SerializedName("identityData")
    private String identityData;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("lastname")
    private String lastName;

    @SerializedName("wallets")
    private List<WalletBalanceDetail> wallets;


    /**
     * Get identity data
     *
     * @return identity data
     */
    public String getIdentityData() {
        return identityData;
    }

    /**
     * Set identity data
     * @param identityData identity data
     */
    public void setIdentityData(String identityData) {
        this.identityData = identityData;
    }

    /**
     * Get first name
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set first name
     *
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get last name
     *
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set last name
     *
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get list wallet's balance detail
     *
     * @return wallet's balance detail
     */
    public List<WalletBalanceDetail> getWallets() {
        return wallets;
    }

    /**
     * Set wallet's balance detail
     *
     * @param wallets wallet's balance detail
     */
    public void setWallets(List<WalletBalanceDetail> wallets) {
        this.wallets = wallets;
    }
}
