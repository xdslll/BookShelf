package read.xiaotu.com.bookshelf;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public class ImageViewDragActivity extends Activity implements View.OnTouchListener {

    ImageView mImgView;
    int mLastX, mLastY, mScreenWidth, mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_img_drag);

        mImgView = (ImageView) findViewById(R.id.img1);
        mImgView.setOnTouchListener(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            int actionBarHeight = actionBar.getHeight();
            log("actionBarHeight=" + actionBarHeight);
            mScreenHeight = mScreenHeight - actionBarHeight;
        }
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        mScreenHeight = mScreenHeight - statusBarHeight;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            getXY(event);
        } else if (action == MotionEvent.ACTION_MOVE) {
            int dx = (int) event.getRawX() - mLastX;
            int dy = (int) event.getRawY() - mLastY;

            int left = v.getLeft() + dx;
            int top = v.getTop() + dy;
            int right = v.getRight() + dx;
            int bottom = v.getBottom() + dy;
            if (left < 0) {
                left = 0;
                right = left + v.getWidth();
            }
            if (right > mScreenWidth) {
                right = mScreenWidth;
                left = right - v.getWidth();
            }
            if (top < 0) {
                top = 0;
                bottom = top + v.getHeight();
            }
            if (bottom > mScreenHeight) {
                bottom = mScreenHeight;
                top = bottom - v.getHeight();
            }
            v.layout(left, top, right, bottom);
            getXY(event);
        }
        return false;
    }

    private void getXY(MotionEvent event) {
        mLastX = (int) event.getRawX();
        mLastY = (int) event.getRawY();
        //log("raw x:" + mLastX + ",raw y:" + mLastY);
        //log("x:" + (int) event.getX() + ",y:" + (int) event.getY());
    }

    private void log(String msg) {
        Log.e("TAG", "----------" + msg + "----------");
    }
}
