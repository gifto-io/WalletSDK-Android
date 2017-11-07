package io.gifto.wallet.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import de.greenrobot.event.EventBus;
import io.gifto.wallet.R;
import io.gifto.wallet.event.OnQRCodeDetectedEvent;
import io.gifto.wallet.interfaces.OnBuyCoinClickListener;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.dialog.GiftoTransactionDetailDialog;
import io.gifto.wallet.ui.dialog.ShowWalletAddressDialog;
import io.gifto.wallet.ui.fragment.GiftoWalletMainFragment;
import io.gifto.wallet.ui.manager.FragmentChangingManager;
import io.gifto.wallet.ui.manager.MainInterface;
import io.gifto.wallet.utils.ScreenUtils;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.CustomSharedPreferences;
import io.gifto.wallet.utils.common.Logger;

/**
 * Created by thongnguyen on 07/09/2017.
 */

public class GiftoActivity extends AppCompatActivity implements MainInterface {

    public static final String TAG = "GiftoActivity";
    public static final int QR_SCANNER_REQUEST_CODE = 101;

    public static final String EXTRA_AVATAR = "extraAvatar";
    public static final String EXTRA_BUY_COIN_LISTENER = "extraBuyCoinListener";

    static GiftoActivity instance;

    private boolean isRunningInBackground;

    public boolean isRunningInBackground() {
        return isRunningInBackground;
    }

    public static GiftoActivity getInstance()
    {
        return instance;
    }

    Toolbar toolbar;

    private BaseFragment selectedFragment;
    private BaseFragment savedFragment;

    private FragmentChangingManager fragmentManager;
    public static OnBuyCoinClickListener onBuyCoinClickListener;

    private EventBus eventBus = EventBus.getDefault();
    private Handler mHandler;
    private String avatar;

    private static int session = 0;
    private int mSession;

    public int getSession() {
        return mSession;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_rosecoin);

        ++session;
        if (session > 999999)
            session = 1;
        mSession = session;
        instance = this;

        ScreenUtils.InitialScreenParams(this, getResources(), getResources().getBoolean(R.bool.isTablet));

