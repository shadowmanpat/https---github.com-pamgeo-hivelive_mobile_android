package gr.extract.hivelive.Fragments;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.Postprocessor;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Activities.LoginActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Services.NetworkServices;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.SnappyCalls;
import gr.extract.hivelive.hiveUtilities.Constants;
import gr.extract.hivelive.hiveUtilities.Research;
import gr.extract.hivelive.hiveUtilities.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private View mMainLayout;
    private EditText email_edt, password_edt;
    private Button mLoginBtn;
    private TextView mSignup_tv, warning_tv, remindPassword_tv;
    private CircularProgressView progressView;

    private ArrayList<Integer> mLayoutsArraylist;
    private boolean networkFlag = false;
    private Context mContext;
    private SimpleDraweeView draweeView;
    private Postprocessor processor = null;

    private BroadcastReceiver mNetworkStateChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if ((ni != null && ni.getState() == NetworkInfo.State.CONNECTED) || isConnected) {
                    networkFlag = true;
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    networkFlag = false;
                }
            } else {
                networkFlag = false;
            }
        }

    };


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mContext.registerReceiver(mNetworkStateChange, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mLayoutsArraylist = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainLayout = (View) inflater.inflate(R.layout.fragment_login, container, false);
        email_edt = (EditText) mMainLayout.findViewById(R.id.login_email_edt);
        password_edt = (EditText) mMainLayout.findViewById(R.id.login_password_edt);
        password_edt.setTransformationMethod(new PasswordTransformationMethod());
        mLoginBtn = (Button) mMainLayout.findViewById(R.id.login_button);
        mSignup_tv = (TextView) mMainLayout.findViewById(R.id.login_signup_tv);
        warning_tv = (TextView) mMainLayout.findViewById(R.id.login_message);
        remindPassword_tv = (TextView) mMainLayout.findViewById(R.id.login_remind_password_tv);
        progressView = (CircularProgressView) mMainLayout.findViewById(R.id.progress_view);
//        progressView.startAnimation();
//        draweeView = (SimpleDraweeView) mMainLayout.findViewById(R.id.login_fresco_background_iv);
//        Uri uri = Uri.parse("http://hivelive.gr/img/loginbg.jpg");
////
//        Uri uri = new Uri.Builder()
//                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
//                .path(String.valueOf(R.drawable.loginbg))
//                .build();
//
//        processor = new BlurPostprocessor(mContext, 25);
//
//
//        ImageRequest request =
//                ImageRequestBuilder.newBuilderWithResourceId(R.drawable.loginbg)
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

//        Uri uri = new Uri.Builder()
//                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
//                .path(String.valueOf(R.drawable.loginbg))
//                .build();
//        draweeView.setImageURI(uri);

        String prevUsername = getActivity().getSharedPreferences(Constants.APP, Context.MODE_PRIVATE)
                .getString(Constants.USER_LOGGED, "");
        if (!prevUsername.isEmpty()) email_edt.setText(prevUsername);
        String pass = getActivity().getSharedPreferences(Constants.APP, Context.MODE_PRIVATE)
                .getString(Constants.USER_PASS, "");
        if (!pass.isEmpty()) password_edt.setText(pass);

        setClickListeners();

        return mMainLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().getApplicationContext().unregisterReceiver(mNetworkStateChange);

    }

    public void onEvent(Events.LoginEvent event) {
        progressView.setVisibility(View.GONE);
        if (event.getResult() == 0) {
            SnappyCalls.open(mContext);
            SnappyCalls.saveUserToDB(event.getUser());
            SnappyCalls.close();

            Singleton.getInstance(mContext).setCurrentUser(event.getUser());
            /*save users credential*/
            getActivity().getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).edit()
                    .putString(Constants.USER_LOGGED, email_edt.getText().toString())
                    .putString(Constants.USER_PASS, password_edt.getText().toString()).apply();
            EventBus.getDefault().post(new Events.CommunicationBetweenActivitiesEvent(Constants.START_ACTIVITY));
            ((LoginActivity) getActivity()).replaceFragment("all");
        } else {
            if (event.getWarning() != null) {
                warning_tv.setVisibility(View.VISIBLE);
                if (!event.getWarning().isEmpty())
                    warning_tv.setText(event.getWarning());
                else
                    warning_tv.setText("Παρακαλώ ελέξτε τη σύνδεσή σας");
            }
        }
    }

    private void setClickListeners() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                boolean previousWasResearcher = getActivity().getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).getBoolean(Constants.RESEARCHER, false);

                if (previousWasResearcher && differentUserLoggedIn()) {
                    SnappyCalls.open(mContext);
                    ArrayList<Research> researchesSaved = SnappyCalls.getAnsweredResearches(true);
                    SnappyCalls.close();
                    if (researchesSaved.size() == 0) {
                        loginWithOptions(previousWasResearcher);
                    } else {
                        Toast.makeText(mContext, "Κάντε login με τα στοιχεία του ερευνητή που συμπλήρωσε τις έρευνες αυτές.", Toast.LENGTH_LONG).show();
                        ((LoginActivity) getActivity()).createRemainingResearchesSnackbar();
                    }
                }else{
                    loginWithOptions(previousWasResearcher);
                }
            }
        });

        password_edt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mLoginBtn.performClick();
                    return true;
                }
                return false;
            }
        });

        mSignup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).replaceFragment("login_frag");
            }
        });


        remindPassword_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).replaceFragment("remind_frag");
            }
        });
    }

    private boolean differentUserLoggedIn(){
        String prevUsername = getActivity().getSharedPreferences(Constants.APP, Context.MODE_PRIVATE)
                .getString(Constants.USER_LOGGED, "");
        if (prevUsername.equals(email_edt.getText().toString())){
            return false;
        }else
            return true;
    }

    private void loginWithOptions(boolean isResearcher){
        if (networkFlag) {
            loginUser();
        } else if (isResearcher) {
            if (fieldsAreOk()) {
                SnappyCalls.open(mContext);
                User user = SnappyCalls.getUserFromDb();
                SnappyCalls.close();

                if (user.getEmail().equals(email_edt.getText().toString()) && user.getPassword().equals(password_edt.getText().toString())) {
                    Singleton.getInstance(mContext).setCurrentUser(user);
                             /*save users credential*/
                    getActivity().getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).edit()
                            .putString(Constants.USER_LOGGED, email_edt.getText().toString())
                            .putString(Constants.USER_PASS, password_edt.getText().toString()).apply();
                    EventBus.getDefault().post(new Events.CommunicationBetweenActivitiesEvent(Constants.START_ACTIVITY));
                    ((LoginActivity) getActivity()).replaceFragment("all");
                } else if (networkFlag) {
                    getActivity().getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).edit().putBoolean(Constants.RESEARCHER, false).apply();
                    loginUser();
                } else {
                    ((LoginActivity) getActivity()).createInternetSnackbar();
                }
            }
        } else
            ((LoginActivity) getActivity()).createInternetSnackbar();
    }

    public void loginUser() {
        if (fieldsAreOk()) {
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
            NetworkServices.startUserLogin(getActivity().getApplicationContext(),
                    email_edt.getText().toString(),
                    password_edt.getText().toString());
        }
    }


    private boolean fieldsAreOk() {
        String email = email_edt.getText().toString();
        if (email.isEmpty()) {
            email_edt.setError("Συμπληρώστε το πεδίο");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_edt.setError("To email που πληκτρολογήσατε δεν είναι στη σωστή μορφή");
            return false;
        }
        if (password_edt.getText().toString().isEmpty()) {
            password_edt.setError("Συμπληρώστε το πεδίο");
            return false;
        }
        return true;
    }

    public void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


}
