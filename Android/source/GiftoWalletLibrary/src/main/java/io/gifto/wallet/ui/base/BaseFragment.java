package io.gifto.wallet.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.progressfragment.ProgressFragment;

import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.manager.FragmentType;
import io.gifto.wallet.ui.manager.IFragmentManager;
import io.gifto.wallet.utils.Utils;
import io.gifto.wallet.utils.common.ICallback;
import io.gifto.wallet.utils.common.MyHandler;

/**
 * Created by ThangPM on 9/17/15.
 */
public abstract class BaseFragment extends ProgressFragment
{
    protected IFragmentManager fragmentManagerInterface;
    /**
     * Used to save from reinitializing Views when onViewCreated is called again
     * after a PopBackStack() call. To be used only when caching view state.
     */
    private boolean hasInitializedRootView = false;
    public boolean isInDialog = false;

    public void setIsInDialog(boolean isInDialog) {
        this.isInDialog = isInDialog;
    }

    private View mRootView;
    private final MyHandler mHandler = new MyHandler(GiftoActivity.getInstance());

    private FragmentType type;

    //Callback
    protected ICallback onFragmentChangingSuccessCallback;
//    protected ICallback onBackPressedCallback;

    /**
     * Will be used as handle to save transactions in backstack
     * 
     * @return tag text
     */
    public abstract FragmentType getType();

    /**
     * To enable fragments capture back-press event and utilize it before it's
     * used in the hosting Activity.
     * 
     * @return true if consumed, else false
     */
    public abstract boolean onBackPressed();

    //Winny's Abstract Methods
//    protected abstract void AttachToUI(IFragmentManager manager, boolean isForceChange);
    protected abstract View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void ViewInjectionSuccess();
    protected abstract void BindView(View rootView);

    public BaseFragment() {}

    /**
     * Must be used if view caching is required from onCreateView of child
     * fragment to create the rootview for it.
     *
     * This is a trick to retain View state of a fragment when removed and added back.
     *
     * However, this is not ideal and essentially a hack. This contradicts Fragment's goal of being
     * memory-friendly. This method keeps cached view state of a fragment when until it is referenced.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @param layout
     * @return rootView for fragment, either new or a cached one.
     */
    public View createPersistentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layout)
    {
        if(mRootView == null)
        {
            // Inflate the layout for this fragment
            mRootView = inflater.inflate(layout, null);
        }
        else
        {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove _rootView from the existing parent view group (it will be added back).
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getActivity() instanceof IFragmentManager)
        {
            fragmentManagerInterface = (IFragmentManager) getActivity();
        }
        else
        {
            /**
             * ThangPM
             * Date: 30-10-2016
             * Note: Comment this line because there are some new activities which doesn't implement MainInterface
             */
            //throw new ClassCastException("Hosting activity must implement HostActivityInterface and FacebookInterface.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = CreateView(inflater, container, savedInstanceState);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        setContentView(mRootView);

        BindView(mRootView);

        Utils.setUpForceHideSoftKeyboardWhenTouchOutsideEditText(getActivity(), getView());

        ViewInjectionSuccess();

        obtainData();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mHandler.removeCallbacks(mShowContentRunnable);
    }

    private void obtainData()
    {
        // Show indeterminate progress
        mHandler.postDelayed(mShowContentRunnable, 500);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Mark this fragment as the selected Fragment.
        if(fragmentManagerInterface != null)
            fragmentManagerInterface.setSelectedFragment(this);
    }

    public boolean isOnForeground()
    {
        return this.isVisible();
    }

    private Runnable mShowContentRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            setContentShown(true);
        }
    };
}
