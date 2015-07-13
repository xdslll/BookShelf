package read.xiaotu.com.bookshelf.grid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.util.LinkedList;
import java.util.List;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public class DragGridView extends GridView {

    /**
     * 震动的时间
     */
    private final static long VIBRATE_TIME = 50;

    /**
     * 拖动时生成镜像的Alpha值
     */
    private final static float DRAG_IMAGE_ALPHA = 0.55f;

    /**
     * 长按的响应时间
     */
    private long mDragResponseMs = 1000;

    /**
     * 拖拽item触发滚动时的速度
     */
    private int mScrollSpeed = 25;

    /**
     * 滚动的触发间隔
     */
    private int mScrollDuration = 10;

    /**
     * 是否可以拖拽
     */
    private boolean mIsDrag = false;

    /**
     * 按下时的X、Y坐标
     */
    private int mDownX;
    private int mDownY;

    /**
     * 移动时的X、Y坐标
     */
    private int mMoveX;
    private int mMoveY;

    /**
     * 拖动item的position
     */
    private int mDragPosition;

    /**
     * 最初拖动item的View
     */
    private View mStartDragItemView = null;

    /**
     * 拖动时的镜像图片
     */
    private ImageView mDragImageView;

    /**
     * 震动服务
     */
    private Vibrator mVibrator;

    /**
     * WindowManager服务
     */
    private WindowManager mWindowManager;

    /**
     * item镜像的布局
     */
    private WindowManager.LayoutParams mWindowLayoutParams;

    /**
     * 拖动的item对应的Bitmap
     */
    private Bitmap mDragBitmap;

    /**
     * 按下点距所在item上边缘的距离
     */
    private int mPoint2ItemTop;

    /**
     * 按下点距所在item左边缘的距离
     */
    private int mPoint2ItemLeft;

    /**
     * DragGridView距离屏幕顶部的偏移量
     */
    private int mOffset2Top;

    /**
     * DragGridView距离屏幕左侧的偏移量
     */
    private int mOffset2Left;

    /**
     * 手机系统状态栏的高度
     */
    private int mStatusHeight;

    /**
     * DragGridView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * DragGridView自动向上滚动的边界值
     */
    private int mUpScrollBorder;

    /**
     * DragGridView自动滚动的速度
     */
    private static final int SPEED = 20;

    private OnChangeListener mOnChangeListener;

    private Handler mHandler = new Handler();

    private boolean mAnimationEnd = true;

    private DragGridBaseAdapter mDragAdapter;

    private int mNumColumns;

    private int mColumnWidth;

    private boolean mNumColumnSet;

    private int mHorizontalSpacing;

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //获取系统震动服务
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //获取WindowManager服务
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //获取系统状态栏的高度
        mStatusHeight = getStatusHeight(context);

        if (!mNumColumnSet) {
            mNumColumns = AUTO_FIT;
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof DragGridBaseAdapter) {
            mDragAdapter = (DragGridBaseAdapter) adapter;
        } else {
            throw new IllegalStateException("the adapter must be implements DragGridBaseAdapter");
        }
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mNumColumnSet = true;
        this.mNumColumns = numColumns;
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        super.setColumnWidth(columnWidth);
        mColumnWidth = columnWidth;
    }

    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        super.setHorizontalSpacing(horizontalSpacing);
        this.mHorizontalSpacing = horizontalSpacing;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNumColumns == AUTO_FIT) {
            int numFittedColumns;
            if (mColumnWidth > 0) {
                int gridWidth = Math.max(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight(), 0);
                numFittedColumns = gridWidth / mColumnWidth;
                if (numFittedColumns > 0) {
                    while (numFittedColumns != 1) {
                        if (numFittedColumns * mColumnWidth + (numFittedColumns - 1) * mHorizontalSpacing > gridWidth) {
                            numFittedColumns--;
                        } else {
                            break;
                        }
                    }
                } else {
                    numFittedColumns = 1;
                }
            } else {
                numFittedColumns = 2;
            }
            mNumColumns = numFittedColumns;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取X、Y坐标
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                //获取用户点击GridView中item的position
                mDragPosition = pointToPosition(mDownX, mDownY);
                if (mDragPosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }
                //如果用户长按则触发长按事件
                mHandler.postDelayed(mLongClickRunnable, mDragResponseMs);
                //根据position获取item所对应的View
                mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());

                mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                mOffset2Top = (int) (ev.getRawX() - mDownY);
                mOffset2Left = (int) (ev.getRawX() - mDownX);

                mDownScrollBorder = getHeight() / 5;
                mUpScrollBorder = getHeight() * 4 / 5;

                mStartDragItemView.setDrawingCacheEnabled(true);
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                mStartDragItemView.destroyDrawingCache();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();

                if (!isTouchItem(mStartDragItemView, moveX, moveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsDrag && mDragImageView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    mMoveX = (int) ev.getX();
                    mMoveY = (int) ev.getY();
                    onDragItem(mMoveX, mMoveY);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    mIsDrag = false;
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 拖动item，实现了item镜像的位置更新，item的相互交互及GridView自动滚动
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams);
        onSwapItem(moveX, moveY);

        mHandler.post(mScrollRunnable);
    }

    /**
     * 交换item，并控制item之间的显示和隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        //获取我们手指移动到的那个item的position
        final int tempPosition = pointToPosition(moveX, moveY);

        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION && mAnimationEnd) {
            mDragAdapter.reorderItems(mDragPosition, tempPosition);
            mDragAdapter.setHideItem(tempPosition);
            final ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    animateReorder(mDragPosition, tempPosition);
                    mDragPosition = tempPosition;
                    return true;
                }
            });

            /*if (mOnChangeListener != null) {
                mOnChangeListener.onChange(mDragPosition, tempPosition);
            }

            getChildAt(tempPosition - getFirstVisiblePosition()).setVisibility(INVISIBLE);
            getChildAt(mDragPosition - getFirstVisiblePosition()).setVisibility(VISIBLE);

            mDragPosition = tempPosition;*/
        }
    }

    private AnimatorSet createTranslationAnimations(View view, float startX, float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    private void animateReorder(final int oldPosition, final int newPosition) {
        boolean isForward = newPosition > oldPosition;
        List<Animator> resultList = new LinkedList<Animator>();
        if (isForward) {
            for (int pos = oldPosition; pos < newPosition; pos++) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                if ((pos + 1) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view, -view.getWidth() * (mNumColumns - 1), 0, view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view, view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            for (int pos = oldPosition; pos > newPosition; pos--) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                if ((pos + mNumColumns) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view, view.getWidth() * (mNumColumns - 1), 0, -view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view, -view.getWidth(), 0, 0, 0));
                }
            }
        }

        AnimatorSet resultSet = new AnimatorSet();
        resultSet.playTogether(resultList);
        resultSet.setDuration(300);
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
            }
        });
        resultSet.start();
    }

    private void onStopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(VISIBLE);
        }
        mDragAdapter.setHideItem(-1);
        removeDragImage();
    }

    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     * GridView自动滚动事件
     */
    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollY;
            if (getFirstVisiblePosition() == 0 || getLastVisiblePosition() == getCount() - 1) {
                mHandler.removeCallbacks(mScrollRunnable);
            }
            if (mMoveY > mUpScrollBorder) {
                scrollY = SPEED;
                mHandler.postDelayed(mScrollRunnable, mScrollSpeed);
            } else if (mMoveY < mDownScrollBorder) {
                scrollY = -SPEED;
                mHandler.postDelayed(mScrollRunnable, mScrollSpeed);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }

            //onSwapItem(mMoveX, mMoveY);
            smoothScrollBy(scrollY, mScrollDuration);
        }
    };

    /**
     * 判断是否点击在GridView的item上面
     *
     * @param dragView
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchItem(View dragView, int x, int y) {
        if (dragView == null) {
            return  false;
        }
        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
            return false;
        }
        if (y < topOffset || y > topOffset + dragView.getHeight()) {
            return false;
        }
        return true;
    }

    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            mIsDrag = true;
            mVibrator.vibrate(VIBRATE_TIME);
            mStartDragItemView.setVisibility(View.INVISIBLE);

            createDragImage(mDragBitmap, mDownX, mDownY);
        }
    };

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    public void setDragResponseMs(long dragResponseMs) {
        this.mDragResponseMs = dragResponseMs;
    }

    /**
     * 创建拖动的镜像
     *
     * @param bitmap    镜像的图片
     * @param downX     按下点相对父控件的X坐标
     * @param downY     按下点相对父控件的Y坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = DRAG_IMAGE_ALPHA;
        mWindowLayoutParams.width = getResources().getDimensionPixelSize(R.dimen.img_width);//WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.img_height);//WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return statusHeight;
    }

    public interface OnChangeListener {
        /**
         * 当item交换位置的时候回调
         *
         * @param form  开始的position
         * @param to    拖拽的position
         */
        public void onChange(int form, int to);
    }
}
