package io.gifto.wallet.networking.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thongnguyen on 10/17/17.
 */

public class ErrorResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("msg")
    private String msg;

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
