package gr.extract.hivelive.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.body.StringPart;
import com.koushikdutta.ion.Ion;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.SnappyCalls;
import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.Constants;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Research;
import gr.extract.hivelive.hiveUtilities.User;

public class NetworkServices extends IntentService {
    private static final String ACTION_LOGIN = "gr.extract.hivelive.Services.action.login";
    private static final String ACTION_REMIND = "gr.extract.hivelive.Services.action.remindpassword";
    private static final String ACTION_SIGNUP = "gr.extract.hivelive.Services.action.signup";
    private static final String ACTION_GETDATA = "gr.extract.hivelive.Services.action.getData";
    private static final String ACTION_GETRESEARCHES = "gr.extract.hivelive.Services.action.getresearches";
    private static final String ACTION_GETRESEARCHES_OFFLINE = "gr.extract.hivelive.Services.action.getresearchesoffline";
    private static final String ACTION_GETQUESTIONS = "gr.extract.hivelive.Services.action.getquestions";
    private static final String ACTION_UPLOAD_ANSWERS = "gr.extract.hivelive.Services.action.uploadanswers";
    private static final String ACTION_UPLOAD_PIC = "gr.extract.hivelive.Services.action.uploadpic";

    private static final String EXTRA_EMAIL = "gr.extract.hivelive.Services.extra.EMAIL";
    private static final String EXTRA_PASSWORD = "gr.extract.hivelive.Services.extra.PASSWORD";

    /*Sign up parameters*/
    private static final String EXTRA_NAME = "gr.extract.hivelive.Services.extra.name";
    private static final String EXTRA_USERNAME = "gr.extract.hivelive.Services.extra.username";
    private static final String EXTRA_CONFIRM = "gr.extract.hivelive.Services.extra.confirm";
    private static final String EXTRA_SEX = "gr.extract.hivelive.Services.extra.sex";
    private static final String EXTRA_BIRTHDATE = "gr.extract.hivelive.Services.extra.birthdate";
    private static final String EXTRA_EDUCATION = "gr.extract.hivelive.Services.extra.education";
    private static final String EXTRA_OCCUPATION = "gr.extract.hivelive.Services.extra.occupation";
    private static final String EXTRA_PHONE = "gr.extract.hivelive.Services.extra.phone";
    private static final String EXTRA_MOBILE = "gr.extract.hivelive.Services.extra.mobile";
    private static final String EXTRA_CITY = "gr.extract.hivelive.Services.extra.city";

    /*Researches*/
    private static final String EXTRA_RESEARCHID = "gr.extract.hivelive.Services.extra.researchid";


    private HashMap<String, String> userDetails;
    private boolean gotResearchesFromDB = false;

    public NetworkServices() {
        super("NetworkServices");
    }


    public static void startGettingAppData(Context context) {
        Intent intent = new Intent(context, NetworkServices.class);
        intent.setAction(ACTION_GETDATA);
        context.startService(intent);
    }


