package gr.extract.hivelive.Fragments.QuestionFragments;


import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

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
public class SingleChoiceFragment extends GlobalQuestionFragment {

    private View mMainView ;
    private WebView mWebview;
    private LinearLayout radiobtnsGroup, anotherOption_ll;
    private TextView questionview;
    private RadioButton anotherRadioBtn;
    private EditText anotherEdt;
    private Question questionToBeAnswered;
    private boolean isVertical = false;
    private Utils mUtils;

    private int secondsToAnswer = 0;
    private Context mContext;


    public SingleChoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUtils = new Utils(mContext);
        questionToBeAnswered = ((ResearchActivity)getActivity()).getNextQuestion();
        if (questionToBeAnswered.getDisplayType().contains("vertical")){
            isVertical = true;
        }
    }

    @Override
    public void onResume() {
        if (mWebview != null){
            mWebview.reload();
        }
        Log.e("Widths", "width now is "+mMainView.getMeasuredWidth());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RadioButton rd;


        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_single_choice, container, false);
        radiobtnsGroup = (RadioGroup) mMainView.findViewById(R.id.single_choice_ll);
        anotherOption_ll = (LinearLayout) mMainView.findViewById(R.id.another_option_ll);
        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        mWebview = (WebView)mMainView.findViewById(R.id.question_wv);

        anotherRadioBtn = (RadioButton) mMainView.findViewById(R.id.another_option_rdb);
        anotherEdt = (EditText) mMainView.findViewById(R.id.another_option_edt);

        if (isVertical) radiobtnsGroup.setOrientation(LinearLayout.VERTICAL);
        else radiobtnsGroup.setOrientation(LinearLayout.HORIZONTAL);
        /*
        Set the next question
         */

        if (mUtils.questionContainsHtml(questionToBeAnswered.getQuestion())) {
            mWebview.setVisibility(View.VISIBLE);
            mWebview.setWebChromeClient(new WebChromeClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.getSettings().setDefaultTextEncodingName("utf-8");
            questionview.setVisibility(View.GONE);
            mWebview.loadData(questionToBeAnswered.getQuestion(), "text/html; charset=utf-8", "utf-8");
            super.setWebView(mWebview);
        } else {
             /*
                 Set the next question
             */
            questionview.setText(questionToBeAnswered.getQuestion());
        }


        if (questionToBeAnswered != null) {

            /*
            for each answer create a radio button layout, with tag the next question
            so, if a user selects one answer, if the next Question button is clicked,
            we return the tag of the redio button, so that the next question is searched.
             */


            for (Answer questionAnswers : questionToBeAnswered.getAnswersArray()) {


                if (questionAnswers.getText().toLowerCase().contains("@html")){
                    anotherOption_ll.setVisibility(View.VISIBLE);
                    anotherRadioBtn.setTag(questionAnswers.getAnswer_id());
                    anotherEdt.addTextChangedListener(new TextValidator(anotherEdt) {
                        @Override
                        public void validate(TextView textView, String text) {
                            if (anotherEdt.getText().length() == 0 && anotherRadioBtn.isChecked()) {
                                ((ResearchActivity) getActivity()).hideNextBtn(true);
                            }else ((ResearchActivity) getActivity()).hideNextBtn(false);
                        }
                    });
                    anotherRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView.isChecked()){
                                ((RadioGroup)radiobtnsGroup).clearCheck();
                            }

                            Answer answer = getAnswerById(buttonView.getTag().toString());
                            answer.setValue(anotherEdt.getText().toString());
                            Log.d("AnswerSelected",answer.getValue());

                            ((ResearchActivity) getActivity()).answerSelection(answer);
                        }
                    });
                }else {



                    rd = new RadioButton(getActivity().getApplicationContext());
                    rd.setText(questionAnswers.getText());
                    rd.setButtonDrawable(ContextCompat.getDrawable(mContext,R.drawable.abc_btn_radio_material));
                    rd.setTag(questionAnswers.getAnswer_id());
                    rd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    rd.setTextColor(getResources().getColor(android.R.color.black));


                    rd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                Answer answerChosen = getAnswerById(buttonView.getTag().toString());
                                ((ResearchActivity) getActivity()).answerSelection(answerChosen);
                                if (anotherRadioBtn.isChecked()) anotherRadioBtn.setChecked(false);
                            }
                        }
                    });
                    radiobtnsGroup.addView(rd);
                }
            }
        }else{
            Toast.makeText(getActivity(), "Question wasn't found! Try to refresh", Toast.LENGTH_SHORT).show();//Todo make swipeRefreshLayout
        }



        return mMainView;
    }




    private Answer getAnswerById(String id){
        for (Answer ans : questionToBeAnswered.getAnswersArray()){
            if (ans.getAnswer_id().equals(id)){
                return ans;
            }
        }
        return null;
    }



}
