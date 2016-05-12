package gr.extract.hivelive.Fragments.QuestionFragments;


import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.Bind;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.TextValidator;
import gr.extract.hivelive.hiveUtilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubmitTextFragment extends GlobalQuestionFragment {

    WebView mWebview;
    TextView questionview;
    TextView currency_symbol;
    EditText answerEdt;
    private View mMainView;
    private Question questionToBeAnswered;
    private int secondsToAnswer = 0;
    private Context mContext;
    private Answer answerAsText;
    private Utils mUtils;

    public SubmitTextFragment() {
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
        mMainView = inflater.inflate(R.layout.fragment_submit_text, container, false);
//        ButterKnife.bind(mMainView);

        mWebview = (WebView)mMainView.findViewById(R.id.question_wv);
        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        currency_symbol = (TextView) mMainView.findViewById(R.id.currency_symbol);
        answerEdt = (EditText) mMainView.findViewById(R.id.answer_edt);


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

        if (questionToBeAnswered.getAnswersArray().size() > 0) {
            answerAsText = questionToBeAnswered.getAnswersArray().get(0);
        } else {
            answerEdt.setVisibility(View.GONE);
        }

        switch (questionToBeAnswered.getDisplayType()) {
            case "numeric":
                //answerEdt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                answerEdt.setRawInputType(Configuration.KEYBOARD_12KEY);
//                int maxLength = 2;
//                InputFilter[] fArray = new InputFilter[1];
//                fArray[0] = new InputFilter.LengthFilter(maxLength);
//                answerEdt.setFilters(fArray);

                answerEdt.addTextChangedListener(new TextValidator(answerEdt) {
                    @Override
                    public void validate(TextView textView, String text) {
                        Pattern ps = Pattern.compile("^[0-9]+$");
                        Matcher ms = ps.matcher(answerEdt.getText().toString());

                        boolean bs = ms.matches();
                        if (!bs) {
                            Toast.makeText(mContext, "Παρακαλώ συμπληρώστε μόνο αριθμούς.", Toast.LENGTH_SHORT).show();
                            ((ResearchActivity) getActivity()).hideNextBtn(true);
                        }else{
                            ((ResearchActivity) getActivity()).hideNextBtn(false);
                        }
                    }
                });
                break;
            case "currency":
                answerEdt.setInputType(Configuration.KEYBOARD_12KEY);
                currency_symbol.setVisibility(View.VISIBLE);
                break;
            default:
                answerEdt.setSingleLine(false);
                answerEdt.setMinLines(3);
        }




        answerEdt.addTextChangedListener(new TextValidator(answerEdt) {
            @Override
            public void validate(TextView textView, String text) {
                if (answerEdt.getText().length() > 0) {
                    answerAsText.setValue(answerEdt.getText().toString());
                    Log.d("AnswerSelected",answerAsText.getValue());

                    ((ResearchActivity) getActivity()).answerSelection(answerAsText);
                    if (answerAsText.getValue().isEmpty())
                        ((ResearchActivity) getActivity()).hideNextBtn(true);
                    else
                        ((ResearchActivity) getActivity()).hideNextBtn(false);
                } else {
                    ((ResearchActivity) getActivity()).hideNextBtn(true);
                }
            }
        });

        return mMainView;
    }

    @Override
    public void pauseWebView(){
        mWebview.onPause();
    }


}
