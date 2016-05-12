package gr.extract.hivelive.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.Postprocessor;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Activities.LoginActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Services.NetworkServices;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Constants;
import gr.extract.hivelive.hiveUtilities.TextValidator;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private TextView dateTextView, login_tv, message_tv;
    private View mMainView;
    private Spinner townSpinner, educationSpinner, jobSpinner;
    private EditText passwordEdt, repeatPasswordEdt, name, username, mobile, phone;
    private EditText email;
    private Button signupBtn;
    private RadioGroup genderGroup;
    private CircularProgressView progressView;


    private Postprocessor processor = null;

    private String mOccupation, mEducation, mCity, mBirthdate, mGender = "Άνδρας";
    private HashMap<String, String> codesToCities, codesToEdu, codesToOccu;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = (View) inflater.inflate(R.layout.fragment_sign_up, container, false);
        dateTextView = (TextView) mMainView.findViewById(R.id.datetextview);
        login_tv = (TextView) mMainView.findViewById(R.id.signup_login_tv);
        townSpinner = (Spinner) mMainView.findViewById(R.id.townspinner);
        educationSpinner = (Spinner) mMainView.findViewById(R.id.education_spinner);
        jobSpinner = (Spinner) mMainView.findViewById(R.id.job_spinner);
        name = (EditText) mMainView.findViewById(R.id.signup_name_edt);
        username = (EditText) mMainView.findViewById(R.id.signup_username_edt);
        email = (EditText) mMainView.findViewById(R.id.signup_email_edt);
        phone = (EditText) mMainView.findViewById(R.id.signup_phone_edt);
        mobile = (EditText) mMainView.findViewById(R.id.signup_mobilephone_edt);
        passwordEdt = (EditText) mMainView.findViewById(R.id.signup_password_edt);
        repeatPasswordEdt = (EditText) mMainView.findViewById(R.id.signup_repeatpassword_edt);
        passwordEdt.setTransformationMethod(new PasswordTransformationMethod());
        repeatPasswordEdt.setTransformationMethod(new PasswordTransformationMethod());
        signupBtn = (Button) mMainView.findViewById(R.id.signup_button);
        genderGroup = (RadioGroup) mMainView.findViewById(R.id.gender_radiogroup);
        progressView = (CircularProgressView) mMainView.findViewById(R.id.progress_view);
        message_tv = (TextView) mMainView.findViewById(R.id.signup_message_tv) ;


//        draweeView = (SimpleDraweeView) mMainView.findViewById(R.id.login_fresco_background_iv);
//        Uri uri = Uri.parse("http://hivelive.gr/img/loginbg.jpg");
//
//        processor = new BlurPostprocessor(ç, 25);
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

//
//        Uri uri = new Uri.Builder()
//                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
//                .path(String.valueOf(R.drawable.loginbg))
//                .build();
//
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

//
//        Uri uri = new Uri.Builder()
//                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
//                .path(String.valueOf(R.drawable.loginbg))
//                .build();
//        draweeView.setImageURI(uri);

        displayEmptyError(name, "your name");
        displayEmptyError(username, "your username");
        displayEmptyError(email, "your email");
        displayEmptyError(mobile, "your mobile phone");
        displayEmptyError(phone, "your phone");
        displayEmptyError(passwordEdt, "your password");
        displayEmptyError(repeatPasswordEdt, "repeat your password, it");

        addItemsOnSpinners();
        setonClickListeners();

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

    public void onEvent(Events.CommunicationBetweenActivitiesEvent ev){

    }

    public void onEvent(Events.SignUpEvent event){
        progressView.setVisibility(View.GONE);
        if (event.getResult() == 0){
            Toast.makeText(getActivity().getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new Events.CommunicationBetweenActivitiesEvent(Constants.START_ACTIVITY));
        }else{
            message_tv.setVisibility(View.VISIBLE);
            message_tv.setText(event.getMessage());
        }
    }

    private void displayEmptyError(final EditText edt, final String message) {
        edt.addTextChangedListener(new TextValidator(edt) {
            @Override
            public void validate(TextView textView, String text) {
                if (edt.getText().length() == 0) {
                    edt.setError(message + " cannot be empty");
                    edt.requestFocus();
                } else {
                    if (edt.getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(edt.getText().toString()).matches()) {
                            edt.setError("το email που πληκτρολογήσατε δεν έχει σωστή μορφή");
                            edt.requestFocus();
                        }
                    } else if (edt.getInputType() == InputType.TYPE_CLASS_PHONE) {

                        if (!Patterns.PHONE.matcher(edt.getText().toString()).matches()) {
                            edt.setError("το τηλέφωνο πρέπει να περιέχει μόνο αριθμούς");
                            edt.requestFocus();
                        }
                    }

                }
            }
        });

    }


    // add items into spinner dynamically
    public void addItemsOnSpinners() {

        ArrayList<String> codesC = Singleton.getInstance(getActivity().getApplicationContext()).getCityCodes();
        ArrayList<String> codesE = Singleton.getInstance(getActivity().getApplicationContext()).getEduCodes();
        ArrayList<String> codesO = Singleton.getInstance(getActivity().getApplicationContext()).getOccuCodes();
        codesToCities = Singleton.getInstance(getActivity().getApplicationContext()).getCodesToCities();
        codesToEdu = Singleton.getInstance(getActivity().getApplicationContext()).getCodesToEducation();
        codesToOccu = Singleton.getInstance(getActivity().getApplicationContext()).getCodesToOccupation();

        List<String> list = new ArrayList<String>();

        for (String code : codesC) {
            if (codesToCities.containsKey(code))
                list.add(codesToCities.get(code));
        }
//
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.custom_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        townSpinner.setAdapter(dataAdapter);
        townSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mCity = parent.getItemAtPosition(pos).toString();
            }
        });

