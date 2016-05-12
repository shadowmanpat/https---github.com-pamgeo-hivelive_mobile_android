package gr.extract.hivelive.hiveUtilities;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Sophie on 02/03/16.
 */
@JsonObject
public class ResearchesWraper {

    @JsonField(name="researches")
    ArrayList<Research> researches;

    public void setResearches(ArrayList<Research> researches) {
        this.researches = researches;
    }

    public ArrayList<Research> getResearches() {
        return researches;
    }

    @Override
    public String toString() {
        try {
            return LoganSquare.serialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
