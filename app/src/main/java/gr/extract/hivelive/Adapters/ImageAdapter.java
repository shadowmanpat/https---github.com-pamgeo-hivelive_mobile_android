package gr.extract.hivelive.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.ButterKnife;
import butterknife.Bind;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.Answer;

/**
 * Created by Sophie on 17/01/16.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private Activity researchActivity;
    private SimpleDraweeView imageView;
    private ArrayList<Uri> images;
    private ArrayList<Answer> imageIDS;
    private HashSet<String> clicked;

    // Constructor
    public ImageAdapter(Context c, ArrayList<Uri> images, ArrayList<Answer> imageID, Activity activity) {
        mContext = c;
        this.images = images;
        clicked = new HashSet<>();
        this.researchActivity = activity;
        this.imageIDS = imageID;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_draweeview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.imagePicked.setImageURI(images.get(position));
        holder.imagePicked.setTag(String.valueOf(position));
        holder.imagePicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Log.e("IMAGEPICK", "Clicked! "+ position+" is the position and "+ v.getTag().toString() + " is the tag");
                if (clicked.contains(v.getTag().toString())) {
                    clicked.remove(v.getTag().toString());
                    ((SimpleDraweeView) v).setColorFilter(null);
                } else {
                    clicked.add(v.getTag().toString());
                    ((SimpleDraweeView) v).setColorFilter(Color.argb(80, 250, 210, 48));
                }

                saveAnswer(imageIDS.get(Integer.valueOf(v.getTag().toString())));


            }
        });
    }

    private void saveAnswer(Answer answerSelected){
        ((ResearchActivity)researchActivity).answerSelection(answerSelected);
        if (clicked.isEmpty())
            ((ResearchActivity)researchActivity).hideNextBtn(true);
    }

    @Override
    public int getItemCount() {
        return this.images.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
       // @Bind(R.id.image_pick_iv)
        SimpleDraweeView imagePicked ;

        public ViewHolder(View itemview){
            super(itemview);
            imagePicked = (SimpleDraweeView)itemview.findViewById(R.id.image_pick_iv);
            //ButterKnife.bind(itemview);
        }

        public void setItemViewClickable(boolean cl){
            imagePicked.setClickable(cl);
        }
    }
}
