package gr.extract.hivelive.Singletons;

import android.content.Context;

import com.koushikdutta.async.http.body.Part;

import java.util.ArrayList;
import java.util.HashMap;

import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Research;
import gr.extract.hivelive.hiveUtilities.User;

public class Singleton {
    private static Singleton mInstance;
    private Research finalResearchwithResults;
    private ArrayList<String> cityCodes = new ArrayList<>();
    private ArrayList<String> eduCodes = new ArrayList<>();
    private ArrayList<String> occuCodes = new ArrayList<>();
    private ArrayList<Part> picParts = new ArrayList<>();
    private ArrayList<String> researchesAnswered = new ArrayList<>(); /*this session*/
    private HashMap<String, String> codesToCities = new HashMap<>();
    private HashMap<String, String> codesToEducation = new HashMap<>();
    private HashMap<String, String> codesToOccupation = new HashMap<>();
    private HashMap<String, Research> researchesArray = new HashMap<>();
    private HashMap<String, Integer> secondsToAnswer = new HashMap<>(); /*answer id to seconds answered */
    private HashMap<String, String> questionsToAnswers = new HashMap<>(); /*question id to answeres id */

    private User currentUser;


    /*
    Offline mode
     */

    private boolean isCapi = false;

    private HashMap<String, Question> IDsToQuestions = new HashMap<>();


    public static Singleton getInstance(Context con) {
        if (mInstance == null) {
            mInstance = new Singleton();
        }
        return mInstance;
    }

    private Singleton() {
    }

    public void resetSingleton() {
//        mInstance = null;
//        cityCodes = new ArrayList<>();
//        occuCodes = new ArrayList<>();
//        eduCodes = new ArrayList<>();
//        codesToCities = new HashMap<>();
//        codesToEducation = new HashMap<>();
//        codesToOccupation = new HashMap<>();
        currentUser = null;


        finalResearchwithResults = null;
        researchesAnswered = new ArrayList<>();
        researchesArray = new HashMap<>();
        secondsToAnswer = new HashMap<>(); /*answer id to seconds answered */
        questionsToAnswers = new HashMap<>(); /*question id to answeres id */
        currentUser = null;
        isCapi = false;
        IDsToQuestions = new HashMap<>();
    }

    public ArrayList<String> getCityCodes() {
        return cityCodes;
    }

    public HashMap<String, String> getCodesToCities() {
        return codesToCities;
    }

    public HashMap<String, String> getCodesToEducation() {
        return codesToEducation;
    }

    public HashMap<String, String> getCodesToOccupation() {
        return codesToOccupation;
    }

    public void setCityCodes(ArrayList<String> cityCodes) {

        this.cityCodes = cityCodes;
    }

    public ArrayList<String> getEduCodes() {
        return eduCodes;
    }

    public void setEduCodes(ArrayList<String> eduCodes) {
        this.eduCodes = eduCodes;
    }

    public ArrayList<String> getOccuCodes() {
        return occuCodes;
    }

    public void setOccuCodes(ArrayList<String> occuCodes) {
        this.occuCodes = occuCodes;
    }

    public void setCitiesMap(ArrayList<String> cityCodes, HashMap<String, String> codesToCities) {
        this.cityCodes = cityCodes;
        this.codesToCities = codesToCities;
    }

    public void setEducationMap(ArrayList<String> codes, HashMap<String, String> codesMap) {
        this.eduCodes = codes;
        this.codesToEducation = codesMap;
    }

    public void setOccupationMap(ArrayList<String> codes, HashMap<String, String> codesMap) {
        this.occuCodes = codes;
        this.codesToOccupation = codesMap;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public HashMap<String, Research> getResearchesArray() {
        return researchesArray;
    }

    public void setResearchesArray(HashMap<String, Research> researchesArray) {
        this.researchesArray = researchesArray;
    }


    public HashMap<String, Question> getIDsToQuestions() {
        return IDsToQuestions;
    }

    public void setIDsToQuestions(HashMap<String, Question> IDsToQuestions) {
        this.IDsToQuestions = IDsToQuestions;
    }

    public void setSecondsToAnswer(HashMap<String, Integer> secondsToAnswer) {
        this.secondsToAnswer = secondsToAnswer;
    }

    public HashMap<String, Integer> getSecondsToAnswer(){
        return this.secondsToAnswer;
    }

    public HashMap<String, String> getQuestionsToAnswers() {
        return questionsToAnswers;
    }

    public void setQuestionsToAnswers(HashMap<String, String> questionsToAnswers) {
        this.questionsToAnswers = questionsToAnswers;
    }

    public Research getFinalResearchwithResults() {
        return finalResearchwithResults;
    }

    public void setFinalResearchwithResults(Research finalResearchwithResults) {
        this.finalResearchwithResults = finalResearchwithResults;
    }

    public ArrayList<String> getResearchesAnswered() {
        return researchesAnswered;
    }

    public void setResearchesAnswered(ArrayList<String> researchesAnswered) {
        this.researchesAnswered = researchesAnswered;
    }

    public boolean isCapi() {
        return isCapi;
    }

    public void setCapi(boolean capi) {
        isCapi = capi;
    }

    public ArrayList<Part> getPicParts() {
        return picParts;
    }

    public void setPicParts(ArrayList<Part> picParts) {
        this.picParts = picParts;
    }
}
