package gr.extract.hivelive;

import android.content.Context;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.IOException;
import java.util.ArrayList;

import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Constants;
import gr.extract.hivelive.hiveUtilities.Question;
import gr.extract.hivelive.hiveUtilities.Research;
import gr.extract.hivelive.hiveUtilities.ResearchesWraper;
import gr.extract.hivelive.hiveUtilities.StableData;
import gr.extract.hivelive.hiveUtilities.User;

/**
 * Created by Sophie on 02/03/16.
 */
public class SnappyCalls {

    public static DB snappy;

    public static String RESEARCH_ARRAY_KEY = "research_key";
    public static String RESEARCH_OFFLINE_KEY = "research_key_offline";
    public static String USER_KEY = "user_key";
    public static String STABLE_DATA = "stable_data";
    private static Context mContext;


    public static void open(Context con) {
        mContext = con;
        try {
            snappy = DBFactory.open(con, Constants.DBNAME);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }


    public static void close() {
        if (snappy != null) {
            try {
                snappy.close();
            } catch (SnappydbException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addResearch(Research toAdd, boolean offline) throws SnappydbException, IOException {
        String saved = "";
        ResearchesWraper researchWraper = null;
        try {
            if (offline)
                saved = snappy.get(RESEARCH_OFFLINE_KEY);
            else
                saved = snappy.get(RESEARCH_ARRAY_KEY);
        } catch (SnappydbException e) {

        }

        if (!saved.isEmpty()) {
            researchWraper = (ResearchesWraper) LoganSquare.parse(saved, ResearchesWraper.class);
        } else {
            researchWraper = new ResearchesWraper();
            researchWraper.setResearches(new ArrayList<Research>());
        }
        researchWraper.getResearches().add(toAdd);
        if (offline)
            snappy.put(RESEARCH_OFFLINE_KEY, LoganSquare.serialize(researchWraper));
        else
            snappy.put(RESEARCH_ARRAY_KEY, LoganSquare.serialize(researchWraper));
    }

    public static ArrayList<Research> getAnsweredResearches(boolean offline) {

        try {
            ResearchesWraper wraper;
            if (offline) {
                wraper = LoganSquare.parse(snappy.get(RESEARCH_OFFLINE_KEY), ResearchesWraper.class);
            } else {
                wraper = LoganSquare.parse(snappy.get(RESEARCH_ARRAY_KEY), ResearchesWraper.class);
            }
            return wraper.getResearches();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();

    }


    public static Research getSavedResearchFromID(String researchID) {
        Research resSaved;
        ArrayList<Research> researchesSaved = getAnsweredResearches(false);
        for (Research res : researchesSaved) {
            if (res != null && res.getResearchID().equals(researchID)) {
                resSaved = res;
                return resSaved;
            }
        }

        return new Research();
    }



    public static void deleteAllResearches(boolean isCapi) throws SnappydbException {
        if (isCapi) {
            snappy.del(RESEARCH_OFFLINE_KEY);
        } else
            snappy.del(RESEARCH_ARRAY_KEY);
    }

    public static boolean removeResearchFromSaved(String researchID, boolean offline) {
        boolean removed = false;
        ArrayList<Research> researchesSaved = getAnsweredResearches(offline);
        removed = researchesSaved.remove(getSavedResearchFromID(researchID));
        if (removed) {
            ResearchesWraper researchWraper = new ResearchesWraper();
            researchWraper.setResearches(researchesSaved);
            try {
                if (!offline)
                    snappy.put(RESEARCH_ARRAY_KEY, LoganSquare.serialize(researchWraper));
                else
                    snappy.put(RESEARCH_OFFLINE_KEY, LoganSquare.serialize(researchWraper));
            } catch (SnappydbException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return removed;
    }


    public static void saveOfflineResearches(ArrayList<Research> researchesSaved) {
//        List<Research> researches = researchesSaved;

        try {
            ResearchesWraper researchesWraper = new ResearchesWraper();
            researchesWraper.setResearches(researchesSaved);
            snappy.put(Constants.RESEARCHES_OFFLINE, LoganSquare.serialize(researchesWraper));
//            snappy.put(Constants.RESEARCHES_OFFLINE, researches);
        } catch (SnappydbException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ArrayList<Book> mySavedBooks = (ArrayList<Book>) snappyDB.getObject("books", ArrayList.class);
    }

    public static ArrayList<Research> getOfflineResearches() {
        ArrayList<Research> mySavedResearches = null;
        ResearchesWraper resWrapper;
        try {
            resWrapper = LoganSquare.parse(snappy.get(Constants.RESEARCHES_OFFLINE), ResearchesWraper.class);
            Log.d("QUESTIONSCAPI", resWrapper.toString());
            mySavedResearches = resWrapper.getResearches();
            for (Research res : mySavedResearches) {
                res.createQuestionsMap();
                for (Question ques : res.getQuestionsOfResearch()) {
                    ques.createAnswersMap();
                }
            }

        } catch (SnappydbException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mySavedResearches != null) return mySavedResearches;
        return new ArrayList<>();
    }

    public static void deleteUserFromDB() throws SnappydbException, IOException {
        snappy.del(USER_KEY);
    }

    public static User getUserFromDb() {
        try {
            return (User) LoganSquare.parse(snappy.get(USER_KEY), User.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return new User();
    }

    public static void saveUserToDB(User user) {
        try {
            snappy.put(USER_KEY, LoganSquare.serialize(user));
        } catch (SnappydbException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveStableData(StableData data) throws SnappydbException {
        snappy.put(STABLE_DATA, data);
    }

    public static StableData getStableData() throws SnappydbException {
        StableData std = (StableData) snappy.getObject(STABLE_DATA, StableData.class);
        return (std == null ? new StableData() : std);
    }


}
