package io.gifto.wallet.networking;

import io.gifto.wallet.networking.models.response.WalletApiError;

/**
 * Created by thongnguyen on 10/16/17.
 */

public interface WalletApiResponseCallback<T> {
    void success(String statusCode, T responseData);

    void failed(WalletApiError error);
}
