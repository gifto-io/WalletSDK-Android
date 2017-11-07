package io.gifto.wallet.model;

/**
 * Created by thongnguyen on 10/14/17.
 *
 * Define types of transaction's fee - who is paying transaction fee
 */
public enum TransferFeeType {

    SENDER("sender"),
    RECEIVER("receiver");

    private String name;

    TransferFeeType(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
