package read.xiaotu.com.bookshelf.grid;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/11.
 */
public interface DragGridBaseAdapter {

    /**
     * 重新排列数据
     *
     * @param oldPosition
     * @param newPosition
     */
    public void reorderItems(int oldPosition, int newPosition);

    /**
     * 设置某个item隐藏
     *
     * @param hidePosition
     */
    public void setHideItem(int hidePosition);

}
