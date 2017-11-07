package io.gifto.wallet.utils.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by ThangPM on 9/17/15.
 */
public class MyHandler extends Handler
{
    private final WeakReference<Context> mContext;

    public MyHandler(Context context)
    {
        mContext = new WeakReference<Context>(context);
    }

    @Override
    public void handleMessage(Message msg)
    {
        Context activity = mContext.get();
        if (activity != null)
        {
            // DO SOMETHING NICE
        }
    }
}

// INSTRUCTION
/*
    private final MyHandler mHandler = new MyHandler(this);

    //Instances of anonymous classes do not hold an implicit reference to their outer class when they are "static".

    private static final Runnable sRunnable = new Runnable() {
        @Override
        public void run() { }
    };

    // USING: mHandler.postDelayed(sRunnable, 1000 * 60 * 10);
 */