package gr.extract.hivelive.Services;


import java.util.ArrayList;

import gr.extract.hivelive.hiveUtilities.Answer;
import gr.extract.hivelive.hiveUtilities.User;

public class Events {

    public static class LoginEvent{
        private int result; /*1 : everything ok , 0: something wrong*/
        private User user; /*null if success 0*/
        private String warning;/* not empty if success is 0*/

        public LoginEvent(int res, User user, String war){
            this.result = res;
            this.user = user;
            this.warning = war;
        }

        public int getResult() {
            return result;
        }

        public User getUser() {
            return user;
        }

        public String getWarning() {
            return warning;
        }
    }

    public static class CommunicationBetweenActivitiesEvent{
        private String message;
        private boolean isResearcher;

        public CommunicationBetweenActivitiesEvent(String messa){
            this.message = messa;
        }

        public String getMessage() {
            return message;
        }

        public boolean isResearcher() {
            return isResearcher;
        }
    }

    public static class RemindEvent{
        private int result; /*0 is success, 1 is failure with message, 2 is null result*/
        private String message;

        public RemindEvent(int res, String mess){
            this.result = res;
            this.message = mess;
        }

        public int getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class SignUpEvent{
        private int result; /*0 is success, 1 is failure with message, 2 is null result*/
        private String message;

        public SignUpEvent(int res, String mess){
            this.result = res;
            this.message = mess;
        }

        public int getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class DataSavedEvent{

    }

    public static class ResearchesFoundEvent{
        private int result;

        public ResearchesFoundEvent(int res){
            this.result = res;
        }

        public int getResult() {
            return result;
        }
    }

    public static class QuestionsFound{
        private int result;
        private String researchIdChosen;

        public QuestionsFound(int res, String resID){
            this.result = res;
            this.researchIdChosen = resID;
        }

        public int getResult() {
            return result;
        }

        public String getResearchIdChosen() {
            return researchIdChosen;
        }
    }

    public static class AnswerSelected{
        private String answerPos;
        private String answerValue;
        private String questionAnswered;
        private boolean multipleAswers = false;
        private ArrayList<String> multipleAnswers;


        public AnswerSelected(){};

        public AnswerSelected(String answerposition){
            this.answerPos = answerposition;
        }

        public String getAnswerValue() {
            return answerValue;
        }

        public void setAnswerValue(String answerValue) {
            this.answerValue = answerValue;
        }

        public String getAnswerPos() {
            return answerPos;
        }

        public void setAnswerPos(String answerPos) {
            this.answerPos = answerPos;
        }

        public String getQuestionAnswered() {
            return questionAnswered;
        }

        public void setQuestionAnswered(String questionAnswered) {
            this.questionAnswered = questionAnswered;
        }

        public boolean isMultipleAswers() {
            return multipleAswers;
        }

        public void setMultipleAswers(boolean multipleAswers) {
            this.multipleAswers = multipleAswers;
        }

        public ArrayList<String> getMultipleAnswers() {
            return multipleAnswers;
        }

        public void setMultipleAnswers(ArrayList<String> multipleAnswers) {
            this.multipleAnswers = multipleAnswers;
        }
    }

    public static class ItemMovedEvent{
            private ArrayList<Answer> data;

            public ItemMovedEvent(ArrayList<Answer> dataArranged){
                this.data = dataArranged;
            }

            public ArrayList<Answer> getData() {
                return data;
            }
    }

    public static class InsideCommunicationEvent{
        private String answerPos;
        private String answerValue;

        public InsideCommunicationEvent(String ansPos,String ansValue){
            this.answerPos = ansPos;
            this.answerValue = ansValue;
        }

        public String getAnswerPos() {
            return answerPos;
        }

        public String getAnswerValue() {
            return answerValue;
        }
    }

    public static class PictureUploadEvent{
        private int result;
        private String profpicPath;

        public PictureUploadEvent(int result, String profpicPath){
            this.result = result;
            this.profpicPath = profpicPath;
        }

        public int getResult() {
            return result;
        }

        public String getProfpicPath() {
            return profpicPath;
        }
    }


}
