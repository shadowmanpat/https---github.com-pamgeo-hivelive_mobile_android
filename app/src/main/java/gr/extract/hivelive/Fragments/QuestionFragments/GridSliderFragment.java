package gr.extract.hivelive.Fragments.QuestionFragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.Adapters.GridSliderAdapted;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GridSliderFragment extends GlobalQuestionFragment {


    TextView questionview;
    private View mMainView;
    RecyclerView mRecyclerview;
    private WebView mWebView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Context mContext;
    private ArrayList<Answer> columnAnswers = new ArrayList<>(), rowAnswers = new ArrayList<>();
    private HashMap<String,String> answersGiven;

    private Question questionToBeAnswered;
    private int secondsToAnswer = 0;
    private Utils mUtils;

    public GridSliderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mUtils = new Utils(mContext);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        questionToBeAnswered = ((ResearchActivity) getActivity()).getNextQuestion();
        answersGiven = new HashMap<>();
//        int counter = 0;
//        for (Answer ans : questionToBeAnswered.getAnswersArray()){
//            answersGiven.put(String.valueOf(counter), "0");
//            counter++;
//        }
//        Answer ans = new Answer();
//        ans.setNextQuestion("0");
//        ((ResearchActivity)getActivity()).answerSelection(ans);

        if (columnAnswers.isEmpty()) {

            for (Answer ans : questionToBeAnswered.getAnswersArray()) {
                if (ans.isColumn()) {
                    columnAnswers.add(ans);
                } else {
                    rowAnswers.add(ans);
                }
            }

            String rID, cID;
//            for (int i = 0; i < rowAnswers.size(); i++) {
//                rID = rowAnswers.get(i).getAnswer_id();
//                cID = columnAnswers.get(0).getAnswer_id();
//                answersGiven.put(rID, cID);
////            Log.d("ErrorDebugging", "fragment on Create, added "+rID+","+cID);
//            }
            ((ResearchActivity) getActivity()).hideNextBtn(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onPause() {
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        if (mWebView != null) mWebView.onPause();
        super.onPause();
    }



    public void onEvent(Events.AnswerSelected event){
        answersGiven.put(event.getAnswerPos(), event.getAnswerValue());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = (View) inflater.inflate(R.layout.fragment_grid_slider, container, false);
        ButterKnife.bind(mMainView);
        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        mRecyclerview = (RecyclerView)mMainView.findViewById(R.id.grid_radio_rv);
        mWebView = (WebView) mMainView.findViewById(R.id.question_wv);
        return mMainView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mUtils.questionContainsHtml(questionToBeAnswered.getQuestion())) {
            mWebView.setVisibility(View.VISIBLE);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDefaultTextEncodingName("utf-8");
            questionview.setVisibility(View.GONE);
            mWebView.loadData(questionToBeAnswered.getQuestion(), "text/html; charset=utf-8", "utf-8");
        } else {
             /*
                 Set the next question
             */
            questionview.setText(questionToBeAnswered.getQuestion());
        }

        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mAdapter = new GridSliderAdapted(mContext,rowAnswers, columnAnswers, this, questionToBeAnswered.getAnswersArray().size());
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(mLayoutManager);
    }


    public void addNewAnswer(int position, int value){
        String rowAnswerID = rowAnswers.get(position).getAnswer_id();
        String colAnswerID = columnAnswers.get(value-1).getAnswer_id();
        answersGiven.put(rowAnswerID, colAnswerID);
        Log.d("GridSlider", "fragment, added " + rowAnswerID + "," + colAnswerID);
        sendAnswers();
    }


    private String getColumnAnswerid(String value) {
        if (Integer.valueOf(value) < columnAnswers.size())
            return columnAnswers.get(Integer.valueOf(value)).getAnswer_id();

        return "0";

    }

    private String getNextQuestionIDFromAnswer(String answerID) {
        for (Answer ans : questionToBeAnswered.getAnswersArray()) {
            if (answerID.equals(ans.getAnswer_id())) {
                return ans.getNextQuestion();
            }
        }
        return "0";
    }


    private void sendAnswers() {
//        ((ResearchActivity)getActivity()).hideNextBtn(false);
        Answer finalAns;
        String columnID;
        ArrayList<Answer> answersOfQuestion = questionToBeAnswered.getAnswersArray();
        for (String key : answersGiven.keySet()) {
            finalAns = new Answer();
            columnID = answersGiven.get(key);
            finalAns.setAnswer_id(key);
            finalAns.setNextQuestion(getNextQuestionIDFromAnswer(key));
            finalAns.setValue(key + "," + columnID);
            ((ResearchActivity) getActivity()).answerSelection(finalAns);
//            Log.e("GridAnswers", "final answer has id " + finalAns.getAnswer_id() + " , next Question is " + finalAns.getNextQuestion());
        }

        boolean allAnswersGiven = true;




        for (Answer rowAns : columnAnswers){
            if (!answersGiven.containsKey(rowAns.getAnswer_id())){
                allAnswersGiven = false;
                ((ResearchActivity)getActivity()).hideNextBtn(true);
            }
        }

        if (answersGiven.size() == rowAnswers.size()){
            allAnswersGiven = true;
        }

        if (allAnswersGiven)
            ((ResearchActivity)getActivity()).hideNextBtn(false);
        else
            ((ResearchActivity)getActivity()).hideNextBtn(true);
    }




}
