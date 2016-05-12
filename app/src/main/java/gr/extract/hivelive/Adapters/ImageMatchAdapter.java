package gr.extract.hivelive.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Fragments.QuestionFragments.ImageMatchingFragment;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.hiveUtilities.Answer;

public class ImageMatchAdapter extends ImageAdapter {
    private ArrayList<Uri> imagesUris;
    private HashMap<Integer, SimpleDraweeView> mapOfViews;
    private ViewHolder vh;
    private boolean row1;
    private ImageMatchingFragment mFragment;

    public ImageMatchAdapter(Context c, ArrayList<Uri> images, ArrayList<Answer> imageID, Activity activity, boolean row1,ImageMatchingFragment frag) {
        super(c, images, imageID, activity);

        this.imagesUris = images;
        this.row1 = row1;
        mapOfViews = new HashMap<>();
        this.mFragment=frag;
    }


    public void unselect(int position){
        mapOfViews.get(position).setBackgroundColor(Color.TRANSPARENT);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        vh= holder;

        holder.imagePicked.setImageURI(imagesUris.get(position));
        holder.imagePicked.setTag(String.valueOf(position));
        if(!mapOfViews.containsKey(position)){
            mapOfViews.put(position,holder.imagePicked);
        }

        holder.imagePicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(row1 && mFragment.CanSelectFromfirstRow()){
                    if(mFragment.setFirstSelection(position)) {
                        holder.imagePicked.setColorFilter(mFragment.getChosenColor());
                        mFragment.swapCanSelectFromFirstRow();
                    }
                }else if(row1 && !mFragment.CanSelectFromfirstRow()){
                    if(mFragment.setFirstSelection(position)){
                        holder.imagePicked.setColorFilter(null);
                        mFragment.swapCanSelectFromFirstRow();
                   }
                }else if(!row1 && !mFragment.CanSelectFromfirstRow()){
                    if(mFragment.setSecondSelection(position)) {
                        holder.imagePicked.setColorFilter(mFragment.getChosenColor());
                        mFragment.swapCanSelectFromFirstRow();
                    }
                }
            }
        });
    }




}
