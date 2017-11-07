package io.gifto.wallet.model;

/**
 * Created by thongnguyen on 10/14/17.
 *
 * Define type of transferring
 */
public enum TransferType {

    TRANSFER("transfer"),
    TIP("tip");

    private String name;

    TransferType(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
