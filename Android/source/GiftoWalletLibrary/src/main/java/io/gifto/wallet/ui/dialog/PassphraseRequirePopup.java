package io.gifto.wallet.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import io.gifto.wallet.R;
import io.gifto.wallet.utils.ScreenUtils;


/**
 * Created by thongnguyen on 7/18/17.
 *
 * A popup for asking passphrase from user
 */

public class PassphraseRequirePopup extends DialogMenu implements View.OnClickListener
{
    private static final String TAG = "PassphraseRequirePopup";

    EditText etPassword;
    RelativeLayout btnContinue;

    ImageView background;
    LinearLayout llContainer;


    private int xPos = 0;
    private int yPos = 0;

    private Context mContext;

    private long lastClickTime = 0;

    public static String passpharse;

    @Override
    public void dismiss()
    {
        super.dismiss();
    }

    @SuppressWarnings("deprecation")
    public PassphraseRequirePopup(Context context)
    {
        mContext = context;

        this.menuWindow = new PopupWindow(context);
        menuWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (onDialogTouchOutsideListener != null) {
                        onDialogTouchOutsideListener.onDialogTouchOutside();
                    }
                    return true;
                }
                return false;
            }
        });
        menuWindow.setOutsideTouchable(true);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.fragment_require_passpharse, null);

        etPassword = (EditText) rootView.findViewById(R.id.et_password);
        btnContinue = (RelativeLayout) rootView.findViewById(R.id.btn_continue);

        background = (ImageView) rootView.findViewById(R.id.background);
        llContainer = (LinearLayout) rootView.findViewById(R.id.ll_container);

        btnContinue.setOnClickListener(this);
        background.setOnClickListener(this);
        llContainer.setOnClickListener(this);

        // ====================================== Adjust item =======================================

        menuWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        menuWindow.setFocusable(true);
        menuWindow.update();
    }

    @Override
    public void onClick(View v)
    {
        if(SystemClock.elapsedRealtime() - lastClickTime < 500)
            return;

        lastClickTime = SystemClock.elapsedRealtime();

        int id = v.getId();
        if (id == R.id.btn_continue)
        {
            passpharse = etPassword.getText().toString();
            dismiss();
        }
        else if (id == R.id.background)
        {
            passpharse = etPassword.getText().toString();
            dismiss();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void show(@NonNull View vc)
    {
        if (isShow)
            return;

        windowWidth = (int) (ScreenUtils.SCREEN_WIDTH);
        windowHeight = (int) (ScreenUtils.SCREEN_HEIGHT);

        menuWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuWindow.setContentView(rootView);

        xPos = 0;
        yPos = 0;

        menuWindow.setWidth(windowWidth);
        menuWindow.setHeight(windowHeight);
        menuWindow.showAtLocation(vc, Gravity.NO_GRAVITY, xPos, yPos);

        isShow = true;
        passpharse = "";
    }
}
