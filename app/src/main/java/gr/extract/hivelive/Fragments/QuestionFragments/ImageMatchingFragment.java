package gr.extract.hivelive.Fragments.QuestionFragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Bind;
import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.Adapters.ImageMatchAdapter;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Utils;

public class ImageMatchingFragment extends Fragment {
    RecyclerView firstRow;
    RecyclerView secondRow;
    TextView questionview;
    WebView mWebview;


    private View mMainView;
    private RecyclerView.LayoutManager mLayoutManager1, mLayoutManager2;
    private Question questionToBeAnswered;
    private boolean isVertical = false;
    private boolean canSelectFromfirstRow=true;
    private int mFirstSelection=-1;
    private int chosenColor ;
    private int secondsToAnswer = 0;
    private ImageMatchAdapter imgAdapter1, imgAdapter2;
    private Context mContext;
    private Utils mUtils;
    private HashMap<Integer, Boolean> alreadyUsedColors = new HashMap<>();
    private HashMap<Integer,Integer> mPairs=new HashMap<>();
    private HashMap<Integer, Integer> pairedImages;
    private ArrayList<Integer> matchingColors;
    private ArrayList<String> firstRowImages = new ArrayList<>(), secondRowImages = new ArrayList<>();



    public ImageMatchingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUtils = new Utils(mContext);

        questionToBeAnswered = ((ResearchActivity) getActivity()).getNextQuestion();
        if (questionToBeAnswered.getDisplayType().contains("vertical")) {
            isVertical = true;
        }

        pairedImages = new HashMap<>();
        matchingColors = new ArrayList<>();

        int color2 = Color.argb(150, 153, 0, 76);
        int color4 = Color.argb(150, 51, 51, 255);
        int color5 = Color.argb(150, 255, 128, 0);
        int color6 = Color.argb(150, 153, 0, 0); //red
        int color7 = Color.argb(150, 255, 255, 0); //yellow
        int color8 = Color.argb(150, 153, 22, 76); //pink dark
        int color9 = Color.argb(150, 0, 76, 153); //blue
        int color10 = Color.argb(150, 255, 102, 102);
        matchingColors.add(color2);
        alreadyUsedColors.put(color2, false);
        matchingColors.add(color4);
        alreadyUsedColors.put(color4, false);
        matchingColors.add(color5);
        alreadyUsedColors.put(color5, false);
        matchingColors.add(color6);
        alreadyUsedColors.put(color6, false);
        matchingColors.add(color7);
        alreadyUsedColors.put(color7, false);
        matchingColors.add(color8);
        alreadyUsedColors.put(color8, false);
        matchingColors.add(color9);
        alreadyUsedColors.put(color9, false);
        matchingColors.add(color10);
        alreadyUsedColors.put(color10, false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = (View) inflater.inflate(R.layout.fragment_image_matching, container, false);
        ButterKnife.bind(mMainView);
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
            mWebview.getSettings().setDefaultTextEncodingName("utf-8");
            mWebview.setWebChromeClient(new WebChromeClient());
            questionview.setVisibility(View.GONE);
            mWebview.loadData(questionToBeAnswered.getQuestion(), "text/html; charset=utf-8", "utf-8");
        } else {
             /*
                 Set the next question
             */
            questionview.setText(questionToBeAnswered.getQuestion());
        }

        for (Answer ans : questionToBeAnswered.getAnswersArray() ){
            firstRowImages.add(ans.getAnswer_id());
            secondRowImages.add(ans.getAnswer_id());
        }

        mLayoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        imgAdapter1 = new ImageMatchAdapter(getActivity().getApplicationContext(),
                getImageNames(),
                questionToBeAnswered.getAnswersArray(),
                getActivity(), true,this);

        imgAdapter2 = new ImageMatchAdapter(getActivity().getApplicationContext(),
                getImageNames(),
                questionToBeAnswered.getAnswersArray(),
                getActivity(), false,this);



        firstRow.setLayoutManager(mLayoutManager1);
        secondRow.setLayoutManager(mLayoutManager2);
        firstRow.setAdapter(imgAdapter1);
        secondRow.setAdapter(imgAdapter2);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mWebview != null) mWebview.onPause();
        super.onPause();
    }

    public  boolean CanSelectFromfirstRow(){
        return canSelectFromfirstRow;
    }

    public void swapCanSelectFromFirstRow(){
        canSelectFromfirstRow=!canSelectFromfirstRow;
    }

    public boolean setFirstSelection(int index){
        if(mFirstSelection==-1 && !mPairs.containsKey(index)) {
            chosenColor = getRandomColor();
            alreadyUsedColors.put(chosenColor, true);
            mFirstSelection = index;
            return true;
        }else if(mPairs.containsKey(index)){
           int secondIndex= mPairs.remove(index);
            if (alreadyUsedColors.get(chosenColor)) alreadyUsedColors.put(chosenColor, false);
            imgAdapter1.unselect(index);
            imgAdapter2.unselect(secondIndex);
            Log.d("ImageMatching","removed pair " + index + " with " + secondIndex);
            if(mFirstSelection>-1){
                imgAdapter1.unselect(mFirstSelection);
            }
            mFirstSelection=-1;
            ((ResearchActivity)getActivity()).hideNextBtn(true);
        }else {
            imgAdapter1.unselect(mFirstSelection);
            mFirstSelection=index;
            canSelectFromfirstRow=true;
            return true;
        }
        return false;
    }





    public boolean setSecondSelection(int index){

        if(mFirstSelection>-1 && (!mPairs.containsValue(index) || !(mPairs.containsKey(mFirstSelection) && mPairs.get(mFirstSelection) == index)) ){
            mPairs.put(mFirstSelection,index);

            Log.d("ImageMatching","Made pair "+mFirstSelection+ " with "+ index);
            if (mPairs.size() == questionToBeAnswered.getAnswersArray().size())
            {
                sendAnswer();
            }
            mFirstSelection=-1;
            return true;
        }

        return false;
    }

    public int getChosenColor(){
        return this.chosenColor;
    }




    private ArrayList<Uri> getImageNames() {
        ArrayList<Answer> answers = questionToBeAnswered.getAnswersArray();
        ArrayList<Uri> imgArrays = new ArrayList<>();
        for (Answer imageAnswers : answers) {
            imgArrays.add(Uri.parse(imageAnswers.getImageurl()));
        }
        return imgArrays;
    }

    private int getRandomColor() {
        int nextColor = 0,counter = 0;
        for (Integer color: alreadyUsedColors.keySet()){
            if (counter >= alreadyUsedColors.size()) break;
            if (!alreadyUsedColors.get(color)){
                nextColor = color;
                break;
            }
        }

        return nextColor;
    }

    private void sendAnswer() {
        Log.d("ImageMatching", "All images are paired :) We are sending the answers");
        Answer ans;
        int counter=0;
        ((ResearchActivity)getActivity()).hideNextBtn(false);
        for (Integer pos1 : mPairs.keySet()){
            ans = new Answer();
            ans.setAnswer_id(questionToBeAnswered.getAnswersArray().get(counter).getAnswer_id());
            ans.setValue(firstRowImages.get(pos1)+","+secondRowImages.get(mPairs.get(pos1)));
            ans.setNextQuestion(questionToBeAnswered.getAnswersArray().get(counter).getNextQuestion());
            Log.e("imageurls", "answer to be sent is "+ans.getAnswer_id()+" "+ans.getValue());
            ((ResearchActivity)getActivity()).answerSelection(ans);
            counter++;
        }
    }

    public void pauseWebView(){
        mWebview.onPause();
    }

}



