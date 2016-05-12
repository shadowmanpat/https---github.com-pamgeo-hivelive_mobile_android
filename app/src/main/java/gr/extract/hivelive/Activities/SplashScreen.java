package gr.extract.hivelive.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Services.NetworkServices;
import gr.extract.hivelive.hiveUtilities.Constants;


public class SplashScreen extends AppCompatActivity {

    private SimpleDraweeView draweeView;
    private boolean eventReceived = false;
    private boolean isResearcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // Uri uri = Uri.parse("http://hivelive.gr/img/loginbg.jpg");

//        Uri uri = new Uri.Builder()
//                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
//                .path(String.valueOf(R.drawable.loginbg))
//                .build();
//        draweeView = (SimpleDraweeView) findViewById(R.id.splash_fresco_background_iv);
//        draweeView.setImageURI(uri);

        isResearcher = getSharedPreferences(Constants.APP, MODE_PRIVATE).getBoolean(Constants.RESEARCHER, false);

        NetworkServices.startGettingAppData(this);


        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (!eventReceived) {
                    Log.e("Events", "Data event received");
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }

            }
        }.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);

    }

    public void onEvent(Events.DataSavedEvent event){
        eventReceived = true;
        Log.e("Events", "Data event received");
        Intent i = new Intent(SplashScreen.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

}
