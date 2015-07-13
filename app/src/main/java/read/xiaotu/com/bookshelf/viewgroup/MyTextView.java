package read.xiaotu.com.bookshelf.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public class MyTextView extends TextView {

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Log.e("TAG", "TextView onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //Log.e("TAG", "TextView onLayout(changed=" + changed + "," + "left=" + left + ",top=" + top + ",right=" + right + ",bottom=" + bottom + ")");
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        //Log.e("TAG", "TextView layout(left=" + l + ",top=" + t + ",right=" + r + ",bottom=" + b + ")");
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        int action = event.getAction();
        boolean result = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                result = super.onTouchEvent(event);
                Log.e("TAG", "TextView onTouchEvent.ACTION_DOWN(result=" + result + ",x=" + x + ",y=" + y + ",rawX=" + rawX + ",rawY=" + rawY + ")");
                break;
            case MotionEvent.ACTION_UP:
                result = super.onTouchEvent(event);
                Log.e("TAG", "TextView onTouchEvent.ACTION_UP(result=" + result + ",x=" + x + ",y=" + y + ",rawX=" + rawX + ",rawY=" + rawY + ")");
                break;
            case MotionEvent.ACTION_MOVE:
                result = super.onTouchEvent(event);
                Log.e("TAG", "TextView onTouchEvent.ACTION_MOVE(result=" + result + ",x=" + x + ",y=" + y + ",rawX=" + rawX + ",rawY=" + rawY + ")");
                break;
        }
        return false;
    }*/
}
