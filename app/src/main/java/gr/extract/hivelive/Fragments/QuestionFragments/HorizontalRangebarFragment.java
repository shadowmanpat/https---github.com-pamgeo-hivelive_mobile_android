package gr.extract.hivelive.Fragments.QuestionFragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Bind;
import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalRangebarFragment extends GlobalQuestionFragment {


    RangeBar horizontalRangebar;
    TextView question_tv;


    WebView mWebview;
    private Context mContext;
    private Utils mUtils;
    private View mMainView;
    private Question questionToBeAnswered;
    private int secondsToAnswer = 0;
    

    public HorizontalRangebarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mUtils = new Utils(mContext);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        questionToBeAnswered = ((ResearchActivity)getActivity()).getNextQuestion();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_horizontal_rangebar, container, false);
        question_tv = (TextView) mMainView.findViewById(R.id.question_tv);
        mWebview = (WebView)mMainView.findViewById(R.id.question_wv);

        horizontalRangebar = (RangeBar)mMainView.findViewById(R.id.horizontal_rangebar);
           /*
             Set the next question
         */
        if (mUtils.questionContainsHtml(questionToBeAnswered.getQuestion())) {
            mWebview.setVisibility(View.VISIBLE);
            mWebview.setWebChromeClient(new WebChromeClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.getSettings().setDefaultTextEncodingName("utf-8");
            question_tv.setVisibility(View.GONE);
            mWebview.loadData(questionToBeAnswered.getQuestion(), "text/html; charset=utf-8", "utf-8");
        } else {
             /*
                 Set the next question
             */
            question_tv.setText(questionToBeAnswered.getQuestion());
        }

        horizontalRangebar.setFormatter(new IRangeBarFormatter() {
            @Override
            public String format(String s) {
                // Transform the String s here then return s
                return s;
            }
        });
        horizontalRangebar.setTickStart(1);
        horizontalRangebar.setTickEnd(questionToBeAnswered.getAnswersArray().size());
        horizontalRangebar.setTickInterval(1);
        horizontalRangebar.setSeekPinByIndex(1);
        return mMainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        horizontalRangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
//                Toast.makeText(mContext, "position now is "+rightPinValue, Toast.LENGTH_SHORT).show();
                rightPinValue = getAnswerByPosition(rightPinValue).getText();
                ((ResearchActivity)getActivity()).answerSelection(getAnswerByPosition(rightPinValue));
            }
        });
    }


    private Answer getAnswerByPosition(String position){
        Log.d("AnswerSelected", questionToBeAnswered.getAnswersArray().get(Integer.valueOf(position)-1).getValue());
        return questionToBeAnswered.getAnswersArray().get(Integer.valueOf(position)-1);
    }

}
