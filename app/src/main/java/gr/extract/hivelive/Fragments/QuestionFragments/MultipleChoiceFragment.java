package gr.extract.hivelive.Fragments.QuestionFragments;


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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;

import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.TextValidator;
import gr.extract.hivelive.hiveUtilities.Utils;

public class MultipleChoiceFragment extends GlobalQuestionFragment {

    WebView mWebview;
    TextView questionview;
    LinearLayout multipleChoice_ll;
    LinearLayout anotherOption_ll;
    CheckBox anotherCheckbox;
    EditText anotherEdt;

    private View mMainView;
    private Question questionToBeAnswered;
    private boolean isVertical = false, someOtherCheckboxesChecked = false;
    private Utils mUtils;
    private int secondsToAnswer = 0;
    private Context mContext;
    private HashSet<String> answersSelected;


    public MultipleChoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        mUtils = new Utils(mContext);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        questionToBeAnswered = ((ResearchActivity) getActivity()).getNextQuestion();
        if (questionToBeAnswered.getDisplayType().contains("vertical")) {
            isVertical = true;
        }
        answersSelected = new HashSet<>();
    }

    @Override
    public void onResume() {
        questionToBeAnswered = ((ResearchActivity) getActivity()).getNextQuestion();
        if (questionToBeAnswered != null && questionToBeAnswered.getDisplayType().contains("vertical")) {
            isVertical = true;
        }
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
        mMainView = inflater.inflate(R.layout.fragment_multiple_choice, container, false);

        mWebview = (WebView) mMainView.findViewById(R.id.question_wv);
        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        multipleChoice_ll = (LinearLayout) mMainView.findViewById(R.id.multiple_choice_ll);
        anotherOption_ll = (LinearLayout) mMainView.findViewById(R.id.another_option_ll);
        anotherCheckbox = (CheckBox) mMainView.findViewById(R.id.another_option_chbx);
        anotherEdt = (EditText) mMainView.findViewById(R.id.another_option_edt);
        return mMainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CheckBox rd;

        if (isVertical) multipleChoice_ll.setOrientation(LinearLayout.VERTICAL);
        else multipleChoice_ll.setOrientation(LinearLayout.HORIZONTAL);

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


        if (questionToBeAnswered != null) {
            /*
            for each answer create a radio button layout, with tag the next question
            so, if a user selects one answer, if the next Question button is clicked,
            we return the tag of the redio button, so that the next question is searched.
             */
            for (Answer questionAnswers : questionToBeAnswered.getAnswersArray()) {

                if (questionAnswers.getText().toLowerCase().contains("@html")) {
                    anotherOption_ll.setVisibility(View.VISIBLE);
                    anotherCheckbox.setTag(questionAnswers.getAnswer_id());
                    anotherEdt.addTextChangedListener(new TextValidator(anotherEdt) {
                        @Override
                        public void validate(TextView textView, String text) {
                            if (anotherEdt.getText().length() == 0 && anotherCheckbox.isChecked() && !someOtherCheckboxesChecked) {
                                ((ResearchActivity) getActivity()).hideNextBtn(true);
                            } else ((ResearchActivity) getActivity()).hideNextBtn(false);
                        }
                    });
                    anotherCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Answer answer = getAnswerById(buttonView.getTag().toString());
                            answer.setText(anotherEdt.getText().toString());
                            saveAnswer(answer);

                        }
                    });

                } else {
                    rd = new CheckBox(getActivity().getApplicationContext());
                    rd.setText(questionAnswers.getText());
                    rd.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.abc_btn_check_material));
                    rd.setTag(questionAnswers.getAnswer_id());
                    rd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    rd.setTextColor(ContextCompat.getColor(mContext,android.R.color.black));


                    rd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            saveAnswer(getAnswerById(buttonView.getTag().toString()));
                        }
                    });
                    multipleChoice_ll.addView(rd);
                }
            }
        }
    }

    private void saveAnswer(Answer ans) {
        if (answersSelected.contains(ans.getAnswer_id())) {
            answersSelected.remove(getAnswerById(ans.getAnswer_id()));
            if (answersSelected.isEmpty()) {
                ((ResearchActivity) getActivity()).hideNextBtn(true);
                someOtherCheckboxesChecked = false;
            } else {
                ((ResearchActivity) getActivity()).hideNextBtn(false);
                someOtherCheckboxesChecked = true;
            }
        } else {
            answersSelected.add(ans.getAnswer_id());
        }
        ((ResearchActivity) getActivity()).answerSelection(ans);
    }

    private Answer getAnswerById(String id) {
        for (Answer ans : questionToBeAnswered.getAnswersArray()) {
            if (ans.getAnswer_id().equals(id)) {
                Log.d("AnswerSelected", ans.getValue());

                return ans;
            }
        }
        return null;
    }

    @Override
    public void pauseWebView() {
        mWebview.onPause();
    }


}
