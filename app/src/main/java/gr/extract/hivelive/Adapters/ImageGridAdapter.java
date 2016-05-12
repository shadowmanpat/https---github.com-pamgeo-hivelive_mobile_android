package gr.extract.hivelive.Adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import gr.extract.hivelive.Fragments.QuestionFragments.ImageMatchDND;
import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.Answer;


public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Answer> mItems;
    private RelativeLayout.LayoutParams layoutParams;
    private float initialPosX, initialPosY;
    private ImageMatchDND mFragment;

    public ImageGridAdapter(Context con,ImageMatchDND frag, ArrayList<Answer> imageAnswers) {
        this.mContext = con;
        this.mFragment = frag;
        this.mItems = imageAnswers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_draweeview, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // RelativeLayout.LayoutParams layoutParams ;
//        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT) ;
        final String msg = "imageDrag";

        holder.imageView.setImageURI(Uri.parse(mItems.get(position).getImageurl()));
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(holder.imageView);
                mFragment.setImageSelectedURL(mItems.get(position).getImageurl(), mItems.get(position).getAnswer_id());
                v.startDrag(dragData, myShadow, null, View.SYSTEM_UI_FLAG_VISIBLE);
                return true;
            }
        });

        holder.imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        initialPosX = v.getX();
                        initialPosY = v.getY();
                        // RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)v.getLayoutParams();
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");
                        // Do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
//                        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT) ;
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        Log.e("MovedItem", "x : " + x_cord + " , y : " + y_cord);
//                        layoutParams.leftMargin = x_cord;
//                        layoutParams.topMargin = y_cord;
//                        v.setLayoutParams(layoutParams);
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        Log.e("MovedItem", "Location x : " + x_cord + " , y : " + y_cord);
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        Log.e("MovedItem", "x : " + x_cord + " , y : " + y_cord);
                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:
                        Log.d(msg, "ACTION_DROP event");

                        // Do nothing
                        break;
                    default: break;
                }
                return true;
            }
        });

        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(holder.imageView);

                    mFragment.setImageSelectedURL(mItems.get(position).getImageurl(), mItems.get(position).getAnswer_id());
                    holder.imageView.startDrag(data, shadowBuilder, holder.imageView, 0);
                   // holder.imageView.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.image_pick_iv);
        }
    }

}


