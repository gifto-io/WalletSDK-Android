# Gifto Wallet Library
### Update for API V2
### This is a library support GUI and API for Gifto Wallet. Visit https://gifto.io/ for more information.

### Features
* Build-in GUI
  * **Home**
  
 ![Screenshot](https://github.com/Giftoio/GiftoWalletSDK/blob/master/Android/screenshots/screenshot_home.png)
  
  * **Create Wallet**
  
 ![Screenshot](https://github.com/Giftoio/GiftoWalletSDK/blob/master/Android/screenshots/screenshot_create.png)
  
  * **View Wallet Address**
  
 ![Screenshot](https://github.com/Giftoio/GiftoWalletSDK/blob/master/Android/screenshots/screenshot_address.png)

  * **Transfer Gifto**
  
 ![Screenshot](https://github.com/Giftoio/GiftoWalletSDK/blob/master/Android/screenshots/screenshot_transfer.png)

  * **View Transaction History**
  
 ![Screenshot](https://github.com/Giftoio/GiftoWalletSDK/blob/master/Android/screenshots/screenshot_history.png)
 ![Screenshot](https://github.com/Giftoio/GiftoWalletSDK/blob/master/Android/screenshots/screenshot_history_detail.png)

* Supported APIs:
  * **Create Gifto Wallet using identity data and password**
  * **Get Gifto Wallet Detail of an account using identity data**
  * **Transfer Gifto from an account to another account using wallet address**
  * **Transfer Gifto from an account to another account using identity data (Tipping)**
  * **Get History of Transferring Gifto (Sent and Received, include Transfer and Tipping)**
  
### Installation
* Add maven url into your project's build.gradle file:
```
    allprojects {
      repositories {
          //...
          maven {
              url  "https://dl.bintray.com/gifto-io/maven"
          }
      }
  }
```

* Add the following dependency to your module's build.gradle file:
```
  compile 'io.gifto.wallet:gifto-wallet-sdk:2.0.3'
```

### Usage
* Initial Sdk:
 * **Using your api key and identityData to initial sdk:**
  ```
   new GiftoWalletManager.Builder(yourContext)
                .setApiKey("your-api-key")
                .setUserIdentityData("your-identity-data")
                .setUsingStoringPassphrase(true) //default is false if not set
                .build();
  ```
* Using Build-in GUI:
  * **Start GUI from Java code:**
  ```
    WalletBuildInGUIBuilder.with(yourActivity).start();
  ```
  * **If you want to display user's avatar on QR Code:**
  ```
    WalletBuildInGUIBuilder.with(yourActivity).setUserAvatar("avatar-url").start();
  ```
  
* Using Wrapped APIs:
  * **Add the following import into your java class file:**
  ```
    import io.gifto.wallet.networking.RestClient;
  ```
  * **Using supported APIs**
  ```
    /**
     * Create wallet
     *
     * @param createWalletRequest request for creating the wallet
     * @param callback callback for response
     */
    public static void CreateWallet(final CreateWalletRequest createWalletRequest, final WalletApiResponseCallback<NoResponse> callback)
    
    /**
     * Get wallet's detail
     *
     * @param getWalletDetailRequest request for getting wallet's detail
     * @param callback callback for response
     */
    public static void GetWalletDetail(final GetWalletDetailRequest getWalletDetailRequest, final WalletApiResponseCallback<WalletDetail> callback)
    
    /**
     * Transfer Coin
     *
     * @param transferGiftoRequest request for transferring coin
     * @param callback callback for response
     */
    public static void TransferCoin(final TransferGiftoRequest transferGiftoRequest, final WalletApiResponseCallback<NoResponse> callback)
    
    /**
     * Tip Coin
     *
     * @param tipGiftoRequest request for tipping coin
     * @param callback callback for response
     */
    public static void SendGift(final TipGiftoRequest tipGiftoRequest, final WalletApiResponseCallback<NoResponse> callback)

    /**
     * Tip coin with Build-in GUI
     *
     * @param activity parrent activity
     * @param fromIdentityData sender's identity data
     * @param toIdentityData receiver's identity data
     * @param callback callback for response
     */
    public static void SendGiftWithBuildInGUI(final Activity activity, final String fromIdentityData, final String toIdentityData, final WalletApiResponseCallback<TippingCoinResponse> callback)
    
    /**
     * Get transaction list
     *
     * @param getGiftoTransactionRequest request for getting transaction list
     * @param callback callback for response
     */
    public static void GetTransactionList(final GetGiftoTransactionRequest getGiftoTransactionRequest, final WalletApiResponseCallback<List<GetGiftoTransactionListResponse>> callback)
  ```
  
### Version Information

  * **Version 2.0.3:**
    * Bug fixes
    * Update resources
    
  * **Version 2.0.2:**
    * Refactor "rosecoin" to "gifto"
    * Change repository from "https://dl.bintray.com/rosecoin-io/maven" to "https://dl.bintray.com/gifto-io/maven"
    * Change package id from "io.rosecoin.wallet" to "io.gifto.wallet"
    * Change github repository: https://github.com/gifto-io/WalletSDK-Android.git
    
  * **Version 2.0.1: (https://github.com/rosecoin-io/GiftoWalletSDK/tree/master/Android)**
    * Checking wallet's data before create new wallet
    * Let user use fingerprint to authorize when transfer coin
    * Checking apiKey and identity-data before using SDK (Throw an exception if one is null or empty)
  
  * **Version 2.0.0: (https://github.com/rosecoin-io/GiftoWalletSDK/tree/master/Android)**
    * Release wallet API v2.0
  
## License

    Copyright 2017 Gifto

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
