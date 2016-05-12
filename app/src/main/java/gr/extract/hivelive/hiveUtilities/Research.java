package gr.extract.hivelive.hiveUtilities;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sophie on 30/12/15.
 */
@JsonObject
public class Research {


    @JsonField(name="title")
    String title = "";
    @JsonField(name="fromDate")
    String fromDate = "";
    @JsonField(name="toDate")
    String toDate = "";
    @JsonField
    String sex = "";
    @JsonField
    String fromAge = "";
    @JsonField
    String toAge = "";
    @JsonField
    String ageGroup = "";
    @JsonField
    String urban = "";
    @JsonField
    String quota = "";
    @JsonField
    String education = "";
    @JsonField
    String occupation = "";
    @JsonField
    String rv1 = "";
    @JsonField
    String rv2 = "";
    @JsonField
    String description = "";
    @JsonField
    String type = "";
    @JsonField(name="questionIdsSorted")
    String[] questionIdsSorted;

    /*
    * for capi
     */
    @JsonField(name="firstName")
    String firstName;
    @JsonField(name="email")
    String email;
    @JsonField(name="phone")
    String phone;
    @JsonField(name="address")
    String address;

    @JsonField(name="researchID")
    public String researchID ="";
    @JsonField(name="timeCompleted")
    public long timeOfCompletion = 0;
    @JsonField(name="questionsOfResearch")
    public ArrayList<Question> questionsOfResearch = new ArrayList<>();
    @JsonField(name="questionsForResearch")
    public Map<String, Question> questionsForResearch = new HashMap<>();


    public Research() {
    }

    public Research(String title, String fromDate, String todate, String sex, String fromAge, String toAge,
                    String ageGroup, String urban , String quota, String education, String occupation, String rv1,
                    String rv2, String description, String type, String[] qidssorted, String researchID,
                    long timeOfCompletion, ArrayList<Question> questionsOfResearch, Map<String, Question> quesHashmap){
        this.title = title;
        this.fromDate = fromDate;
        this.toDate = todate;
        this.fromAge = fromAge;
        this.toAge = toAge;
        this.sex = sex;
        this.ageGroup = ageGroup;
        this.urban = urban;
        this.quota = quota;
        this.education = education;
        this.occupation = occupation;
        this.rv1 = rv1;
        this.rv2 = rv2;
        this.description = description;
        this.type = type;
        this.questionIdsSorted = qidssorted;
        this.researchID = researchID;
        this.timeOfCompletion = timeOfCompletion;
        this.questionsOfResearch = questionsOfResearch;
        this.questionsForResearch = quesHashmap;
    }

    public static Research newInstance(Research aGalaxy) {
        return new Research(aGalaxy.getTitle() ,aGalaxy.getFromDate(), aGalaxy.getToDate(), aGalaxy.getSex(), aGalaxy.getFromAge(),
                aGalaxy.getToAge(), aGalaxy.getAgeGroup(), aGalaxy.getUrban(), aGalaxy.getQuota(), aGalaxy.getEducation(),
                aGalaxy.getOccupation(), aGalaxy.getRv1(), aGalaxy.getRv2(), aGalaxy.getDescription(), aGalaxy.getType(),
                aGalaxy.getQuestionIdsSorted(), aGalaxy.getResearchID(), aGalaxy.getTimeOfCompletion(), aGalaxy.getQuestionsOfResearch(),
                aGalaxy.getQuestionsForResearch());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResearchID() {
        return researchID;
    }

    public void setResearchID(String researchID) {
        this.researchID = researchID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFromAge() {
        return fromAge;
    }

    public void setFromAge(String fromAge) {
        this.fromAge = fromAge;
    }

    public String getToAge() {
        return toAge;
    }

    public void setToAge(String toAge) {
        this.toAge = toAge;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getUrban() {
        return urban;
    }

    public void setUrban(String urban) {
        this.urban = urban;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getRv1() {
        return rv1;
    }

    public void setRv1(String rv1) {
        this.rv1 = rv1;
    }

    public String getRv2() {
        return rv2;
    }

    public void setRv2(String rv2) {
        this.rv2 = rv2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getQuestionIdsSorted() {
        return questionIdsSorted;
    }

    public void setQuestionIdsSorted(String[] questionIdsSorted) {
        this.questionIdsSorted = questionIdsSorted;
    }

    public HashMap<String, Question> getQuestionsForResearch() {
        return (HashMap)questionsForResearch;
    }

    public void setQuestionsForResearch(HashMap<String, Question> questionsForResearch) {
        this.questionsForResearch = questionsForResearch;
    }

    public ArrayList<Question> getQuestionsOfResearch() {
        return questionsOfResearch;
    }

    public void setQuestionsOfResearch(ArrayList<Question> questionsOfResearch) {
        this.questionsOfResearch = questionsOfResearch;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    /*in case of saved researches with logansquare*/
    public void createQuestionsMap(){
        questionsForResearch = new HashMap<>();
        for (Question ques: questionsOfResearch){
            questionsForResearch.put(ques.questionId, ques);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTimeOfCompletion() {
        return timeOfCompletion;
    }

    public void setTimeOfCompletion(long timeOfCompletion) {
        this.timeOfCompletion = timeOfCompletion;
    }


}
