package gr.extract.hivelive.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.TextValidator;

public class StartResearchFragment extends Fragment {

    private View mMainView;
    private EditText firstname, phone, email, address;
    private boolean allOK = false;


    public StartResearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_start_research, container, false);
        return mMainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstname = (EditText) mMainView.findViewById(R.id.start_firstname_edt);
//        lastname = (EditText) mMainView.findViewById(R.id.start_lastname_edt);
        email = (EditText) mMainView.findViewById(R.id.start_email_edt);
        phone = (EditText) mMainView.findViewById(R.id.start_phone_edt);
        address = (EditText) mMainView.findViewById(R.id.start_address_edt);


        displayEmptyError(firstname, "Όνομα");
        displayEmptyError(email, "Email");
        displayEmptyError(phone, "Τηλέφωνο");

        ((ResearchActivity)getActivity()).hideNextBtn(false);

    }


    private void displayEmptyError(final EditText edt, final String message) {
        edt.addTextChangedListener(new TextValidator(edt) {
            @Override
            public void validate(TextView textView, String text) {
                if (edt.getText().length() == 0) {
                    edt.setError(message + " : το πεδίο δεν μπορεί να είναι άδειο");
                    edt.requestFocus();
                    allOK = false;
                }else if (edt.getText().length() > 0) {
                    if (edt.getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS && !Patterns.EMAIL_ADDRESS.matcher(edt.getText().toString()).matches()) {
                            edt.setError("το email που πληκτρολογήσατε δεν έχει σωστή μορφή");
                            edt.requestFocus();
                            allOK = false;
                    } else if (edt.getInputType() == InputType.TYPE_CLASS_PHONE && !Patterns.PHONE.matcher(edt.getText().toString()).matches()) {
                            edt.setError("το τηλέφωνο πρέπει να περιέχει μόνο αριθμούς");
                            edt.requestFocus();
                            allOK = false;
                    }
                    else if (edt.getInputType() == InputType.TYPE_CLASS_PHONE && Patterns.PHONE.matcher(edt.getText().toString()).matches() && edt.getText().length()<10){
                        edt.setError("το τηλέφωνο πρέπει να περιέχει 10 αριθμούς");
                    }
                    else allOK = true;
                }
            }

        });

    }

    public boolean areAllOK(){
        setAllOK();
        return allOK;
    }

    public void setAllOK(){
        if (firstname.getText().length()>0 ){
           if ( ((email.getText().length() >0 && Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) ||email.getText().length() ==0) &&
                   ((phone.getText().length() ==0) || (Patterns.PHONE.matcher(phone.getText().toString()).matches() &&
            phone.getText().length() == 10))){
               allOK = true;
           }else{
               allOK = false;
           }
        } else{
            firstname.setError("Το ονοματεπώνυμο δεν πρέπει να είναι κενό.");
            allOK = false;
        }
    }


    public HashMap<String, String> getQuestionedPersonsDetails(){
        HashMap<String, String> dets = new HashMap<>();
        dets.put("firstname", firstname.getText().toString());
        dets.put("phone", phone.getText().toString());
        dets.put("email", email.getText().toString());
        dets.put("address", address.getText().toString());
        return dets;
    }


}
