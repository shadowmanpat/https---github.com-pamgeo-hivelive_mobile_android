package gr.extract.hivelive.hiveUtilities;

import java.util.ArrayList;
import java.util.HashMap;

public class StableData {

    private ArrayList<String> cityCodes = new ArrayList<>();
    private ArrayList<String> eduCodes = new ArrayList<>();
    private ArrayList<String> occuCodes = new ArrayList<>();

    private HashMap<String, String> codesToCities = new HashMap<>();
    private HashMap<String, String> codesToEducation = new HashMap<>();
    private HashMap<String, String> codesToOccupation = new HashMap<>();

    public ArrayList<String> getCityCodes() {
        return cityCodes;
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

    public HashMap<String, String> getCodesToCities() {
        return codesToCities;
    }

    public void setCodesToCities(HashMap<String, String> codesToCities) {
        this.codesToCities = codesToCities;
    }

    public HashMap<String, String> getCodesToEducation() {
        return codesToEducation;
    }

    public void setCodesToEducation(HashMap<String, String> codesToEducation) {
        this.codesToEducation = codesToEducation;
    }

    public HashMap<String, String> getCodesToOccupation() {
        return codesToOccupation;
    }

    public void setCodesToOccupation(HashMap<String, String> codesToOccupation) {
        this.codesToOccupation = codesToOccupation;
    }
}
