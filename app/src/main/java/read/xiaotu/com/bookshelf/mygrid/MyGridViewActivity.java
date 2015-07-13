package read.xiaotu.com.bookshelf.mygrid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/12.
 */
public class MyGridViewActivity extends Activity {

    MyGridView mGrid;
    BookAdapter mAdapter;
    List<HashMap<String, Object>> mDataList = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initList();

        setContentView(R.layout.my_grid);
        mGrid = (MyGridView) findViewById(R.id.my_grid);
        mAdapter = new BookAdapter(this, mDataList);
        mGrid.setAdapter(mAdapter);
    }

    private void initList() {
        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
            switch (i % 6) {
                case 0:
                    itemHashMap.put("img", R.drawable.sbook1);
                    itemHashMap.put("txt", "MUJI");
                    break;
                case 1:
                    itemHashMap.put("img", R.drawable.sbook2);
                    itemHashMap.put("txt", "零售心理战");
                    break;
                case 2:
                    itemHashMap.put("img", R.drawable.sbook3);
                    itemHashMap.put("txt", "三体");
                    break;
                case 3:
                    itemHashMap.put("img", R.drawable.sbook4);
                    itemHashMap.put("txt", "失恋33天");
                    break;
                case 4:
                    itemHashMap.put("img", R.drawable.sbook5);
                    itemHashMap.put("txt", "失控");
                    break;
                case 5:
                    itemHashMap.put("img", R.drawable.sbook6);
                    itemHashMap.put("txt", "乔布斯传");
                    break;
            }
            mDataList.add(itemHashMap);
        }
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                log("Activity dispatchTouchEvent.ACTION_DOWN");
                result = super.dispatchTouchEvent(ev);
                log("Activity dispatchTouchEvent.ACTION_DOWN(" + result + ")");
                break;
            case MotionEvent.ACTION_MOVE:
                log("Activity dispatchTouchEvent.ACTION_MOVE");
                result = super.dispatchTouchEvent(ev);
                log("Activity dispatchTouchEvent.ACTION_MOVE(" + result + ")");
                break;
            case MotionEvent.ACTION_UP:
                log("Activity dispatchTouchEvent.ACTION_UP");
                result = super.dispatchTouchEvent(ev);
                log("Activity dispatchTouchEvent.ACTION_UP(" + result + ")");
                break;
        }
        return result;
    }*/

    private void log(String msg) {
        Log.e("TAG", msg);
    }
}
