package gr.extract.hivelive.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.Postprocessor;
import com.snappydb.SnappydbException;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Fragments.LoginFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.ImageMatchDND;
import gr.extract.hivelive.Fragments.RemindFragment;
import gr.extract.hivelive.Fragments.SignUpFragment;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.SnappyCalls;
import gr.extract.hivelive.hiveUtilities.Constants;
import gr.extract.hivelive.hiveUtilities.StableData;
import gr.extract.hivelive.hiveUtilities.Utils;

public class LoginActivity extends AppCompatActivity {

    private Fragment mfragment;
    private final String LOGIN_FRAGMENT = "login_frag";
    private final String SIGNUP_FRAGMENT = "signup_frag";
    private final String LOGIN_SUCCESS = "login_success";
    private final String REMIND_FRAGMENT = "remind_frag";

    private SimpleDraweeView draweeView;
    private View coordinatorLayoutView;
    private Postprocessor processor = null;
    private Utils mUtils;
    private String tag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayoutView = findViewById(R.id.snackbarPosition);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP, MODE_PRIVATE);

//        if (sharedPreferences.getBoolean(Constants.AUTOLOGIN, false)) {
//            if (!sharedPreferences.getString(Constants.USER_LOGGED, "").isEmpty() && !sharedPreferences.getString(Constants.USER_PASS, "").isEmpty()) {
//                NetworkServices.startUserLogin(this, sharedPreferences.getString(Constants.USER_LOGGED, ""), sharedPreferences.getString(Constants.USER_PASS, ""));
//            }
//        }else {
        mfragment = new LoginFragment();
        tag = LOGIN_FRAGMENT;
        mUtils = new Utils(this);
        if (mUtils.isConnectedToTheInternet())
            saveStableData();

        getFragmentManager().beginTransaction().addToBackStack(null).add(R.id.login_container, mfragment, LOGIN_FRAGMENT).commit();
//       getFragmentManager().beginTransaction().addToBackStack(null).add(R.id.login_container, new ImageMatchDND(), LOGIN_FRAGMENT).commit();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Events.CommunicationBetweenActivitiesEvent event) {

        if (event.getMessage().equals(Constants.START_ACTIVITY)) {
            boolean researcher = Singleton.getInstance(this).getCurrentUser().getGroupid() == 4;
            if (researcher) {
                Singleton.getInstance(this).setCapi(true);
                getSharedPreferences(Constants.APP, MODE_PRIVATE).edit().putBoolean(Constants.RESEARCHER, true).apply();
            } else {
                getSharedPreferences(Constants.APP, MODE_PRIVATE).edit().putBoolean(Constants.RESEARCHER, false).apply();
            }
            ProgressBar pb = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
            pb.setVisibility(ProgressBar.VISIBLE);
            Intent i = new Intent(this, ResearchActivity.class);
            i.setAction(LOGIN_SUCCESS);
            i.putExtra(Constants.RESEARCHER, researcher);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            removeAllFragments();
            startActivity(i);
            finish();
        }
//        else {
//            EventBus.getDefault().removeStickyEvent(event);
//        }


    }


//    public void hideKeyboard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }


    public void createRemainingResearchesSnackbar(){
        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {

            }
        };
        Snackbar snackbar = Snackbar
                .make(coordinatorLayoutView, R.string.snackbar_researches_saved, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_retry, clickListener)
                .setDuration(Snackbar.LENGTH_INDEFINITE).setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                .setAction("ΣΥΝΕΧΕΙΑ", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(LoginActivity.this, "Διαγραφή δεδομένων, σύνδεση νέου χρήστη..", Toast.LENGTH_SHORT).show();
                        if (tag == LOGIN_FRAGMENT)
                            ((LoginFragment) mfragment).loginUser();
                    }
                });

        snackbar.show();
    }

    public void createInternetSnackbar() {
        Log.d("Snackbar", "in createSnackbar");

        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {

            }
        };
        Snackbar snackbar = Snackbar
                .make(coordinatorLayoutView, R.string.snackbar_text_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_retry, clickListener)
                .setDuration(Snackbar.LENGTH_INDEFINITE).setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                .setAction("LOGIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tag == LOGIN_FRAGMENT)
                            ((LoginFragment) mfragment).loginUser();
                    }
                });

        snackbar.show();
    }

    public void replaceFragment(String fragmentTag) {
        FragmentManager mfragmentManager = getFragmentManager();
        Log.e("Fragments", "in here2");
        switch (fragmentTag) {
            case LOGIN_FRAGMENT:
                mfragment = new SignUpFragment();
                tag = SIGNUP_FRAGMENT;
                mfragmentManager.beginTransaction().add(R.id.login_container, mfragment, SIGNUP_FRAGMENT).addToBackStack(null).commit();
                break;
            case SIGNUP_FRAGMENT:
                tag = LOGIN_FRAGMENT;
                removeAllFragments();
                break;
            case REMIND_FRAGMENT:
                mfragment = new RemindFragment();
                tag = REMIND_FRAGMENT;
                mfragmentManager.beginTransaction().add(R.id.login_container, mfragment, REMIND_FRAGMENT).addToBackStack(null).commit();
                break;
            default:
                tag = LOGIN_FRAGMENT;
                removeAllFragments();
        }
    }

    private void removeAllFragments() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {

            Fragment frag = getFragmentManager().findFragmentByTag(LOGIN_FRAGMENT);
            if (frag != null) {
                if (mfragment == frag) {
                    Log.e("Fragments", "last fragment was login");
                    finish();
                } else {
                    Log.e("Fragments", "removing previous fragment");
                    getFragmentManager().beginTransaction().remove(mfragment).show(getFragmentManager().findFragmentByTag(LOGIN_FRAGMENT)).commit();
                    getFragmentManager().popBackStack();
                }
            } else Log.e("Fragments", "login fragment not found");

        } else {
            Log.e("Fragments", "finishing activity");
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        removeAllFragments();

    }

    private void saveStableData() {
        SnappyCalls.open(this);
        StableData std = new StableData();
        std.setCityCodes(Singleton.getInstance(this).getCityCodes());
        std.setEduCodes(Singleton.getInstance(this).getEduCodes());
        std.setOccuCodes(Singleton.getInstance(this).getOccuCodes());

        std.setCodesToCities(Singleton.getInstance(this).getCodesToCities());
        std.setCodesToEducation(Singleton.getInstance(this).getCodesToEducation());
        std.setCodesToOccupation(Singleton.getInstance(this).getCodesToOccupation());
        try {
            SnappyCalls.saveStableData(std);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        SnappyCalls.close();
    }

}
