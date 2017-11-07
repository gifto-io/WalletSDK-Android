package io.gifto.wallet.networking;

import io.gifto.wallet.networking.models.response.DataResponse;

/**
 * Created by thongnguyen on 9/9/17.
 */

public interface GiftoApiResponse<T> {
    public void onSuccess(DataResponse<T> dataResponse);

    public void onError(DataResponse<T> dataResponse, String errorMessage);
}
