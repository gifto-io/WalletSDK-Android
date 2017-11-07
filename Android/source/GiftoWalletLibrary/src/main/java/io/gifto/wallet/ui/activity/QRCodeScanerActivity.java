package io.gifto.wallet.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.Result;

import io.gifto.wallet.R;
import io.gifto.wallet.utils.SweetAlertDialogUtils;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.Logger;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by thongnguyen on 9/29/17.
 */

public class QRCodeScanerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    private static final String TAG = "QRCodeScanerActivity";
    public static final String QR_CODE_DATA = "qr_code_data";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 201;
    private static final int CAMERA_PERMISSION_SETTING_REQUEST_CODE = 202;

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scaner);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        else
            scan();
    }

    /**
     * Start scanner
     */
    private void scan()
    {
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null)
            mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        Logger.e(TAG, rawResult.getText()); // Prints scan results
        Logger.e(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        setResult(RESULT_OK, new Intent().putExtra(QR_CODE_DATA, rawResult.getText()));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PERMISSION_SETTING_REQUEST_CODE)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                finish();
            else scan();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
                    finish();
                else
                {
                    SweetAlertDialogUtils.ShowMessageWithActionButton(this, getString(R.string.ask_camera_permission), getString(R.string.setting), new ICallback() {
                        @Override
                        public Object doIt(Object... params) {
                            boolean result = (boolean) params[0];
                            if (result)
                            {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, CAMERA_PERMISSION_SETTING_REQUEST_CODE);
                            }
                            else finish();
                            return null;
                        }
                    });
                }
            }
            else scan();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
