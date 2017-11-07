package io.gifto.wallet.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.gifto.wallet.model.WalletDetail;
import io.gifto.wallet.networking.models.response.GetGiftoTransactionListResponse;
import io.gifto.wallet.networking.models.response.WalletApiError;

/**
 * Created by thongnguyen on 10/17/17.
 */

public class JSonUtils {

    /**
     * Parse a String or JsonElement to WalletApiError object
     *
     * @param response String or JsonElement
     * @return WalletApiError
     */
    public static WalletApiError parseErrorResponse(Object response)
    {
        try {
            Gson gson = new Gson();
            Type typeToken = new TypeToken<WalletApiError>(){}.getType();
            if (response instanceof String)
                return gson.fromJson((String)response, typeToken);
            else if (response instanceof JsonElement)
                return gson.fromJson((JsonElement) response, typeToken);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parse a String or JsonElement to WalletDetail object
     * @param response String or JsonElement
     * @return WalletDetail
     */
    public static WalletDetail parseWalletDetail(Object response)
    {
        try {
            Gson gson = new Gson();
            Type typeToken = new TypeToken<WalletDetail>(){}.getType();
            if (response instanceof String)
                return gson.fromJson((String)response, typeToken);
            else if (response instanceof JsonElement)
                return gson.fromJson((JsonElement) response, typeToken);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parse a String or JsonElement to List<GetGiftoTransactionListResponse> object
     * @param response String or JsonElement
     * @return List<GetGiftoTransactionListResponse>
     */
    public static List<GetGiftoTransactionListResponse> parseListTransaction(Object response)
    {
        try {
            Gson gson = new Gson();
            Type typeToken = new TypeToken<List<GetGiftoTransactionListResponse>>(){}.getType();
            if (response instanceof String)
                return gson.fromJson((String)response, typeToken);
            else if (response instanceof JsonElement)
                return gson.fromJson((JsonElement) response, typeToken);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
