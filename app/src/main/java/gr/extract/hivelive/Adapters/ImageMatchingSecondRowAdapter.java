package gr.extract.hivelive.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import gr.extract.hivelive.Fragments.QuestionFragments.ImageMatchDND;
import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.Answer;


public class ImageMatchingSecondRowAdapter extends RecyclerView.Adapter<ImageMatchingSecondRowAdapter.ViewHolder> {

    private Context mContext;
    private SimpleDraweeView image;
    private RecyclerView.LayoutManager mManager;
    private ImageMatchDND mFragment;
    private ArrayList<Answer> mItems;
    private HashMap<Integer, String> pairedMatches = new HashMap<>();

    public ImageMatchingSecondRowAdapter(Context context, ImageMatchDND frag, ArrayList<Answer> mItems) {
        this.mContext = context;
        this.mFragment = frag;
        this.mItems = mItems;
//        mItems = new ArrayList<>();
//
//        Answer ans = new Answer();
//        ans.setAnswer_id("2001");
//        ans.setImageurl("http://exmoorpet.com/wp-content/uploads/2012/08/cat.png");
//        mItems.add(ans);
//
//        ans = new Answer();
//        ans.setAnswer_id("2002");
//        ans.setImageurl("http://exmoorpet.com/wp-content/uploads/2012/08/cat.png");
//        mItems.add(ans);
//
//        ans = new Answer();
//        ans.setAnswer_id("2003");
//        ans.setImageurl("http://exmoorpet.com/wp-content/uploads/2012/08/cat.png");
//        mItems.add(ans);
//
//        ans = new Answer();
//        ans.setAnswer_id("2004");
//        ans.setImageurl("http://exmoorpet.com/wp-content/uploads/2012/08/cat.png");
//        mItems.add(ans);

        for (int i=0; i<mItems.size(); i++){
            pairedMatches.put(i , "");
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_image_matching_second_row, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.topImageview.setImageURI(Uri.parse(mItems.get(position).getImageurl()));

        drawImagePairs(holder.columnLayout, position);

        holder.mLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                String msg2 = "SecondRecyclerView";
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:

                        // RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)v.getLayoutParams();
                        Log.d(msg2, "Action is DragEvent.ACTION_DRAG_STARTED");

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(msg2, "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
//                        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT) ;
                        Log.d(msg2, "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
//                        layoutParams.leftMargin = x_cord;
//                        layoutParams.topMargin = y_cord;
//                        v.setLayoutParams(layoutParams);
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d(msg2, "Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d(msg2, "Action is DragEvent.ACTION_DRAG_ENDED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:
                        Log.d(msg2, "ACTION_DROP event");
                        if (!saveImagePair(position, mFragment.getImageSelectedURL())) {
                            holder.columnLayout.addView(makeDrawee(mFragment.getImageSelectedURL(), position));
                            mFragment.savePairs(mFragment.getAnswerSelectedID(), mItems.get(position).getAnswer_id());
                        }

                        // Do nothing
                        break;
                    default: break;
                }
                return true;
            }

        });

    }

    public View makeDrawee(String url, int position) {
        View main = LayoutInflater.from(mContext).inflate(R.layout.simple_drawee, null, false);
        SimpleDraweeView simple = (SimpleDraweeView) (main.findViewById(R.id.simple));
        simple.setTag(position + " " + url);
        ImageView closeImg = (ImageView) (main.findViewById(R.id.remove_image));
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout parent = (FrameLayout) v.getParent();
                String tag = parent.getChildAt(0).getTag().toString();
                removePair(tag.split(" ")[0], tag.split(" ")[1]);
                mFragment.removePair(tag.split(" ")[0]);
                parent.removeAllViews();
            }
        });
        simple.setImageURI(Uri.parse(url));

        return main;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    private void drawImagePairs(LinearLayout layout, int pos) {
        layout.removeAllViews();
        ArrayList<String> pairsArrayList = getPairsForLayout(pos);
        if (!pairsArrayList.isEmpty()) {
            for (String url : pairsArrayList) {
                layout.addView(makeDrawee(url, pos));
            }
        }
    }

    private void removePair(String position, String url) {
        int pos = Integer.valueOf(position);
        ArrayList<String> pairs = getPairsForLayout(pos);
        String finalStringofPairs = "";
        int counter = 0;

        if (pairs.contains(url)) {
            pairs.remove(url);

            for (String pairItem : pairs) {
                finalStringofPairs += pairItem;
                if (counter < pairs.size() - 1) {
                    finalStringofPairs += ",";
                }
            }
            pairedMatches.put(pos, finalStringofPairs);
        }
    }


    private boolean saveImagePair(int layoutPosition, String url) {
        ArrayList<String> pairsArrayList;
        int counter = 0;
        boolean pairExists = false;
        String pairsSaved = "";

        pairsArrayList = getPairsForLayout(layoutPosition);
        if (!pairsArrayList.isEmpty()) {
            if (!pairsArrayList.contains(url)) {
                pairsArrayList.add(url);
                pairsSaved = "";
                for (String pairItem : pairsArrayList) {
                    pairsSaved += pairItem;
                    if (counter < pairsArrayList.size() - 1) {
                        pairsSaved += ",";
                    }
                }
                pairedMatches.put(layoutPosition, pairsSaved);
            } else
                pairExists = true;
        } else {
            pairsSaved += url;
            pairedMatches.put(layoutPosition, pairsSaved);
        }

        return pairExists;
    }

    private ArrayList getPairsForLayout(int position) {
        ArrayList<String> pairsArrayList;
        String pairsSaved = pairedMatches.get(position);
        if (!pairsSaved.isEmpty()) {
            String[] pairs = pairsSaved.split(",");
            pairsArrayList = new ArrayList<>(Arrays.asList(pairs));
            return pairsArrayList;
        } else
            return new ArrayList();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLayout;
        private SimpleDraweeView topImageview;
        private LinearLayout columnLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.layout);
            topImageview = (SimpleDraweeView) itemView.findViewById(R.id.top_drawee);
            columnLayout = (LinearLayout) itemView.findViewById(R.id.top_img_col);
        }
    }
}
