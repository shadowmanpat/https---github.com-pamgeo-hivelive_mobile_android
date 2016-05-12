package gr.extract.hivelive.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.koushikdutta.async.http.body.FilePart;
import com.koushikdutta.async.http.body.Part;
import com.snappydb.SnappydbException;
import com.soundcloud.android.crop.Crop;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Services.NetworkServices;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.SnappyCalls;
import gr.extract.hivelive.hiveUtilities.StableData;
import gr.extract.hivelive.hiveUtilities.TextValidator;
import gr.extract.hivelive.hiveUtilities.User;
import gr.extract.hivelive.hiveUtilities.Utils;

public class ProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int SELECT_PHOTO = 100;

    private Spinner townSpinner, educationSpinner, jobSpinner;
    private EditText passwordEdt, repeatPasswordEdt, name, username, mobile, phone;
    private EditText email;
    private RadioGroup genderGroup;
    private RadioButton malebtn, femalebtn;
    private View mMainView;
    private TextView dateTextView, login_tv;
    private SimpleDraweeView profile_pic_dv;
    private FloatingActionButton fab;

    private Utils mUtils;
    private String mOccupation, mEducation, mCity, mBirthdate, mGender;
    private HashMap<String, String> codesToCities, codesToEdu, codesToOccu;
    private ArrayList<Part> picParts = new ArrayList<>();
    private User currentUser;
    private Uri outputUri;
    private boolean newPictureAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mUtils = new Utils(this);

        currentUser = Singleton.getInstance(this).getCurrentUser();

        currentUser.printUser();

        fab = (FloatingActionButton) findViewById(R.id.fab_profile);
        mMainView = (View) findViewById(R.id.profile_content);
        dateTextView = (TextView) mMainView.findViewById(R.id.datetextview);
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
        genderGroup = (RadioGroup) mMainView.findViewById(R.id.gender_radiogroup);
        malebtn = (RadioButton) mMainView.findViewById(R.id.male_radiobtn);
        femalebtn = (RadioButton) mMainView.findViewById(R.id.female_radiobtn);
        profile_pic_dv = (SimpleDraweeView) mMainView.findViewById(R.id.profile_picture_dv);


        if (currentUser.getSex().equals("Άνδρας")) malebtn.setChecked(true);
        else femalebtn.setChecked(true);

        if (!currentUser.getUserphoto().isEmpty()) {
            Uri uri = Uri.parse(currentUser.getUserphoto());
            profile_pic_dv.setImageURI(uri);
        }

        if (currentUser.getBirthdate().isEmpty() || currentUser.getBirthdate().equals("0000-00-00"))
            dateTextView.setText("Ημ/νια Γέννησης");
        else dateTextView.setText(currentUser.getBirthdate());
        name.setText(currentUser.getFullname());
        username.setText(currentUser.getUsername());
        email.setText(currentUser.getEmail());
        phone.setText(currentUser.getPhoneNo());
        mobile.setText(currentUser.getMphoneNo());
        passwordEdt.setText(currentUser.getPassword());
        repeatPasswordEdt.setText(currentUser.getPassword());

        displayEmptyError(name, "your name");
        displayEmptyError(username, "your username");
        displayEmptyError(email, "your email");
        displayEmptyError(mobile, "your mobile phone");
        displayEmptyError(phone, "your phone");
        displayEmptyError(passwordEdt, "your password");
        displayEmptyError(repeatPasswordEdt, "repeat your password, it");

        addItemsOnSpinners();
        setonClickListeners();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUtils.isConnectedToTheInternet() && newPictureAdded) {
                    Singleton.getInstance(ProfileActivity.this).setPicParts(picParts);
                    NetworkServices.startUploadingImage(ProfileActivity.this);
                }else if (!newPictureAdded){
                    finish();
                }else {
                    Toast.makeText(ProfileActivity.this, "Παρακαλώ ελέξτε τη σύνδεσή σας στο Internet και ξαναδοκιμάστε.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    public void onEvent(Events.PictureUploadEvent event){
        if (event.getResult() == 0){
            Toast.makeText(ProfileActivity.this, "Η φωτογραφία ανέβηκε!", Toast.LENGTH_SHORT).show();
            Singleton.getInstance(this).getCurrentUser().setUserphoto(event.getProfpicPath());
            finish();
        }else{
            Toast.makeText(ProfileActivity.this, "Η φωτογραφία δεν ανέβηκε..Παρακαλώ ξαναπροσπαθήστε αργότερα.", Toast.LENGTH_LONG).show();
        }
    }

    private void displayEmptyError(final EditText edt, final String message) {
        edt.addTextChangedListener(new TextValidator(edt) {
            @Override
            public void validate(TextView textView, String text) {
                if (edt.getText().length() == 0) {
                    edt.setError(message + " cannot be empty");
                } else {
                    if (edt.getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(edt.getText().toString()).matches()) {
                            edt.setError("το email που πληκτρολογήσατε δεν έχει σωστή μορφή");
                        }
                    } else if (edt.getInputType() == InputType.TYPE_CLASS_PHONE) {

                        if (!Patterns.PHONE.matcher(edt.getText().toString()).matches()) {
                            edt.setError("το τηλέφωνο πρέπει να περιέχει μόνο αριθμούς");
                        }
                    }

                }
            }
        });

    }


    // add items into spinner dynamically
    public void addItemsOnSpinners() {

        StableData std;
        if (Singleton.getInstance(this).getCityCodes().isEmpty()) {
            SnappyCalls.open(this);
            try {
                std = SnappyCalls.getStableData();

                Singleton.getInstance(this).setCitiesMap(std.getCityCodes(), std.getCodesToCities());
                Singleton.getInstance(this).setEducationMap(std.getEduCodes(), std.getCodesToEducation());
                Singleton.getInstance(this).setOccupationMap(std.getOccuCodes(), std.getCodesToEducation());
            } catch (SnappydbException e) {
                e.printStackTrace();
            }
        }


        ArrayList<String> codesC = Singleton.getInstance(this).getCityCodes();
        ArrayList<String> codesE = Singleton.getInstance(this).getEduCodes();
        ArrayList<String> codesO = Singleton.getInstance(this).getOccuCodes();
        codesToCities = Singleton.getInstance(this).getCodesToCities();
        codesToEdu = Singleton.getInstance(this).getCodesToEducation();
        codesToOccu = Singleton.getInstance(this).getCodesToOccupation();
        String cityChosen = codesToCities.get(currentUser.getCity());
        String educationChosen = codesToEdu.get(currentUser.getEducation());
        String occupationChosen = codesToOccu.get(currentUser.getOccupation());
        int chosenPos = 0;

        List<String> list = new ArrayList<String>();

        for (String code : codesC) {
            if (codesToCities.containsKey(code))
                list.add(codesToCities.get(code));
        }
//
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chosenPos = dataAdapter.getPosition(cityChosen);
        townSpinner.setAdapter(dataAdapter);
        townSpinner.setSelection(chosenPos);
        townSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mCity = parent.getItemAtPosition(pos).toString();
            }
        });


        list = new ArrayList<String>();

        for (String code : codesE) {
            if (codesToEdu.containsKey(code))
                list.add(codesToEdu.get(code));
        }

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, list);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chosenPos = dataAdapter2.getPosition(educationChosen);
        educationSpinner.setAdapter(dataAdapter2);
        educationSpinner.setSelection(chosenPos);
        educationSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mEducation = parent.getItemAtPosition(pos).toString();
            }
        });


        list = new ArrayList<String>();

        for (String code : codesO) {
            if (codesToOccu.containsKey(code))
                list.add(codesToOccu.get(code));
        }

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, list);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chosenPos = dataAdapter3.getPosition(occupationChosen);
        jobSpinner.setAdapter(dataAdapter3);
        jobSpinner.setSelection(chosenPos);
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
                String[] birthParts;
                DatePickerDialog dpd;
                String birthdate = currentUser.getBirthdate();
                if (!birthdate.isEmpty() && !birthdate.equals("0000-00-00")) {
                    birthParts = birthdate.split("-");
                    dpd = DatePickerDialog.newInstance(
                            ProfileActivity.this,
                            Integer.valueOf(birthParts[0]),
                            Integer.valueOf(birthParts[1]) - 1,
                            Integer.valueOf(birthParts[2])
                    );
                } else {
                    Calendar now = Calendar.getInstance();
                    dpd = DatePickerDialog.newInstance(
                            ProfileActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                }

                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });


        profile_pic_dv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()

                                               {
                                                   @Override
                                                   public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                       if (checkedId == R.id.gender_male) {
                                                           mGender = "male";
                                                       } else mGender = "female";
                                                   }
                                               }

        );

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        dateTextView.setText(date);
        mBirthdate = date;
    }

    private boolean fieldsAreOk() {
        boolean allOK = true;
        ViewGroup group = (ViewGroup) mMainView.findViewById(R.id.fragment_signup_ll);
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                if (((EditText) view).getText().toString().isEmpty()) {
                    allOK = false;
                    ((EditText) view).setError("Παρακαλώ συμπληρώστε το πεδίο");
                    break;
                }
            }
        }

        if (allOK) {
            if (!repeatPasswordEdt.getText().toString().equals(passwordEdt.getText().toString())) {
                repeatPasswordEdt.setError("Οι κωδικοί δεν ταιριάζουν");
                allOK = false;
            } else repeatPasswordEdt.setError(null);
        }
        return allOK;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    Crop.of(selectedImage, outputUri).asSquare().start(this);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    profile_pic_dv.setImageURI(outputUri);
                    saveImage(outputUri);
                }
                break;
        }

    }


    private void saveImage(Uri uri) {
        newPictureAdded = true;
        String imagePath = getRealPathFromURI(uri);
        Bitmap thum = createThumbNail(imagePath);
        prepareMultiPartsfromBt(thum);

    }

    private Bitmap createThumbNail(String filepath) {
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filepath), 200, 200);
        return ThumbImage;
    }

    private void prepareMultiPartsfromBt(Bitmap thumbpic) {

        File f;

        String randomName = currentUser.getToken()+"_pic.png";
        f = new File(this.getCacheDir(), randomName);
        picParts = new ArrayList<>();


        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            thumbpic.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            FilePart tempFilePart = new FilePart("profileImage", f);

            picParts.add(tempFilePart);

        } catch (IOException e) {
            Log.e("MyProfile Error", "Cannot create file from bitmap");
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}

class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
