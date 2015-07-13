package read.xiaotu.com.bookshelf.grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public class DragAdapter extends BaseAdapter implements DragGridBaseAdapter {

    private List<HashMap<String, Object>> mList;
    private LayoutInflater mInflater;
    private int mHidePosition = -1;

    public DragAdapter(Context context, List<HashMap<String, Object>> list) {
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DragGridViewHolder holder = null;
        if (convertView == null) {
            holder = new DragGridViewHolder();
            convertView = mInflater.inflate(R.layout.drag_gridview_item, null);
            holder.img = (ImageView) convertView.findViewById(R.id.drag_gridview_item_img);
            holder.txt = (TextView) convertView.findViewById(R.id.drag_gridview_item_text);
            convertView.setTag(holder);
        } else {
            holder = (DragGridViewHolder) convertView.getTag();
        }
        holder.img.setImageResource((Integer) mList.get(position).get("img" + position));
        holder.txt.setText((CharSequence) mList.get(position).get("txt" + position));

        if (position == mHidePosition) {
            convertView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        HashMap<String, Object> temp = mList.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else  if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        mList.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }
}

class DragGridViewHolder {
    ImageView img;
    TextView txt;
}
