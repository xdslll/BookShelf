package read.xiaotu.com.bookshelf.viewgroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public class MyViewGroup extends ViewGroup {

    Rect mTouchFrame;

    public static final int INVALID_POSITION = -1;

    WindowManager mWindowManager;
    WindowManager.LayoutParams mWindowLayoutParams;

    int mDownX;
    int mDownY;
    int mMoveX;
    int mMoveY;
    int mDragPosition;

    View mStartDragItemView;

    ImageView mDragImageView;

    Bitmap mDragBitmap;

    private Handler mHandler = new Handler();

    private static final int LONG_CLICK_RESPONSE = 1000;

    private boolean mIsDrag = false;

    public MyViewGroup(Context context) {
        this(context, null);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        addListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.e("TAG", "width=" + widthMeasureSpec);
        //Log.e("TAG", "height=" + heightMeasureSpec);
        //Log.e("TAG", "getSuggestedMinimumWidth()=" + getSuggestedMinimumWidth());
        //Log.e("TAG", "getSuggestedMinimumHeight()=" + getSuggestedMinimumHeight());

        //Log.e("TAG", "ViewGroup onMeasure");
        //Log.e("TAG", "ViewGroup getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec)=" + getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec));
        //Log.e("TAG", "ViewGroup getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec)=" + getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));

        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //Log.e("TAG", "ViewGroup onLayout(changed=" + changed + ",left=" + l + ",top=" + t + ",right=" + r + ",bottom=" + b + ")");
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int height = b - t;
            int childHeight = height / getChildCount();
            int top = t + childHeight * i;
            int bottom = top + childHeight;
            view.layout(l, top, r, bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (mIsDrag && mDragImageView != null) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.e("TAG", "ViewGroup onTouchEvent.ACTION_DOWN");
                    break;
                case MotionEvent.ACTION_MOVE:
                    mMoveX = (int) event.getX();
                    mMoveY = (int) event.getY();
                    Log.e("TAG", "ViewGroup onTouchEvent.ACTION_MOVE(" + mMoveX + "," + mMoveY + ")");
                    mWindowLayoutParams.x = mMoveX;
                    mWindowLayoutParams.y = mMoveY;
                    mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams);

                    break;
                case MotionEvent.ACTION_UP:
                    Log.e("TAG", "ViewGroup onTouchEvent.ACTION_UP");
                    stopDrag();
                    break;
            }
            return true;
        } else {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                int rawX = (int) event.getRawX();
                int rawY = (int) event.getRawY();
                mDragPosition = pointToPosition(mDownX, mDownY);
                Log.e("TAG", "ViewGroup dispatchTouchEvent.ACTION_DOWN(x=" + mDownX + ",y=" + mDownY + ",rawX=" + rawX + ",rawY=" + rawY + ",position=" + mDragPosition + ")");

                if (mDragPosition == INVALID_POSITION) {
                    return super.dispatchTouchEvent(event);
                }

                mHandler.postDelayed(mLongClickListener, LONG_CLICK_RESPONSE);

                mStartDragItemView = getChildAt(mDragPosition);

                mStartDragItemView.setDrawingCacheEnabled(true);
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                mStartDragItemView.destroyDrawingCache();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("TAG", "ViewGroup dispatchTouchEvent.ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("TAG", "ViewGroup dispatchTouchEvent.ACTION_UP");
                mHandler.removeCallbacks(mLongClickListener);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.e("TAG", "ViewGroup onInterceptTouchEvent.ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("TAG", "ViewGroup onInterceptTouchEvent.ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("TAG", "ViewGroup onInterceptTouchEvent.ACTION_UP");
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private void stopDrag() {
        mIsDrag = false;
        if (mStartDragItemView != null) {
            mStartDragItemView.setVisibility(VISIBLE);
        }
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
            mDragBitmap = null;
        }
    }

    private Runnable mLongClickListener = new Runnable() {
        @Override
        public void run() {
            mIsDrag = true;
            mStartDragItemView.setVisibility(INVISIBLE);

            mWindowLayoutParams = new WindowManager.LayoutParams();
            mWindowLayoutParams.x = (int) mStartDragItemView.getX();
            mWindowLayoutParams.y = (int) mStartDragItemView.getY();
            mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowLayoutParams.alpha = 0.55f;

            mDragImageView = new ImageView(getContext());
            mDragImageView.setImageBitmap(mDragBitmap);

            mWindowManager.addView(mDragImageView, mWindowLayoutParams);
        }
    };

    private int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return i;
                }
            }
        }
        return INVALID_POSITION;
    }

    private void addListener() {
        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                log("ViewGroup onPreDraw");
                return false;
            }
        });
        observer.addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                log("ViewGroup onGlobalFocusChanged");
            }
        });
        observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                log("ViewGroup onScrollChanged");
            }
        });
        observer.addOnTouchModeChangeListener(new ViewTreeObserver.OnTouchModeChangeListener() {
            @Override
            public void onTouchModeChanged(boolean isInTouchMode) {
                log("ViewGroup onTouchModeChanged");
            }
        });
    }

    private void log(String msg) {
        Log.e("TAG", msg);
    }
}
