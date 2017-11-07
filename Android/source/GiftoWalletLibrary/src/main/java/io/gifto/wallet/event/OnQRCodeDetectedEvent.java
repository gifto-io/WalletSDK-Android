package io.gifto.wallet.event;

/**
 * Created by thongnguyen on 8/4/17.
 *
 * An Object contain data that is detected by QRCode scanner
 */
public class OnQRCodeDetectedEvent {
    private String data;

    public OnQRCodeDetectedEvent(String data)
    {
        this.data = data;
    }

    /**
     * Get data
     *
     * @return data of QRCode
     */
    public String getData() {
        return data;
    }

    /**
     * Set data
     *
     * @param data data of QRCode
     */
    public void setData(String data) {
        this.data = data;
    }
}
