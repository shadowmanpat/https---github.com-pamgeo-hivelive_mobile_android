package gr.extract.hivelive.Fragments.QuestionFragments;


import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ademar.phasedseekbar.PhasedListener;
import ademar.phasedseekbar.PhasedSeekBar;
import ademar.phasedseekbar.SimplePhasedAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThermometerFragment extends GlobalQuestionFragment {

    TextView questionview;
    TextView questionNum;
    PhasedSeekBar psbLike;
    WebView mWebview;
    private View mMainView;
    private ImageView infosign;
    private TextView valueChosen;

    private Question questionToBeAnswered;
    private Context mContext;
    private Utils mUtils;
    private Resources resources;
    private int[] thermometerValues ;


    public ThermometerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
//        resources = mContext.getResources();
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUtils = new Utils(mContext);

        questionToBeAnswered = ((ResearchActivity) getActivity()).getNextQuestion();
        Answer ans = new Answer();
        ans.setAnswer_id(questionToBeAnswered.getAnswersArray().get(0).getAnswer_id());
        ans.setText(questionToBeAnswered.getAnswersArray().get(0).getText());
        ans.setValue(questionToBeAnswered.getAnswersArray().get(0).getValue());
        ans.setNextQuestion(questionToBeAnswered.getAnswersArray().get(0).getNextQuestion());
        ((ResearchActivity) getActivity()).answerSelection(ans);

        int sizeOfAnswers = questionToBeAnswered.getAnswersArray().size();
        thermometerValues = new int[sizeOfAnswers];
        for (int i=0; i<sizeOfAnswers; i++){
            thermometerValues[i] = R.drawable.thermometer_1;
        }

//       thermometerValues[0] =  R.drawable.thermometer_5;
//       thermometerValues[1] =  R.drawable.thermometer_4;
//       thermometerValues[2] =  R.drawable.thermometer_3;
//       thermometerValues[3] =  R.drawable.thermometer_2;
//       thermometerValues[4] =  R.drawable.thermometer_1;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_thermometer, container, false);
        mWebview = (WebView) mMainView.findViewById(R.id.question_wv);
        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        questionNum = (TextView) mMainView.findViewById(R.id.question_num);
        valueChosen = (TextView) mMainView.findViewById(R.id.value_of_therm);
        psbLike = (PhasedSeekBar) mMainView.findViewById(R.id.thermometer);
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

//        infosign.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "Σύρετε την μπάρα του θερμόμετρου πάνω ή κάτω. Η τιμή που επιλέγετε θα εμφανιστεί στα δεξιά.", Toast.LENGTH_LONG).show();
//            }
//        });

        int answersNum = questionToBeAnswered.getAnswersArray().size();
        int[] finalValues = new int[answersNum];

        for (int w=0; w<answersNum; w++){
            finalValues[w] = thermometerValues[w];
        }

        SimplePhasedAdapter mAdapter = new SimplePhasedAdapter(resources, finalValues);
        psbLike.setAdapter(mAdapter);

        /*at 5 position is 0 */

        psbLike.setPosition(answersNum - 1);
        valueChosen.setText("Επιλεγμένη τιμή :\n"+answersToPositions(psbLike.getCurrentItem()).getText());

        ((ResearchActivity) getActivity()).answerSelection(questionToBeAnswered.getAnswersArray().get(psbLike.getCurrentItem()));

        psbLike.setListener(new PhasedListener() {
            @Override
            public void onPositionSelected(int i) {
                Log.e("Thermometro", "answer selected now is "+ answersToPositions(i).getText() +" and value "+ answersToPositions(i).getValue());
                valueChosen.setText("Επιλεγμένη τιμή :\n"+answersToPositions(i).getText());
                ((ResearchActivity) getActivity()).answerSelection(answersToPositions(i));

            }
        });


    }


    private Answer answersToPositions(int position){
        ArrayList<Answer> answerArrayList = questionToBeAnswered.getAnswersArray();
        return answerArrayList.get(answerArrayList.size()-(position+1));
//        switch(position){
//            case 0:
//                return answerArrayList.get(answerArrayList.size()-1);
//
//            case 1:
//                return answerArrayList.get(answerArrayList.size()-2);
//
//            case 2:
//                return answerArrayList.get(answerArrayList.size()-3);
//
//            case 3:
//                return answerArrayList.get(answerArrayList.size()-4);
//
//            case 4:
//                return answerArrayList.get(answerArrayList.size()-5);
//
//            default:
//                return answerArrayList.get(answerArrayList.size()-1);
       // }
    }

    @Override
    public void pauseWebView(){
        mWebview.onPause();
    }

    public static ThermometerFragment newInstance(Resources res) {

        Bundle args = new Bundle();

        ThermometerFragment fragment = new ThermometerFragment();
        fragment.setArguments(args);
        fragment.resources = res;
        return fragment;
    }

}
