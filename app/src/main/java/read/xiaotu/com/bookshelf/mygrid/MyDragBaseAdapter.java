package read.xiaotu.com.bookshelf.mygrid;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/12.
 */
public interface MyDragBaseAdapter {

    public static final int INVALID_POSITION = -1;

    /**
     * 重新排列items
     *
     * @param oldPosition
     * @param newPosition
     */
    public void reorderItems(int oldPosition, int newPosition);

    /**
     * 设置隐藏的item
     *
     * @param hidePosition
     */
    public void setHideItem(int hidePosition);

    /**
     * 合并时调用
     */
    public void setCombinePosition(int combinePosition, int hidePosition);
}
