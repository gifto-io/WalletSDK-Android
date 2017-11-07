package io.gifto.wallet.ui.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.gifto.wallet.R;
import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.adapter.GiftoHistoryAdapter;
import io.gifto.wallet.event.OnRefreshGiftoTransactionEvent;
import io.gifto.wallet.networking.RestClient;
import io.gifto.wallet.networking.WSConstants;
import io.gifto.wallet.networking.WalletApiResponseCallback;
import io.gifto.wallet.networking.models.request.GetGiftoTransactionRequest;
import io.gifto.wallet.networking.models.response.DataResponse;
import io.gifto.wallet.networking.models.response.GetGiftoTransactionListResponse;
import io.gifto.wallet.networking.models.response.WalletApiError;
import io.gifto.wallet.pullandloadmore.LoadMoreListView;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.dialog.GiftoTransactionDetailDialog;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.SweetAlertDialogUtils;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.Logger;
import io.gifto.wallet.utils.common.MyHandler;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by thongnguyen on 8/21/17.
 *
 * Display transaction history
 */

public class TransferGiftoHistoryFragment extends BaseFragment
{
    private static final String TAG = "TransferGiftoHistoryFragment";
    private static final int TRANSACTION_LIMIT = 20;

    LoadMoreListView lvCreditTransaction;

    TextView tvNoData;

    GiftoHistoryAdapter creditHistoryAdapter;
    List<GetGiftoTransactionListResponse> mCreditTransactionList;

    private long lastClickTime = 0;

    private String transactionMode = Constants.TRANSACTION_MODE_SENDING;

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    private EventBus eventBus = EventBus.getDefault();

