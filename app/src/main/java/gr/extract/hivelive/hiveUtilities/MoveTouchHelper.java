package gr.extract.hivelive.hiveUtilities;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import gr.extract.hivelive.Adapters.ImageGridAdapter;

public class MoveTouchHelper extends ItemTouchHelper.SimpleCallback{

    ImageGridAdapter mAdapter;

    public MoveTouchHelper(ImageGridAdapter mAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mAdapter = mAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.e("MoveItems", "position is "+target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}