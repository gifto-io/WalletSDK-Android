package io.gifto.wallet.model;

/**
 * Created by thongnguyen on 10/16/17.
 *
 * Define statuses of transaction
 */
public enum TransactionStatus {

    SUCCESS("success"),
    FAILED("failed"),
    PROCESSING("processing");

    private String name;

    TransactionStatus(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
