package io.gifto.wallet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import io.gifto.wallet.R;
import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.networking.RestClient;
import io.gifto.wallet.networking.WSConstants;
import io.gifto.wallet.networking.WalletApiResponseCallback;
import io.gifto.wallet.networking.models.request.CreateWalletRequest;
import io.gifto.wallet.networking.models.response.DataResponse;
import io.gifto.wallet.networking.models.response.GetWalletAddressResponse;
import io.gifto.wallet.networking.models.response.NoResponse;
import io.gifto.wallet.networking.models.response.WalletApiError;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.utils.Constants;
import io.gifto.wallet.utils.SweetAlertDialogUtils;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.Logger;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class CreateGiftoWalletFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = "CreateGiftoWalletFragment";

    EditText etPassword;

    EditText etConfirmPassword;

    EditText etEmail;

    EditText etFirstName;

    EditText etLastName;

    RelativeLayout btnCreate;

    RelativeLayout rlFirstName;
    RelativeLayout rlLastName;
    RelativeLayout rlPassword;
    RelativeLayout rlConfirmPassword;

    private boolean hasName;

    @Override
    public FragmentType getType()
    {
        return FragmentType.CREATE_ROSE_COIN_WALLET;
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
        super.onDestroy();
    }

    @Override
    protected View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(!isInDialog)
            setHasOptionsMenu(true);
        if (GiftoActivity.getInstance() != null)
            GiftoActivity.getInstance().SetActivityTitle(getResources().getString(R.string.create_rosecoin_wallet));

        return inflater.inflate(R.layout.fragment_create_rosecoin_wallet, null);
    }

    @Override
    protected void BindView(View rootView)
    {
        etEmail = (EditText) rootView.findViewById(R.id.et_email);

        etFirstName = (EditText) rootView.findViewById(R.id.et_firstname);

        etLastName = (EditText) rootView.findViewById(R.id.et_lastname);

        etPassword = (EditText) rootView.findViewById(R.id.et_password);

        etConfirmPassword = (EditText) rootView.findViewById(R.id.et_confirm_password);

        btnCreate = (RelativeLayout) rootView.findViewById(R.id.btn_create);

        rlFirstName = (RelativeLayout) rootView.findViewById(R.id.rl_first_name);
        rlLastName = (RelativeLayout) rootView.findViewById(R.id.rl_last_name);
        rlPassword = (RelativeLayout) rootView.findViewById(R.id.rl_password);
        rlConfirmPassword = (RelativeLayout) rootView.findViewById(R.id.rl_confirm_password);
    }

    @Override
    protected void ViewInjectionSuccess()
    {
        etEmail.setText(GiftoWalletManager.getUserIdentityData());
        btnCreate.setOnClickListener(this);

        if (GiftoWalletManager.getUserWalletDetail() != null && (Utils.isStringValid(GiftoWalletManager.getUserWalletDetail().getFirstName()) || Utils.isStringValid(GiftoWalletManager.getUserWalletDetail().getLastName())))
        {
            rlFirstName.setVisibility(View.GONE);
            rlLastName.setVisibility(View.GONE);
            hasName = true;
        }
        else
        {
            rlFirstName.setVisibility(View.VISIBLE);
            rlLastName.setVisibility(View.VISIBLE);
            hasName = false;
        }

        int addOrCreateWallet = GiftoWalletManager.getInstance().needToAddOrCreateWallet();
        if (addOrCreateWallet == 1)
        {
            rlPassword.setVisibility(View.VISIBLE);
            rlConfirmPassword.setVisibility(View.VISIBLE);
        }
        else
        {
            rlPassword.setVisibility(View.GONE);
            rlConfirmPassword.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.btn_create)
            CreateWalletV2();
    }

    /**
     * Create wallet - api v1
     */
    public void CreateWallet()
    {
        if (!ValidateInputs())
            return;

        SweetAlertDialogUtils.ShowLoadingPopup(GiftoActivity.getInstance());

        final ICallback callback = new ICallback() {
            @Override
            public Object doIt(Object... params) {
                final String result = (String) params[0];
                GiftoActivity.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (result)
                        {
                            case WSConstants.CREATE_WALLET_SUCCESS:
                                SweetAlertDialogUtils.DismissSweetDialog();
                                SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_success),
                                        getString(R.string.create_wallet_success), true, new ICallback() {
                                            @Override
                                            public Object doIt(Object... params) {
                                                GiftoActivity.getInstance().onBackPressed();
                                                return null;
                                            }
                                        });
                                break;
                            case WSConstants.CREATE_WALLET_EXISTS:
                                SweetAlertDialogUtils.DismissSweetDialog();
                                SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_success),
                                        getString(R.string.create_wallet_exists), true, new ICallback() {
                                            @Override
                                            public Object doIt(Object... params) {
                                                GiftoActivity.getInstance().onBackPressed();
                                                return null;
                                            }
                                        });
                                break;
                            case WSConstants.DENY_REQUEST:
                                SweetAlertDialogUtils.DismissSweetDialog();
                                SweetAlertDialogUtils.ShowMessageDialog(GiftoActivity.getInstance(), getString(R.string.another_request_being_processed));
                                break;
                            default:
                                SweetAlertDialogUtils.DismissSweetDialog();
                                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_error),
                                        getString(R.string.create_wallet_fail));
                                break;
                        }
                    }
                });
                return null;
            }
        };

        String email = etEmail.getText().toString().trim();
        String passphrase = etPassword.getText().toString();
        CreateWalletRequest createWalletRequest = new CreateWalletRequest(email, passphrase);
        RestClient.instance().getRestGiftoWalletService().CreateWallet(GiftoWalletManager.getAuthorization(), createWalletRequest, new Callback<DataResponse<GetWalletAddressResponse>>() {
            @Override
            public void success(DataResponse<GetWalletAddressResponse> getWalletAddressResponseDataResponse, Response response) {
                if (getWalletAddressResponseDataResponse == null ||
                        response == null ||
                        response.getStatus() != WSConstants.RESPONSE_SUCCESS)
                {
                    Logger.e(TAG, "GetWalletAddress Unknown Error");
                    callback.doIt(WSConstants.CREATE_WALLET_ERROR);
                }
                else
                {
                    switch (getWalletAddressResponseDataResponse.getStatusCode())
                    {
                        case WSConstants.CREATE_WALLET_SUCCESS:
                            callback.doIt(WSConstants.CREATE_WALLET_SUCCESS, getWalletAddressResponseDataResponse.getData().getWalletAddress());
                            break;
                        case WSConstants.CREATE_WALLET_EXISTS:
                            callback.doIt(WSConstants.CREATE_WALLET_EXISTS);
                            break;
                        case WSConstants.DENY_REQUEST:
                            callback.doIt(WSConstants.DENY_REQUEST);
                            break;
                        default:
                            Logger.e(TAG, "GetWalletAddress Unknown Error");
                            callback.doIt(WSConstants.CREATE_WALLET_ERROR);
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.e(TAG, "GetWalletAddress error");
                callback.doIt(WSConstants.CREATE_WALLET_ERROR);
            }
        });
    }

    /**
     * Create wallet - api v2
     */
    public void CreateWalletV2()
    {
        if (!ValidateInputs())
            return;

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
                                                GiftoActivity.getInstance().onBackPressed();
                                                return null;
                                            }
                                        });
                                break;
                            case WSConstants.RESPONSE_CONFLICT:
                                SweetAlertDialogUtils.ShowSuccessOrFailDialog(GiftoActivity.getInstance(), getResources().getString(R.string.title_success),
                                        getString(R.string.create_wallet_exists), true, new ICallback() {
                                            @Override
                                            public Object doIt(Object... params) {
                                                GiftoActivity.getInstance().onBackPressed();
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

        String email = etEmail.getText().toString().trim();
        String passphrase = GiftoWalletManager.getInstance().needToAddOrCreateWallet() == 1? etPassword.getText().toString() : "abcxyz";
        String firstName = null;
        String lastName = null;
        if (!hasName)
        {
            firstName = etFirstName.getText().toString().trim();
            lastName = etLastName.getText().toString().trim();
        }
        CreateWalletRequest createWalletRequest = new CreateWalletRequest(email, passphrase, firstName, lastName, Constants.CURRENCY_CODE);
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

    /**
     * Check whether all of inputs are correct or not
     * @return true if all of inputs are correct
     *         false else
     */
    public boolean ValidateInputs()
    {
        String error_title = getResources().getString(R.string.title_error);

        int addOrCreateWallet = GiftoWalletManager.getInstance().needToAddOrCreateWallet();
        if (addOrCreateWallet == 1)
        {
            if(!Utils.isStringValid(etPassword.getText().toString()))
            {
                String content = getResources().getString(R.string.msg_please_enter_password);
                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), error_title, content);
                return false;
            }
            else if(!Utils.isStringValid(etPassword.getText().toString().trim()))
            {
                String content = getResources().getString(R.string.msg_invalid_password_whitespace);
                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), error_title, content);
                return false;
            }
            else if(etPassword.getText().toString().contains(" "))
            {
                String content = getString(R.string.msg_password_not_contain_whitespace);
                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), error_title, content);
                return false;
            }
            else if(etPassword.getText().toString().trim().length() < 5)
            {
                String content = getResources().getString(R.string.msg_password_must_have_5_char);
                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), error_title, content);
                return false;
            }
            else if(!Utils.isStringValid(etConfirmPassword.getText().toString()))
            {
                String content = getResources().getString(R.string.msg_please_enter_confirm_password);
                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), error_title, content);
                return false;
            }
            else if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString()))
            {
                String content = getResources().getString(R.string.msg_pass_confirm_pass_not_match);
                SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), error_title, content);
                return false;
            }
        }

        if (Utils.isStringValid(etFirstName.getText().toString().trim()) && etFirstName.getText().toString().trim().length() > Constants.USER_NAME_LIMIT_CHARACTER)
        {
            String content = getResources().getString(R.string.msg_name_max_length);
            SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), error_title, content);
            return false;
        }
        else if (Utils.isStringValid(etLastName.getText().toString().trim()) && etLastName.getText().toString().trim().length() > Constants.USER_NAME_LIMIT_CHARACTER)
        {
            String content = getResources().getString(R.string.msg_name_max_length);
            SweetAlertDialogUtils.ShowErrorDialog(GiftoActivity.getInstance(), error_title, content);
            return false;
        }
        return true;
    }
}