//
//        List<String> list2 = new ArrayList<String>();
//        list2.add("ΚΑΘΟΛΟΥ ΜΟΡΦΩΣΗ (Δεν έχει πάει σχολείο ή μέχρι τη δευτέρα δημοτικού)");
//        list2.add("ΚΑΤΩΤΕΡΗ ΜΟΡΦΩΣΗ (3η Δημοτικού μέχρι 3η Γυμνασίου)");
//        list2.add("ΜΕΣΗ ΜΟΡΦΩΣΗ (1η-3η Λυκείου ή 4η -6η παλαιού Γυμνασίου)");
//        list2.add("ΑΝΩΤΕΡΗ ΜΟΡΦΩΣΗ (Απόφοιτοι ΤΕΙ ή άλλων αντίστοιχων ιδιωτικών σχολών που απαιτούν απολυτήριο Λυκείου)");
//        list2.add("ΑΝΩΤΑΤΗ ΜΟΡΦΩΣΗ (Απόφοιτοι ΑΕΙ)");

        list = new ArrayList<String>();
        for (String code : codesE) {
            if (codesToEdu.containsKey(code))
                list.add(codesToEdu.get(code));
        }

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(),
                R.layout.custom_spinner_item, list);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationSpinner.setAdapter(dataAdapter2);
        educationSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mEducation = parent.getItemAtPosition(pos).toString();
            }
        });


        list = new ArrayList<String>();
