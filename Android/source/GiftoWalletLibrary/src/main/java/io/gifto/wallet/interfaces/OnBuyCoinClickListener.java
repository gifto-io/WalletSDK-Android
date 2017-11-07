package io.gifto.wallet.interfaces;

import android.view.View;

import java.io.Serializable;

import io.gifto.wallet.ui.manager.IFragmentManager;

/**
 * Created by thongnguyen on 10/28/17.
 */

public interface OnBuyCoinClickListener extends Serializable {

    void onClick(View view, IFragmentManager fragmentManager);
}
