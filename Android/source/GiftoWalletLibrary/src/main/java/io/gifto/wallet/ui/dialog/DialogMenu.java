package io.gifto.wallet.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import io.gifto.wallet.ui.interfaces.OnDialogTouchOutsideListener;
import io.gifto.wallet.utils.ScreenUtils;

/**
 * Wrapped GUI for a popup window
 */
public class DialogMenu
{
	protected View rootView;
	public PopupWindow menuWindow;
	protected Drawable backGround;
	protected LayoutInflater inflater;
	
	protected int windowWidth = WindowManager.LayoutParams.WRAP_CONTENT;
	protected int windowHeight = WindowManager.LayoutParams.WRAP_CONTENT;
	protected boolean isShow = false;
	protected boolean isLeftToParrent = false;
	protected boolean isAboveOfParrent = false;

	/*
	 * NATRE - Sticker
	 */
	private int leftMargin = 0;
	private int topMargin = 0;
	private int rightMargin = 0;
	private int bottomMargin = 0;

	protected OnDialogTouchOutsideListener onDialogTouchOutsideListener;
	public OnDialogTouchOutsideListener getOnDialogTouchOutsideListener()
	{
		return onDialogTouchOutsideListener;
	}

	/**
	 * Register a listener to handle when user touch outside of popup
	 *
	 * @param onDialogTouchOutsideListener listener to run
	 */
	public void setOnDialogTouchOutsideListener(OnDialogTouchOutsideListener onDialogTouchOutsideListener)
	{
		this.onDialogTouchOutsideListener = onDialogTouchOutsideListener;
	}
	
	/*
	 * NATRE - Sticker
	 */
	public void show(View v)
	{
		if(isShow == false)
		{
			int xPos = 0;
			int yPos = 0;

			int[] location = new int[2];
			v.getLocationOnScreen(location);

			if(isFullScreen)
			{
				int W = (int) ScreenUtils.SCREEN_WIDTH;
				int H = (int) ScreenUtils.SCREEN_HEIGHT;

				xPos = 0;
				yPos = 0;

				H = location[1];

				windowWidth = W;
				windowHeight = H;

				rootView.measure(windowWidth, windowHeight);

				menuWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				menuWindow.setWidth(windowWidth);
				menuWindow.setHeight(windowHeight);
				menuWindow.setContentView(rootView);
			}
			else
			{
				rootView.measure(windowWidth, windowHeight);

				menuWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				menuWindow.setWidth(windowWidth);
				menuWindow.setHeight(windowHeight);
				menuWindow.setContentView(rootView);

				/*
				 * Since the anchor position for a popup is the left top of the anchor view, calculate the x position and y position and override the
				 * location manually
				 */
				xPos = location[0] + v.getMeasuredWidth() / 2 - rootView.getMeasuredWidth() / 2 + getLeftMargin(); // -> center
				if(isLeftToParrent)
				{
					xPos = location[0] + getLeftMargin();
				}

				yPos = location[1] - rootView.getMeasuredHeight() - getBottomMargin(); // -> margin 10dp
				if(isAboveOfParrent) // -> fix
				{
					yPos = location[1] - rootView.getMeasuredHeight() - getBottomMargin();
				}
				if(yPos < 0)
				{
					yPos = location[1] + v.getMeasuredHeight() + 10;
				}
			}

			menuWindow.showAtLocation(v, Gravity.NO_GRAVITY, xPos, yPos);

			this.isShow = true;
		}
	}

	public void setBackground(Drawable image)
	{
		this.backGround = image;
	}

	private boolean isFullScreen = false;

	public void setIsFullScreen(boolean enable)
	{
		isFullScreen = enable;
	}

	public void setContentView(View view)
	{
		this.rootView = view;
		this.menuWindow.setContentView(this.rootView);
	}

	/**
	 * Set listener on window dismissed.
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(OnDismissListener listener)
	{
		if (menuWindow != null)
			menuWindow.setOnDismissListener(listener);
	}

	/**
	 * Dismiss the popup window.
	 */
	public void dismiss()
	{
		this.isShow = false;
		menuWindow.dismiss();
		
//		backGround = null;
//		rootView = null;
//		menuWindow.setBackgroundDrawable(null);
//		menuWindow = null;
	}
	
	public void DestroyPopupParameters()
	{
		
	}

	public boolean isShow()
	{
		return this.isShow;
	}
	public void setIsShow(boolean isShow)
	{
		this.isShow = isShow;
	}

	public int getWindowWidth()
	{
		return windowWidth;
	}
	public void setWindowWidth(int windowWidth)
	{
		this.windowWidth = windowWidth - getRightMargin();
	}
	public int getWindowHeight()
	{
		return windowHeight;
	}
	public void setWindowHeight(int windowHeight)
	{
		this.windowHeight = windowHeight;
	}
	
	public boolean isLeftToParrent()
	{
		return isLeftToParrent;
	}
	public void setLeftToParrent(boolean isLeftToParrent)
	{
		this.isLeftToParrent = isLeftToParrent;
	}
	public boolean isAboveOfParrent()
	{
		return isAboveOfParrent;
	}
	public void setAboveOfParrent(boolean isAboveOfParrent)
	{
		this.isAboveOfParrent = isAboveOfParrent;
	}

	public int getLeftMargin()
	{
		return leftMargin;
	}
	public void setLeftMargin(int leftMargin)
	{
		this.leftMargin = leftMargin;
	}
	public int getTopMargin()
	{
		return topMargin;
	}
	public void setTopMargin(int topMargin)
	{
		this.topMargin = topMargin;
	}
	public int getRightMargin()
	{
		return rightMargin;
	}
	public void setRightMargin(int rightMargin)
	{
		this.rightMargin = rightMargin;
	}
	public int getBottomMargin()
	{
		return bottomMargin;
	}
	public void setBottomMargin(int bottomMargin)
	{
		this.bottomMargin = bottomMargin;
	}

	public void setAnimationStyle(int animationStyle)
	{
		if(menuWindow != null)
			menuWindow.setAnimationStyle(animationStyle);
	}
}
