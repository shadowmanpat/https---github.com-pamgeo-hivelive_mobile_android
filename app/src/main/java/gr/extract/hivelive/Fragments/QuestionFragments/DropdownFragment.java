package gr.extract.hivelive.Fragments.QuestionFragments;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Bind;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Utils;

public class DropdownFragment extends GlobalQuestionFragment {

    @Bind(R.id.question_tv)
    TextView questionview;
    @Bind(R.id.question_num)
    TextView questionNum;
    @Bind(R.id.question_wv)
    WebView mWebview;
    private View mMainView;
    private Question questionToBeAnswered;
    private Context mContext;
    private Utils mUtils;
    private int secondsToAnswer = 0;


    public DropdownFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUtils = new Utils(mContext);

        questionToBeAnswered = ((ResearchActivity) getActivity()).getNextQuestion();
        Answer ans = new Answer();
        ans.setNextQuestion("0");
        ((ResearchActivity) getActivity()).answerSelection(ans);
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_dropdown, container, false);
        ButterKnife.bind(mMainView);
        mWebview = (WebView)mMainView.findViewById(R.id.question_wv);
        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        questionNum = (TextView) mMainView.findViewById(R.id.question_num);
        return mMainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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
    }


}
