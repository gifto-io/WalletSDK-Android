package io.gifto.wallet.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import io.gifto.wallet.BuildConfig;
import io.gifto.wallet.R;
import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.adapter.WalletBalanceListAdapter;
import io.gifto.wallet.event.OnRefreshGiftoTransactionEvent;
import io.gifto.wallet.model.WalletBalanceDetail;
import io.gifto.wallet.model.WalletCurrency;
import io.gifto.wallet.model.WalletDetail;
import io.gifto.wallet.networking.RestClient;
import io.gifto.wallet.networking.WSConstants;
import io.gifto.wallet.networking.WalletApiResponseCallback;
import io.gifto.wallet.networking.models.request.CreateWalletRequest;
import io.gifto.wallet.networking.models.request.GetGiftoBalanceRequest;
import io.gifto.wallet.networking.models.request.GetWalletAddressRequest;
import io.gifto.wallet.networking.models.request.GetWalletDetailRequest;
import io.gifto.wallet.networking.models.request.RefreshGiftoRequest;
import io.gifto.wallet.networking.models.response.DataResponse;
import io.gifto.wallet.networking.models.response.GetGiftoBalanceResponse;
import io.gifto.wallet.networking.models.response.GetGiftoTransactionListResponse;
import io.gifto.wallet.networking.models.response.GetWalletAddressResponse;
import io.gifto.wallet.networking.models.response.NoResponse;
import io.gifto.wallet.networking.models.response.WalletApiError;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.dialog.ShowWalletAddressDialog;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.SweetAlertDialogUtils;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.Logger;
import io.gifto.wallet.utils.common.MyHandler;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class GiftoWalletFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = "GiftoWalletFragment";

    SwipeRefreshLayout srlMainContainer;

    TextView tvBlockchainBalance;
    TextView tvInternalBalance;

    RecyclerView rvWalletBalanceList;
    WalletBalanceListAdapter walletBalanceListAdapter;


    RelativeLayout btnMove;

    RelativeLayout btnTransfer;

    RelativeLayout btnTip;

    RelativeLayout btnShowWalletAddress;

    RelativeLayout btnCreateWallet;

    RelativeLayout btnMoveHistory;

    RelativeLayout btnHistory;

    LinearLayout llBuySellGifto;
    RelativeLayout btnBuyGifto;
    RelativeLayout btnSellGifto;

    TextView tvVersion;

    private boolean isFirstTime = true;

    public static String mMessage;

    private double blockchainBalance, internalBalance;

    private boolean isTransferComplete;
    private MyHandler myHandler;
    private Runnable handleAfterTransferTask;

    @Override
    public FragmentType getType()
    {
        return FragmentType.ROSE_COIN;
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onStart()
    {
        isInDialog = false;
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (myHandler != null && handleAfterTransferTask != null)
            myHandler.removeCallbacks(handleAfterTransferTask);
        super.onDestroy();
    }

    @Override
    protected View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(!isInDialog)
            setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_rosecoin, null);
    }

    @Override
    protected void BindView(View rootView)
    {
        srlMainContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_main_container);

        tvBlockchainBalance = (TextView) rootView.findViewById(R.id.tv_blockchain_balance);
        tvInternalBalance = (TextView) rootView.findViewById(R.id.tv_internal_balance);

        rvWalletBalanceList = (RecyclerView) rootView.findViewById(R.id.rv_wallet_balance_list);

        btnMove = (RelativeLayout) rootView.findViewById(R.id.btn_move);

        btnTransfer = (RelativeLayout) rootView.findViewById(R.id.btn_transfer);

        btnTip = (RelativeLayout) rootView.findViewById(R.id.btn_tip);

        btnShowWalletAddress = (RelativeLayout) rootView.findViewById(R.id.btn_show_wallet_address);

        btnCreateWallet = (RelativeLayout) rootView.findViewById(R.id.btn_create_wallet);

        btnHistory = (RelativeLayout) rootView.findViewById(R.id.btn_transfer_history);

        btnMoveHistory = (RelativeLayout) rootView.findViewById(R.id.btn_move_history);

        llBuySellGifto = (LinearLayout) rootView.findViewById(R.id.ll_sell_buy_rosecoin);
        btnBuyGifto = (RelativeLayout) rootView.findViewById(R.id.btn_buy_rosecoin);
        btnSellGifto = (RelativeLayout) rootView.findViewById(R.id.btn_sell_rosecoin);

        tvVersion = (TextView) rootView.findViewById(R.id.tv_version);
    }

    @Override
    protected void ViewInjectionSuccess()
    {
        myHandler = new MyHandler(GiftoActivity.getInstance());

        handleAfterTransferTask = new Runnable() {
            @Override
            public void run() {
                if (!isTransferComplete)
                {
                    SweetAlertDialogUtils.ShowMessageDialog(GiftoActivity.getInstance(), getString(R.string.your_request_being_processed));
                }
            }
        };

        btnMove.setOnClickListener(this);
        btnTransfer.setOnClickListener(this);
        btnTip.setOnClickListener(this);
        btnShowWalletAddress.setOnClickListener(this);
        btnCreateWallet.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnMoveHistory.setOnClickListener(this);
        btnBuyGifto.setOnClickListener(this);
        btnSellGifto.setOnClickListener(this);

        tvBlockchainBalance.setText("0");
        tvInternalBalance.setText("0");

        tvVersion.setText(getString(R.string.rosecoin_wallet) + " SDK v" + BuildConfig.VERSION_NAME);

        if (Utils.isStringValid(GiftoWalletManager.getUserIdentityData()))
            UpdateWalletInfoV2();
        else
        {
            SweetAlertDialogUtils.ShowMessageDialog(GiftoActivity.getInstance(), getString(R.string.user_identity_missed), new ICallback() {
                @Override
                public Object doIt(Object... params) {
                    GiftoActivity.getInstance().finish();
                    return null;
                }
            });
        }

        srlMainContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UpdateWalletInfoV2();
            }
        });

        srlMainContainer.setColorScheme(R.color.whispers_pink,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.btn_move)
        {
            RefreshGiftoOnBlockchain();
        }
        else if (id == R.id.btn_transfer)
        {
            TransferGiftoFragment transferGiftoWalletFragment = new TransferGiftoFragment();
            fragmentManagerInterface.AddFragment(transferGiftoWalletFragment, true);
        }
        else if (id == R.id.btn_tip)
        {
            TipGiftoFragment tipGiftoWalletFragment = new TipGiftoFragment();
            fragmentManagerInterface.AddFragment(tipGiftoWalletFragment, true);
        }
        else if (id == R.id.btn_create_wallet)
        {
            CreateWallet();
        }
        else if (id == R.id.btn_buy_rosecoin)
        {
            if (GiftoActivity.onBuyCoinClickListener != null)
                GiftoActivity.onBuyCoinClickListener.onClick(v, fragmentManagerInterface);
//            SweetAlertDialogUtils.ShowMessageDialog(GiftoActivity.getInstance(), "This feature will be enabled after ICO");
        }
        else if (id == R.id.btn_sell_rosecoin)
        {
            SweetAlertDialogUtils.ShowMessageDialog(GiftoActivity.getInstance(), "This feature will be enabled after ICO");
        }
        else if (id == R.id.btn_show_wallet_address)
        {
            ShowWalletAddressDialog.getNewInstance()
                    .setWalletAddress(GiftoWalletManager.getUserWalletAddress())
                    .setWalletAddressQRCode(GiftoWalletManager.getUserWalletAddressQRCode())
                    .ShowDialog(getFragmentManager());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.rosecoin_menu_main, menu);
        menu.findItem(R.id.action_refresh).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            UpdateWalletInfoV2();
        }
        return super.onOptionsItemSelected(item);
    }

    // API V1
    /**
     * Update wallet detail - api v1
     */
    public void UpdateWalletInfo()
    {
        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance());

        final String message = mMessage;
        mMessage = null;
        blockchainBalance = 0;
        internalBalance = 0;
        UpdateWalletAddress(new ICallback() {
            @Override
            public Object doIt(Object... params) {
                boolean result = (boolean) params[0];
                if (result)
                    GiftoWalletManager.setUserWalletAddress((String) params[1]);

                if (Utils.isStringValid(GiftoWalletManager.getUserWalletAddress()))
                {
                    GiftoActivity.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            btnShowWalletAddress.setVisibility(View.VISIBLE);
                            btnMove.setVisibility(View.VISIBLE);
                            btnTransfer.setVisibility(View.VISIBLE);
                            btnTip.setVisibility(View.GONE);
                            btnHistory.setVisibility(View.GONE);
                            btnMoveHistory.setVisibility(View.GONE);
                            llBuySellGifto.setVisibility(View.GONE);
                        }
                    });

                    UpdateBlockchainGiftoBalance(new ICallback() {
                        @Override
                        public Object doIt(final Object... params) {
                            final boolean result = (boolean) params[0];
                            GiftoActivity.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    SweetAlertDialogUtils.DismissSweetDialog();
                                    if (result)
                                    {
                                        String coin = (String) params[1];
                                        try {
                                            blockchainBalance = Double.parseDouble(coin);
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                            blockchainBalance = 0;
                                        }
                                        UpdateBalanceGUI();
                                        isFirstTime = false;
                                    }
                                }
                            });
                            return null;
                        }
                    });

                    UpdateInternalGiftoBalance(new ICallback() {
                        @Override
                        public Object doIt(final Object... params) {
                            final boolean result = (boolean) params[0];
                            GiftoActivity.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    SweetAlertDialogUtils.DismissSweetDialog();
                                    if (result)
                                    {
                                        String coin = (String) params[1];
                                        try {
                                            internalBalance = Double.parseDouble(coin);
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                            internalBalance = 0;
                                        }
                                        UpdateBalanceGUI();
                                        isFirstTime = false;
                                    }
                                }
                            });
                            return null;
                        }
                    });

                    if (getContext() != null)
                    {
                        GiftoWalletManager.RecycleQRCode();
                        GiftoWalletManager.setUserWalletAddressQRCode(Utils.GenerateQRCode(GiftoWalletManager.getUserWalletAddress(), 600, 600, ContextCompat.getColor(getContext(), R.color.whispers_pink)));
                    }
                }
                else
                {
                    GiftoActivity.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            btnShowWalletAddress.setVisibility(View.GONE);
                            btnMove.setVisibility(View.GONE);
                            btnTransfer.setVisibility(View.GONE);
                            btnTip.setVisibility(View.GONE);
                            btnHistory.setVisibility(View.GONE);
                            btnMoveHistory.setVisibility(View.GONE);
                            llBuySellGifto.setVisibility(View.GONE);
                        }
                    });

                    GiftoWalletManager.RecycleQRCode();

                    if (result)
                    {
                        GiftoActivity.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                btnCreateWallet.setVisibility(View.VISIBLE);
                                SweetAlertDialogUtils.DismissSweetDialog();
                                SweetAlertDialogUtils.ShowMessageWithActionButton(GiftoActivity.getInstance(), getString(R.string.msg_dont_have_coin),
                                        getString(R.string.create), new ICallback() {
                                            @Override
                                            public Object doIt(Object... params) {
                                                boolean result = (boolean) params[0];
                                                if (result)
                                                {
                                                    btnCreateWallet.callOnClick();
                                                }
                                                return null;
                                            }
                                        });
                            }
                        });
                        return null;
                    }
                    GiftoActivity.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            SweetAlertDialogUtils.DismissSweetDialog();
                            SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), GiftoActivity.getInstance().getResources().getString(R.string.title_error),
                                    getString(R.string.update_wallet_fail));
                        }
                    });
                }
                return null;
            }
        });
    }

    /**
     * Update internal balance - api v1
     *
     * @param callback callback for response
     */
    public void UpdateInternalGiftoBalance(final ICallback callback)
    {
        GetGiftoBalanceRequest getGiftoBalanceRequest = new GetGiftoBalanceRequest(GiftoWalletManager.getUserWalletAddress());
        RestClient.instance().getRestGiftoWalletService().GetInternalGiftoBalance(GiftoWalletManager.getAuthorization(), getGiftoBalanceRequest, new Callback<DataResponse<GetGiftoBalanceResponse>>() {
            @Override
            public void success(DataResponse<GetGiftoBalanceResponse> getGiftoBalanceResponseDataResponse, Response response) {
                if (getGiftoBalanceResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "GetGiftoBalance Unknown Error");
                    callback.doIt(false, 0);
                }
                else
                {
                    switch (getGiftoBalanceResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.GET_INTERNAL_ROSE_COIN_BALANCE_SUCCESS:
                            callback.doIt(true, getGiftoBalanceResponseDataResponse.getData().getBalance());
                            break;
                        default:
                            Logger.e(TAG, "GetGiftoBalance Unknown Error");
                            callback.doIt(false, 0);
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "GetGiftoBalance error");
                callback.doIt(false, 0);
            }
        });
    }

    /**
     * Update blockchain balance - api v1
     *
     * @param callback callback for response
     */
    public void UpdateBlockchainGiftoBalance(final ICallback callback)
    {
        GetGiftoBalanceRequest getGiftoBalanceRequest = new GetGiftoBalanceRequest(GiftoWalletManager.getUserWalletAddress());
        RestClient.instance().getRestGiftoWalletService().GetBlockchainGiftoBalance(GiftoWalletManager.getAuthorization(), getGiftoBalanceRequest, new Callback<DataResponse<GetGiftoBalanceResponse>>() {
            @Override
            public void success(DataResponse<GetGiftoBalanceResponse> getGiftoBalanceResponseDataResponse, Response response) {
                if (getGiftoBalanceResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "GetGiftoBalance Unknown Error");
                    callback.doIt(false, 0);
                }
                else
                {
                    switch (getGiftoBalanceResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.GET_BLOCKCHAIN_ROSE_COIN_BALANCE_SUCCESS:
                            callback.doIt(true, getGiftoBalanceResponseDataResponse.getData().getBalance());
                            break;
                        default:
                            Logger.e(TAG, "GetGiftoBalance Unknown Error");
                            callback.doIt(false, 0);
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "GetGiftoBalance error");
                callback.doIt(false, 0);
            }
        });
    }

    /**
     * Update wallet's address
     *
     * @param callback callback for response
     */
    public void UpdateWalletAddress(final ICallback callback)
    {
        GetWalletAddressRequest getWalletAddressRequest = new GetWalletAddressRequest(GiftoWalletManager.getUserIdentityData());
        RestClient.instance().getRestGiftoWalletService().GetWalletAddress(GiftoWalletManager.getAuthorization(), getWalletAddressRequest, new Callback<DataResponse<GetWalletAddressResponse>>() {
            @Override
            public void success(DataResponse<GetWalletAddressResponse> getWalletAddressResponseDataResponse, Response response) {
                if (getWalletAddressResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "GetWalletAddress Unknown Error");
                    callback.doIt(false);
                }
                else
                {
                    switch (getWalletAddressResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.GET_WALLET_ADDRESS_SUCCESS:
                            callback.doIt(true, getWalletAddressResponseDataResponse.getData().getWalletAddress());
                            break;
                        case WSConstants.GET_WALLET_ADDRESS_NOT_EXISTS:
                            callback.doIt(true, "");
                            break;
                        default:
                            Logger.e(TAG, "GetWalletAddress Unknown Error");
                            callback.doIt(false);
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "GetWalletAddress error");
                callback.doIt(false);
            }
        });
    }

    /**
     * Refresh balance on blockchain - api v1
     */
    public void RefreshGiftoOnBlockchain()
    {
        if (myHandler != null && handleAfterTransferTask != null)
            myHandler.removeCallbacks(handleAfterTransferTask);

        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance(), getString(R.string.processing));

        final ICallback callback = new ICallback() {
            @Override
            public Object doIt(final Object... params) {
                isTransferComplete = true;
                if (myHandler != null && handleAfterTransferTask != null)
                    myHandler.removeCallbacks(handleAfterTransferTask);
                if (GiftoActivity.getInstance() != null)
                {
                    SweetAlertDialogUtils.DismissSweetDialog();
                    final boolean result = (boolean) params[0];
                    if (result) {
                        Logger.e(TAG, "Updating successful");
                        EventBus.getDefault().post(new OnRefreshGiftoTransactionEvent());
                        UpdateBalance();
                        SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getString(R.string.title_success), getString(R.string.update_wallet_success), true, null);
                    } else {
                        String msg = (String) params[1];
                        Logger.e(TAG, "Updating error: " + msg);
                        SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getString(R.string.title_error), msg, false, null);
                    }
                }
                return null;
            }
        };

        RefreshGiftoRequest refreshGiftoRequest = new RefreshGiftoRequest(GiftoWalletManager.getUserIdentityData());

        isTransferComplete = false;
        RestClient.instance().getRestGiftoWalletService().RefreshGiftoOnBlockChain(GiftoWalletManager.getAuthorization(), refreshGiftoRequest, new Callback<DataResponse<GetGiftoTransactionListResponse>>() {
            @Override
            public void success(DataResponse<GetGiftoTransactionListResponse> transferGiftoResponseDataResponse, Response response) {
                if (transferGiftoResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "RefreshGiftoOnBlockChain Unknown Error");
                    callback.doIt(false, getString(R.string.unknown_error));
                }
                else
                {
                    switch (transferGiftoResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.REFRESH_ROSE_COIN_SUCCESS:
                            callback.doIt(true);
                            break;
                        case WSConstants.REFRESH_ROSE_COIN_BALANCE_NOT_ENOUGH:
                            Logger.e(TAG, "RefreshGiftoOnBlockChain Balance not enough");
                            callback.doIt(false, getString(R.string.balance_not_enough_for_updating));
                            break;
                        case WSConstants.DENY_REQUEST:
                            Logger.e(TAG, "RefreshGiftoOnBlockChain: The another request is being processed. Please request again later.");
                            callback.doIt(false, getString(R.string.another_request_being_processed));
                            break;
                        default:
                            Logger.e(TAG, "RefreshGiftoOnBlockChain Unknown Error");
                            callback.doIt(false, getString(R.string.unknown_error));
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "RefreshGiftoOnBlockChain Error: " + error.getMessage());
                callback.doIt(false, getString(R.string.error_due_to_network));
            }
        });

        myHandler.postDelayed(handleAfterTransferTask, 20000);
    }

    /**
     * Update total balance - api v1
     */
    public void UpdateBalance()
    {
        blockchainBalance = 0;
        internalBalance = 0;
        UpdateBlockchainGiftoBalance(new ICallback() {
            @Override
            public Object doIt(final Object... params) {
                final boolean result = (boolean) params[0];
                GiftoActivity.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result)
                        {
                            String coin = (String) params[1];
                            try {
                                blockchainBalance = Double.parseDouble(coin);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                blockchainBalance = 0;
                            }
                            UpdateBalanceGUI();
                        }
                    }
                });
                return null;
            }
        });

        UpdateInternalGiftoBalance(new ICallback() {
            @Override
            public Object doIt(final Object... params) {
                final boolean result = (boolean) params[0];
                GiftoActivity.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result)
                        {
                            String coin = (String) params[1];
                            try {
                                internalBalance = Double.parseDouble(coin);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                internalBalance = 0;
                            }
                            UpdateBalanceGUI();
                        }
                    }
                });
                return null;
            }
        });
    }

    /**
     * Update balance on GUI - api v1
     */
    private synchronized void UpdateBalanceGUI()
    {
        GiftoActivity.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                double total = blockchainBalance + internalBalance;
                tvBlockchainBalance.setText(Utils.FormatAmount(total));
            }
        });
    }

    // API v2
    /**
     * Update wallet's information - api v2
     */
    public void UpdateWalletInfoV2()
    {
        final ICallback callback = new ICallback() {
            @Override
            public Object doIt(Object... params) {
                srlMainContainer.setRefreshing(false);
                boolean result = (boolean) params[0];

                int needToCreateWallet = GiftoWalletManager.getInstance().needToAddOrCreateWallet();

                if (result && needToCreateWallet == -1)
                {
                    GiftoActivity.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            btnShowWalletAddress.setVisibility(View.GONE);
                            btnMove.setVisibility(View.GONE);
                            btnTransfer.setVisibility(View.VISIBLE);
                            btnTip.setVisibility(View.GONE);
                            btnHistory.setVisibility(View.GONE);
                            btnMoveHistory.setVisibility(View.GONE);
                            btnCreateWallet.setVisibility(View.GONE);
                            if (GiftoActivity.onBuyCoinClickListener != null)
                                llBuySellGifto.setVisibility(View.VISIBLE);
                            else llBuySellGifto.setVisibility(View.GONE);
                        }
                    });

                    if (walletBalanceListAdapter == null)
                        walletBalanceListAdapter = new WalletBalanceListAdapter(GiftoWalletManager.getUserWalletDetail().getWallets());
                    else walletBalanceListAdapter.setWalletBalanceDetailList(GiftoWalletManager.getUserWalletDetail().getWallets());

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(GiftoActivity.getInstance());
                    rvWalletBalanceList.setLayoutManager(mLayoutManager);
                    rvWalletBalanceList.setHasFixedSize(true);
                    rvWalletBalanceList.setAdapter(walletBalanceListAdapter);

                    walletBalanceListAdapter.setOnItemClickListener(new ICallback() {
                        @Override
                        public Object doIt(Object... params) {
                            WalletBalanceDetail walletDetail = (WalletBalanceDetail) params[0];
                            if (walletDetail != null)
                            {
                                ShowWalletAddressDialog.getNewInstance()
                                        .setWalletAddress(walletDetail.getAddress())
                                        .setWalletAddressQRCode(Utils.GenerateQRCode(walletDetail.getAddress(), 600, 600, ContextCompat.getColor(getContext(), R.color.whispers_pink)))
                                        .ShowDialog(getFragmentManager());
                            }
                            return null;
                        }
                    });

                    GiftoActivity.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            SweetAlertDialogUtils.DismissSweetDialog();
                        }
                    });
                }
                else
                {
                    GiftoActivity.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            btnShowWalletAddress.setVisibility(View.GONE);
                            btnMove.setVisibility(View.GONE);
                            btnTransfer.setVisibility(View.GONE);
                            btnTip.setVisibility(View.GONE);
                            btnHistory.setVisibility(View.GONE);
                            btnMoveHistory.setVisibility(View.GONE);
                            llBuySellGifto.setVisibility(View.GONE);
                            btnCreateWallet.setVisibility(View.GONE);
                        }
                    });

                    if (!result)
                    {
                        String errorCode = (String) params[1];

                        switch (errorCode)
                        {
                            case WSConstants.RESPONSE_IDENTITY_DATA_NOT_FOUND: case WSConstants.RESPONSE_NOT_FOUND:
                                needToCreateWallet = 1;
                                break;
                            default:
                                needToCreateWallet = -1;
                                break;
                        }
                    }

                    switch (needToCreateWallet)
                    {
                        case 1:case 2:
                            String message = "";
                            if (needToCreateWallet == 1)
                                message = String.format(getString(R.string.msg_dont_have_wallet), GiftoWalletManager.getUserIdentityData());
                            else message = String.format(getString(R.string.msg_dont_have_coin), GiftoWalletManager.getUserIdentityData(), WalletCurrency.GetNameByCode(Constants.CURRENCY_CODE));
                            final String mMessage = message;
                            GiftoActivity.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnCreateWallet.setVisibility(View.VISIBLE);
                                    SweetAlertDialogUtils.DismissSweetDialog();
                                    SweetAlertDialogUtils.ShowMessageWithActionButton(GiftoActivity.getInstance(), mMessage,
                                            getString(R.string.create), new ICallback() {
                                                @Override
                                                public Object doIt(Object... params) {
                                                    boolean result = (boolean) params[0];
                                                    if (result)
                                                    {
                                                        btnCreateWallet.callOnClick();
                                                    }
                                                    return null;
                                                }
                                            });
                                }
                            });
                            break;
                        default:
                            GiftoActivity.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    SweetAlertDialogUtils.DismissSweetDialog();
                                    SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), GiftoActivity.getInstance().getResources().getString(R.string.title_error),
                                            getString(R.string.update_wallet_fail));
                                }
                            });
                            break;
                    }
                }
                return null;
            }
        };

        GiftoWalletManager.setUserWalletDetail(null);
        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance());
        GetWalletDetail(callback);
    }

    /**
     * Get wallet's detail
     *
     * @param callback callback for response
     */
    public void GetWalletDetail(final ICallback callback)
    {
        GetWalletDetailRequest getWalletDetailRequest = new GetWalletDetailRequest(GiftoWalletManager.getUserIdentityData());
        RestClient.GetWalletDetail(getWalletDetailRequest, new WalletApiResponseCallback<WalletDetail>() {
            int mSession = GiftoActivity.getInstance().getSession();
            @Override
            public void success(String statusCode, WalletDetail responseData) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                {
                    GiftoWalletManager.setUserWalletDetail(responseData);
                    callback.doIt(true);
                }
            }

            @Override
            public void failed(WalletApiError error) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                {
                    callback.doIt(false, error.getCode());
                }
            }
        });
    }

    /**
     * Check data to create wallet automatically or let user input some data to create wallet
     */
    private void CreateWallet()
    {
        int addOrCreateWallet = GiftoWalletManager.getInstance().needToAddOrCreateWallet();

        CreateGiftoWalletFragment createGiftoWalletFragment = new CreateGiftoWalletFragment();
        switch (addOrCreateWallet)
        {
            case 1:
                fragmentManagerInterface.AddFragment(createGiftoWalletFragment, true);
                break;
            case 2:
                if (GiftoWalletManager.getUserWalletDetail() != null && (Utils.isStringValid(GiftoWalletManager.getUserWalletDetail().getFirstName()) || Utils.isStringValid(GiftoWalletManager.getUserWalletDetail().getLastName())))
                    AutoAddWallet();
                else fragmentManagerInterface.AddFragment(createGiftoWalletFragment, true);
                break;
            default:
                break;
        }
    }

    /**
     * Create Wallet automatically
     */
    private void AutoAddWallet()
    {
        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance());
        final ICallback callback = new ICallback() {
            @Override
            public Object doIt(Object... params) {
                final String result = (String) params[0];
                GiftoActivity.runOnUIThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        SweetAlertDialogUtils.DismissSweetDialog();
                        switch (result)
                        {
                            case WSConstants.RESPONSE_WALLET_CREATED:

                                SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_success),
                                        getString(R.string.create_wallet_success), true, new ICallback() {
                                            @Override
                                            public Object doIt(Object... params) {
                                                UpdateWalletInfoV2();
                                                return null;
                                            }
                                        });
                                break;
                            case WSConstants.RESPONSE_CONFLICT:
                                SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_success),
                                        getString(R.string.create_wallet_exists), true, new ICallback() {
                                            @Override
                                            public Object doIt(Object... params) {
                                                UpdateWalletInfoV2();
                                                return null;
                                            }
                                        });
                                break;
                            default:
                                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_error),
                                        getString(R.string.create_wallet_fail));
                                break;
                        }
                    }
                });
                return null;
            }
        };

        CreateWalletRequest createWalletRequest = new CreateWalletRequest(GiftoWalletManager.getUserIdentityData(), "abcxyz", null, null, Constants.CURRENCY_CODE);
        RestClient.CreateWallet(createWalletRequest, new WalletApiResponseCallback<NoResponse>() {
            int mSession = GiftoActivity.getInstance().getSession();
            @Override
            public void success(String statusCode, NoResponse responseData) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                    callback.doIt(statusCode);
            }

            @Override
            public void failed(WalletApiError error) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                    callback.doIt(error.getCode());
            }
        });
    }
}
