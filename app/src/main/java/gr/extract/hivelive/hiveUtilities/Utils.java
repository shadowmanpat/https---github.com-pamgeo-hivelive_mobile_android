package gr.extract.hivelive.hiveUtilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sophie on 04/02/16.
 */
public class Utils {
    private Context mContext ;

    public Utils(Context context){
        this.mContext = context;
    }

    private static final int[] EMPTY_STATE = new int[] {};

    public static void clearState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(EMPTY_STATE);
        }
    }

    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    public boolean questionContainsHtml(String questionText){
        if (questionText != null & questionText.contains("</")){
            return true;
        }
        return false;
    }

    public boolean isConnectedToTheInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }

    public String randomFileName() {
        char[] symbols;
        char[] buf;
        int idx;


        Random random = new Random();
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();


        boolean inserted = false;
        String k = "";
        for (idx = 0; idx < 5; ++idx) {

            k = k + symbols[random.nextInt(symbols.length)];
        }

        tmp = new StringBuilder();
        tmp.append(k);
        k = tmp.toString();
        for (int i = idx; i < 10; i++) {
            k = k + symbols[random.nextInt(symbols.length)];
        }

        if (k.length() > 90) {
            k.substring(0, 90);
        }

        return k;
    }


}