    public static void startUserLogin(Context context, String email, String password) {
        Intent intent = new Intent(context, NetworkServices.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(EXTRA_EMAIL, email).putExtra(EXTRA_PASSWORD, password);
        context.startService(intent);
    }

    public static void startRemindPassword(Context context, String email) {
        Intent intent = new Intent(context, NetworkServices.class);
        intent.setAction(ACTION_REMIND);
        intent.putExtra(EXTRA_EMAIL, email);
        context.startService(intent);
    }

    public static void startSignUp(Context context, String name, String username, String password, String email,
                                   String confirm, String sex, String birthdate, String educ, String occ,
                                   String phone, String mobile, String city) {
        Intent intent = new Intent(context, NetworkServices.class);
        intent.setAction(ACTION_SIGNUP);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PASSWORD, password);
        intent.putExtra(EXTRA_EDUCATION, educ);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_OCCUPATION, occ);
        intent.putExtra(EXTRA_CONFIRM, confirm);
        intent.putExtra(EXTRA_SEX, sex);
        intent.putExtra(EXTRA_BIRTHDATE, birthdate);
        intent.putExtra(EXTRA_PHONE, phone);
        intent.putExtra(EXTRA_MOBILE, mobile);
        intent.putExtra(EXTRA_CITY, city);
        context.startService(intent);
    }

    public static void startGetResearches(Context con, boolean offline) {
        Intent intent = new Intent(con, NetworkServices.class);
        if (offline) intent.setAction(ACTION_GETRESEARCHES_OFFLINE);
        else intent.setAction(ACTION_GETRESEARCHES);
        con.startService(intent);
    }

    public static void startgetQuestionsForResearch(Context con, String researchID) {
        Intent intent = new Intent(con, NetworkServices.class);
        intent.putExtra(EXTRA_RESEARCHID, researchID);
        intent.setAction(ACTION_GETQUESTIONS);
        con.startService(intent);
    }

    public static void startUploadingAnswers(Context con) {
        Intent intent = new Intent(con, NetworkServices.class);
        intent.setAction(ACTION_UPLOAD_ANSWERS);
        con.startService(intent);
    }

    public static void startUploadingImage(Context con) {
        Intent intent = new Intent(con, NetworkServices.class);
        intent.setAction(ACTION_UPLOAD_PIC);
        con.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GETDATA.equals(action)) {
                handleActionSaveAppData();
            } else if (ACTION_LOGIN.equals(action)) {
                final String email = intent.getStringExtra(EXTRA_EMAIL);
                final String password = intent.getStringExtra(EXTRA_PASSWORD);
                handleActionUserLogin(email, password);
            } else if (ACTION_REMIND.equals(action)) {
                final String email = intent.getStringExtra(EXTRA_EMAIL);
                handleActionRemindPass(email);
            } else if (ACTION_SIGNUP.equals(action)) {
                userDetails = new HashMap<>();

                String name = intent.getStringExtra(EXTRA_NAME);
                String username = intent.getStringExtra(EXTRA_USERNAME);
                String password = intent.getStringExtra(EXTRA_PASSWORD);
                String education = intent.getStringExtra(EXTRA_EDUCATION);
                String email = intent.getStringExtra(EXTRA_EMAIL);
                String occupation = intent.getStringExtra(EXTRA_OCCUPATION);
                String confirm = intent.getStringExtra(EXTRA_CONFIRM);
                String sex = intent.getStringExtra(EXTRA_SEX);
                String birthdate = intent.getStringExtra(EXTRA_BIRTHDATE);
                String phone = intent.getStringExtra(EXTRA_PHONE);
                String mobile = intent.getStringExtra(EXTRA_MOBILE);
                String city = intent.getStringExtra(EXTRA_CITY);
                ArrayList<String> params = new ArrayList<>();
                params.add("name");
                params.add(name);
                userDetails.put("name", name);
                params.add("username");
                params.add(username);
                userDetails.put("username", username);
                params.add("email");
                params.add(email);
                userDetails.put("email", email);
                params.add("password");
                params.add(password);
                userDetails.put("password", password);
                params.add("confirm");
                params.add(confirm);
                userDetails.put("confirm", confirm);
                params.add("sex");
                params.add(sex);
                userDetails.put("sex", sex);
                params.add("dateOfBirth");
                params.add(birthdate);
                userDetails.put("birthdate", birthdate);
                params.add("education");
                params.add(education);
                userDetails.put("education", education);
                params.add("occupation");
                params.add(occupation);
                userDetails.put("occupation", occupation);
                params.add("phone");
                params.add(phone);
                userDetails.put("phone", phone);
                params.add("mobile");
                params.add(mobile);
                userDetails.put("mobile", mobile);
                params.add("city");
                params.add(city);
                userDetails.put("city", city);

                handleActionUserSignUp(params);
            } else if (ACTION_GETRESEARCHES.equals(action)) {
                handleActionGetResearches();
            } else if (ACTION_GETRESEARCHES_OFFLINE.equals(action)) {
                handleActionGetResearchesOffline();
            } else if (ACTION_GETQUESTIONS.equals(action)) {
                String researchid = intent.getStringExtra(EXTRA_RESEARCHID);
                handleActionGetQuestions(researchid);
            } else if (ACTION_UPLOAD_ANSWERS.equals(action)) {
                handleActionUploadAnswers();
            } else if (ACTION_UPLOAD_PIC.equals(action)) {
                handleActionUploadPicture();
            }
        }
    }

    private void handleActionSaveAppData() {
        String finalUrl = UrlFromBase(null, "registrationData");


        Ion.with(getApplicationContext())
                .load(finalUrl)
                .setTimeout(15000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null && e == null) {
                            // Log.e("AppData", result.toString());
                            JsonObject cities = result.get("data").getAsJsonObject().get("cities").getAsJsonObject();
                            JsonObject occupations = result.get("data").getAsJsonObject().get("occupations").getAsJsonObject();
                            JsonObject educations = result.get("data").getAsJsonObject().get("educations").getAsJsonObject();

                            if (!cities.isJsonNull()) saveDataToMaps(1, cities);
                            if (!occupations.isJsonNull()) saveDataToMaps(2, occupations);
                            if (!educations.isJsonNull()) saveDataToMaps(3, educations);
                            EventBus.getDefault().post(new Events.DataSavedEvent());
                        }
                    }
                });
    }


    private void handleActionUserLogin(String email, final String password) {
        String baseUrl = Constants.URL_LOGIN;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baseUrl).append("&view=user").append("&action=login&email=").append(email).append("&password=").append(password);
        String URL = stringBuilder.toString();

        Log.d("Login", "Final url is " + URL);

        Ion.with(getApplicationContext())
                .load(URL)
                .setTimeout(25000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null && e == null) {
                            Log.d("Login", "Result from server is " + result);
                            switch (result.get("success").getAsString()) {
                                case "1":
                                    handleSuccessGettingUser(result.get("user").getAsJsonObject(), result.get("token").isJsonNull() ? "" : result.get("token").getAsString(), password);
                                    break;
                                case "0":
                                    EventBus.getDefault().post(new Events.LoginEvent(1, null, result.get("notifications").getAsJsonObject().get("error").getAsJsonArray().get(0).getAsString()));
                                    break;
                                default:
                            }

                        } else {
                            Log.e("Login", "Login error, null response");
                            EventBus.getDefault().post(new Events.LoginEvent(1, null, null));
                            if (e != null) {
                                Log.e("Login", "Login error " + e.getMessage());
                                Toast.makeText(getApplicationContext(), "Αποτυχία σύνδεσης, παρακαλώ δοκιμάστε αργότερα.", Toast.LENGTH_LONG).show();

                                e.printStackTrace();
                            }else{
                                Toast.makeText(getApplicationContext(), "Παρακαλώ ελέξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }


    private void handleActionRemindPass(String email) {
        String baseUrl = Constants.URL_BASE;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baseUrl).append("&view=user").append("&action=forgot&email=").append(email);
        String URL = stringBuilder.toString();

        Log.d("Remind", "Final url is " + URL);

        Ion.with(getApplicationContext())
                .load(URL)
                .setTimeout(15000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null && e == null) {
                            Log.d("Remind", "Result from server is " + result);
                            switch (result.get("success").getAsString()) {
                                case "1":
                                    EventBus.getDefault().post(new Events.RemindEvent(0, getMessage(true, result)));
                                    break;
                                case "0":
                                    EventBus.getDefault().post(new Events.RemindEvent(1, getMessage(false, result)));
                                    break;
                                default:
                            }

                        } else {
                            Log.e("Remind", "Remind error, null response");
                            EventBus.getDefault().post(new Events.RemindEvent(2, null));
                            if (e != null) {
                                Log.e("Remind", "Remind error " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void handleActionUserSignUp(final ArrayList<String> parameters) {
        String finalUrl = UrlFromBase(parameters, "signup");

        Ion.with(getApplicationContext())
                .load(finalUrl)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        if (result != null && e == null) {
                            Log.e("SignUp", "Result from server is " + result.toString());
                            JsonObject jsRes = new JsonParser().parse(result).getAsJsonObject();
                            switch (jsRes.get("success").getAsString()) {
                                case "1":
                                    createNewUser();
                                    EventBus.getDefault().post(new Events.SignUpEvent(0, getMessage(true, jsRes)));
                                    break;
                                case "0":
                                    EventBus.getDefault().post(new Events.SignUpEvent(1, getMessage(false, jsRes)));
                                    break;
                                default:
                            }
                        } else {
                            Log.e("Signup", "Signup error, null response");
                            // EventBus.getDefault().post(new Events.RemindEvent(2, null));
                            if (e != null) {
                                Log.e("Signup", "Signup error " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void handleActionGetResearches() {
        String URL = Constants.URL_RESEARCHES + Singleton.getInstance(getApplicationContext()).getCurrentUser().getToken();
        Log.e("Researches", "url is " + URL);

        Ion.with(getApplicationContext())
                .load(URL)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (result != null) {
                            Log.d("Researches", "result is " + result);
                            JsonObject resultAsJson = new JsonParser().parse(result).getAsJsonObject();
                            if (resultAsJson.has("researches") && resultAsJson.get("researches").getAsJsonArray().size() > 0) {
                                saveReseaches(resultAsJson.get("researches").getAsJsonArray(), false);
                            } else {
                                Toast.makeText(getApplicationContext(), "This request isn't authorised. Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Παρακαλώ ελέγξτε τη σύνδεσή σας και ξαναπροσπαθήστε.", Toast.LENGTH_SHORT).show();
                            Log.e("Researches", "Result is null");
                        }
                    }
                });
    }

    private void handleActionGetResearchesOffline() {
        String URL = Constants.URL_RESEARCHES_OFFLINE + Singleton.getInstance(getApplicationContext()).getCurrentUser().getToken();
        Log.e("Researches Offline", "url is " + URL);

        Ion.with(getApplicationContext())
                .load(URL)
                .setTimeout(500000)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (result != null) {
                            Log.d("Researches Offline", "result is " + result);
                            JsonObject resultAsJson = new JsonParser().parse(result).getAsJsonObject();
                            if (resultAsJson.has("researches") && resultAsJson.get("researches").getAsJsonArray().size() > 0) {
                                saveReseaches(resultAsJson.get("researches").getAsJsonArray(), true);

                            } else {
                                Toast.makeText(getApplicationContext(), "Something went wrong, please contact the administrator.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Παρακαλώ ελέγξτε τη σύνδεσή σας και ξαναπροσπαθήστε.", Toast.LENGTH_SHORT).show();
                            Log.e("Researches", "Result is null");
                        }
                    }
                });
    }

    private void handleActionGetQuestions(final String resID) {
        String URL = Constants.UrlQuestionsForResearch(resID, Singleton.getInstance(getApplicationContext()).getCurrentUser().getToken());
        Log.e("Researches", "url is " + URL);

        Ion.with(getApplicationContext())
                .load(URL)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (result != null && result.contains("questions")) {
                            Log.d("Researches", "questions result is " + result);
                            JsonObject resultAsJson = new JsonParser().parse(result).getAsJsonObject();
                            if (resultAsJson.get("questions").getAsJsonArray().size() > 0) {
                                saveQuestions(resultAsJson.get("questions").getAsJsonArray(), resID);
                            } else {
                                Toast.makeText(getApplicationContext(), "Δεν υπάρχουν διαθέσιμες ερωτήσεις γι αυτή την έρευνα.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            EventBus.getDefault().post(new Events.QuestionsFound(2, resID));
                            Log.e("Researches", "Result is null");
                        }
                    }
                });
    }

    private void handleActionUploadPicture() {
        ArrayList<Part> picToUpload = Singleton.getInstance(getApplicationContext()).getPicParts();
        String URL = Constants.URL_UPLOAD_PROFILE_PIC + Singleton.getInstance(getApplicationContext()).getCurrentUser().getToken();


        Ion.with(getApplicationContext())
                .load(URL)
                .setTimeout(20000)
                .setLogging("MyLogs", Log.DEBUG)
                .addMultipartParts(picToUpload)
                .asString()

                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        if (result != null) {
                            Log.d("PicUpload", "..result is " + result);
                            JsonObject resultObj = new JsonParser().parse(result).getAsJsonObject();
                            if (resultObj.get("success").getAsInt() == 1) {

                                EventBus.getDefault().post(new Events.PictureUploadEvent(0, resultObj.get("profileImagePath").getAsString()));
                            } else {
                                EventBus.getDefault().post(new Events.PictureUploadEvent(1, null));
                                Log.e("PicUpload", "error :" + resultObj.get("notifications").getAsString());
                            }

                        } else {
                            Log.e("PicUpload", "PicUpload result is null");
                            EventBus.getDefault().post(new Events.PictureUploadEvent(2, null));
                        }
                    }
                });

    }


    private void handleActionUploadAnswers() {
        String URL;
        String token = "";
        boolean isResearcher = getSharedPreferences(Constants.APP, MODE_PRIVATE).getBoolean(Constants.RESEARCHER, false);
        if (Singleton.getInstance(getApplicationContext()).getCurrentUser() != null) {
            token = Singleton.getInstance(getApplicationContext()).getCurrentUser().getToken();
        } else {
            SnappyCalls.open(getApplicationContext());
            User user = SnappyCalls.getUserFromDb();
            SnappyCalls.close();
            if (user != null) {
                token = user.getToken();
            } else {
                Toast.makeText(getApplicationContext(), "You have to login again, to upload the answers", Toast.LENGTH_SHORT).show();
            }
        }
        if (isResearcher) {
            URL = Constants.URLforCapiAnswers(token);
            SnappyCalls.open(getApplicationContext());
            ArrayList<Research> savedResearches = SnappyCalls.getAnsweredResearches(true);
            uploadAnswersForCapi(URL, savedResearches);
            SnappyCalls.close();
        } else {
            URL = Constants.URLforAnswerUploading(Singleton.getInstance(getApplicationContext()).getCurrentUser().getToken());
            uploadAnswersNormal(URL);
        }
    }

    private void uploadAnswersNormal(String URL) {
//        Research researchResults = Singleton.getInstance(getApplicationContext()).getFinalResearchwithResults();
        ArrayList<Research> researchesSaved = new ArrayList<>();
        ArrayList<Part> partsToUpload = new ArrayList<>();

        SnappyCalls.open(getApplicationContext());
        researchesSaved = SnappyCalls.getAnsweredResearches(false);
        gotResearchesFromDB = true;

        if (researchesSaved != null && !researchesSaved.isEmpty()) {
            JsonArray jsonResults = answersToJsonArray(researchesSaved);


            Ion.with(getApplicationContext())
                    .load(URL)
                    .setTimeout(20000)
                    .setLogging("MyLogs", Log.DEBUG)
                    .setJsonArrayBody(jsonResults)
                    .asString()

                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {

                            if (result != null) {
                                Log.d("AnswersUploaded", "answers uploaded.. result is " + result);
                                Log.d("Answerfinal", "answers uploaded.. result is " + result);
                                try {
                                    SnappyCalls.deleteAllResearches(false);
                                } catch (SnappydbException e1) {
                                    e1.printStackTrace();
                                }
                                gotResearchesFromDB = false;
                                SnappyCalls.close();
                            } else {
                                Log.e("AnswersUploaded", "Answers Uploaded result is null");
                                Log.e("Answerfinal", "Answers Uploaded result is null");
//                            MyTaskService.scheduleOneOff(getApplicationContext(), researchID);
                            }
                        }
                    });


//            partsToUpload = createArrayFromResultObj(researchResults);
        } else {
            // partsToUpload = createArrayFromResultObj(researchResults);
        }
    }

    private void uploadAnswersForCapi(String URL, ArrayList<Research> researches) {
//        *[{"user":{"first_name":"", "last_name":"", "email":"", "phone":""},
// "answers":[{"question_id":"", "answer_id":"",... }, {}, {}...]}, {}, {}...]

        String qnrIDKey, questionIDKey, answerIDKey, timeIDKey, valueIDKey, dateKey;
        HashMap<String, Integer> timesToAnswer = Singleton.getInstance(getApplicationContext()).getSecondsToAnswer();
        JSONObject userObj;
        JSONArray answersArray ;
        JSONObject answerObject;
        JSONObject finalObject;
        JsonArray gsonarray = new JsonArray();
        JsonParser jsonParser = new JsonParser();



        for (Research res : researches) {
            answersArray = new JSONArray();
            countAswers(res);
            if (res != null) {
                finalObject = new JSONObject();
                userObj = new JSONObject();
                try {
                    userObj.put("firstName", res.getFirstName());
                    userObj.put("email", res.getEmail());
                    userObj.put("phone", res.getPhone());
                    userObj.put("address", res.getAddress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (Question qAnswered : res.getQuestionsOfResearch()) {
                    if (qAnswered.getAnswered().equals("1")) {
                        for (Answer ansGiven : qAnswered.getAnswersGiven()) {
                            answerObject = new JSONObject();
                            qnrIDKey = "qnrid";
                            questionIDKey = "question_id";
                            answerIDKey = "answer_id";
                            valueIDKey = "value";
                            timeIDKey = "time";
                            dateKey = "date";


                            try {
                                answerObject.put(qnrIDKey, res.getResearchID());
                                answerObject.put(questionIDKey, qAnswered.getQuestionId());
                                answerObject.put(answerIDKey, ansGiven.getAnswer_id());
                                answerObject.put(valueIDKey, ansGiven.getValue());
                                answerObject.put(timeIDKey, qAnswered.getTime());
                                answerObject.put(dateKey, res.getTimeOfCompletion());
//                                answerObject.put(timeIDKey, (timesToAnswer.get(qAnswered.getQuestionId()) == null ? "0" : String.valueOf(timesToAnswer.get(qAnswered.getQuestionId()))));
//                                answerObject.put(timeIDKey, String.valueOf(finalTimeToAnswer));
                                answersArray.put(answerObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //   previousQuestion = qAnswered.getQuestionId();
                }//endfor of questions

                try {
                    finalObject.put("user", (JSONObject) userObj);
                    finalObject.put("answers", (JSONArray) answersArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                gsonarray.add((JsonObject) jsonParser.parse(finalObject.toString()));
            }
        }







        Log.e("QUESTIONSCAPI", "final object is " + gsonarray.toString());

        Ion.with(getApplicationContext())
                .load(URL)
                .setTimeout(20000)
                .setLogging("MyLogs", Log.DEBUG)
                .setJsonArrayBody(gsonarray)
                .asString()

                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        if (result != null) {
                            Log.d("AnswersUploaded", "answers uploaded.. result is " + result);
                            Log.d("Answerfinal", "answers uploaded.. result is " + result);

                            JsonObject jsResult = null;
                            try {
                                jsResult = new JsonParser().parse(result).getAsJsonObject();
                            } catch (JsonSyntaxException ee) {
                                Log.e("AnswersUploaded", "exception caught " + ee.getMessage().toString());
                                Toast.makeText(getApplicationContext(), "Something went wrong with saving the answers. Please contact the database admin", Toast.LENGTH_LONG).show();
                            }
                            if (jsResult != null && jsResult.has("success") && jsResult.get("success").getAsInt() == 1) { /* success */

                                try {
                                    SnappyCalls.open(getApplicationContext());
                                    SnappyCalls.deleteAllResearches(true);
                                    Log.e("Pending researches","result : " + SnappyCalls.getAnsweredResearches(true).size());
                                    SnappyCalls.close();
                                } catch (SnappydbException e1) {
                                    e1.printStackTrace();
                                }


                                gotResearchesFromDB = false;
                            } else if (jsResult != null && jsResult.has("success") && jsResult.get("success").getAsInt() == 0) {
                                Toast.makeText(getApplicationContext(), jsResult.get("notifications").getAsJsonObject()
                                        .get("message").getAsJsonArray()
                                        .get(0).getAsString(), Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Error returned from server. Please contact the admin.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("AnswersUploaded", "Answers Uploaded result is null or has an error");
                        }
                    }
                });


    }

    private void countAswers(Research res){
        int count = 0;
        for (Question q : res.getQuestionsOfResearch()){
            if (q.getAnswered().equals("1")){
                for (Answer ans : q.answersGiven){
                    count++;
                }
            }
        }
        Log.e("QA", "Count of answers is "+count);
    }

    private JsonArray answersToJsonArray(ArrayList<Research> researches) {
        String qnrIDKey, questionIDKey, answerIDKey, timeIDKey, valueIDKey;
        int finalTimeToAnswer = 0;
        int counter = 0;
        HashMap<String, Integer> timesToAnswer = Singleton.getInstance(getApplicationContext()).getSecondsToAnswer();

        JSONArray answersArray = new JSONArray();
        JSONObject answerObject;

        for (Research res : researches) {
            if (res != null) {
                for (Question qAnswered : res.getQuestionsOfResearch()) {
                    if (qAnswered.getAnswered().equals("1")) {
                        for (Answer ansGiven : qAnswered.getAnswersGiven()) {
                            answerObject = new JSONObject();
                            qnrIDKey = "qnrid";
                            questionIDKey = "question_id";
                            answerIDKey = "answer_id";
                            valueIDKey = "value";
                           timeIDKey = "time";
//                            finalTimeToAnswer = timesToAnswer.get(qAnswered.getQuestionId());


                            try {
                                answerObject.put(qnrIDKey, res.getResearchID());
                                answerObject.put(questionIDKey, qAnswered.getQuestionId());
                                answerObject.put(answerIDKey, ansGiven.getAnswer_id());
                                answerObject.put(valueIDKey, ansGiven.getValue());
                                answerObject.put(timeIDKey, qAnswered.getTime());
                                answerObject.put(timeIDKey, (timesToAnswer.get(qAnswered.getQuestionId()) == null ? "0" : String.valueOf(timesToAnswer.get(qAnswered.getQuestionId()))));
                                answersArray.put(answerObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            counter++;
                        }
                    }
                    //   previousQuestion = qAnswered.getQuestionId();
                }//endfor of questions
            }
        }

        JSONObject finalObject = new JSONObject();
        try {
            finalObject.put("answers", (JSONArray) answersArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArray gsonarray;
        JsonParser jsonParser = new JsonParser();
        gsonarray = (JsonArray) jsonParser.parse(answersArray.toString()).getAsJsonArray();

        Log.e("QUESTIONSNORMAL", "final object is " + gsonarray.toString());
        return gsonarray;
    }


    /************************************
     * SUCCESS
     ****************************************/

    private ArrayList<Part> createArrayFromResultObj(Research research) {
        StringPart part;
        int counter = 0;
        int finalTimeToAnswer = 0;
        int prevtime = 0;
        int nexttime = 0;
        String questionIDKey, answerIDKey, valueIDKey, timeIDKey, previousQuestion = "";
        ArrayList<Part> listOfParts = new ArrayList<>();


        JsonArray finalResultsArray = new JsonArray();
        JsonObject resultElement;
        HashMap<String, Integer> timesToAnswer = Singleton.getInstance(getApplicationContext()).getSecondsToAnswer();
        if (research != null) {
            for (Question qAnswered : research.getQuestionsOfResearch()) {
                if (qAnswered.getAnswered().equals("1")) {
                    for (Answer ansGiven : qAnswered.getAnswersGiven()) {
                        questionIDKey = "answers[" + counter + "][question_id]";
                        part = new StringPart(questionIDKey, qAnswered.getQuestionId());
                        Log.d("ErrorDebugServer", "key :" + questionIDKey + ", value :" + qAnswered.getQuestionId());
                        listOfParts.add(part);
                        answerIDKey = "answers[" + counter + "][answer_id]";
                        part = new StringPart(answerIDKey, ansGiven.getAnswer_id());
                        Log.d("ErrorDebugServer", "key :" + answerIDKey + ", value :" + ansGiven.getAnswer_id());
                        listOfParts.add(part);
                        valueIDKey = "answers[" + counter + "][value]";
                        part = new StringPart(valueIDKey, ansGiven.getValue());
                        Log.d("ErrorDebugServer", "key :" + valueIDKey + ", value :" + ansGiven.getValue());
                        listOfParts.add(part);
                        timeIDKey = "answers[" + counter + "][time]";
                        Log.d("ErrorDebugServer1", "before : finalTimeToAnswer is " + timesToAnswer.get(qAnswered.getQuestionId()));
//                        if (counter > 0){
//                            prevtime =  timesToAnswer.get(previousQuestion);
//                            Log.d("ErrorDebugServer1", "prevtime :"+prevtime);
//                            nexttime = timesToAnswer.get(qAnswered.getQuestionId());
//                            Log.d("ErrorDebugServer1", "nexttime :"+nexttime);
//                            finalTimeToAnswer = nexttime - prevtime;
//                            Log.d("ErrorDebugServer1", "final is :"+finalTimeToAnswer);
//                        }else{
                        finalTimeToAnswer = timesToAnswer.get(qAnswered.getQuestionId());
//                        }

                        part = new StringPart(timeIDKey, String.valueOf(finalTimeToAnswer));
                        Log.d("ErrorDebugServer", "key :" + timeIDKey + ", value :" + String.valueOf(finalTimeToAnswer));

                        listOfParts.add(part);

                        counter++;
//                        resultElement = new JsonObject();
//                        resultElement.addProperty("question_id", qAnswered.getQuestionId());
//                        resultElement.addProperty("answer_id", ansGiven.getAnswer_id());
//                        resultElement.addProperty("value", ansGiven.getValue());
//                        resultElement.addProperty("time", timesToAnswer.get(qAnswered.getQuestionId()));
//                        finalResultsArray.add(resultElement);
                    }
                }
                previousQuestion = qAnswered.getQuestionId();
            }
        }

        Log.d("ErrorDebugServer", "final array size " + counter);
        return listOfParts;
    }

    private ArrayList<Part> createArrayFromSavedResearches(ArrayList<Research> array) {
        StringPart part;
        int counter = 0;
        int finalTimeToAnswer = 0;
        int prevtime = 0;
        int nexttime = 0;
        String questionIDKey, answerIDKey, valueIDKey, timeIDKey, previousQuestion = "";
        ArrayList<Part> listOfParts = new ArrayList<>();
        HashMap<String, Integer> timesToAnswer = Singleton.getInstance(getApplicationContext()).getSecondsToAnswer();


        for (Research res : array) {
            for (Question qAnswered : res.getQuestionsOfResearch()) {
                if (qAnswered.getAnswered().equals("1")) {
                    for (Answer ansGiven : qAnswered.getAnswersGiven()) {
                        questionIDKey = "answers[" + counter + "][question_id]";
                        part = new StringPart(questionIDKey, qAnswered.getQuestionId());
                        Log.d("ErrorDebugServer", "key :" + questionIDKey + ", value :" + qAnswered.getQuestionId());
                        listOfParts.add(part);
                        answerIDKey = "answers[" + counter + "][answer_id]";
                        part = new StringPart(answerIDKey, ansGiven.getAnswer_id());
                        Log.d("ErrorDebugServer", "key :" + answerIDKey + ", value :" + ansGiven.getAnswer_id());
                        listOfParts.add(part);
                        valueIDKey = "answers[" + counter + "][value]";
                        part = new StringPart(valueIDKey, ansGiven.getValue());
                        Log.d("ErrorDebugServer", "key :" + valueIDKey + ", value :" + ansGiven.getValue());
                        listOfParts.add(part);
                        timeIDKey = "answers[" + counter + "][time]";
                        Log.d("ErrorDebugServer1", "before : finalTimeToAnswer is " + timesToAnswer.get(qAnswered.getQuestionId()));
//                        if (counter > 0){
//                            prevtime =  timesToAnswer.get(previousQuestion);
//                            Log.d("ErrorDebugServer1", "prevtime :"+prevtime);
//                            nexttime = timesToAnswer.get(qAnswered.getQuestionId());
//                            Log.d("ErrorDebugServer1", "nexttime :"+nexttime);
//                            finalTimeToAnswer = nexttime - prevtime;
//                            Log.d("ErrorDebugServer1", "final is :"+finalTimeToAnswer);
//                        }else{
                        finalTimeToAnswer = timesToAnswer.get(qAnswered.getQuestionId());
//                        }

                        part = new StringPart(timeIDKey, String.valueOf(finalTimeToAnswer));
                        Log.d("ErrorDebugServer", "key :" + timeIDKey + ", value :" + String.valueOf(finalTimeToAnswer));

                        listOfParts.add(part);

                        counter++;
                    }
                }
                previousQuestion = qAnswered.getQuestionId();
            }
        }
        Log.d("ErrorDebugServer", "final array size " + counter);
        return listOfParts;
    }

    private void saveReseaches(JsonArray researches, boolean offline) {
        JsonObject researchItem;
        Research res;
        HashMap<String, Research> researchesArray = new HashMap<>();
        ArrayList<Research> researchesArrayList = new ArrayList<>();
        //  Log.d("Researches", "JsonArray size of researches is "+ researches.size());

        for (JsonElement item : researches) {
            researchItem = item.getAsJsonObject();
            res = new Research();
            res.setResearchID(attributeHandler(researchItem.get("qnrid")));
            res.setTitle(attributeHandler(researchItem.get("title")));
            res.setFromDate(attributeHandler(researchItem.get("fromdate")));
            res.setToDate(attributeHandler(researchItem.get("todate")));
            res.setSex(attributeHandler(researchItem.get("sex")));
            res.setFromAge(attributeHandler(researchItem.get("fromage")));
            res.setToAge(attributeHandler(researchItem.get("toage")));
            res.setAgeGroup(attributeHandler(researchItem.get("agegroup")));
            res.setUrban(attributeHandler(researchItem.get("urban")));
            res.setQuota(attributeHandler(researchItem.get("quota")));
            res.setEducation(attributeHandler(researchItem.get("education")));
            res.setOccupation(attributeHandler(researchItem.get("occupation")));
            res.setRv1(attributeHandler(researchItem.get("rv1")));
            res.setRv2(attributeHandler(researchItem.get("rv2")));
            res.setDescription(attributeHandler(researchItem.get("descr")));
            res.setType(attributeHandler(researchItem.get("type")));


            if (offline) {
                JsonArray questions = researchItem.get("questions").getAsJsonArray();
                if (questions != null) res = saveQuestionsOffline(questions, res);
            }

            researchesArray.put(res.getResearchID(), res);
            researchesArrayList.add(res);


        }

        if (offline) {
            SnappyCalls.open(getApplicationContext());
            SnappyCalls.saveOfflineResearches(researchesArrayList);
            SnappyCalls.close();
        }
        Singleton.getInstance(getApplicationContext()).setResearchesArray(researchesArray);


        //else Singleton.getInstance(getApplicationContext()).setResearchesOffline(researchesArray);
        Log.d("Researches", "hashmap size of researches is " + researchesArray.size());


        EventBus.getDefault().post(new Events.ResearchesFoundEvent(0));
    }

    private String attributeHandler(JsonElement attr) {
        return ((attr == null || attr.isJsonNull()) ? "0" : attr.getAsString());
    }


    private void saveQuestions(JsonArray questionsArray, String researchID) {
        JsonObject questionobj;
        String question, sortorder, questionId, answered, Type, MaxAnswersRequired, DisplayType, Template;
        boolean israndom;
        JsonArray options;
        HashMap<String, Question> questionsArraySaved = new HashMap<>();
        HashMap<String, String> SortIndexesToIds = new HashMap<>();
        ArrayList<Question> questionsOfResearch = new ArrayList<>();
        Question newQuestion;
        String[] questionIDSsorted, sortIdexesArray;
        int counter = 0;
        String imgmatchingQuesID = "0";
        boolean foundImgDND = false;

        for (JsonElement js : questionsArray) {
            questionobj = js.getAsJsonObject();
            answered = (questionobj.get("answered") == null ? "" : questionobj.get("answered").getAsString());

            if (answered.equals("0")) { /* This means that the question isn't already answered */

                newQuestion = new Question();
                question = (questionobj.get("Question") == null ? "" : questionobj.get("Question").getAsString());
                sortorder = (questionobj.get("sortorder") == null ? "" : questionobj.get("sortorder").getAsString());
                israndom = (questionobj.get("israndom") == null ? false : (questionobj.get("israndom").getAsInt() == 0 ? false : true));
                questionId = (questionobj.get("question_id") == null ? "" : questionobj.get("question_id").getAsString());
                Type = (questionobj.get("Type") == null ? "" : questionobj.get("Type").getAsString());
                MaxAnswersRequired = (questionobj.get("MaxAnswersRequired") == null ? "" : questionobj.get("MaxAnswersRequired").getAsString());
                DisplayType = (questionobj.get("DisplayType") == null ? "" : questionobj.get("DisplayType").getAsString());
                Template = (questionobj.get("Template") == null ? "" : questionobj.get("Template").getAsString());
                options = questionobj.get("options") == null ? new JsonArray() : questionobj.get("options").getAsJsonArray();

                Log.d("Questions", "ID: " + questionId);
                Log.d("Template", Template);

                newQuestion.setQuestion(question.trim());
                newQuestion.setSortorder(sortorder);
                newQuestion.setIsrandom(israndom);
                newQuestion.setQuestionId(questionId);
                newQuestion.setAnswered(answered);
                newQuestion.setType(Type);
                newQuestion.setMaxAnswersRequired(MaxAnswersRequired);
                newQuestion.setDisplayType(DisplayType);
                newQuestion.setOptions(options);
                newQuestion.setTemplate(Template);
//                if (newQuestion.getTemplate().equals("matchimage")) {
//                    Log.e("found", newQuestion.getQuestion());
//                    foundImgDND = true;
//                    imgmatchingQuesID = newQuestion.getQuestionId();
//                }

            /*
            Save answers for this question
             */
                newQuestion.initializeOptions();


                questionsArraySaved.put(questionId, newQuestion);
                questionsOfResearch.add(newQuestion);
                SortIndexesToIds.put(newQuestion.getSortorder(), newQuestion.getQuestionId());
            } /* if the question is answered, we won't save it, so we move to the next one */
        }

        questionIDSsorted = new String[questionsArraySaved.size()];
        sortIdexesArray = new String[questionsArraySaved.size()];
        for (String qid : SortIndexesToIds.keySet()) {
            sortIdexesArray[counter] = qid;
            counter++;
        }

        if (counter == 0) { /* all the questions were answered */
            Log.d("Questions", "all the questions are answered for this research!");
            EventBus.getDefault().post(new Events.QuestionsFound(1, researchID));
        } else {
            Log.d("Questions", "some questions aren't answered for this research!");
            if (sortIdexesArray != null && sortIdexesArray.length > 0) {
//                for (int ii = 0; ii < questionIDSsorted.length; ii++) {
//                    Log.d("Questions", "ID: " + questionIDSsorted[ii] + " question: ");// + questionsArraySaved.get(questionIDSsorted[ii]).getQuestion());
//                }
                Arrays.sort(sortIdexesArray);

                for (int j=0; j<sortIdexesArray.length; j++){
                    questionIDSsorted[j] = SortIndexesToIds.get(sortIdexesArray[j]);
                }


//                if (foundImgDND)
//                    questionIDSsorted[0] = imgmatchingQuesID;

            } else Log.d("Questions", "questionIDSsorted is null or empty!");

            Singleton.getInstance(getApplicationContext()).getResearchesArray().get(researchID).setQuestionIdsSorted(questionIDSsorted);
            Singleton.getInstance(getApplicationContext()).getResearchesArray().get(researchID).setQuestionsForResearch(questionsArraySaved);
            Singleton.getInstance(getApplicationContext()).getResearchesArray().get(researchID).setQuestionsOfResearch(questionsOfResearch);
            EventBus.getDefault().post(new Events.QuestionsFound(0, researchID));
            //   Toast.makeText(getApplicationContext(), "total questions received are "+counter, Toast.LENGTH_SHORT).show();
        }

    }

    private Research saveQuestionsOffline(JsonArray questionsArray, Research res) {
        JsonObject questionobj;
        String question, sortorder, questionId, answered, Type, MaxAnswersRequired, DisplayType;
        boolean israndom;
        JsonArray options;

        HashMap<String, Question> questionsArraySaved = new HashMap<>();
        ArrayList<Question> quesOfResearch = new ArrayList<>();
        Question newQuestion;
        String[] questionIDSsorted = new String[questionsArray.size()];
        int counter = 0;

        for (JsonElement js : questionsArray) {
            newQuestion = new Question();
            questionobj = js.getAsJsonObject();
            question = (questionobj.get("Question") == null ? "" : questionobj.get("Question").getAsString());
            sortorder = (questionobj.get("sortorder") == null ? "" : questionobj.get("sortorder").getAsString());
            israndom = (questionobj.get("israndom") == null ? false : (questionobj.get("israndom").getAsInt() == 0 ? false : true));
            questionId = (questionobj.get("question_id") == null ? "" : questionobj.get("question_id").getAsString());
            answered = (questionobj.get("answered") == null ? "" : questionobj.get("answered").getAsString());
            Type = (questionobj.get("Type") == null ? "" : questionobj.get("Type").getAsString());
            MaxAnswersRequired = (questionobj.get("MaxAnswersRequired") == null ? "" : questionobj.get("MaxAnswersRequired").getAsString());
            DisplayType = (questionobj.get("DisplayType") == null ? "" : questionobj.get("DisplayType").getAsString());

            if (questionobj.has("options"))
                options = questionobj.get("options").getAsJsonArray();
            else options = questionobj.get("stories").getAsJsonArray();

            newQuestion.setQuestion(question.trim());
            newQuestion.setSortorder(sortorder);
            newQuestion.setIsrandom(israndom);
            newQuestion.setQuestionId(questionId);
            newQuestion.setAnswered(answered);
            newQuestion.setType(Type);
            newQuestion.setMaxAnswersRequired(MaxAnswersRequired);
            newQuestion.setDisplayType(DisplayType);
            newQuestion.setOptions(options);

            /*
            Save answers for this question
             */
            newQuestion.initializeOptions();
            quesOfResearch.add(newQuestion);

            questionsArraySaved.put(questionId, newQuestion);


            if (answered.equals("0")) questionIDSsorted[counter] = questionId;
            counter++;
        }

        res.setQuestionsOfResearch(quesOfResearch);
        res.setQuestionsForResearch(questionsArraySaved);
        res.setQuestionIdsSorted(questionIDSsorted);
        return res;
    }


//    public ArrayList<Answer> initializeOptions(JsonArray options, Question q){
//        JsonObject optionsobj;
//        String sortIndex;//null
//        String answer_id, Text, value, NextQuestion, Imageurl, Groupid, answers_given;
//        Answer newAnswer;
//        ArrayList<Answer> answersArray = new ArrayList<>();
//        HashMap<String, Answer> answerHashMap = new HashMap<>();
//        boolean isColumn;
//
//
//        for (JsonElement js2 : options){
//            optionsobj = js2.getAsJsonObject();
//            newAnswer = new Answer();
//
//            sortIndex = (optionsobj.get("sortIndex").isJsonNull() ? "":optionsobj.get("sortIndex").getAsString());
//            answer_id = (optionsobj.get("answer_id") == null ? "": optionsobj.get("answer_id").getAsString());
//            Text = (optionsobj.get("Text") == null ? "": optionsobj.get("Text").getAsString());
//            value = (optionsobj.get("value") == null ? "": optionsobj.get("value").getAsString());
//            NextQuestion = (optionsobj.get("NextQuestion") == null ? "": optionsobj.get("NextQuestion").getAsString());
//            Imageurl = (optionsobj.get("Imageurl") == null ? "": "http://hivelive.gr/HiveLive/"+optionsobj.get("Imageurl").getAsString());
//            Groupid = (optionsobj.get("Groupid") == null ? "": optionsobj.get("Groupid").getAsString());
//            answers_given = (optionsobj.get("answers_given") == null ? "": optionsobj.get("answers_given").getAsString());
//            isColumn = (optionsobj.has("isColumn") ?
//                    ((optionsobj).get("isColumn") == null ? false : ((optionsobj).get("isColumn").getAsInt() == 1 ? true : false)) : false);
////            Log.d("Answers", "answer found ->"+Text);
//
//            newAnswer.setSortIndex(sortIndex);
//            newAnswer.setAnswer_id(answer_id);
//            newAnswer.setText(Text);
//            newAnswer.setValue(value);
//            newAnswer.setNextQuestion(NextQuestion);
//            newAnswer.setImageurl(Imageurl);
//            newAnswer.setGroupid(Groupid);
//            newAnswer.setAnswers_given(answers_given);
//            newAnswer.setColumn(isColumn);
//
//
//            answersArray.add(newAnswer);
//
//            answerHashMap.put(newAnswer.getAnswer_id(), newAnswer);
//            //Log.d("Answers","!!!!!!!!!"+ newAnswer.getSortIndex());
//        }
//
//
//        if (!q.getIsrandom()) {
//            Collections.sort(answersArray, new Comparator<Answer>() {
//                @Override
//                public int compare(Answer qID1, Answer qID2) {
//                    return qID1.getSortIndex().compareTo(qID2.getSortIndex());
//                }
//            });
//        }
//        return answersArray;
//    }


    private String getMessage(boolean success, JsonObject js) {
        String message = "";
        boolean bothMessages = false;
        if (success) {
            if (js.get("notifications").getAsJsonObject().get("message").getAsJsonArray().size() > 0) {
                bothMessages = true;
                message = js.get("notifications").getAsJsonObject().get("message").getAsJsonArray().get(0).getAsString();
            }
            if (js.get("notifications").getAsJsonObject().get("success").getAsJsonArray().size() > 0) {
                if (bothMessages) message += "\n";
                message += js.get("notifications").getAsJsonObject().get("success").getAsJsonArray().get(0).getAsString();
            }
        } else {
            if (js.get("notifications").getAsJsonObject().get("error").getAsJsonArray().size() > 0) {
                bothMessages = true;
                message = js.get("notifications").getAsJsonObject().get("error").getAsJsonArray().get(0).getAsString();
            }
            if (js.get("notifications").getAsJsonObject().get("warning").getAsJsonArray().size() > 0) {
                if (bothMessages) message += "\n";
                message += js.get("notifications").getAsJsonObject().get("warning").getAsJsonArray().get(0).getAsString();
            }
        }
        return message;
    }


    private void createNewUser() {
        User hiveLiveUser = new User();
        hiveLiveUser.setFullname(userDetails.get("name"));
        hiveLiveUser.setUsername(userDetails.get("username"));
        hiveLiveUser.setPassword(userDetails.get("password"));
        hiveLiveUser.setEmail(userDetails.get("email"));
        hiveLiveUser.setBirthdate(userDetails.get("birthdate"));
        hiveLiveUser.setCity(userDetails.get("city"));
        hiveLiveUser.setEducation(userDetails.get("education"));
        hiveLiveUser.setOccupation(userDetails.get("occupation"));
        hiveLiveUser.setMphoneNo(userDetails.get("mobile"));
        hiveLiveUser.setPhoneNo(userDetails.get("phone"));
        hiveLiveUser.setSex(userDetails.get("sex"));

        Singleton.getInstance(getApplicationContext()).setCurrentUser(hiveLiveUser);
    }

    private void saveDataToMaps(int option, JsonObject array) {
        int valuesNumber = 0;
        JsonArray values = array.get("values").getAsJsonArray(), options = array.get("options").getAsJsonArray();
        ArrayList<String> valuesTotal = new ArrayList<String>();
        HashMap<String, String> codesToValues = new HashMap<String, String>();
        for (JsonElement val : values) {
            valuesTotal.add(val.getAsString());
        }

        for (JsonElement city : options) {

            if (valuesNumber < valuesTotal.size()) {
                codesToValues.put(valuesTotal.get(valuesNumber), city.getAsString());
            }
            valuesNumber++;
        }

        //   Log.e("AppData", "codes size is " + valuesTotal.size() + " and cities size is " + codesToValues.size());


        switch (option) {
            case 1:
                /*cities*/
                Singleton.getInstance(getApplicationContext()).setCitiesMap(valuesTotal, codesToValues);
                break;
            case 2:
                Singleton.getInstance(getApplicationContext()).setOccupationMap(valuesTotal, codesToValues);
                break;
            case 3:
                Singleton.getInstance(getApplicationContext()).setEducationMap(valuesTotal, codesToValues);
                break;
            default:
                /* DO NOTHING */
        }
    }

    private void handleSuccessGettingUser(JsonObject userObj, String token, String password) {
        User user = new User();
        JsonElement field;
        field = userObj.get("ID");
        if (validateField(field)) user.setUserid(field.getAsString());
        field = userObj.get("username");
        if (validateField(field)) user.setUsername(field.getAsString());
        field = userObj.get("email");
        if (validateField(field)) user.setEmail(field.getAsString());
        user.setPassword(password);
        field = userObj.get("sex");
        if (validateField(field)) user.setSex(field.getAsString());
        field = userObj.get("groupid");
        if (validateField(field)) user.setGroupid(Integer.valueOf(field.getAsString()));
        field = userObj.get("fullname");
        if (validateField(field)) user.setFullname(field.getAsString());
        field = userObj.get("address");
        if (validateField(field)) user.setAddress(field.getAsString());
        field = userObj.get("dateofbirth");
        if (validateField(field)) user.setBirthdate(field.getAsString());
        field = userObj.get("userphoto");
        if (validateField(field)) user.setUserphoto(field.getAsString());
        field = userObj.get("zipcode");
        if (validateField(field)) user.setZipcode(field.getAsString());
        field = userObj.get("phoneno");
        if (validateField(field)) user.setPhoneNo(field.getAsString());
        field = userObj.get("mphoneno");
        if (validateField(field)) user.setMphoneNo(field.getAsString());
        field = userObj.get("active");
        if (validateField(field)) user.setActive(field.getAsInt());
        field = userObj.get("city");
        if (validateField(field)) user.setCity(field.getAsString());
        field = userObj.get("country");
        if (validateField(field)) user.setCountry(field.getAsString());
        field = userObj.get("education");
        if (validateField(field)) user.setEducation(field.getAsString());
        field = userObj.get("occupation");
        if (validateField(field)) user.setOccupation(field.getAsString());
//        field = userObj.get("registerdate");
//        if (validateField(field)) user.setRegisterdate(new Date(field.getAsString()));
        field = userObj.get("groupid");
        if (validateField(field)) user.setGroupid(field.getAsInt());
        if (token != null) user.setToken(token);


        EventBus.getDefault().post(new Events.LoginEvent(0, user, ""));
    }


    private boolean validateField(JsonElement jsonField) {
        if (jsonField.isJsonNull() || jsonField == null) return false;
        return true;
    }

    private String UrlFromBase(ArrayList<String> parameters, String action) {
        String finalUrl = "";

        int counter = 2;
        String baseUrl = Constants.URL_BASE;
        StringBuilder builder = new StringBuilder();
        builder.append("&view=user").append("&action=").append(action);

        if (parameters != null) {
            for (String par : parameters) {
                if (par != null) {
                    par = par.replaceAll("\\s+", "%20");
                }
                if (counter % 2 == 0) {
                    builder.append("&").append(par).append("=");
                } else {
                    builder.append(par);
                }
                counter++;
            }
        }
        finalUrl = builder.toString();

        finalUrl = baseUrl + finalUrl;
        Log.e("SignUp", "url from builder is " + finalUrl);
        return finalUrl;
    }


}
