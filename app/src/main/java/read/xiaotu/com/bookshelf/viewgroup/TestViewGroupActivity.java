package read.xiaotu.com.bookshelf.viewgroup;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public class TestViewGroupActivity extends Activity {

    MyViewGroup mViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_viewgroup);

        mViewGroup = (MyViewGroup) findViewById(R.id.test_view_group);

        ImageView imgView = new ImageView(this);
        imgView.setImageResource(R.drawable.sbook1);
        imgView.setPadding(0, 10, 0, 10);
        mViewGroup.addView(imgView);

        ImageView imgView2 = new ImageView(this);
        imgView2.setImageResource(R.drawable.sbook2);
        imgView2.setPadding(0, 10, 0, 10);
        mViewGroup.addView(imgView2);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.e("TAG", "(" + metrics.widthPixels + "," + metrics.heightPixels + ")");

        /*
        int width = 1073742544;
        int height = 1073742862;

        int wMode = View.MeasureSpec.getMode(width);
        int wSize = View.MeasureSpec.getSize(width);

        int hMode = View.MeasureSpec.getMode(height);
        int hSize = View.MeasureSpec.getSize(height);

        Log.e("TAG", "wMode=" + wMode + ",wSize=" + wSize + ",hMode=" + hMode + ",hSize=" + hSize);
        */
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e("TAG", "ActionBar height=" + getActionBar().getHeight());
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        Log.e("TAG", "StatusBar height=" + rect.top);
    }
}