        CustomSharedPreferences.Init(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Utils.setUpForceHideSoftKeyboardWhenTouchOutsideEditText(this, getWindow().getDecorView().getRootView());

        fragmentManager = new FragmentChangingManager(R.id.rl_main_container);
        mHandler = new Handler();
        InitToolBar();

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_AVATAR))
            avatar = getIntent().getStringExtra(EXTRA_AVATAR);

        GiftoWalletMainFragment roseCoinFragment = new GiftoWalletMainFragment();
        DisplayFragment(roseCoinFragment);

        setToolbar(View.VISIBLE);
    }

    @Override
    protected void onDestroy()
    {
        onBuyCoinClickListener = null;
        DismissAllDialogs();
        if (session == mSession)
            instance = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        isRunningInBackground = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isRunningInBackground = true;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rosecoin_menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Display a fragment on GUI
     *
     * @param baseFragment fragment for display
     */
    public void DisplayFragment(final BaseFragment baseFragment)
    {
        GiftoActivity.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.rl_main_container, baseFragment);
                transaction.commitAllowingStateLoss();
            }
        });
    }

    /**
     * Initialize toolbar
     */
    public void InitToolBar()
    {
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);                             // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);                             // Show burger button
        ab.setHomeButtonEnabled(true);
        //ab.setIcon(R.drawable.logo_top);

        ab.setTitle("");
        //ab.setDisplayShowCustomEnabled(true);                         // enable overriding the default toolbar layout
        //ab.setDisplayShowTitleEnabled(false);                         // disable the default title element here (for centered title)
        //ab.setHomeAsUpIndicator(R.drawable.btn_settings_selector);    // set a custom icon for the default home button

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        toolbar.setVisibility(View.VISIBLE);
    }

    /**
     * Set title which is displayed on toolbar
     *
     * @param title title
     */
    public void SetActivityTitle(String title)
    {
        if (Utils.isStringValid(title))
        {
            getSupportActionBar().setTitle(title);
        }
        else
        {
            getSupportActionBar().setTitle("");
        }
    }

    /**
     * Show or hide toolbar
     *
     * @param visibility visibility {VISIBLE, GONE}
     */
    public void setToolbar(int visibility)
    {
        if (toolbar != null)
            toolbar.setVisibility(visibility);
        RelativeLayout rlTopContainer = (RelativeLayout) findViewById(R.id.rl_top_container);
        if (rlTopContainer != null)
        {
            int paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            if (visibility == View.VISIBLE)
                rlTopContainer.setPadding(0, paddingTop, 0, 0);
            else
                rlTopContainer.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * Get current fragment
     *
     * @return fragment is displayed on main screen
     */
    public BaseFragment getSelectedFragment() {
        return selectedFragment;
    }

    @Override
    public void onBackPressed() {

        if(selectedFragment == null)
        {
            instance = null;
            super.onBackPressed();
            return;
        }

        // Check if selectedFragment is not consuming back press
        if(!selectedFragment.onBackPressed())
        {
            // If not consumed, handle it.
            if(!DismissAllDialogs())
            {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void setSelectedFragment(BaseFragment fragment) {
        if(fragment.isInDialog)
        {
            savedFragment = selectedFragment;
        }
        selectedFragment = fragment;
    }

    @Override
    public void AddFragment(BaseFragment fragment, boolean withAnimation) {
        fragmentManager.ChangeFragment(getSupportFragmentManager(), fragment, withAnimation);
    }

    @Override
    public void AddMultipleFragments(BaseFragment[] fragments) {
        fragmentManager.AddMultipleFragments(getSupportFragmentManager(), fragments);
    }

    @Override
    public void PopBackStack() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void PopBackStackTillTag(String tag) {
        getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == QR_SCANNER_REQUEST_CODE && resultCode == RESULT_OK)
        {
            if (data != null && data.getExtras() != null && data.getExtras().containsKey(QRCodeScanerActivity.QR_CODE_DATA))
            {
                String text = data.getExtras().getString(QRCodeScanerActivity.QR_CODE_DATA);
                eventBus.post(new OnQRCodeDetectedEvent(text));
            }
        }
        else
        {
            if (selectedFragment != null)
                selectedFragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Get device orientation
     * @return orientation
     */
    public static int getDeviceDefaultOrientation()
    {
        Activity parrentActivity = GiftoActivity.getInstance();
        WindowManager windowManager = (WindowManager) parrentActivity.getSystemService(parrentActivity.WINDOW_SERVICE);
        Configuration config = GiftoActivity.getInstance().getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();
        if(((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && config.orientation == Configuration.ORIENTATION_PORTRAIT))
        {
            return Configuration.ORIENTATION_LANDSCAPE;
        }
        else
        {
            return Configuration.ORIENTATION_PORTRAIT;
        }
    }

    /**
     * Dismiss all dialogs that is showing
     * @return true if has at least 1 dialog is dismissed
     *          false if no dialog is dismissed
     */
    public boolean DismissAllDialogs()
    {
        if (GiftoTransactionDetailDialog.getInstance() != null)
        {
            GiftoTransactionDetailDialog.getInstance().DismissDialog();
            AfterDismissDialog();
            return true;
        }
        else if (ShowWalletAddressDialog.getInstance() != null)
        {
            ShowWalletAddressDialog.getInstance().DismissDialog();
            AfterDismissDialog();
            return true;
        }
        return false;
    }

    /**
     * Restore previous fragment after dismiss dialog
     */
    public void AfterDismissDialog()
    {
        if(savedFragment != null)
        {
            selectedFragment = savedFragment;
            savedFragment = null;
        }

        invalidateOptionsMenu();
    }

    /**
     * Run a task on UI thread
     *
     * @param runnable task to run
     */
    public static void runOnUIThread(Runnable runnable)
    {
        try
        {
            if (getInstance() != null)
                getInstance().runOnUiThread(runnable);
        }
        catch (Exception e)
        {
            Logger.e(TAG, "runOnUIThread error: " + e.getMessage());
        }
    }

    /**
     * Start activity to scan QRCode
     */
    public static void scanQRCode()
    {
        if (getInstance() != null)
            getInstance().startActivityForResult(new Intent(getInstance(), QRCodeScanerActivity.class), QR_SCANNER_REQUEST_CODE);
    }
}
