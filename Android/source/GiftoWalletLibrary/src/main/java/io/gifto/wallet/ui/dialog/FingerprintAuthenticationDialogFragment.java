/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package io.gifto.wallet.ui.dialog;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import io.gifto.wallet.R;
import io.gifto.wallet.GiftoWalletManager;
import io.gifto.wallet.utils.PrefConstants;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.CustomSharedPreferences;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 *
 *
 * Edited by ThongNguyen - 1/11/2017
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements TextView.OnEditorActionListener, FingerprintUiHelper.Callback {

    static final String DEFAULT_KEY_NAME = "wallet_sdk_default_key";
    public static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7;
    public static final String CHARSET_NAME = "UTF-8";

    private Button mCancelButton;
    private Button mSecondDialogButton;
    private View mFingerprintContent;
    private View mBackupContent;
    private EditText mPassword;
    private CheckBox mUseFingerprintFutureCheckBox;
    private TextView mPasswordDescriptionTextView;
    private TextView mFingerprintDescriptionTextView;
    private TextView mNewFingerprintEnrolledTextView;

    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private Context mActivity;
    private Callback callback;

    private InputMethodManager mInputMethodManager;

    private boolean isFingerprintSupported;

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;

    private boolean onSaveInstanceState = false;
    private boolean needToDismiss = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onSaveInstanceState = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onSaveInstanceState || needToDismiss)
            dismiss();
        onSaveInstanceState = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.authorization));
        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancel();
            }
        });

        mSecondDialogButton = (Button) v.findViewById(R.id.second_dialog_button);
        mSecondDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStage == Stage.FINGERPRINT) {
                    goToBackup(Stage.PASSWORD);
                } else {
                    verifyPassword();
                }
            }
        });
        mFingerprintContent = v.findViewById(R.id.fingerprint_container);
        mBackupContent = v.findViewById(R.id.backup_container);
        mPassword = (EditText) v.findViewById(R.id.password);
        mPassword.setOnEditorActionListener(this);
        mPasswordDescriptionTextView = (TextView) v.findViewById(R.id.password_description);
        mFingerprintDescriptionTextView = (TextView) v.findViewById(R.id.fingerprint_description);
        mUseFingerprintFutureCheckBox = (CheckBox)
                v.findViewById(R.id.use_fingerprint_in_future_check);
        mNewFingerprintEnrolledTextView = (TextView)
                v.findViewById(R.id.new_fingerprint_enrolled_description);
        mFingerprintUiHelper = new FingerprintUiHelper(
                mActivity.getSystemService(FingerprintManager.class),
                (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status), this);

        isFingerprintSupported = true;

        KeyguardManager keyguardManager = getContext().getSystemService(KeyguardManager.class);
        if (!keyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            isFingerprintSupported = false;
        }

        // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
        // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        if (!mFingerprintUiHelper.isFingerprintAuthAvailable()) {
            // This happens when no fingerprints are registered.
            isFingerprintSupported = false;
        }

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        try {
            mCipher = Cipher.getInstance(TRANSFORMATION);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        String encryptedPass = CustomSharedPreferences.getPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
        if (!Utils.isStringValid(encryptedPass))
        {
            mStage = Stage.NEW_PASSWORD;
            CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, false);
        }
        else
        {
            if (CustomSharedPreferences.getPreferences(PrefConstants.PREF_USE_FINGERPRINT, false))
            {
                if (!isFingerprintSupported)
                {
                    mStage = Stage.NO_FINGERPRINT_ENROLLED;
                }
                else if (initCipher(mCipher, DEFAULT_KEY_NAME, Cipher.DECRYPT_MODE))
                {
                    mCryptoObject = new FingerprintManager.CryptoObject(mCipher);
                    mStage = Stage.FINGERPRINT;
                    mFingerprintUiHelper.startListening(mCryptoObject);
                }
                else
                {
                    mStage = Stage.NEW_FINGERPRINT_ENROLLED;
                    CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
                    CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, false);
                }
            }
            else mStage = Stage.PASSWORD;
        }

        updateStage();

        switch (mStage)
        {
            case PASSWORD: case NEW_PASSWORD: case NEW_FINGERPRINT_ENROLLED: case NO_FINGERPRINT_ENROLLED:
                mPassword.requestFocus();
                mPassword.postDelayed(mShowKeyboardRunnable, 500);
                break;
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = context;
        mInputMethodManager = context.getSystemService(InputMethodManager.class);
    }

    /**
     * Sets the callback for result
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     *
     */
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }

            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the
     * {@link #createKey(String, boolean)} method.
     *
     * @param keyName the key name to init the cipher
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    private boolean initCipher(Cipher cipher, String keyName, int cipherMode) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            if (cipherMode == Cipher.ENCRYPT_MODE)
                cipher.init(cipherMode, key);
            else if (cipherMode == Cipher.DECRYPT_MODE)
            {
                String encryptionIV = CustomSharedPreferences.getPreferences(PrefConstants.PREF_ENCRYPTION_IV, "");
                cipher.init(cipherMode, key, new IvParameterSpec(Base64.decode(encryptionIV, 0)));
            }
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /**
     * Switches to backup (password) screen. This either can happen when fingerprint is not
     * available or the user chooses to use the password authentication method by pressing the
     * button. This can also happen when the user had too many fingerprint attempts.
     */
    private void goToBackup(Stage stage) {
        mStage = stage;
        updateStage();
        mPassword.requestFocus();

        // Show the keyboard.
        mPassword.postDelayed(mShowKeyboardRunnable, 500);

        // Fingerprint is not used anymore. Stop listening for it.
        mFingerprintUiHelper.stopListening();
    }

    /**
     * Checks whether the current entered password is correct, and dismisses the the dialog and
     * let's the activity know about the result.
     */
    private void verifyPassword() {
        if (!Utils.isStringValid(mPassword.getText().toString())) {
            return;
        }

        switch (mStage)
        {
            case NEW_PASSWORD: case NEW_FINGERPRINT_ENROLLED:
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, isFingerprintSupported && mUseFingerprintFutureCheckBox.isChecked());
                if (isFingerprintSupported && mUseFingerprintFutureCheckBox.isChecked())
                {
                    createKey(DEFAULT_KEY_NAME, true);
                    initCipher(mCipher, DEFAULT_KEY_NAME, Cipher.ENCRYPT_MODE);
                    mCryptoObject = new FingerprintManager.CryptoObject(mCipher);
                    mFingerprintUiHelper.startListening(mCryptoObject);
                    mStage = Stage.FINGERPRINT_WITHOUT_PASSWORD;
                    updateStage();
                }
                else
                {
                    GiftoWalletManager.setUserSecurePassphrase(mPassword.getText().toString());
                    if (callback != null)
                        callback.onPasswordAuthenticated(mPassword.getText().toString(), PassphraseSource.STORE_PASSPHRASE);
                    if (onSaveInstanceState)
                        needToDismiss = true;
                    else dismiss();
                }
                break;
            case PASSWORD: case NO_FINGERPRINT_ENROLLED:
                if (callback != null)
                    callback.onPasswordAuthenticated(mPassword.getText().toString(), PassphraseSource.USER_INPUT_PASSPHRASE);
            if (onSaveInstanceState)
                needToDismiss = true;
            else dismiss();
                break;
        }
    }

    private final Runnable mShowKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            mInputMethodManager.showSoftInput(mPassword, 0);
        }
    };

    /**
     * Update GUI by current state
     */
    private void updateStage() {
        switch (mStage) {
            case NEW_PASSWORD:
                mCancelButton.setVisibility(View.VISIBLE);
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setVisibility(View.VISIBLE);
                mSecondDialogButton.setText(R.string.ok);
                mFingerprintContent.setVisibility(View.GONE);
                mBackupContent.setVisibility(View.VISIBLE);
                mPasswordDescriptionTextView.setVisibility(View.VISIBLE);
                mPasswordDescriptionTextView.setText(getString(R.string.require_password_intro));
                mPassword.setText("");
                if (isFingerprintSupported) {
                    mUseFingerprintFutureCheckBox.setVisibility(View.VISIBLE);
                    mUseFingerprintFutureCheckBox.setChecked(false);
                }
                else mUseFingerprintFutureCheckBox.setVisibility(View.GONE);
                break;
            case FINGERPRINT_WITHOUT_PASSWORD:
                mCancelButton.setVisibility(View.VISIBLE);
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setVisibility(View.GONE);
                mFingerprintContent.setVisibility(View.VISIBLE);
                mBackupContent.setVisibility(View.GONE);
                break;
            case FINGERPRINT:
                mCancelButton.setVisibility(View.VISIBLE);
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setVisibility(View.VISIBLE);
                mSecondDialogButton.setText(R.string.use_password);
                mFingerprintContent.setVisibility(View.VISIBLE);
                mBackupContent.setVisibility(View.GONE);
                break;
            case PASSWORD: case NO_FINGERPRINT_ENROLLED:
                mCancelButton.setVisibility(View.VISIBLE);
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setVisibility(View.VISIBLE);
                mSecondDialogButton.setText(R.string.ok);
                mFingerprintContent.setVisibility(View.GONE);
                mBackupContent.setVisibility(View.VISIBLE);
                mPasswordDescriptionTextView.setVisibility(View.VISIBLE);
                mPasswordDescriptionTextView.setText(getString(R.string.please_enter_password_to_continue));
                mPassword.setText("");
                mUseFingerprintFutureCheckBox.setVisibility(View.GONE);
                if (mStage == Stage.NO_FINGERPRINT_ENROLLED)
                {
                    mPasswordDescriptionTextView.setText(getString(R.string.no_fingerprint_enrolled));
                }
                break;
            case NEW_FINGERPRINT_ENROLLED:
                mCancelButton.setVisibility(View.VISIBLE);
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setVisibility(View.VISIBLE);
                mSecondDialogButton.setText(R.string.ok);
                mFingerprintContent.setVisibility(View.GONE);
                mBackupContent.setVisibility(View.VISIBLE);
                mPasswordDescriptionTextView.setVisibility(View.VISIBLE);
                mPasswordDescriptionTextView.setText(getString(R.string.new_fingerprint_enrolled_description));
                mPassword.setText("");
                if (isFingerprintSupported) {
                    mUseFingerprintFutureCheckBox.setVisibility(View.VISIBLE);
                    mUseFingerprintFutureCheckBox.setChecked(false);
                }
                else mUseFingerprintFutureCheckBox.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            verifyPassword();
            return true;
        }
        return false;
    }

    @Override
    public void onAuthenticated() {
        if (mStage == Stage.FINGERPRINT_WITHOUT_PASSWORD)
        {
            try {

                byte[] encryptionIv = mCipher.getIV();
                byte[] encrypted = mCipher.doFinal(mPassword.getText().toString().getBytes(CHARSET_NAME));
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, Base64.encodeToString(encrypted, 0));
                CustomSharedPreferences.setPreferences(PrefConstants.PREF_ENCRYPTION_IV, Base64.encodeToString(encryptionIv, 0));

                if (callback != null)
                    callback.onPasswordAuthenticated(mPassword.getText().toString(), PassphraseSource.STORE_PASSPHRASE);
                if (onSaveInstanceState)
                    needToDismiss = true;
                else dismiss();

            } catch (Exception e) {
                e.printStackTrace();

                if (e instanceof IllegalBlockSizeException && Utils.isStringValid(e.getCause().getMessage()) && e.getCause().getMessage().equals("Key user not authenticated"))
                {
                    mStage = Stage.NEW_PASSWORD;
                    mUseFingerprintFutureCheckBox.setChecked(true);
                    verifyPassword();
                    mFingerprintDescriptionTextView.setText(getString(R.string.new_fingerprint_confirm_again));
                }
                else
                {
                    CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, false);
                    if (callback != null)
                        callback.onPasswordAuthenticated(mPassword.getText().toString(), PassphraseSource.USER_INPUT_PASSPHRASE);
                    if (onSaveInstanceState)
                        needToDismiss = true;
                    else dismiss();
                }
            }
        }
        else
        {
            try {
                String encrypted = CustomSharedPreferences.getPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
                byte[] decrypted = mCipher.doFinal(Base64.decode(encrypted, 0));
                String password = new String(decrypted, CHARSET_NAME);

                if (callback != null)
                    callback.onPasswordAuthenticated(password, PassphraseSource.STORE_PASSPHRASE);
                if (onSaveInstanceState)
                    needToDismiss = true;
                else dismiss();

            } catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
                e.printStackTrace();

                if (e instanceof IllegalBlockSizeException && Utils.isStringValid(e.getCause().getMessage()) && e.getCause().getMessage().equals("Key user not authenticated"))
                {
                    CustomSharedPreferences.setPreferences(PrefConstants.PREF_USER_SECURE_PASSPHRASE, "");
                    CustomSharedPreferences.setPreferences(PrefConstants.PREF_USE_FINGERPRINT, false);
                    goToBackup(Stage.NEW_FINGERPRINT_ENROLLED);
                }
                else
                {
                    goToBackup(Stage.PASSWORD);
                    mPasswordDescriptionTextView.setText(getString(R.string.failed_to_authorize));
                }
            }
        }
    }

    @Override
    public void onError() {
        if (mStage == Stage.FINGERPRINT_WITHOUT_PASSWORD)
        {
            onCancel();
        }
        else goToBackup(Stage.PASSWORD);
    }

    public void onCancel()
    {
        switch (mStage)
        {
            case NEW_PASSWORD:
                break;
            case FINGERPRINT_WITHOUT_PASSWORD:
                mStage = Stage.NEW_PASSWORD;
                mUseFingerprintFutureCheckBox.setChecked(false);
                verifyPassword();
                return;
            case FINGERPRINT:
                break;
            case PASSWORD: case NO_FINGERPRINT_ENROLLED:
                break;
            case NEW_FINGERPRINT_ENROLLED:
                break;
        }
        if (onSaveInstanceState)
            needToDismiss = true;
        else dismiss();
    }

    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    public enum Stage {
        NEW_PASSWORD,                       // 1
        FINGERPRINT_WITHOUT_PASSWORD,       // 2
        FINGERPRINT,                        // 3
        PASSWORD,                           // 4
        NEW_FINGERPRINT_ENROLLED,           // 5
        NO_FINGERPRINT_ENROLLED             // 6
    }

    public enum PassphraseSource
    {
        STORE_PASSPHRASE,
        USER_INPUT_PASSPHRASE
    }

    public interface Callback
    {
        void onPasswordAuthenticated(String passphrase, PassphraseSource source);

        void onCanceled();
    }
}