    @Override
    public FragmentType getType()
    {
        return FragmentType.ROSE_COIN_HISTORY;
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onStart()
    {
        if (!eventBus.isRegistered(this))
            eventBus.register(this);
        super.onStart();
    }

    @Override
    public void onDestroy()
    {
        if (eventBus.isRegistered(this))
            eventBus.unregister(this);
        if (mCreditTransactionList != null)
            mCreditTransactionList.clear();
        super.onDestroy();
    }

    @Override
    protected View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(!isInDialog)
            setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_rosecoin_credit_history, null);
    }

    @Override
    protected void BindView(View rootView)
    {
        lvCreditTransaction = (LoadMoreListView) rootView.findViewById(R.id.lv_credit_transaction);

        tvNoData = (TextView) rootView.findViewById(R.id.tv_no_data);
    }

    @Override
    protected void ViewInjectionSuccess()
    {
        InitTransactionListV2(false, new ICallback() {
            @Override
            public Object doIt(Object... params) {
                if (getContext() == null)
                    return null;
                boolean result = (boolean) params[0];
                if (creditHistoryAdapter == null)
                    creditHistoryAdapter = new GiftoHistoryAdapter(getContext(), transactionMode, mCreditTransactionList);
                else creditHistoryAdapter.UpdateTransactionList(mCreditTransactionList);
                lvCreditTransaction.setAdapter(creditHistoryAdapter);

                if(mCreditTransactionList.size() == 0)
                {
                    lvCreditTransaction.setEmptyView(tvNoData);
                    tvNoData.setVisibility(View.VISIBLE);
                }
                else
                    tvNoData.setVisibility(View.GONE);

                lvCreditTransaction.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener()
                {
                    public void onLoadMore()
                    {
                        // Do the work to load more items at the end of list here
                        if(mCreditTransactionList != null && mCreditTransactionList.size() >= TRANSACTION_LIMIT)
                        {
                            LoadMoreTransactionListV2(mCreditTransactionList.size(), new ICallback()
                            {
                                @Override
                                public Object doIt(Object... params)
                                {
                                    creditHistoryAdapter.notifyDataSetChanged();
                                    lvCreditTransaction.onLoadMoreComplete();
                                    SweetAlertDialogUtils.DismissSweetDialog();
                                    return null;
                                }
                            });
                        }
                        else
                        {
                            lvCreditTransaction.onLoadMoreComplete();
                        }
                    }
                });

                lvCreditTransaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(SystemClock.elapsedRealtime() - lastClickTime < 1000)
                            return;
                        lastClickTime = SystemClock.elapsedRealtime();

                        if (position >= 0 && position < creditHistoryAdapter.getCount())
                        {
                            GiftoTransactionDetailDialog.getNewInstance()
                                    .setTransactionDetail((GetGiftoTransactionListResponse) creditHistoryAdapter.getItem(position))
                                    .setTransactionMode(transactionMode)
                                    .ShowDialog(getFragmentManager());
                        }
                    }
                });
                return null;
            }
        });
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
            RefreshTransactionList(true);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize the first transaction list - api v1
     *
     * @param isShowLoading true if want to show loading popup
     * @param callback callbacl for response
     */
    public void InitTransactionList(final boolean isShowLoading, final ICallback callback)
    {
        if (mCreditTransactionList != null)
            mCreditTransactionList.clear();
        mCreditTransactionList = new ArrayList<>();

        if (isShowLoading)
            SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance());

        int offset = 0;

        final Callback<DataResponse<List<GetGiftoTransactionListResponse>>> getTransactionListCallback = new Callback<DataResponse<List<GetGiftoTransactionListResponse>>>() {
            @Override
            public void success(DataResponse<List<GetGiftoTransactionListResponse>> listDataResponse, Response response) {
                if (response.getStatus() == WSConstants.RESPONSE_SUCCESS) {
                    if (listDataResponse == null) {
                        Logger.e(TAG, "Data response is NULL.");
                        if ((transactionMode.equals(Constants.TRANSACTION_MODE_SENDING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_SENDING_HISTORY) ||
                                (transactionMode.equals(Constants.TRANSACTION_MODE_RECEIVING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_RECEIVING_HISTORY))
                            SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_error),
                                getResources().getString(R.string.get_transaction_list_error));
                        return;
                    }

                    switch (listDataResponse.getStatusCode()) {
                        case WSConstants.GET_ROSE_COIN_TRANSACTION_SUCCESS:
                            List<GetGiftoTransactionListResponse> data = listDataResponse.getData();
                            if (data != null)
                                mCreditTransactionList.addAll(data);
                            callback.doIt(true);
                            if (isShowLoading)
                                SweetAlertDialogUtils.DismissSweetDialog();
                            break;
                        default:
                            Logger.e(TAG, "Unknown error occurred: " + response.getStatus() + " --- " + response.getReason());
                            if ((transactionMode.equals(Constants.TRANSACTION_MODE_SENDING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_SENDING_HISTORY) ||
                                    (transactionMode.equals(Constants.TRANSACTION_MODE_RECEIVING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_RECEIVING_HISTORY))
                                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_error),
                                    getResources().getString(R.string.get_transaction_list_error));
                            callback.doIt(false);
                            break;
                    }
                } else {
                    //DO SOMETHING
                    Logger.e(TAG, "Error response: " + response.getStatus() + " --- " + response.getReason());
                    if ((transactionMode.equals(Constants.TRANSACTION_MODE_SENDING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_SENDING_HISTORY) ||
                            (transactionMode.equals(Constants.TRANSACTION_MODE_RECEIVING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_RECEIVING_HISTORY))
                        SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_error),
                            getResources().getString(R.string.get_transaction_list_error));
                    callback.doIt(false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "Error failure: " + error.getMessage());
                callback.doIt(false);
                if (isShowLoading)
                    SweetAlertDialogUtils.DismissSweetDialog();
                if ((transactionMode.equals(Constants.TRANSACTION_MODE_SENDING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_SENDING_HISTORY) ||
                        (transactionMode.equals(Constants.TRANSACTION_MODE_RECEIVING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_RECEIVING_HISTORY))
                    (new MyHandler(GiftoActivity.getInstance())).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_error),
                                getResources().getString(R.string.msg_please_check_network));
                    }
                }, 500);
            }
        };

        GetGiftoTransactionRequest getGiftoTransactionRequest = new GetGiftoTransactionRequest(GiftoWalletManager.getUserIdentityData(), String.valueOf(offset), String.valueOf(TRANSACTION_LIMIT), Constants.TRANSACTION_TYPE_ALL, transactionMode);

        RestClient.instance().getRestGiftoWalletService().GetGiftoTransactionList(GiftoWalletManager.getAuthorization(), getGiftoTransactionRequest, getTransactionListCallback);
    }

    /**
     * Load more transaction - api v1
     *
     * @param offset from offset
     * @param callback callback for response
     */
    public void LoadMoreTransactionList(int offset, final ICallback callback)
    {
        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance());

        final Callback<DataResponse<List<GetGiftoTransactionListResponse>>> getTransactionListCallback = new Callback<DataResponse<List<GetGiftoTransactionListResponse>>>()
        {
            @Override
            public void success(DataResponse<List<GetGiftoTransactionListResponse>> dataResponse, Response response)
            {
                SweetAlertDialogUtils.DismissSweetDialog();

                if (response.getStatus() == WSConstants.RESPONSE_SUCCESS)
                {
                    if (dataResponse == null)
                    {
                        Logger.e(TAG, "Data response is NULL.");
                        return;
                    }

                    switch (dataResponse.getStatusCode())
                    {
                        case WSConstants.GET_ROSE_COIN_TRANSACTION_SUCCESS:
                            List<GetGiftoTransactionListResponse> data = dataResponse.getData();
                            if(data == null)
                            {
                                callback.doIt(false);
                                return;
                            }
                            mCreditTransactionList.addAll(data);
                            callback.doIt(true);
                            break;
                        default:
                            callback.doIt(false);
                            break;
                    }
                }
                else
                {
                    //DO SOMETHING
                    Logger.e(TAG, "Error response: " + response.getStatus() + " --- " + response.getReason());
                    callback.doIt(false);
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                SweetAlertDialogUtils.DismissSweetDialog();

                Logger.e(TAG, "Error failure: " + error.getMessage());
                callback.doIt(false);
            }
        };

        GetGiftoTransactionRequest getGiftoTransactionRequest = new GetGiftoTransactionRequest(GiftoWalletManager.getUserIdentityData(), String.valueOf(offset), String.valueOf(TRANSACTION_LIMIT), Constants.TRANSACTION_TYPE_ALL, transactionMode);

        RestClient.instance().getRestGiftoWalletService().GetGiftoTransactionList(GiftoWalletManager.getAuthorization(), getGiftoTransactionRequest, getTransactionListCallback);
    }

    /**
     * Initialize the first transaction list - api v2
     *
     * @param isShowLoading true if want to show loading popup
     * @param callback callbacl for response
     */
    public void InitTransactionListV2(final boolean isShowLoading, final ICallback callback)
    {
        if (mCreditTransactionList != null)
            mCreditTransactionList.clear();
        mCreditTransactionList = new ArrayList<>();

        if (isShowLoading)
            SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance());

        final ICallback responseCallback = new ICallback() {
            @Override
            public Object doIt(Object... params) {
                if (isShowLoading)
                    SweetAlertDialogUtils.DismissSweetDialog();

                String code = (String) params[0];
                switch (code)
                {
                    case WSConstants.RESPONSE_SUCCESS_STR:
                        List<GetGiftoTransactionListResponse> data = (List<GetGiftoTransactionListResponse>)params[1];
                        if (data != null)
                            mCreditTransactionList.addAll(data);
                        callback.doIt(true);
                        break;
                    default:
                        Logger.e(TAG, "Get transaction error");
                        if ((transactionMode.equals(Constants.TRANSACTION_MODE_SENDING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_SENDING_HISTORY) ||
                                (transactionMode.equals(Constants.TRANSACTION_MODE_RECEIVING) && GiftoWalletMainFragment.SELECTED_POSITION == GiftoWalletMainFragment.POSITION_RECEIVING_HISTORY))
                            SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_error),
                                    getResources().getString(R.string.get_transaction_list_error));
                        callback.doIt(false);
                        break;
                }
                return null;
            }
        };

        int offset = 0;
        GetGiftoTransactionRequest getGiftoTransactionRequest = new GetGiftoTransactionRequest(GiftoWalletManager.getUserIdentityData(), String.valueOf(offset), String.valueOf(TRANSACTION_LIMIT), Constants.TRANSACTION_TYPE_ALL, transactionMode);

        RestClient.GetTransactionList(getGiftoTransactionRequest, new WalletApiResponseCallback<List<GetGiftoTransactionListResponse>>() {
            int mSession = GiftoActivity.getInstance().getSession();
            @Override
            public void success(String statusCode, List<GetGiftoTransactionListResponse> responseData) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && responseCallback != null)
                    responseCallback.doIt(statusCode, responseData);
            }

            @Override
            public void failed(WalletApiError error) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && responseCallback != null)
                    responseCallback.doIt(error.getCode());
            }
        });
    }

    /**
     * Load more transaction - api v2
     *
     * @param offset from offset
     * @param callback callback for response
     */
    public void LoadMoreTransactionListV2(int offset, final ICallback callback)
    {
        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance());

        GetGiftoTransactionRequest getGiftoTransactionRequest = new GetGiftoTransactionRequest(GiftoWalletManager.getUserIdentityData(), String.valueOf(offset), String.valueOf(TRANSACTION_LIMIT), Constants.TRANSACTION_TYPE_ALL, transactionMode);

        RestClient.GetTransactionList(getGiftoTransactionRequest, new WalletApiResponseCallback<List<GetGiftoTransactionListResponse>>() {
            int mSession = GiftoActivity.getInstance().getSession();
            @Override
            public void success(String statusCode, List<GetGiftoTransactionListResponse> responseData) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                {
                    if (mCreditTransactionList != null)
                        mCreditTransactionList.addAll(responseData);
                    callback.doIt(true);
                }
            }

            @Override
            public void failed(WalletApiError error) {
                if (GiftoActivity.getInstance() != null && mSession == GiftoActivity.getInstance().getSession() && callback != null)
                    callback.doIt(false);
            }
        });
    }

    /**
     * Refresh transaction list
     *
     * @param isShowLoading true if want to show loading popup
     */
    public void RefreshTransactionList(final boolean isShowLoading)
    {
        if (getContext() == null)
            return;

        if (creditHistoryAdapter == null)
        {
            creditHistoryAdapter = new GiftoHistoryAdapter(getContext(), transactionMode, null);
            lvCreditTransaction.setAdapter(creditHistoryAdapter);
        }
        else creditHistoryAdapter.UpdateTransactionList(null);
        creditHistoryAdapter.notifyDataSetChanged();
        InitTransactionListV2(isShowLoading, new ICallback()
        {
            @Override
            public Object doIt(Object... params)
            {
                creditHistoryAdapter.UpdateTransactionList(mCreditTransactionList);
                creditHistoryAdapter.notifyDataSetChanged();

                if(mCreditTransactionList.size() == 0)
                {
                    lvCreditTransaction.setEmptyView(tvNoData);
                    tvNoData.setVisibility(View.VISIBLE);
                }
                else
                    tvNoData.setVisibility(View.GONE);
                return null;
            }
        });
    }

    /**
     * Need to refresh transaction list when catching this event
     *
     * @param event
     */
    public void onEvent(OnRefreshGiftoTransactionEvent event)
    {
        GiftoActivity.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                RefreshTransactionList(false);
            }
        });
    }
}
