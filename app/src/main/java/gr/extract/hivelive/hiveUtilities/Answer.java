package gr.extract.hivelive.hiveUtilities;


import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Answer {

    @JsonField(name = "sortIndex")
    String sortIndex;//null
    @JsonField(name = "answer_id")
    String answer_id;
    @JsonField(name = "Text")
    String Text;
    @JsonField(name = "value")
    String value;
    @JsonField(name = "NextQuestion")
    String NextQuestion;
    @JsonField(name = "Imageurl")
    String Imageurl;
    @JsonField(name = "Groupid")
    String Groupid;
    @JsonField(name = "answers_given")
    String answers_given;
    @JsonField(name = "isColumn")
    boolean isColumn = false;

    public Answer(){}

    public String getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(String sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNextQuestion() {
        return NextQuestion;
    }

    public void setNextQuestion(String nextQuestion) {
        NextQuestion = nextQuestion;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }

    public String getGroupid() {
        return Groupid;
    }

    public void setGroupid(String groupid) {
        Groupid = groupid;
    }

    public String getAnswers_given() {
        return answers_given;
    }

    public void setAnswers_given(String answers_given) {
        this.answers_given = answers_given;
    }

    public boolean isColumn() {
        return isColumn;
    }

    public void setColumn(boolean column) {
        isColumn = column;
    }
}
