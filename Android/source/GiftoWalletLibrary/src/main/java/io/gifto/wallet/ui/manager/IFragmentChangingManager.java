package io.gifto.wallet.ui.manager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import io.gifto.wallet.R;
import io.gifto.wallet.ui.base.BaseFragment;

/**
 * Created by ThangPM on 9/17/15.
 */
public abstract class IFragmentChangingManager
{
    public static final String TAG = "IFragmentChangingManager";

    protected int containerViewId;

    public enum LAYOUT_POSITION
    {
        LEFT,   //NOT FOR NOW
        RIGHT,  //NOT FOR NOW
        MAIN,   //NOT FOR NOW
        FULL
    }

    //init values for mapping helpers
//    protected abstract void initValuesHelperMappings();
    protected abstract void onPrepareChangingFragment(FragmentType type, BaseFragment fragment);
    protected abstract void onPostChangingFragment(FragmentType type, BaseFragment fragment);

    public int getContainerViewId() {
        return containerViewId;
    }
    public void setContainerViewId(int containerViewId) {
        this.containerViewId = containerViewId;
    }

    public IFragmentChangingManager(int viewId)
    {
        this.containerViewId = viewId;
//        initValuesHelperMappings();
    }

    public void ChangeFragment(FragmentManager fragmentManager, BaseFragment fragment, boolean withAnimation)
    {
        try
        {
            //Before: Pop lastest fragment out of backstack and remove it
            onPrepareChangingFragment(fragment.getType(), fragment);

            //Change
            DoChangeFragment(fragmentManager, containerViewId, fragment, withAnimation);

            //After: Push new fragment to backstack
            onPostChangingFragment(fragment.getType(), fragment);
        }
        catch(Exception e)
        {
            e.printStackTrace();
//            Utils.RestartApplication(App.getContext(), MainActivity.class);
        }
    }

    public void DoChangeFragment(FragmentManager fragmentManager, int containerViewId, BaseFragment newFragment, boolean withAnimation)
    {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(withAnimation)
        {
            // TO ENABLE FRAGMENT ANIMATION
            // Format: setCustomAnimations(old_frag_exit, new_frag_enter, old_frag_enter, new_frag_exit);
            ft.setCustomAnimations(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_left,
                    R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_right);
        }
        ft.replace(containerViewId, newFragment, newFragment.getType().getName());
        ft.addToBackStack(newFragment.getType().getName());
        ft.commitAllowingStateLoss();
    }

    public void AddMultipleFragments(FragmentManager fragmentManager, BaseFragment[] fragments)
    {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // Record all steps for the transaction.
        for(int i = 0; i < fragments.length; i++)
        {
            ft.setCustomAnimations(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_left,
                    R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_right);
            ft.replace(containerViewId, fragments[i], fragments[i].getType().getName());
        }

        // Add the transaction to backStack with tag of first added fragment
        ft.addToBackStack(fragments[0].getType().getName());

        // Commit the transaction.
        ft.commit();
    }
}

