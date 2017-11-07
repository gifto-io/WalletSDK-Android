package io.gifto.wallet.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import io.gifto.wallet.R;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.dialog.ShowWalletAddressDialog;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.utils.Utils;

/**
 * Created by thongnguyen on 8/4/17.
 */

public class ShowWalletAddressFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = "ShowWalletAddressFragment";

    TextView tvWalletAddress;

    ImageView ivQRCode;

    CircleImageView civAvatar;

    RelativeLayout btnCopy;

    ImageView btnClose;

    private String walletAddress;
    private Bitmap qrCode;

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public void setQrCode(Bitmap qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public FragmentType getType()
    {
        return FragmentType.SHOW_WALLET_ADDRESS;
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onStart()
    {
        isInDialog = true;
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

        if(ShowWalletAddressDialog.getInstance() != null)
        {
            ShowWalletAddressDialog.getInstance().RemoveAllButtons();

            // Change Header GUI
            ShowWalletAddressDialog.getInstance().setHeader(View.GONE, R.color.white);
            ShowWalletAddressDialog.getInstance().setFooter(View.GONE, R.color.edoopad_bg_light_grey);

            ShowWalletAddressDialog.getInstance().setHeaderTitle(getString(R.string.wallet_address), R.color.edoopad_text_color_dark_grey);
        }

        return inflater.inflate(R.layout.fragment_show_wallet_address, null);
    }

    @Override
    protected void BindView(View rootView) {
        tvWalletAddress = (TextView) rootView.findViewById(R.id.tv_wallet_address);

        ivQRCode = (ImageView) rootView.findViewById(R.id.iv_qrCode);

        btnCopy = (RelativeLayout) rootView.findViewById(R.id.btn_copy);

        btnClose = (ImageView) rootView.findViewById(R.id.btn_close);

        civAvatar = (CircleImageView) rootView.findViewById(R.id.civ_avatar);
    }

    @Override
    protected void ViewInjectionSuccess()
    {
        tvWalletAddress.setText(Utils.isStringValid(walletAddress)? walletAddress : "");
        if (qrCode != null)
        {
            ivQRCode.setImageBitmap(qrCode);
            if (Utils.isStringValid(GiftoActivity.getInstance().getAvatar()))
            {
                civAvatar.setVisibility(View.VISIBLE);
                int width = Utils.DpToPx(getContext(), 50);
                Utils.DisplayImage(getContext(), GiftoActivity.getInstance().getAvatar(), civAvatar, width, width);
            }
        }
        else civAvatar.setVisibility(View.GONE);

        btnCopy.setOnClickListener(this);
        btnClose.setOnClickListener(this);


//        if(ShowWalletAddressDialog.getInstance() != null)
//        {
//            ShowWalletAddressDialog.getInstance().AddCloseButton(new ICallback()
//            {
//                @Override
//                public Object doIt(Object... params)
//                {
//                    getActivity().onBackPressed();
//                    return null;
//                }
//            });
//        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.btn_copy)
        {
            ClipboardManager clipboard = (ClipboardManager) GiftoActivity.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("messageCopied", tvWalletAddress.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(GiftoActivity.getInstance(), getString(R.string.copied), Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.btn_close)
        {
            GiftoActivity.getInstance().onBackPressed();
        }
    }
}
