package gr.extract.hivelive.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Utils;

public class DraggableTextItemAdapter extends RecyclerView.Adapter<DraggableTextItemAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableTextItemAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Answer> data;
    private ArrayList<Answer> newData;
    private Answer mLastRemovedData;
    private int mLastRemovedPosition = -1;

    int STATE_FLAG_DRAGGING = (1 << 0);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that this item is being dragged.
     */
    int STATE_FLAG_IS_ACTIVE = (1 << 1);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that this item is in the range of drag-sortable items
     */
    int STATE_FLAG_IS_IN_RANGE = (1 << 2);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * If this flag is set, some other flags are changed and require to apply.
     */
    int STATE_FLAG_IS_UPDATED = (1 << 31);

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {

        FrameLayout container;
        TextView text_tv;
        View mDragHandle;


        public MyViewHolder(View v) {
            super(v);
            container = (FrameLayout) v.findViewById(R.id.container);
            text_tv = (TextView) v.findViewById(R.id.image_sort_iv);
            mDragHandle = (View) v.findViewById(R.id.drag_handle);
        }
    }

    public DraggableTextItemAdapter(Context con, ArrayList<Answer> imageUrls) {
        mContext = con;
        data = imageUrls;
        newData = new ArrayList<>();
        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(data.get(position).getAnswer_id());
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate((viewType == 0) ? R.layout.custom_draggable_text_item : R.layout.custom_draggable_text_item, parent, false);
//        final View v = inflater.inflate(R.layout.layout_image_sort, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Answer item = data.get(position);

        // set text

//        Log.e("AdapterForSorting", "item has image? "+ item.getImageurl());
        holder.text_tv.setText(item.getText());

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                Utils.clearState(holder.container.getForeground());
            } else if ((dragState & STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.container.setBackgroundResource(bgResId);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d("MyDraggableItemAdapter", "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }


    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final Answer item = data.remove(fromPosition);
        data.add(toPosition, item);
        mLastRemovedPosition = -1;
        EventBus.getDefault().post(new Events.ItemMovedEvent(data));
    }


    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.container;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return Utils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }
}