//        list3.add("ΑΓΡΟΤΗΣ (-50 στρέμματα)");
//        list3.add("ΑΓΡΟΤΗΣ (50+ στρέμματα)");
//        list3.add("ΕΛΕΥΘΕΡΟΣ ΕΠΑΓΓΕΛΜΑΤΙΑΣ ΧΩΡΙΣ ΥΠΑΛΛΗΛΟΥΣ");
//        list3.add("ΕΛΕΥΘΕΡΟΣ ΕΠΑΓΓΕΛΜΑΤΙΑΣ ΜΕ 1-2 ΥΠΑΛΛΗΛΟΥΣ");
//        list3.add("ΕΛΕΥΘΕΡΟΣ ΕΠΑΓΓΕΛΜΑΤΙΑΣ ΜΕ 3-5 ΥΠΑΛΛΗΛΟΥΣ");
//        list3.add("ΕΛΕΥΘΕΡΟΣ ΕΠΑΓΓΕΛΜΑΤΙΑΣ ΜΕ 6-10 ΥΠΑΛΛΗΛΟΥΣ");
//        list3.add("ΕΛΕΥΘΕΡΟΣ ΕΠΑΓΓΕΛΜΑΤΙΑΣ ΜΕ 11-49 ΥΠΑΛΛΗΛΟΥΣ");
//        list3.add(" ΕΛΕΥΘΕΡΟΣ ΕΠΑΓΓΕΛΜΑΤΙΑΣ ΜΕ 50+ ΥΠΑΛΛΗΛΟΥΣ");
//        list3.add("EΠΙΣΤΗΜΟΝΑΣ/ ΕΙΔΙΚΟΣ");
//        list3.add("ΓΕΝΙΚΟΣ ΔΙΕΥΘΥΝΤΗΣ (-5 ΥΠΑΛΛΗΛΟΥΣ)");
//        list3.add("ΓΕΝΙΚΟΣ ΔΙΕΥΘΥΝΤΗΣ (6-10 ΥΠΑΛΛΗΛΟΥΣ)");
//        list3.add("ΓΕΝΙΚΟΣ ΔΙΕΥΘΥΝΤΗΣ (11+ ΥΠΑΛΛΗΛΟΥΣ)");
//        list3.add("ΠΡΟΪΣΤΑΜΕΝΟΣ (-5 ΥΠΑΛΛΗΛΩΝ)");
//        list3.add("ΠΡΟΪΣΤΑΜΕΝΟΣ (6+ ΥΠΑΛΛΗΛΩΝ)");
//        list3.add("ΥΠΑΛΛΗΛΟΣ ΓΡΑΦΕΙΟΥ");
//        list3.add("ΥΠΑΛΛΗΛΟΣ ΕΚΤΟΣ ΓΡΑΦΕΙΟΥ");
//        list3.add("ΕΡΓΑΤΗΣ ΕΙΔΙΚΕΥΜΕΝΟΣ");
//        list3.add("ΕΡΓΑΤΗΣ ΑΝΕΙΔΙΚΕΥΤΟΣ");
//        list3.add("ΝΟΙΚΟΚΥΡΑ/ ΕΙΣΟΔΗΜΑΤΙΑΣ");
//        list3.add("ΦΟΙΤΗΤΗΣ/ ΣΠΟΥΔΑΣΤΗΣ");
//        list3.add("ΣΥΝΤΑΞΙΟΥΧΟΣ");
//        list3.add("ΑΝΕΡΓΟΣ");


        for (String code : codesO) {
            if (codesToOccu.containsKey(code))
                list.add(codesToOccu.get(code));
        }

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(getActivity(),
                R.layout.custom_spinner_item, list);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobSpinner.setAdapter(dataAdapter3);
        jobSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mOccupation = parent.getItemAtPosition(pos).toString();
            }
        });

    }

    private void setonClickListeners() {
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SignUpFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).replaceFragment("signup_frag");
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message_tv.setVisibility(View.INVISIBLE);
                if (fieldsAreOk()) {
                    hideKeyboard();
                    progressView.setVisibility(View.VISIBLE);
                    progressView.startAnimation();
                    String cityCodeChosen = "";
                    for (String code : codesToCities.keySet()) {
                        if (mCity.equals(codesToCities.get(code))) {
                            cityCodeChosen = code;
                            break;
                        }
                    }

                    String eduCodeChosen = "";
                    for (String code : codesToEdu.keySet()) {
                        if (mEducation.equals(codesToEdu.get(code))) {
                            eduCodeChosen = code;
                            break;
                        }
                    }


                    String occuCodeChosen = "";
                    for (String code : codesToOccu.keySet()) {
                        if (mOccupation.equals(codesToOccu.get(code))) {
                            occuCodeChosen = code;
                            break;
                        }
                    }


                    NetworkServices.startSignUp(getActivity().getApplicationContext(),
                            name.getText().toString(),
                            username.getText().toString(),
                            passwordEdt.getText().toString(),
                            email.getText().toString(),
                            repeatPasswordEdt.getText().toString(),
                            mGender,
                            mBirthdate,
                            eduCodeChosen,
                            occuCodeChosen,
                            phone.getText().toString(),
                            mobile.getText().toString(),
                            cityCodeChosen);
                }
            }
        });

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.gender_male) {
                    mGender = "Άνδρας";
                } else mGender = "Γυναίκα";
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        dateTextView.setText(date);
        mBirthdate = date;
    }

    private boolean fieldsAreOk(){
        boolean allOK = true;
        ViewGroup group = (ViewGroup)mMainView.findViewById(R.id.fragment_signup_ll);
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                if (((EditText)view).getText().toString().isEmpty()){
                    allOK = false;
                    ((EditText)view).setError("Παρακαλώ συμπληρώστε το πεδίο");
                    ((EditText)view).requestFocus();
                    break;
                }
            }
        }

        if (allOK){
            if (!repeatPasswordEdt.getText().toString().equals(passwordEdt.getText().toString())){
                repeatPasswordEdt.setError("Οι κωδικοί δεν ταιριάζουν");
                repeatPasswordEdt.requestFocus();
                allOK = false;
            }
            else repeatPasswordEdt.setError(null);
        }

        if (allOK){
            if (mBirthdate == null) {
                dateTextView.setError("Παρακαλώ συμπληρώστε την ημ/νια γέννησης");
                dateTextView.requestFocus();
                allOK = false;
            }
        }




        return allOK;
    }

    public void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


}

class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


//        Toast.makeText(parent.getContext(),
//                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}