package gr.extract.hivelive.Fragments.QuestionFragments;


import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.Adapters.GridRadioAnswersAdapter;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Research;
import gr.extract.hivelive.hiveUtilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GridRadiobuttonFragment extends GlobalQuestionFragment {


    TextView questionview;
    private View mMainView;
    WebView mWebview;
    RecyclerView mRecyclerview;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Context mContext;
    private HashMap<String, String> answersGiven = new HashMap<>();
    private ArrayList<Answer> columnAnswers = new ArrayList<>()
            , rowAnswers = new ArrayList<>();
    private Question questionToBeAnswered;
    private Utils mUtils;
    private boolean gridImage = false;

    public GridRadiobuttonFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        Fresco.initialize(mContext);
        mUtils = new Utils(mContext);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        questionToBeAnswered = ((ResearchActivity) getActivity()).getNextQuestion();

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
            ((ResearchActivity)getActivity()).hideNextBtn(true);
            if (questionToBeAnswered.getTemplate().equals("gridimagegridimage")){
                gridImage = true;
            }
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_grid_radiobutton, container, false);
        mWebview = (WebView)mMainView.findViewById(R.id.question_wv);

        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        mRecyclerview = (RecyclerView) mMainView.findViewById(R.id.grid_radio_rv);
        return mMainView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView colValue;

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
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mAdapter = new GridRadioAnswersAdapter(mContext, questionToBeAnswered.getAnswersArray(),rowAnswers, columnAnswers, this, gridImage);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(mLayoutManager);


    }

    public void addNewAnswer(String position, String value){
        String rowAnswerID = rowAnswers.get(Integer.valueOf(position)).getAnswer_id();
        String colAnswerID = getColumnAnswerid(value);
        answersGiven.put(rowAnswerID, colAnswerID);
        Log.d("ErrorDebugging", "fragment, added " + rowAnswerID + "," + colAnswerID);
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

