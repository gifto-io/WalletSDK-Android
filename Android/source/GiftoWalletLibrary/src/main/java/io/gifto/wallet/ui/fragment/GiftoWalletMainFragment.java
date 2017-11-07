package io.gifto.wallet.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import de.greenrobot.event.EventBus;
import io.gifto.wallet.R;
import io.gifto.wallet.adapter.GiftoWalletMainAdapter;
import io.gifto.wallet.ui.activity.GiftoActivity;
import io.gifto.wallet.ui.base.BaseFragment;
import io.gifto.wallet.ui.manager.FragmentType;

/**
 * Created by thongnguyen on 8/21/17.
 */

public class GiftoWalletMainFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = "GiftoWalletMainFragment";

    PagerSlidingTabStrip mainTabs;

    ViewPager vpMainLayout;

    private GiftoWalletMainAdapter mainAdapter;

    public static int POSITION_ROSECOIN_HOME = 0;
    public static int POSITION_SENDING_HISTORY = 1;
    public static int POSITION_RECEIVING_HISTORY = 2;

    public static int SELECTED_POSITION = 0;

    private EventBus eventBus = EventBus.getDefault();

    @Override
    public FragmentType getType()
    {
        return FragmentType.ROSE_COIN;
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        SELECTED_POSITION = 0;
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(!isInDialog)
            setHasOptionsMenu(true);
        String title = getString(R.string.rosecoin_wallet);

        if (GiftoActivity.getInstance() != null)
        {
            GiftoActivity.getInstance().SetActivityTitle(title);
        }

        return inflater.inflate(R.layout.fragment_rosecoin_main, null);
    }

    @Override
    protected void BindView(View rootView)
    {
        mainTabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.psts_main_tabs);
        vpMainLayout = (ViewPager) rootView.findViewById(R.id.vp_main_layout);
    }

    @Override
    protected void ViewInjectionSuccess()
    {
        initViewPager();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.rosecoin_menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    public void initViewPager()
    {
        // Init view pager
        if(mainAdapter == null)
        {
            mainAdapter = new GiftoWalletMainAdapter(getContext(), getChildFragmentManager());
            mainAdapter.setInDialog(false);
        }

        vpMainLayout.setAdapter(mainAdapter);
        vpMainLayout.setOffscreenPageLimit(2);

        mainTabs.setViewPager(vpMainLayout);
        mainTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                SELECTED_POSITION = position;
                Refresh();
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
    }

    public void Refresh()
    {
        mainTabs.notifyDataSetChanged();
    }
}
