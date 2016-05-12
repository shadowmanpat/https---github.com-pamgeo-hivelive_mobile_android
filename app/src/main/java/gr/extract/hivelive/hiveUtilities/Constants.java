package gr.extract.hivelive.hiveUtilities;


public class Constants {



    public final static String URL_BASE = "https://hivelive.gr/HiveLive/";
//    public final static String URL_BASE = "http://100.0.0.3/usvs/HiveLive/";
    public final static String URL_LOGIN = URL_BASE + "index.php?option=com_hivelive&format=raw&ismobile=1";
    public final static String URL_RESEARCHES = URL_BASE + "index.php?option=com_hivelive&format=raw&ismobile=1&view=researchAPI&action=getResearches&token=" ;
    public final static String URL_RESEARCHES_OFFLINE = URL_BASE + "index.php?option=com_hivelive&format=raw&ismobile=1&view=researchAPI&action=all&token=";
    public final static String URL_UPLOAD_ANSWERS = URL_BASE + "index.php?option=com_hivelive&format=raw&ismobile=1&view=researchAPI&action=all&token=";
    public final static String URL_UPLOAD_PROFILE_PIC = URL_BASE + "index.php?option=com_hivelive&view=profile&format=raw&action=uploadProfileImage&mobile=1&token=";
    public final static String START_ACTIVITY = "startActivity";
    public final static String APP = "gr.extract.hivelive";
    public final static String USER_LOGGED = "useremail";
    public final static String USER_PASS = "userpass";
    public final static String RESEARCHER = "researcher";
    public final static String RESEARCHES_OFFLINE = "researchesoffline";

    public static final String GCM_ONEOFF_TAG = "oneoff|[0,0]";
    public static final String GCM_REPEAT_TAG = "repeat|[7200,1800]";


    public static final String OFFLINE_MODE = "offline_mode";
    public static final String AUTOLOGIN = "autologin";

    /* snappy db */
    public static final String DBNAME = "HiveLiveDB";
    public static final String RESEARCH_RESULTS = "researchresults";



    public static String UrlQuestionsForResearch(String researchID, String token){
       return  URL_BASE + "index.php?option=com_hivelive&view=questionsAPI&action=getQuestions&qnrid="+ researchID+"&format=raw&ismobile=1&token="+token;
    }

    public static String URLforAnswerUploading(String token){
//        return "https://hivelive.gr/hl1/HiveLive/index.php?option=com_hivelive&view=questionsAPI&action=saveAnswer&format=raw&ismobile=1&qnrid="+ researchID+"&token="+token;
        return  URL_BASE + "index.php?option=com_hivelive&view=questionsAPI&action=saveAnswersBatch&format=raw&ismobile=1"+"&token="+token;
    }

    public static String URLforCapiAnswers(String token){
        return  URL_BASE + "index.php?option=com_hivelive&view=questionsAPI&action=saveCapiAnswers&format=raw&token="+token+"&ismobile=1";
    }

    public static String URLForQualitative(String qnrid, String token){
        return  URL_BASE + "index.php?option=com_hivelive&view=qualitative&qnrid="+qnrid+"&token="+token+"&ismobile=1";
    }

}


