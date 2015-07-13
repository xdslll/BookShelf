package read.xiaotu.com.bookshelf.view;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/12.
 */
public class TestViewActivity extends Activity {

    LinearLayout mLayout;
    TextView mTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_view);

        mLayout = (LinearLayout) findViewById(R.id.test_view_layout);
        mTxt = (TextView) findViewById(R.id.test_view_text);
        initListener();
    }

    private void initListener() {
        mLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Rect hitRect = new Rect();
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TAG", "x=" + x + ",y=" + y);
                        mTxt.getHitRect(hitRect);
                        Log.e("TAG", "hitRect=" + hitRect.toString());
                        if (hitRect.contains(x, y)) {
                            Log.e("TAG", "点击了TextView！");
                        }
                        return true;
                }
                return false;
            }
        });
    }
}
