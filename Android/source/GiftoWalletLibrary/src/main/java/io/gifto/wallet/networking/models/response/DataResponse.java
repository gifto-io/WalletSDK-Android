package io.gifto.wallet.networking.models.response;

import com.google.gson.annotations.SerializedName;

import io.gifto.wallet.utils.Utils;

/**
 * Created by ThangPM on 11/12/15.
 */
public class DataResponse<T>
{
    @SerializedName("statuscode")
    private String statusCode;

    @SerializedName("errormsg")
    private String errorMsg;

    @SerializedName("data")
    private T data;

    public String getStatusCode() {
        return Utils.isStringValid(statusCode)? statusCode : "";
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
