package read.xiaotu.com.bookshelf.mygrid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/12.
 */
public class BookAdapter extends BaseAdapter implements MyDragBaseAdapter {

    List<HashMap<String, Object>> mDataList;
    LayoutInflater mInflater;
    int mHidePosition = -1;
    int mCombinePosition = -1;

    public BookAdapter(Context context, List<HashMap<String, Object>> dataList) {
        this.mDataList = dataList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Map<String, Object> getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*BookHolder holder;
        if (convertView == null) {
            holder = new BookHolder();
            convertView = mInflater.inflate(R.layout.drag_gridview_item, null);
            holder.img = (ImageView) convertView.findViewById(R.id.drag_gridview_item_img);
            holder.txt = (TextView) convertView.findViewById(R.id.drag_gridview_item_text);
            convertView.setTag(holder);
        } else {
            holder = (BookHolder) convertView.getTag();
        }
        holder.img.setImageResource((Integer) getItem(position).get("img"));
        holder.txt.setText((CharSequence) getItem(position).get("txt"));*/
        convertView = mInflater.inflate(R.layout.drag_gridview_item, null);
        ImageView img = (ImageView) convertView.findViewById(R.id.drag_gridview_item_img);
        TextView txt = (TextView) convertView.findViewById(R.id.drag_gridview_item_text);
        img.setImageResource((Integer) getItem(position).get("img"));
        txt.setText((CharSequence) getItem(position).get("txt"));

        if (position == mCombinePosition) {
            img.setBackgroundResource(R.drawable.badge_red);
        } else if (position == mHidePosition) {
            convertView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private void log(String msg) {
        Log.e("TAG", msg);
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        HashMap<String, Object> temp = mDataList.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(mDataList, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(mDataList, i, i - 1);
            }
        }
        mDataList.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }

    @Override
    public void setCombinePosition(int combinePosition, int hidePosition) {
        this.mCombinePosition = combinePosition;
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }

}

class BookHolder {
    ImageView img;
    TextView txt;
}
