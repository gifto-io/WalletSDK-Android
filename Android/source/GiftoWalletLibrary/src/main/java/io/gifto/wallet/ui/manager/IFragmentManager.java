package io.gifto.wallet.ui.manager;


import io.gifto.wallet.ui.base.BaseFragment;

/**
 * Created by ThangPM on 9/17/15.
 */
public interface IFragmentManager
{
    public void setSelectedFragment(BaseFragment fragment);
    public void PopBackStack();
    public void PopBackStackTillTag(String tag);
    public void AddFragment(BaseFragment fragment, boolean withAnimation);
    public void AddMultipleFragments(BaseFragment fragments[]);
}
