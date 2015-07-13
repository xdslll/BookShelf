package read.xiaotu.com.bookshelf.grid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public class _GridViewDragActivity extends Activity {

    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_grid_drag);

        mGridView = (GridView) findViewById(R.id.grid1);
        mGridView.setAdapter(new BookAdapter());
    }

    private class BookAdapter extends BaseAdapter {

        final int[] RES_IDS = new int[] {
              R.drawable.book1, R.drawable.book2,
                R.drawable.book3, R.drawable.book4,
                R.drawable.book5, R.drawable.book6,
        };

        @Override
        public int getCount() {
            return RES_IDS.length;
        }

        @Override
        public Integer getItem(int position) {
            return RES_IDS[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.grid_item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.grid_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.img.setImageResource(getItem(position));

            return convertView;
        }
    }

    private class ViewHolder {
        ImageView img;
    }

    private void log(String msg) {
        Log.e("TAG", "----------" + msg + "----------");
    }
}
