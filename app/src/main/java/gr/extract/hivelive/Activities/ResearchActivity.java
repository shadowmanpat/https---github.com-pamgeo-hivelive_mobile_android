package gr.extract.hivelive.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.snappydb.SnappydbException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Fragments.QuestionFragments.DropdownFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.GlobalQuestionFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.GridRadiobuttonFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.GridSliderFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.HorizontalRangebarFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.ImageMatchDND;
import gr.extract.hivelive.Fragments.QuestionFragments.ImagepickFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.MultipleChoiceFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.OrderingImagesFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.QualityFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.SingleChoiceFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.SubmitTextFragment;
import gr.extract.hivelive.Fragments.QuestionFragments.ThermometerFragment;
import gr.extract.hivelive.Fragments.ResearchesMainFragment;
import gr.extract.hivelive.Fragments.StartResearchFragment;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Services.MyTaskService;
import gr.extract.hivelive.Services.NetworkServices;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.SnappyCalls;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Constants;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Research;
import gr.extract.hivelive.hiveUtilities.User;
import gr.extract.hivelive.hiveUtilities.Utils;
import me.drakeet.materialdialog.MaterialDialog;

public class ResearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int backPressedCounter = 0, questionsAnswered = 0;
    private long startTime = 0, endTime = 0;
    private boolean networkFlag = false, multipleanswers = false, researchFinished = false, hideNextBtn = false,
            isResearcher = false, errorFragmentAdded = false, newWebViewAdded = false, trialResearch = false,
            finalQuestionaireShown = false, researchInAction = false, onPause = false;
    public String nextQuestionType;
    private String tag; /* tags for fragments */
    private String finalNextQuestionID;
    private String previousQuestionAnsweredID = "0";
    private String[] questionIDSsorted; /* initial questions sorted, as received from server */
    private HashSet<String> nextQuestionsHashSet = new HashSet<>(); /* unique nextquesionIDS before sorting */
    private HashMap<String, String> qAnsweredToNextqIDs = new HashMap<>();
    private ArrayList<String> nextQuestionsArraySorted = new ArrayList<>();
    private ArrayList<String> questionsAnsweredArray = new ArrayList<>();
    private ArrayList<Answer> answersSelected; /* array with one ore more answers selected by the user, depending on

    the multipleness of the question */
    private Question nextQuestion;
    private Research researchChosen;
    private Utils mUtils;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private String firstFragmentTag = "";
    private StartResearchFragment startFr;
    private QualityFragment qualFrag;
    private GlobalQuestionFragment currentFrag;
    private MaterialDialog mMaterialDialog;
    private NavigationView navigationView;


    private static final int GPS_REQUEST_CODE = 1000;
    ImageLoaderConfiguration config;


    private BroadcastReceiver mNetworkStateChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (intent.getExtras() != null) {
                if (isConnected) {
                    networkFlag = true;
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    networkFlag = false;
                }
            } else {
                networkFlag = false;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_research);

        registerReceiver(mNetworkStateChange, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        File cacheDir = StorageUtils.getCacheDirectory(this);
        config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .writeDebugLogs()
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUtils = new Utils(this);
        isResearcher = getIntent().getBooleanExtra(Constants.RESEARCHER, false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//      customizeUserSettings();

        User currUser = Singleton.getInstance(this).getCurrentUser();
        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_research, null);
        TextView userName = (TextView) headerView.findViewById(R.id.nav_username);
        userName.setText(currUser.getUsername());
        SimpleDraweeView userpic = (SimpleDraweeView) headerView.findViewById(R.id.nav_userpic);
        if (!currUser.getUserphoto().isEmpty())
            userpic.setImageURI(Uri.parse(Singleton.getInstance(this).getCurrentUser().getUserphoto()));
        navigationView.addHeaderView(headerView);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
//                currentFrag = (GlobalQuestionFragment) getFragmentManager().findFragmentById(R.id.container);

            }
        });

        /**********************************************/


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tag.equals("StartResearchFragment")) {
                    checkFieldsToMoveOn();
                } else {
                    pauseWebView();
                    endTime = System.currentTimeMillis();
                    saveTimeToAnswer();
                    if (!answersSelected.isEmpty()) {
                        saveAnswerToResearch(); /* save answer(s) given and arrange nextquestions */
                    }
                    clearAnswersForNextQuestion();
                    view.setVisibility(View.GONE);

                    if (researchFinished && nextQuestionsArraySorted.isEmpty()) {
                        finishResearch();
                    } else {
                        //  showNextQuestions();
                        previousQuestionAnsweredID = finalNextQuestionID;
                        finalNextQuestionID = getFinalNextQuestionID();
                        Log.e("BAM", "------------------> finalQuestion id is " + finalNextQuestionID);

                    /*
                    * the final nextQuestionID is saved in the questionsArrayAnswered,
                    * the question will be considered as answered.
                     */
                        questionsAnsweredArray.add(finalNextQuestionID);
                        questionsAnswered++;
                        selectNextQuestion();
                    }
                }
            }
        });

        checkServicesAvailable();
        if (mUtils.isConnectedToTheInternet()){
            SnappyCalls.open(this);
            if (SnappyCalls.getAnsweredResearches(true).size() > 0){
                MyTaskService.scheduleOneOff(this);
            }
            SnappyCalls.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        /** change username and pic in header of drawer **/
        if (onPause) {
            onPause = false;
            customizeUserSettings();
        }
        getResearchesForUser();
        clearAnswersForNextQuestion();
    }

    @Override
    protected void onPause() {
        onPause = true;
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        unregisterReceiver(mNetworkStateChange);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            backPressedCounter = 0;
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (newWebViewAdded) {
                //do nothing here
                qualFrag.removeWebView();
                newWebViewAdded = false;
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    pauseWebView();/* besides the researchesFragment*/
                    if (qualFrag != null) {
                        if (!errorFragmentAdded) {
                            getFragmentManager().popBackStackImmediate();
                        } else {
                            removeAllFragments();
                        }
                        //do nothing

                    } else {

                        if (startFr == null && !trialResearch) {
                            if (questionsAnswered > 1) questionsAnswered--;

                            fixNextQuestionIDSArray();
                            setUpTagandRest();
                            if (questionsAnsweredArray != null && questionsAnsweredArray.size() > 0)
                                questionsAnsweredArray.remove(questionsAnsweredArray.size() - 1); /* we remove the last one from the answers, like taking a step back  */

                            if (getFragmentManager().getBackStackEntryCount() > 2) {
                                if (!researchFinished && qualFrag == null) {
                                    researchChosen.getQuestionsForResearch().get(finalNextQuestionID).setAnswered("0");
                                    refillAnswersArray(previousQuestionAnsweredID);
                                }
                            }
                        }


                        if (startFr != null) {
                            setUpTagandRest();
                            refillAnswersArray(previousQuestionAnsweredID);

                        }
                        startTime = 0;
                        endTime = 0;
                        startTime = System.currentTimeMillis();
                        getFragmentManager().popBackStackImmediate();


                        if (getFragmentManager().getBackStackEntryCount() == 1) {
                            resetAllData();
                            fab.hide();
                        }
                    }

                } else {
                    if (getFragmentManager().getBackStackEntryCount() == 1 && backPressedCounter == 0) {
                        resetAllData();
                        Toast.makeText(ResearchActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                        backPressedCounter++;
                    } else {
                        Singleton.getInstance(this).resetSingleton();
                        finish();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.research, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        hideKeyboard();

        if (id == R.id.researches_ic) {
            if ((newWebViewAdded || startFr != null || trialResearch || researchInAction) && qualFrag == null) {
                if (!trialResearch) {

                    mMaterialDialog = new MaterialDialog(this)
                            .setTitle("Έρευνα σε εκκρεμότητα")
                            .setMessage("Έχετε αφήσει μια έρευνα χωρίς να την έχετε ολοκληρώσει. Αν φύγετε, όλα τα δεδομένα θα χαθούν. Πατήστε Παραμονή για να μείνετε στην έρευνα, ή Συνέχεια για να φύγετε")
                            .setPositiveButton("Συνέχεια", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                    getFragmentManager().popBackStack();
                                    resetAllData();
                                    getResearchesForUser();
                                }
                            })
                            .setNegativeButton("Παραμονή", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                }
                            });

                    mMaterialDialog.show();
                }
            } else {
                getFragmentManager().popBackStack();
                resetAllData();
                getResearchesForUser();
            }

        } else if (id == R.id.profile_ic) {
            if ((newWebViewAdded || startFr != null || trialResearch || researchInAction) && qualFrag == null) {
                if (!trialResearch) {
                    mMaterialDialog = new MaterialDialog(this)
                            .setTitle("Έρευνα σε εκκρεμότητα")
                            .setMessage("Έχετε αφήσει μια έρευνα χωρίς να την έχετε ολοκληρώσει. Αν φύγετε, όλα τα δεδομένα θα χαθούν. Πατήστε Παραμονή για να μείνετε στην έρευνα, ή Συνέχεια για να φύγετε")
                            .setPositiveButton("Συνέχεια", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();

                                    removeAllFragments();

                                    resetAllData();
                                    Intent i = new Intent(ResearchActivity.this, ProfileActivity.class);
                                    startActivity(i);

                                }
                            })
                            .setNegativeButton("Παραμονή", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                }
                            });

                    mMaterialDialog.show();
                }
            } else {
                resetAllData();
                Intent i = new Intent(ResearchActivity.this, ProfileActivity.class);
                startActivity(i);
            }

        }
