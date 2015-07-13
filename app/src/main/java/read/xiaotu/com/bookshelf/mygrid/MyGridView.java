package read.xiaotu.com.bookshelf.mygrid;

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
import android.util.AttributeSet;
import android.util.Log;
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

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/12.
 */
public class MyGridView extends GridView {

    /**
     * 能否被拖动，默认为关闭
     */
    private boolean mIsDrag = false;

    /**
     * 触发长按事件的时间
     */
    private static final int LONG_CLICK_RESP = 600;

    /**
     * 拖动item的position
     */
    private int mDragPosition;

    /**
     * 按下时的X、Y坐标
     */
    private int mDownX, mDownY;

    /**
     * 移动时的X、Y轴坐标
     */
    private int mMoveX, mMoveY;

    /**
     * 用户点击的点距item的上边距和左边距
     */
    private int mPoint2ItemTop, mPoint2ItemLeft;

    /**
     * GridView距上边距和左边距
     */
    private int mOffset2Top, mOffset2Left;

    /**
     * 系统状态栏的高度
     */
    private int mStatusBarHeight;

    /**
     * DragGridView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * DragGridView自动向上滚动的边界值
     */
    private int mUpScrollBorder;

    /**
     * 单次滚动的距离
     */
    private int mScrollDistance = 20;

    /**
     * 两次滚动之间的间隔时间
     */
    private int mScrollDelay = 25;

    /**
     * 单词滚动的持续时间
     */
    private int mScrollDuration = 10;

    /**
     * 拖动item的View
     */
    private View mDragItemView;

    /**
     * 拖动item的镜像
     */
    private ImageView mDragImageView;

    /**
     * 拖动item的图片
     */
    private Bitmap mDragBitmap;

    /**
     * 用于显示拖动的图片
     */
    private WindowManager mWindowManager;

    /**
     * 用于规定拖动图片的布局
     */
    private WindowManager.LayoutParams mWindowLayoutParams;

    /**
     * 拖动时的Alpha值
     */
    private static final float IMAGE_DRAG_ALPHA = 0.55f;

    /**
     * 正常时的Alpha值
     */
    private static final float IMAGE_NORMAL_ALPHA = 1.0f;

    /**
     * 交换动画是否结束
     */
    private boolean mAnimationEnd = true;

    /**
     * 每行显示的item数量
     */
    private int mNumColumns;

    private MyDragBaseAdapter mDragAdapter;

    private Handler mHandler = new Handler();

    public MyGridView(Context context) {
        this(context, null);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusBarHeight = getStatusBarHeight(context);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof MyDragBaseAdapter) {
            mDragAdapter = (MyDragBaseAdapter) adapter;
        } else {
            throw new IllegalStateException("the adapter must implement MyDragBaseAdapter!");
        }
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        this.mNumColumns = numColumns;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取用户按下屏幕时的x、y轴坐标
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                //找到用户点击的item的位置
                mDragPosition = pointToPosition(mDownX, mDownY);
                //如果item的位置不存在，则退出事件
                if (mDragPosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }
                handleActionDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();

                if (!isTouchItem(mDragItemView, moveX, moveY)) {
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
                case MotionEvent.ACTION_UP:
                    stopDrag();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mMoveX = (int) ev.getX();
                    mMoveY = (int) ev.getY();
                    dragItem(mMoveX, mMoveY);
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void handleActionDown(MotionEvent ev) {
        //如果用户不松手，固定时间后触发长按事件
        mHandler.postDelayed(mLongClickRunnable, LONG_CLICK_RESP);
        //获取用户点击的item的View
        mDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());

        //计算用户点击位置距item的上边距和左边距
        mPoint2ItemTop = mDownY - mDragItemView.getTop();
        mPoint2ItemLeft = mDownX - mDragItemView.getLeft();

        //计算GridView距屏幕的上边距和左边距
        mOffset2Top = (int) (ev.getRawY() - mDownY);
        mOffset2Left = (int) (ev.getRawX() - mDownX);

        //定义滚动的边界值
        mDownScrollBorder = getHeight() / 5;
        mUpScrollBorder = getHeight() * 4 / 5;

        //获取用户点击的item的截图
        mDragItemView.setDrawingCacheEnabled(true);
        //生成镜像item的图片
        mDragBitmap = Bitmap.createBitmap(mDragItemView.getDrawingCache());
        //清除用户点击的item的截图
        mDragItemView.destroyDrawingCache();
    }

