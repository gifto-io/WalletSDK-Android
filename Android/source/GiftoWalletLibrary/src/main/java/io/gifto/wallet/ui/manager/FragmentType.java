package io.gifto.wallet.ui.manager;

/**
 * Created by ThangPM on 9/17/15.
 */
public enum FragmentType
{
    ROSE_COIN(1, "Gifto"),
    TRANSFER_ROSE_COIN(2, "Transfer Gifto"),
    CREATE_ROSE_COIN_WALLET(2, "Create Gifto Wallet"),
    ROSE_COIN_HISTORY(2, "Gifto History"),
    TRANSFER_ROSE_COIN_HISTORY(2, "Transfer Gifto History"),
    HISTORY_DETAIL(3, "History Detail"),
    SHOW_WALLET_ADDRESS(2, "Show Wallet Address"),
    SIGN_IN_ROSE_COIN_WALLET(1, "Sign In Gifto Wallet"),
    TIP_ROSE_COIN(1, "Tip Gifto"),
    UNKNOWN;

    private int level;
    private String name;

    FragmentType()
    {

    }

    FragmentType(int id, String name)
    {
        this.level = level;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getLevel()
    {
        return level;
    }
}