//        else if (id == R.id.signout_ic) {
//            SnappyCalls.open(this);
//            resetAllData();
//            try {
//                SnappyCalls.deleteUserFromDB();
//                SnappyCalls.deleteAllResearches();
//                Singleton.getInstance(this).resetSingleton();
//                MyTaskService.cancelAll(this);
//            } catch (SnappydbException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            SnappyCalls.close();
//            getSharedPreferences(Constants.APP, MODE_PRIVATE).edit().putBoolean(Constants.RESEARCHER, false).apply();
//
//            Toast.makeText(ResearchActivity.this, "Αποσύνδεση χρήστη..", Toast.LENGTH_SHORT).show();
//            new CountDownTimer(1000, 4000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//
//                @Override
//                public void onFinish() {
//                    Intent i = new Intent(ResearchActivity.this, LoginActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                    finish();
//                }
//            }.start();
//
//
//        }
        else if (id == R.id.leave_ic) {
            if ((newWebViewAdded || startFr != null || trialResearch || researchInAction) && qualFrag == null) {
                if (!trialResearch) {

                    mMaterialDialog = new MaterialDialog(this)
                            .setTitle("Έρευνα σε εκκρεμότητα")
                            .setMessage("Έχετε αφήσει μια έρευνα χωρίς να την έχετε ολοκληρώσει. Αν φύγετε, όλα τα δεδομένα θα χαθούν. Πατήστε Παραμονή για να μείνετε στην έρευνα, ή Συνέχεια για να φύγετε")
                            .setPositiveButton("Συνέχεια", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                    resetAllData();
                                    Singleton.getInstance(ResearchActivity.this).resetSingleton();
                                    Toast.makeText(ResearchActivity.this, "Αποσύνδεση χρήστη..", Toast.LENGTH_SHORT).show();
                                    new CountDownTimer(1000, 4000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            Intent i = new Intent(ResearchActivity.this, LoginActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            finish();
                                        }
                                    }.start();


                                }
                            })
                            .setNegativeButton("Παραμονή", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                }
                            });

                    mMaterialDialog.show();
                }
            } else {
                resetAllData();
                Singleton.getInstance(this).resetSingleton();
                Toast.makeText(ResearchActivity.this, "Αποσύνδεση χρήστη..", Toast.LENGTH_SHORT).show();
                new CountDownTimer(1000, 4000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent i = new Intent(ResearchActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                }.start();
            }
        }
