package gr.extract.hivelive.Fragments.QuestionFragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import butterknife.ButterKnife;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.Adapters.DNDAdapter;
import gr.extract.hivelive.Adapters.ImageGridAdapter;
import gr.extract.hivelive.Adapters.ImageMatchingSecondRowAdapter;
import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.DragController;
import gr.extract.hivelive.hiveUtilities.MoveTouchHelper;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Utils;


public class ImageMatchDND extends GlobalQuestionFragment {

    private RecyclerView firstRow;
    private RecyclerView secondRow;
    private TextView questionview;
    private WebView mWebview;
    private View mMainView;
    private RecyclerView.LayoutManager mLayoutManager1, mLayoutManager2;
    private Question questionToBeAnswered;
    private int secondsToAnswer = 0;
    private Utils mUtils;
    private HashMap<String, String> pairedImages; // id => id
    private HashMap<String, String> indexOfPairs; // id => URL
    private ArrayList<String> firstRowImages = new ArrayList<>(), secondRowImages = new ArrayList<>();
    private Context mContext;
    private OnFragmentInteractionListener mListener;
    private ImageGridAdapter gridAdapter;
    private String imageSelectedURL="", answerSelectedID = "";


    public ImageMatchDND() {
        // Required empty public constructor
    }

    public static ImageMatchDND newInstance() {
        ImageMatchDND fragment = new ImageMatchDND();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        Fresco.initialize(mContext);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUtils = new Utils(mContext);
        questionToBeAnswered = ((ResearchActivity) getActivity()).getNextQuestion();

        pairedImages = new HashMap<>();

    }

    @Override
    public void onPause() {
        if (mWebview != null) mWebview.onPause();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        mMainView = (View) inflater.inflate(R.layout.fragment_image_match_dnd, container, false);
        mWebview = (WebView)mMainView.findViewById(R.id.question_wv);
        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        firstRow = (RecyclerView) mMainView.findViewById(R.id.first_row);
        secondRow = (RecyclerView) mMainView.findViewById(R.id.second_row);
        return mMainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mUtils.questionContainsHtml(questionToBeAnswered.getQuestion())) {
            mWebview.setVisibility(View.VISIBLE);
            mWebview.setWebChromeClient(new WebChromeClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.getSettings().setDefaultTextEncodingName("utf-8");
            questionview.setVisibility(View.GONE);
            mWebview.loadData(questionToBeAnswered.getQuestion(), "text/html; charset=utf-8", "utf-8");
        } else {
             /*
                 Set the next question
             */
            questionview.setText(questionToBeAnswered.getQuestion());
        }

        mLayoutManager1 = new GridLayoutManager(mContext, 4);
        mLayoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);


        ArrayList<Answer> itemsForSecondRow = new ArrayList<>();
//        Answer ans = new Answer();
//        ans.setAnswer_id("1222");
//        ans.setImageurl("https://s-media-cache-ak0.pinimg.com/736x/03/17/c1/0317c1aa041c68915b809752c3d2da10.jpg");
//        itemsForSecondRow.add(ans);
//        ans = new Answer();
//        ans.setAnswer_id("1223");
//        ans.setImageurl("http://oddstuffmagazine.com/wp-content/uploads/2011/09/Small-Cat-580x574.png");
//        itemsForSecondRow.add(ans);
//        ans = new Answer();
//        ans.setAnswer_id("1224");
//        ans.setImageurl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaxvfhB_yxpF9QFKaWHk3qhEFofObxlMEcW4f56QyoynxZB0iT");
//        itemsForSecondRow.add(ans);
//        ans = new Answer();
//        ans.setAnswer_id("1225");
//        ans.setImageurl("https://pbs.twimg.com/profile_images/1166725501/890ddcf40.jpg");
//        itemsForSecondRow.add(ans);
//        ans = new Answer();
//        ans.setAnswer_id("1226");
//        ans.setImageurl("https://s-media-cache-ak0.pinimg.com/736x/58/67/fd/5867fd0161b1214bb7c654f2ca645857.jpg");
//        itemsForSecondRow.add(ans);
//        ans = new Answer();
//        ans.setAnswer_id("1227");
//        ans.setImageurl("https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQH2gFraKj7HGv3olncD-GPZMn8z6e7j3eoCFWq9ZqCXfv2hLC-zQ");
//        itemsForSecondRow.add(ans);

        itemsForSecondRow = questionToBeAnswered.getAnswersArray();

        initiatePairs(itemsForSecondRow);

        gridAdapter = new ImageGridAdapter(mContext, this, itemsForSecondRow);

        firstRow.setLayoutManager(mLayoutManager1);
        secondRow.setLayoutManager(mLayoutManager2);
        firstRow.setAdapter(gridAdapter);
        secondRow.setAdapter(new ImageMatchingSecondRowAdapter(mContext, this, itemsForSecondRow));


    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initiatePairs(ArrayList<Answer> pairs){
        indexOfPairs = new HashMap<>();

        for (Answer ans : pairs){
            indexOfPairs.put(ans.getAnswer_id(), ans.getImageurl());
        }
    }

    public void savePairs(String secRowid, String firstRowid){
        pairedImages.put(secRowid, secRowid+","+firstRowid);
        if (allImagesMatched()){
            Answer fAns ;
            for (String key : pairedImages.keySet()){
                fAns = new Answer();
                fAns.setAnswer_id(key);
                fAns.setValue(pairedImages.get(key));
                fAns.setNextQuestion(getAnswerByID(secRowid) == null ? "0" : getAnswerByID(secRowid).getNextQuestion());
                ((ResearchActivity)getActivity()).answerSelection(fAns);
            }
            ((ResearchActivity)getActivity()).hideNextBtn(false);
        }else
            ((ResearchActivity)getActivity()).hideNextBtn(true);
    }

    private Answer getAnswerByID(String ansID){
        for (Answer ans : questionToBeAnswered.getAnswersArray()){
            if (ans.getAnswer_id().equals(ansID))
                return ans;
        }
        return null;
    }

    public void removePair(String secRowid){
        if (pairedImages.containsKey(secRowid))
            pairedImages.remove(secRowid);
    }

    private boolean allImagesMatched(){
        boolean allmatched = true;
        for (Answer answerOfGrid : questionToBeAnswered.getAnswersArray()){
            if (!pairedImages.containsKey(answerOfGrid.getAnswer_id())){
                allmatched = false;
                break;
            }
        }
        return allmatched;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getImageSelectedURL() {
        return imageSelectedURL;
    }

    public String getAnswerSelectedID(){
        return this.answerSelectedID;
    }

    public void setImageSelectedURL(String imageSelectedURL, String answerID) {
        this.imageSelectedURL = imageSelectedURL;
        this.answerSelectedID = answerID;
    }


}
