package gr.extract.hivelive.Fragments.QuestionFragments;


import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Bind;
import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.Adapters.ImageAdapter;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagepickFragment extends GlobalQuestionFragment {

    WebView mWebview;
    TextView questionview;
    RecyclerView imageGrid;
    private View mMainView ;
    private RecyclerView.LayoutManager mLayoutManager;
    private Question questionToBeAnswered;
    private boolean isVertical = false;
    private ImageAdapter imgAdapter;
    private Context mContext;
    private Utils mUtils;
    private int secondsToAnswer = 0;

    public ImagepickFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        Fresco.initialize(mContext);
        mUtils = new Utils(mContext);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        questionToBeAnswered = ((ResearchActivity)getActivity()).getNextQuestion();
        if (questionToBeAnswered.getDisplayType().contains("vertical")){
            isVertical = true;
        }
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
        mMainView =  inflater.inflate(R.layout.fragment_imagepick_frament, container, false);
        mWebview = (WebView)mMainView.findViewById(R.id.question_wv);
        questionview = (TextView) mMainView.findViewById(R.id.question_tv);
        imageGrid = (RecyclerView) mMainView.findViewById(R.id.image_grid);

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


        mLayoutManager = new GridLayoutManager(mContext, 2);
        imgAdapter = new ImageAdapter(getActivity().getApplicationContext(),
                getImageNames(),
                questionToBeAnswered.getAnswersArray(),
                getActivity());
        imageGrid.setLayoutManager(mLayoutManager);
        imageGrid.setAdapter(imgAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private ArrayList<Uri> getImageNames(){
        ArrayList<Answer> answers = questionToBeAnswered.getAnswersArray();
        ArrayList<Uri> imgArrays = new ArrayList<>();
        for (Answer imageAnswers : answers){
            imgArrays.add(Uri.parse(imageAnswers.getImageurl()));
        }
        return imgArrays;
    }

}