//        else if (id == R.id.preferences_ic) {
//            PreferencesFragment prefsFrag = new PreferencesFragment();
//            tag = "PreferencesFragment";
//            addNewFragment(prefsFrag);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void customizeUserSettings() {
        User currUser = Singleton.getInstance(this).getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        TextView userName = (TextView) headerView.findViewById(R.id.nav_username);
        userName.setText(currUser.getUsername());
        SimpleDraweeView userpic = (SimpleDraweeView) headerView.findViewById(R.id.nav_userpic);
        if (!currUser.getUserphoto().isEmpty())
            userpic.setImageURI(Uri.parse(Singleton.getInstance(this).getCurrentUser().getUserphoto()));
    }

    private void saveTimeToAnswer() {
        long seconds = (endTime - startTime) / 1000;
        Singleton.getInstance(this).getSecondsToAnswer().put(finalNextQuestionID, (int) seconds);
        startTime = 0;
        endTime = 0;
    }

    private void pauseWebView() {
        if (startFr == null) {
            FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);
            String str = backEntry.getName();
            currentFrag = (GlobalQuestionFragment) getFragmentManager().findFragmentByTag(str);
            if (currentFrag != null) currentFrag.pauseWebView();
        }
    }


    private void checkFieldsToMoveOn() {
        if (startFr != null) {
            if (startFr.areAllOK()) {
                HashMap<String, String> userDetails = startFr.getQuestionedPersonsDetails();
                researchChosen.setFirstName(userDetails.get("firstname"));
                researchChosen.setPhone(userDetails.get("phone"));
                researchChosen.setEmail(userDetails.get("email"));
                researchChosen.setAddress(userDetails.get("address"));
//                getFragmentManager().popBackStackImmediate();
                startFr = null;
                finishResearch();
//                selectNextQuestion();
            } else {
                Toast.makeText(ResearchActivity.this, "Ελέξτε ότι όλα τα πεδία είναι σωστά συμπληρωμένα και ξαναδοκιμάστε.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getFinalNextQuestionID() {
        String finalQID = finalNextQuestionID;
        if (nextQuestionsArraySorted.size() > 0) {
            int counter = 0;
            Log.d("Questions", "the array with the sorted nextQuestions is not empty, so we are choosing from it.");
                        /*
                        * in case we have next questions pending,
                        * we save the previous questionID (previousQuestionAnsweredID), in order to be able to get the previous answers,
                        * if the user presses the back button.
                        * Then, we obtain the nextQuestionID (finalNextQuestion).
                        * We assure that, the nextQuestionID matches to a Question saved in the QuestionsArray
                        * if not, we take the nextquestionID in the sorted array. We do that, while there are more questionIDs
                        * in the sorted array.
                        * If we don't manage to obtain the nextQuestionID,
                        * then we choose the next Question ID from the initially sorted questionIDs.
                         */

            finalQID = nextQuestionsArraySorted.get(0);
            boolean nextQuestionIDfound = false;
            while ((!researchChosen.getQuestionsForResearch().containsKey(finalQID) || questionsAnsweredArray.contains(finalQID))
                    && counter + 1 < nextQuestionsArraySorted.size()) {
                counter++;
                finalQID = nextQuestionsArraySorted.get(counter);
            }
            if (counter < nextQuestionsArraySorted.size()) {
                nextQuestionIDfound = true;
            }
            if (!nextQuestionIDfound) {
                finalQID = getNextIdFromQIDsSorted();
            }


        } else {
                        /*
                        *If we dont have a next Question and the research isn't finished yet
                        * we get the next Question from the array of the initially sorted questionIDS
                         */
            finalQID = getNextIdFromQIDsSorted();
        }
        return finalQID;
    }

    private void getResearchesForUser() {
        if (Singleton.getInstance(this).getResearchesArray().isEmpty()) {
            if (isResearcher) {
                if (mUtils.isConnectedToTheInternet()) {
                    NetworkServices.startGetResearches(this, true);
                } else {
                    SnappyCalls.open(this);
                    ArrayList<Research> researchesSaved = SnappyCalls.getOfflineResearches();
                    SnappyCalls.close();
                    Log.d("QUESTIONSCAPI", "Total size of researches saved is " + (researchesSaved == null ? "0" : researchesSaved.size()));
                    if (researchesSaved.size() > 0) {
                        for (Research r : researchesSaved) {
                            Log.d("QUESTIONSCAPI", r.getResearchID() + " is the resID");
                        }
                    }

                    HashMap<String, Research> indexOfResearches = new HashMap<>();
                    for (Research r : researchesSaved) {
                        indexOfResearches.put(r.getResearchID(), r);
                    }
                    Singleton.getInstance(this).setResearchesArray(indexOfResearches);
                    ResearchesMainFragment researchesMainFragment = new ResearchesMainFragment();
                    firstFragmentTag = "ResearchFragment";
                    getFragmentManager().beginTransaction().replace(R.id.researches_container, researchesMainFragment).addToBackStack(firstFragmentTag).commit();
                    Toast.makeText(ResearchActivity.this, "Οι έρευνες έχουν αποθηκευτεί για offline χρήση", Toast.LENGTH_LONG).show();
                }
            } else {
                NetworkServices.startGetResearches(this, false);
            }
        } else {
            if (getFragmentManager().getBackStackEntryCount() < 2) {
                ResearchesMainFragment researchesMainFragment = new ResearchesMainFragment();
                firstFragmentTag = "ResearchFragment";
                getFragmentManager().beginTransaction().replace(R.id.researches_container, researchesMainFragment).addToBackStack(firstFragmentTag).commit();
            }
        }
    }


    public void onEvent(Events.ResearchesFoundEvent event) {
        if (event.getResult() == 0) {
            ResearchesMainFragment researchesMainFragment = new ResearchesMainFragment();
            firstFragmentTag = "ResearchFragment";
            getFragmentManager().beginTransaction().replace(R.id.researches_container, researchesMainFragment).addToBackStack(firstFragmentTag).commit();
            if (isResearcher) {
                preloadImagesToCache();
                Toast.makeText(ResearchActivity.this, "Οι έρευνες έχουν αποθηκευτεί για offline χρήση", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ResearchActivity.this, "Παρακαλώ ελέξτε τη σύνδεσή σας και ξαναδοκιμάστε.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ResearchActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void onEvent(Events.QuestionsFound event) {
        if (event.getResult() == 0) {
            this.researchChosen = Singleton.getInstance(this).getResearchesArray().get(event.getResearchIdChosen());
            Log.d("BackOperations", "Research id " + researchChosen.getResearchID());
            this.questionIDSsorted = this.researchChosen.getQuestionIdsSorted();
            researchInAction = true;
            selectNextQuestion();
        } else if (event.getResult() == 1) {
            Toast.makeText(ResearchActivity.this, "Όλες οι ερωτήσεις έχουν απαντηθεί για αυτή την έρευνα.", Toast.LENGTH_SHORT).show();
        } else if (event.getResult() == -1) { /* show intermediate fragment */
                this.researchChosen = Research.newInstance(Singleton.getInstance(this).getResearchesArray().get(event.getResearchIdChosen()));
//                this.researchChosen = Singleton.getInstance(this).getResearchesArray().get(event.getResearchIdChosen());


            for (Question ques : researchChosen.getQuestionsOfResearch()){
                if (ques.getAnswered().equals("1")){
                  Log.e("QA", "dooooooont");
                };
            }
            this.questionIDSsorted = this.researchChosen.getQuestionIdsSorted();
            if (questionIDSsorted != null && questionIDSsorted.length > 0) {
                researchInAction = true;
                selectNextQuestion();
            } else
                Toast.makeText(ResearchActivity.this, "Δεν υπάρχουν διαθέσιμες ερωτήσεις για τη συγκεκριμένη έρευνα.", Toast.LENGTH_SHORT).show();
        } else if (event.getResult() == 100) { /*qualitative*/
            if (mUtils.isConnectedToTheInternet()) {
                this.researchChosen = Singleton.getInstance(this).getResearchesArray().get(event.getResearchIdChosen());
                qualFrag = new QualityFragment();
                tag = "QualityFragment";
                addNewFragment(qualFrag);
            } else {
                Toast.makeText(ResearchActivity.this, "Πρέπει να είστε συνδεδεμένος στο Internet, προκειμένου να συμμετέχετε σε ποιοτική έρευνα.", Toast.LENGTH_LONG).show();
            }
        } else if (event.getResult() == 101) { /*** Testing the research ***/
            Toast.makeText(ResearchActivity.this, "Δοκιμαστική λειτουργία. Οι απαντήσεις δε θα αποθηκευτούν", Toast.LENGTH_LONG).show();
            trialResearch = true;
            this.researchChosen = Singleton.getInstance(this).getResearchesArray().get(event.getResearchIdChosen());
            this.questionIDSsorted = this.researchChosen.getQuestionIdsSorted();
            selectNextQuestion();
        } else if (event.getResult() == 1000) { /*** Testing the research ***/
            trialResearch = true;
            Toast.makeText(ResearchActivity.this, "Δοκιμαστική λειτουργία. Οι απαντήσεις δε θα αποθηκευτούν", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(ResearchActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }

    private boolean isImageType(Question ques){
        return (ques.getTemplate().equals("multiplechoiceimage") ||
                ques.getDisplayType().contains("image") ||
                ques.getTemplate().equals("matchimage") ||
                ques.getTemplate().equals("sorthorizontal") ||
                ques.getTemplate().equals("sortvertical"));
    }

    private void preloadImagesToCache() {
        /*
        * preload images to cache , so that they will be available for offline use
        */



        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            public void run() {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(config);
                HashMap<String, Research> researchHashMap =  Singleton.getInstance(ResearchActivity.this).getResearchesArray();

                for (String res : researchHashMap.keySet()){
                    Research research = researchHashMap.get(res);
                    for (Question ques : research.getQuestionsOfResearch()){
                        if (isImageType(ques)){
                            for (Answer ans : ques.getAnswersArray()){
                                if (!ans.getImageurl().equals("http://hivelive.gr/")){
                                    imageLoader.loadImage(ans.getImageurl(), new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            Log.e("ImageURL","----------------");
                                            Log.e("ImageURL",imageUri);
                                            Log.e("ImageURL","Complete loading");
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

            }
        });


    }


    private void checkServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int errorCheck = api.isGooglePlayServicesAvailable(this);
        if (errorCheck == ConnectionResult.SUCCESS) {
            //google play services available, hooray
        } else if (api.isUserResolvableError(errorCheck)) {
            //GPS_REQUEST_CODE = 1000, and is used in onActivityResult
            api.showErrorDialogFragment(this, errorCheck, GPS_REQUEST_CODE);
            //stop our activity initialization code
            return;
        } else {
            createInternetSnackbar();
            return;
        }

    }

    private void createInternetSnackbar() {

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.researches_coordinator_layout);
        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                System.exit(1);
                finish();
            }
        };
        Snackbar
                .make(coordinatorLayout, R.string.snackbar_text_problem, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_exit, clickListener)
                .setDuration(Snackbar.LENGTH_INDEFINITE).setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //GPS successfully updated / enabled, we can continue in our stuff

            } else {
      /* no Google Play, or user denied installing
      here I only exit application */
                finish();
            }
        }
    }

    private void refillAnswersArray(String previousQuesID) {
        /*
            user is going back to a fragment , so we get the previous answers given and save them to the answersSelected
         */

        Question prevQuestion;
        if (startFr != null) {
            startFr = null;
        }
        prevQuestion = researchChosen.getQuestionsForResearch().get(previousQuesID);


        answersSelected.clear();
        if (prevQuestion != null) {

            answersSelected = prevQuestion.getAnswersGiven();
            for (Answer answers : answersSelected) {
                Log.e("PreviousAnswers", "-------------------------------------------");
                Log.e("PreviousAnswers", "answerID " + answers.getAnswer_id() + ", answer value " + answers.getValue());
            }
            fab.show();
        }
    }

    private void setUpTagandRest() {
        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);
        tag = backEntry.getName();
        if (tag.equals("StartResearchFragment") && getFragmentManager().getBackStackEntryCount() > 2) {
            backEntry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 2);
            tag = backEntry.getName();
        }
        hideNextBtn(false);
        //// TODO: 02/04/16 create array with answered questions?
//        if (finalNextQuestionID.equals("0"))


        if (questionsAnsweredArray.size() >= 2) {
            finalNextQuestionID = questionsAnsweredArray.get(questionsAnsweredArray.size() - 1);
            previousQuestionAnsweredID = questionsAnsweredArray.get(questionsAnsweredArray.size() - 2);
        } else {
            previousQuestionAnsweredID = "0";
            finalNextQuestionID = "0";
        }

        switch (tag) {
            case "DropdownFragment":
                multipleanswers = true;
                break;
            case "MultipleChoiceFragment":
                multipleanswers = true;
                break;
            case "SingleChoiceFragment":
                multipleanswers = false;
                break;
            case "SubmitTextFragment":
                multipleanswers = false;
                break;
            case "ThermometerFragment":
                multipleanswers = false;
                break;
            case "HorizontalRangebarFragment":
                multipleanswers = false;
                break;
            case "ImagepickFragment":
                multipleanswers = true;
                break;
            case "ImageMatchDND":
                multipleanswers = true;
                break;
            case "OrderingImagesFragment":
                multipleanswers = true;
                break;
            case "GridSliderFragment":
                multipleanswers = true;
                break;
            case "GridRadiobuttonFragment":
                multipleanswers = true;
                break;
            default:
                multipleanswers = false;
                Log.e("ERROR", "tag on refill not found!!!!!");
        }
    }


    private void selectNextQuestion() {
        if (questionsAnswered == 0) {
            Log.d("Questions", "first question to be shown.");
            finalNextQuestionID = questionIDSsorted[0];
            questionsAnsweredArray.add(finalNextQuestionID);
        } else {
            if (questionsAnswered >= questionIDSsorted.length) {
                //  Log.d("Questions", "We have answered all the questions...! :O");
                finalNextQuestionID = "0";
            }
//            else {
//                Log.d("Questions", "We haven't answered all the questions.. moving forward!");
//            }
        }

        nextQuestion = this.researchChosen.getQuestionsForResearch().get(finalNextQuestionID);
        nextQuestionType = (nextQuestion == null ? "" : (nextQuestion.getTemplate().isEmpty() ? nextQuestion.getDisplayType() : nextQuestion.getTemplate()));

        Log.d("BackOperations", "------------------------NEW QUESTION-----------------------------");
        Log.d("BackOperations", "Next question id " + finalNextQuestionID);
        Log.d("BackOperations", "Next question type is " + nextQuestionType);
        if (nextQuestion != null) {
            Log.d("Questions", "Answers for the question " + nextQuestion.getQuestion() + " with type " + nextQuestion.getDisplayType() + " are :");
            Log.d("BackOperations", "-----------------------ANSWERS------------------------------");
            for (Answer ans : nextQuestion.getAnswersArray()) {
                Log.d("BackOperations", "answerid " + ans.getAnswer_id() + ", answer text " + ans.getText() + ", answer value " + ans.getValue());
//                if (nextQuestion.getDisplayType().toLowerCase().contains("image")){
//                    Log.d("Answerfinal", "answerurl "+ ans.getImageurl());
//                }
            }
        }
        Log.d("BackOperations", "-------------------------------------------------------------");

        switch (nextQuestionType) {
            case "dropdown":
            case "fillindropdownstep":
            case "fillindropdown":
                multipleanswers = true;
                DropdownFragment dropFragment = new DropdownFragment();
                tag = "DropdownFragment";
                addNewFragment(dropFragment);
                fab.show();
                break;
            case "checkboxlist":
            case "checkboxlistvertical":
            case "multiplechoicecustomcheckboxlistvertical":
                multipleanswers = true;
                MultipleChoiceFragment questionsMainFragment = new MultipleChoiceFragment();
                tag = "MultipleChoiceFragment";
                addNewFragment(questionsMainFragment);
                break;
            case "radiobuttonvertical":
            case "radiobutton":
            case "multiplechoicecustomradiobuttonvertical":
                multipleanswers = false;
                SingleChoiceFragment radioBtnChoice = new SingleChoiceFragment();
                tag = "SingleChoiceFragment";
                addNewFragment(radioBtnChoice);
                break;
            case "freetext":
            case "numeric":
            case "currency":
            case "fillinfreetext":
            case "fillinnumeric":
            case "fillincurrency":
                multipleanswers = false;
                SubmitTextFragment textFragment = new SubmitTextFragment();
                tag = "SubmitTextFragment";
                addNewFragment(textFragment);
                break;
            case "thermometer":
            case "percentthermometer":
                multipleanswers = false;
//                ThermometerFragment therm = new ThermometerFragment();
                ThermometerFragment therm = ThermometerFragment.newInstance(getResources());
                tag = "ThermometerFragment";
                addNewFragment(therm);
                break;
            case "slider":
            case "percentslider":
                multipleanswers = false;
                HorizontalRangebarFragment horizontalRangebarFragment = new HorizontalRangebarFragment();
                tag = "HorizontalRangebarFragment";
                addNewFragment(horizontalRangebarFragment);
                break;
            case "multiplechoiceimage":
                multipleanswers = true;
                ImagepickFragment imgFr = new ImagepickFragment();
                tag = "ImagepickFragment";
                addNewFragment(imgFr);
                break;
            case "gridimagegridimage":
                multipleanswers = true;
                GridRadiobuttonFragment gr = new GridRadiobuttonFragment();
                tag = "GridRadiobuttonFragment";
                addNewFragment(gr);
                break;
            case "gridslidergridslider":
                multipleanswers = true;
                GridSliderFragment grslider = new GridSliderFragment();
                tag = "GridSliderFragment";
                addNewFragment(grslider);
                break;
            case "matchimage":

//                ImageMatchingFragment imgMatchfr = new ImageMatchingFragment();
//                tag = "ImageMatchingFragment";
//                addNewFragment(imgMatchfr);

                ImageMatchDND imgMatchfr = new ImageMatchDND();
                tag = "ImageMatchDND";
                addNewFragment(imgMatchfr);
                break;
            case "horizontal":
            case "vertical":
            case "sorthorizontal":
            case "sortvertical":
                multipleanswers = true;
                /*horizontal or vertical sorting of images*/
                OrderingImagesFragment horFrag = new OrderingImagesFragment();
                tag = "OrderingImagesFragment";
                addNewFragment(horFrag);
                break;
            case "gridslider":
                multipleanswers = true;
                GridSliderFragment gridWithsliders = new GridSliderFragment();
                tag = "GridSliderFragment";

                addNewFragment(gridWithsliders);
                break;
            case "gridmultiple":
            case "gridgridmultiple":
                multipleanswers = true;
                GridRadiobuttonFragment gridWithRadios = new GridRadiobuttonFragment();
                tag = "GridRadiobuttonFragment";
                Log.e("ErrorDebugging", "answersArray size is " + answersSelected.size());

                addNewFragment(gridWithRadios);
                break;
            default:
                Log.e("BackOperations", "Display type not found.Please contact the admin.");
                multipleanswers = false;
                finishResearch();
        }

    }

    public Question getNextQuestion() {
        return nextQuestion;
    }


    private void saveAnswerToResearch() {
        Log.d("Questions", "----------------> Saving answer for previous question! <----------------");
        boolean newNextQuestionAdded = false;


        if (!trialResearch)
            researchChosen.getQuestionsForResearch().get(finalNextQuestionID).setAnswered("1");
        researchFinished = false;
        researchChosen.getQuestionsForResearch().get(finalNextQuestionID).setAnswersGiven(new ArrayList<Answer>());
        if (answersSelected.size() > 1) {
            for (Answer ans : answersSelected) {
                Log.e("BAM", "NEXT QUESTION FROM ANSWER " + ans.getNextQuestion());
//                Log.d("Answerfinal", "answer-----> id : " + ans.getAnswer_id() + ", value : " + ans.getValue());
                researchChosen.getQuestionsForResearch().get(finalNextQuestionID).getAnswersGiven().add(ans);
                if (ans.getNextQuestion() == null) {
                    ans.setNextQuestion("0");
                }
                if (!(ans.getNextQuestion().equals("0") || ans.getNextQuestion().equals("-1")) && !questionsAnsweredArray.contains(ans.getNextQuestion())) {
                    addNextQuestionsToHashMap(ans.getNextQuestion());
                    newNextQuestionAdded = true;
                } else if (ans.getNextQuestion().equals("-1")) {
                    researchFinished = true;
                }
            }
            clearAnswersForNextQuestion();
        } else if (answersSelected.size() > 0) {

            Answer newAnswer = answersSelected.get(0);
            // Log.d("Answerfinal", "answer-----> id : " + newAnswer.getAnswer_id() + ", value : " + newAnswer.getValue());
            Log.e("BAM", "NEXT QUESTION FROM ANSWER " + newAnswer.getNextQuestion());
            if (newAnswer.getNextQuestion() == null || newAnswer.getNextQuestion().isEmpty())
                newAnswer.setNextQuestion("0");
            if ((!newAnswer.getNextQuestion().equals("0") && !newAnswer.getNextQuestion().equals("-1")) && !questionsAnsweredArray.contains(newAnswer.getNextQuestion())) {
                addNextQuestionsToHashMap(newAnswer.getNextQuestion());
                newNextQuestionAdded = true;
            } else if (newAnswer.getNextQuestion().equals("-1")) {
                researchFinished = true;
            }
            researchChosen.getQuestionsForResearch().get(finalNextQuestionID).getAnswersGiven().add(newAnswer);
            clearAnswersForNextQuestion();
        } else {
            fab.hide();
        }

        if (nextQuestionsHashSet.size() > 0 && newNextQuestionAdded) {
            Log.d("Questions", "There was a new nextQuestion added to the hashSet!");
            nextQuestionsArraySorted = new ArrayList<>();
            for (String nextQID : nextQuestionsHashSet) {
                if (!questionsAnsweredArray.contains(nextQID))
                    nextQuestionsArraySorted.add(nextQID);
            }

            if (nextQuestionsHashSet.size() > 1) {
                sort();
            }
        }

    }

    private void fixNextQuestionIDSArray() {
        nextQuestionsArraySorted.clear();
        if (previousQuestionAnsweredID != null || !previousQuestionAnsweredID.equals("0")) {
            String nextQuestionIDS = qAnsweredToNextqIDs.get(previousQuestionAnsweredID);
            if (nextQuestionIDS != null) {
                String[] nIDS = nextQuestionIDS.split(",");
                for (String nQID : nIDS) {
                    nextQuestionsHashSet.remove(nQID);
                }
            }
        }
    }


    private void addNextQuestionsToHashMap(String nextQuestion) {
        String[] nextQues;
        if (!nextQuestion.isEmpty()) {
            if (nextQuestion.contains(",")) {
                nextQues = nextQuestion.split(",");
                for (int i = 0; i < nextQues.length; i++) {
                    nextQuestionsHashSet.add(nextQues[i]);
                }
            } else {
                nextQuestionsHashSet.add(nextQuestion);
            }
            qAnsweredToNextqIDs.put(finalNextQuestionID, nextQuestion);
        }
    }


    public void answerSelection(Answer ansSelected) {
        boolean answerFound = false;
        Log.d("Questions", "new Answer selected");
        Log.d("Questions", "answer is " + ansSelected.getText());

        if (ansSelected.getAnswer_id() != null) {

            if (!multipleanswers) {
                Log.d("Questions", "There should be a single answer, so we clear the array of answers and put the new one");
                answersSelected.clear();
                answersSelected.add(ansSelected);
            } else {
                Log.d("Questions", "we have multiple answers! Let's see what happens now...");
                for (Answer ans : answersSelected) {
                    if (ans.getAnswer_id().equals(ansSelected.getAnswer_id() == null ? "" : ansSelected.getAnswer_id())) {
                        answersSelected.remove(ans);
                        answerFound = true;
                        if (tag.equals("OrderingImagesFragment") || tag.equals("GridRadiobuttonFragment") || tag.equals("GridSliderFragment")) {
                            Log.d("Questions", "we have reordering or grid with radios, so we don't remove the answers. we just replace them");
                            answersSelected.add(ansSelected);
                        } else {
                            Log.d("Questions", "we have already added this answer before. we assume the user unselected it, so we have to remove it.");
                        }
                        break;
                    }
                }

                if (!answerFound) {
                    Log.d("Questions", "it's a totally new answer. We added it!");
                    Log.d("ErrorDebugging", "answer added has id " + ansSelected.getAnswer_id() + " and value " + ansSelected.getValue());

//                    if ((tag.equals("GridRadiobuttonFragment") && (ansSelected.getValue().contains(","))) || !tag.equals("GridRadiobuttonFragment")){
                    answersSelected.add(ansSelected);
//                    }
                }
            }

            if (answersSelected.size() > 0 && !hideNextBtn) {
                fab.show();
            } else {
                fab.hide();
            }
        } else {
            if (questionsAnswered >= questionIDSsorted.length) {
                finishResearch();
            }
        }
    }

    private void finishResearch() {
        researchInAction = false;
        if (isResearcher && !finalQuestionaireShown) {
            finalQuestionaireShown = true;
            startFr = new StartResearchFragment();
            tag = "StartResearchFragment";
            addNewFragment(startFr);
        } else {
            if (!trialResearch) {

                Log.e("BackOperations", "finishing research");
//                Singleton.getInstance(this).setFinalResearchwithResults(researchChosen);

                getTimeForEachAnswer();
                researchChosen.setTimeOfCompletion((System.currentTimeMillis() / 1000));

                if (!isResearcher)
                    Singleton.getInstance(this).getResearchesAnswered().add(researchChosen.getResearchID());
                if (questionsAnsweredArray.size() >= 1) {

                    try {
                        SnappyCalls.open(getApplicationContext());
                        SnappyCalls.addResearch(researchChosen, isResearcher);
                        SnappyCalls.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SnappydbException e2) {
                        e2.printStackTrace();
                    }

                    if (mUtils.isConnectedToTheInternet()) {
                        NetworkServices.startUploadingAnswers(this);
                    } else {
                        scheduleUploadingAnswers();
                    }

                }
            }


            removeAllFragments();
            hideKeyboard();
            fab.hide();
            researchFinished = true;
            resetAllData();
        }
    }

    private void getTimeForEachAnswer() {
        HashMap<String, Integer> timesforeachquestion = Singleton.getInstance(this).getSecondsToAnswer();
        int time;

        for (Question ques : researchChosen.getQuestionsOfResearch()) {
            if (ques.getAnswered().equals("1")) {
                time = (timesforeachquestion.get(ques.getQuestionId()) == null ? 0 : timesforeachquestion.get(ques.getQuestionId()));
                ques.setTime(time);
            }
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String getNextIdFromQIDsSorted() {
        Log.d("Questions", "the array with the sorted next questions was empty!");
        Log.d("Questions", "we are going to look for the next question from the initially sorted questionIDS");

        int indexOfQ = 0;
        boolean questionsDone = false;
        String possibleNextQuestionID = questionIDSsorted[indexOfQ];

        Log.d("Questions", "First Step : --------- possible next question is the first one with id " + possibleNextQuestionID);
        while (questionsAnsweredArray.contains(possibleNextQuestionID)) {
            indexOfQ++;
            if (indexOfQ >= questionIDSsorted.length) {
                questionsDone = true;
                break;
            }
            possibleNextQuestionID = questionIDSsorted[indexOfQ];
        }
        if (!questionsDone) {
            Log.d("Questions", "We found the next one!position is " + indexOfQ + "and its id is " + questionIDSsorted[indexOfQ]);
            return questionIDSsorted[indexOfQ];
        } else {
            return "0";
        }
    }

    public Research getResearchChosen() {
        return researchChosen;
    }

    public void setResearchChosen(Research researchChosen) {
        this.researchChosen = researchChosen;
    }

    private void resetAllData() {
        Log.d("BackOperations", "Reseting all data........................");
        this.researchChosen = null;
        this.researchInAction = false;
        this.nextQuestionType = "";
        this.nextQuestion = null;
        this.finalNextQuestionID = "";
        this.previousQuestionAnsweredID = "";
        this.nextQuestionsArraySorted.clear();
        this.nextQuestionsHashSet = new HashSet<>();
        this.answersSelected = new ArrayList<>();
        this.questionsAnsweredArray = new ArrayList<>();
        this.multipleanswers = false;
        this.backPressedCounter = 0;
        this.questionsAnswered = 0;
        this.tag = "";
        this.questionIDSsorted = null;
        this.qualFrag = null;
        this.startFr = null;
        this.errorFragmentAdded = false;
        this.newWebViewAdded = false;
        this.trialResearch = false;
        this.finalQuestionaireShown = false;
        this.startTime = 0;
        this.endTime = 0;


        Singleton.getInstance(this).setFinalResearchwithResults(new Research());
        Singleton.getInstance(this).setQuestionsToAnswers(new HashMap<String, String>());
    }

    private void scheduleUploadingAnswers() {
        MyTaskService.scheduleOneOff(this);
    }

    private void sort() {
        if (nextQuestionsArraySorted != null && nextQuestionsArraySorted.size() > 0) {
            Collections.sort(nextQuestionsArraySorted, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareTo(s2);
                }
            });
        }
    }

    public void addNewFragment(Fragment mFragment) {
        if (startFr == null && qualFrag == null) {
            startTime = System.currentTimeMillis();
        }
        backPressedCounter = 0;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        getFragmentManager().beginTransaction().add(R.id.researches_container, mFragment, tag).addToBackStack(tag).commit();
    }

    public void addErrorFragment(Fragment mFragment) {
        errorFragmentAdded = true;
        backPressedCounter = 0;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        getFragmentManager().beginTransaction().add(R.id.researches_container, mFragment, "CustomDialogFragment").addToBackStack("CustomDialogFragment").commit();
    }

    public void removeErrorFragment() {
        errorFragmentAdded = false;
        getFragmentManager().popBackStackImmediate();
    }


    public void hideNextBtn(boolean hide) {
        if (hide)
            fab.hide();
        else
            fab.show();
        hideNextBtn = hide;
    }

    private void removeAllFragments() {
        FragmentManager fm = getFragmentManager();
        fm.popBackStackImmediate(firstFragmentTag, 0);
        if (qualFrag == null)
            Toast.makeText(ResearchActivity.this, "Όλες οι ερωτήσεις έχουν απαντηθεί γι αυτή την έρευνα.", Toast.LENGTH_SHORT).show();
        qualFrag = null;
    }

    private void clearAnswersForNextQuestion() {
        answersSelected = new ArrayList<>();
    }

    public int getQuestionsAnswered() {
        return this.questionsAnswered + 1;
    }


    private void showNextQuestions() {
        Log.e("BAM", "----------------------------------------------------------------");
        Log.e("BAM", "----***** question ids sorted *****----");
        for (String qIDSSorted : questionIDSsorted) {
            Log.e("BAM", qIDSSorted);
        }

        Log.e("BAM", "----****** next questions sorted ********----");

        for (String qIDSSorted2 : nextQuestionsArraySorted) {
            Log.e("BAM", qIDSSorted2);
        }

        Log.e("BAM", "----******* questions answered *******----");

        for (String qIDSSorted3 : questionsAnsweredArray) {
            Log.e("BAM", qIDSSorted3);
        }

    }

    public boolean isNewWebViewAdded() {
        return newWebViewAdded;
    }

    public void setNewWebViewAdded(boolean newWebViewAdded) {
        this.newWebViewAdded = newWebViewAdded;
    }
}
