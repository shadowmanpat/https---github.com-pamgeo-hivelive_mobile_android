package gr.extract.hivelive.Fragments.QuestionFragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import gr.extract.hivelive.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GlobalQuestionFragment extends Fragment {
    private WebView mWebView;


    public GlobalQuestionFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return null;
    }


    public void setWebView (WebView wb){
        this.mWebView = wb;
    }


    public void pauseWebView(){
        if (mWebView != null){

            mWebView.onPause();
        }
    }

}
