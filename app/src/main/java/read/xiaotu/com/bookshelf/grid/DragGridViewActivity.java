package read.xiaotu.com.bookshelf.grid;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public class DragGridViewActivity extends Activity {

    private List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.drag_gridview);
        DragGridView mDragGridView = (DragGridView) findViewById(R.id.drag_gridview);
        initList();
        final DragAdapter mDragAdapter = new DragAdapter(this, mList);
        mDragGridView.setAdapter(mDragAdapter);
    }

    private void initList() {
        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
            switch (i % 6) {
                case 0:
                    itemHashMap.put("img" + i, R.drawable.sbook1);
                    itemHashMap.put("txt" + i, "book1");
                    break;
                case 1:
                    itemHashMap.put("img" + i, R.drawable.sbook2);
                    itemHashMap.put("txt" + i, "book2");
                    break;
                case 2:
                    itemHashMap.put("img" + i, R.drawable.sbook3);
                    itemHashMap.put("txt" + i, "book3");
                    break;
                case 3:
                    itemHashMap.put("img" + i, R.drawable.sbook4);
                    itemHashMap.put("txt" + i, "book4");
                    break;
                case 4:
                    itemHashMap.put("img" + i, R.drawable.sbook5);
                    itemHashMap.put("txt" + i, "book5");
                    break;
                case 5:
                    itemHashMap.put("img" + i, R.drawable.sbook6);
                    itemHashMap.put("txt" + i, "book6");
                    break;
            }
            mList.add(itemHashMap);
        }
    }
}
