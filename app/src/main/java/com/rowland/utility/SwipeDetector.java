package com.rowland.utility;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Swipe Detector Class to allow support for onClickItem. LongClickItem and onSwipe actions
 *
 * Issue on <p>
 * <a>http://stackoverflow.com/questions/4373485/android-swipe-on-list</a>
 *
 * @author Rowland
 *
 */
public class SwipeDetector implements View.OnTouchListener {

	public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }

    private static final String logTag = "SwipeDetector";
    private static int Y_MIN_DISTANCE ;
	private static int X_MIN_DISTANCE ;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;

	public SwipeDetector(DisplayMetrics displayMetrics)
	{
		Y_MIN_DISTANCE = Utility.convertDpToPixel(5, displayMetrics);
		X_MIN_DISTANCE = Utility.convertDpToPixel(3, displayMetrics);
	}

    public boolean swipeDetected()
    {
        return mSwipeDetected != Action.None;
    }

    public Action getAction()
    {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_MOVE:
            {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > X_MIN_DISTANCE)
                {
                    // left or right
                    if (deltaX < 0) {
                        Log.i(logTag, "Swipe Left to Right");
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        Log.i(logTag, "Swipe Right to Left");
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                }
                else

                // vertical swipe detection
                if (Math.abs(deltaY) > Y_MIN_DISTANCE)
                {
                    // top or down
                    if (deltaY < 0) {
                        Log.i(logTag, "Swipe Top to Bottom");
                        mSwipeDetected = Action.TB;
                        return false;
                    }
                    if (deltaY > 0)
                    {
                        Log.i(logTag, "Swipe Bottom to Top");
                        mSwipeDetected = Action.BT;
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

}
