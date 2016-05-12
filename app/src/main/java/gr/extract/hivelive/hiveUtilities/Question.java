package gr.extract.hivelive.hiveUtilities;

import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

@com.bluelinelabs.logansquare.annotation.JsonObject
public class Question {

    @JsonField(name = "questionId")
    public String questionId="";
    @JsonField(name = "answered")
    public String answered="";
    @JsonField(name = "sortorder")
    private String sortorder="";
    @JsonField(name = "question")
    private String question="";
    @JsonField(name = "Type")
    private String Type="";
    @JsonField(name = "MaxAnswersRequired")
    private String MaxAnswersRequired="";
    @JsonField(name = "DisplayType")
    private String DisplayType="";
    @JsonField(name = "Template")
    private String Template="";
    @JsonField(name = "time")
    private int time=0;
    @JsonField(name = "israndom")
    private boolean israndom = false;
    private JsonArray options= new JsonArray();
    @JsonField(name = "answersArray")
    private ArrayList<Answer> answersArray = new ArrayList<>();

    @JsonField(name = "answersGiven")
    public ArrayList<Answer> answersGiven = new ArrayList<>();
    private HashMap<String, Answer> answersMap = new HashMap<>();


    public Question(){
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSortorder() {
        return sortorder;
    }

    public void setSortorder(String sortorder) {
        this.sortorder = sortorder;
    }

    public boolean getIsrandom() {
        return israndom;
    }

    public void setIsrandom(boolean israndom) {
        this.israndom = israndom;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswered() {
        return answered;
    }

    public void setAnswered(String answered) {
        this.answered = answered;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMaxAnswersRequired() {
        return MaxAnswersRequired;
    }

    public void setMaxAnswersRequired(String maxAnswersRequired) {
        MaxAnswersRequired = maxAnswersRequired;
    }

    public String getDisplayType() {
        return DisplayType;
    }

    public void setDisplayType(String displayType) {
        DisplayType = displayType;
    }

    public JsonArray getOptions() {
        return options;
    }

    public void setOptions(JsonArray options) {
        this.options = options;
    }

    public ArrayList<Answer> getAnswersArray() {
        if (answersArray == null) answersArray = new ArrayList<>();
        return answersArray;
    }

    public void setAnswersArray(ArrayList<Answer> answersArray) {
        this.answersArray = answersArray;
    }

    public ArrayList<Answer> getAnswersGiven() {
        if (answersGiven == null){
            answersGiven = new ArrayList<>();
        }
        return answersGiven;
    }

    public void setAnswersGiven(ArrayList<Answer> answersGiven) {
        this.answersGiven = answersGiven;
    }

    public boolean israndom() {
        return israndom;
    }

    public void initializeOptions(){
        JsonObject optionsobj;
        String sortIndex;//null
        String answer_id, Text, value, NextQuestion, Imageurl, Groupid, answers_given;
        Answer newAnswer;
        boolean isColumn;


        for (JsonElement js2 : this.options){
            optionsobj = js2.getAsJsonObject();
            newAnswer = new Answer();

            sortIndex = (optionsobj.get("sortIndex").isJsonNull() ? "":optionsobj.get("sortIndex").getAsString());
            answer_id = (optionsobj.get("answer_id") == null ? "": optionsobj.get("answer_id").getAsString());
            Text = (optionsobj.get("Text") == null ? "": optionsobj.get("Text").getAsString());
            value = (optionsobj.get("value") == null ? "": optionsobj.get("value").getAsString());
            NextQuestion = (optionsobj.get("NextQuestion") == null ? "": optionsobj.get("NextQuestion").getAsString());
//            Imageurl = (optionsobj.get("Imageurl") == null ? "": "http://hivelive.gr/"+optionsobj.get("Imageurl").getAsString());
            Imageurl = (optionsobj.get("Imageurl") == null ? "": optionsobj.get("Imageurl").getAsString().replaceAll(" ", "%20"));
            Groupid = (optionsobj.get("Groupid") == null ? "": optionsobj.get("Groupid").getAsString());
            answers_given = (optionsobj.get("answers_given") == null ? "": (optionsobj.get("answers_given").getAsString()));
            if (optionsobj.get("isColumn") == null || optionsobj.get("isColumn").isJsonNull()){
                isColumn = false;
            }else{
                isColumn = ((optionsobj).get("isColumn").getAsInt() == 0);
            }


            newAnswer.setSortIndex(sortIndex);
            newAnswer.setAnswer_id(answer_id);
            newAnswer.setText(Text);
            newAnswer.setValue(value);
            newAnswer.setNextQuestion(NextQuestion);
            newAnswer.setImageurl(Imageurl);
            newAnswer.setGroupid(Groupid);
            newAnswer.setAnswers_given(answers_given);
            newAnswer.setColumn(isColumn);

//            if (DisplayType.toLowerCase().contains("image")) {
//                Log.d("Answers", "answer text is "+ newAnswer.getText() );
//            }

            answersArray.add(newAnswer);

            answersMap.put(newAnswer.getAnswer_id(), newAnswer);
            //Log.d("Answers","!!!!!!!!!"+ newAnswer.getSortIndex());
        }


        if (!israndom) {
            Collections.sort(answersArray, new Comparator<Answer>() {
                @Override
                public int compare(Answer qID1, Answer qID2) {
                    return qID1.getSortIndex().compareTo(qID2.getSortIndex());
                }
            });
        }
    }


    public String getTemplate() {
        return Template;
    }

    public void setTemplate(String template) {
        Template = template;
    }


    public HashMap<String, Answer> getAnswersMap() {
        return answersMap;
    }

    public void setAnswersMap(HashMap<String, Answer> answersMap) {
        this.answersMap = answersMap;
    }


    /*in case of saved questions with logansquare*/
    public void createAnswersMap(){
        answersMap = new HashMap<>();
        for (Answer ans: answersArray){
            answersMap.put(ans.answer_id, ans);
        }
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