    /**
     * 长按时触发的方法
     */
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            //进入拖动模式
            mIsDrag = true;
            //隐藏长按的item
            mDragItemView.setVisibility(View.INVISIBLE);
            //创建镜像item
            createDragImage();
        }
    };

    /**
     * 创建镜像item
     */
    private void createDragImage() {
        //初始化镜像View
        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(mDragBitmap);

        //初始化LayoutParams对象
        mWindowLayoutParams = new WindowManager.LayoutParams();
        //支持修改alpha值
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT;
        //设置alpha值，增加透明度
        mWindowLayoutParams.alpha = IMAGE_DRAG_ALPHA;
        //设置镜像View的x、y轴坐标
        mWindowLayoutParams.x = mDownX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = mDownY - mPoint2ItemTop + mOffset2Top - mStatusBarHeight;
        //设置镜像View的宽度和高度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置对齐方式
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        //防止镜像View获得焦点，使得GridView失去焦点无法响应事件
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        //将镜像View以浮窗的形式添加到屏幕上
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

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
            return false;
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

    /**
     * 处理拖动事件
     *
     * @param moveX
     * @param moveY
     */
    private void dragItem(int moveX, int moveY) {
        //重新计算镜像item的坐标
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusBarHeight;
        //更新镜像item的位置
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams);
        //处理交换事件
        onSwapItem(mMoveX, mMoveY);
        //处理GridView的滚动事件
        mHandler.post(mScrollRunnable);
    }

    /**
     * 处理item交换事件
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        int tempPosition = pointToPosition(moveX, moveY);

        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION && mAnimationEnd) {
            swapItem(tempPosition);
        }
    }

    /**
     * 交换dragPosition和tempPosition之间的所有item
     *
     * @param tempPosition
     */
    private void swapItem(final int tempPosition) {
        //重新排列，注意此时position会在绘图前发生变化
        mDragAdapter.reorderItems(mDragPosition, tempPosition);
        //设置需要隐藏的item
        mDragAdapter.setHideItem(tempPosition);

        //在绘图前触发动画事件，动图结束后再开始绘图
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //移除监听事件
                observer.removeOnPreDrawListener(this);
                //触发动画
                animateReorder(mDragPosition, tempPosition);
                //交换位置
                mDragPosition = tempPosition;
                return true;
            }
        });
    }

    /**
     * 显示重新排列动画
     *
     * @param oldPosition
     * @param newPosition
     */
    private void animateReorder(int oldPosition, int newPosition) {
        //判断是向左移动还是向右移动
        boolean isForward = newPosition > oldPosition;
        //创建动画序列
        List<Animator> resultList = new LinkedList<Animator>();
        if (isForward) {
            //向左移动，遍历所有需要移动的position，这些position在oldPosition和newPosition之间
            for (int pos = oldPosition; pos < newPosition; pos++) {
                //取出要移动的View
                View view = getChildAt(pos - getFirstVisiblePosition());
                if (view == null) {
                    continue;
                }
                //绘制移动的动画
                if ((pos + 1) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view, -view.getWidth() * (mNumColumns - 1), 0, view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view, view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            //向右移动
            for (int pos = oldPosition; pos > newPosition; pos--) {
                //取出要移动的View
                View view = getChildAt(pos - getFirstVisiblePosition());
                if (view == null) {
                    continue;
                }
                //绘制移动的动画
                if ((pos + mNumColumns) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view, view.getWidth() * (mNumColumns - 1), 0, -view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view, -view.getWidth(), 0, 0, 0));
                }
            }
        }
        AnimatorSet resultSet = new AnimatorSet();
        //将所有动画同时播放
        resultSet.playTogether(resultList);
        //设置动画播放时间
        resultSet.setDuration(300);
        //设置加速播放动画
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
        //设置动画播放过程的监听
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }
        });
        //开始播放动画
        resultSet.start();
    }

    /**
     * 创建位移动画
     *
     * @param view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    private AnimatorSet createTranslationAnimations(View view, float startX, float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    /**
     * 处理拖动时的滚动事件
     */
    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollY = 0;
            //当到达第一页或最后一页时，停止滚动事件
            if (getFirstVisiblePosition() == 0 || getLastVisiblePosition() == getChildCount() - 1) {
                mHandler.removeCallbacks(mScrollRunnable);
            }
            if (mMoveY > mUpScrollBorder) {//用户向下拖动，如果Y轴坐标超过向上滚动边界，则向上滚动
                scrollY = mScrollDistance;
                mHandler.postDelayed(mScrollRunnable, mScrollDelay);
            } else if (mMoveY < mDownScrollBorder) {//用户向上拖动，如果Y轴坐标超过向下滚动边界，则向下滚动
                scrollY = -mScrollDistance;
                mHandler.postDelayed(mScrollRunnable, mScrollDelay);
            } else {//坐标没有达到极值，不做滚动处理
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }
            smoothScrollBy(scrollY, mScrollDuration);
        }
    };

    /**
     * 停止拖动时触发的方法
     */
    private void stopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(VISIBLE);
        }
        mDragAdapter.setHideItem(MyDragBaseAdapter.INVALID_POSITION);
        removeDragImage();
    }

    /**
     * 移除镜像item
     */
    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
            mDragBitmap = null;
        }
    }

    /**
     * 获取系统状态栏的高度
     *
     * @param c
     * @return
     */
    public static int getStatusBarHeight(Context c) {
        int statusHeight = 0;
        Rect rect = new Rect();
        ((Activity) c).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        statusHeight = rect.top;
        if (statusHeight == 0) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = c.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    private void log(String msg) {
        Log.e("TAG", msg);
    }
}
