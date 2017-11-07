package io.gifto.wallet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 10/14/17.
 *
 * User's info is included in transaction's detail
 */
public class TransactionUserInfo {
    @SerializedName("identityData")
    private String identityData;

    @SerializedName("address")
    private String address;

    @SerializedName("name")
    private String name;

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
     *
     * @param identityData identity data
     */
    public void setIdentityData(String identityData) {
        this.identityData = identityData;
    }

    /**
     * Get wallet's address
     *
     * @return wallet's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set wallet's address
     *
     * @param address wallet's address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get user's name
     * @return user's name
     */
    public String getName() {
        if (name != null)
            return name.trim();
        return null;
    }

    /**
     * Set user's name
     * @param name user's name
     */
    public void setName(String name) {
        this.name = name;
    }
}
