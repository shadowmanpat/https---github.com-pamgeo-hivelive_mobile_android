package gr.extract.hivelive.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Activities.LoginActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Services.NetworkServices;
import jp.wasabeef.fresco.processors.BlurPostprocessor;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemindFragment extends Fragment {
    private View mMainView;
    private EditText reminderEmailEdt;
    private Button mSubmitRetrieveBtn;
    private LinearLayout formll;
    private RelativeLayout messagell;
    private TextView messagetv, gobacktv;

    private SimpleDraweeView draweeView;
    private Postprocessor processor = null;
    private Context mContext;
    private CircularProgressView progressView;


    public RemindFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_remind, container, false);
        reminderEmailEdt = (EditText)mMainView.findViewById(R.id.login_retrieve_edt);
        mSubmitRetrieveBtn = (Button)mMainView.findViewById(R.id.retrieve_pass_button);
        gobacktv = (TextView) mMainView.findViewById(R.id.go_back_tv);
        formll = (LinearLayout)mMainView.findViewById(R.id.reminder_ll);
        messagell = (RelativeLayout)mMainView.findViewById(R.id.message_ll);
        messagetv = (TextView)mMainView.findViewById(R.id.message_tv);
        progressView = (CircularProgressView) mMainView.findViewById(R.id.progress_view);
//        draweeView = (SimpleDraweeView) mMainView.findViewById(R.id.login_fresco_background_iv);
//        Uri uri = Uri.parse("http://hivelive.gr/img/loginbg.jpg");
//        processor = new BlurPostprocessor(getActivity().getApplicationContext(), 25);
//        ImageRequest request =
//                ImageRequestBuilder.newBuilderWithSource(uri)
//                        .setPostprocessor(processor)
//                        .build();
//
//        PipelineDraweeController controller =
//                (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
//                        .setImageRequest(request)
//                        .setOldController(draweeView.getController())
//                        .build();
//
//        draweeView.setController(controller);


//        Uri uri = new Uri.Builder()
//                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
//                .path(String.valueOf(R.drawable.loginbg))
//                .build();
//        draweeView.setImageURI(uri);

        mSubmitRetrieveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Patterns.EMAIL_ADDRESS.matcher(reminderEmailEdt.getText().toString()).matches()){
                    reminderEmailEdt.setError("το email που πληκτρολογήσατε δεν έχει σωστή μορφή");
                }else{
                    hideKeyboard();
                    progressView.setVisibility(View.VISIBLE);
                    progressView.startAnimation();
                    NetworkServices.startRemindPassword(mContext, reminderEmailEdt.getText().toString());
                }
            }
        });
        
       
        return mMainView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);

    }

    public void onEvent(Events.RemindEvent event){
        progressView.setVisibility(View.GONE);
        switch (event.getResult()){
            case 0: /*success*/
            case 1: /*error*/
                messagell.setVisibility(View.VISIBLE);
                formll.setVisibility(View.INVISIBLE);
                messagetv.setText(event.getMessage());
                gobacktv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LoginActivity)getActivity()).replaceFragment("");
                }
            });
                break;
            default:
                Toast.makeText(mContext, "Παρακαλώ ελέγξτε τη σύνδεσή σας και ξαναπροσπαθήστε.", Toast.LENGTH_LONG).show();
        }
    }

    public void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}
