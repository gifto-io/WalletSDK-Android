package io.gifto.wallet.networking.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 10/16/17.
 */

public class WalletApiError {

    @SerializedName("code")
    private String code;

    @SerializedName("msg")
    private String msg;

    public WalletApiError(String errorCode, String message)
    {
        this.code = errorCode;
        this.msg = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
