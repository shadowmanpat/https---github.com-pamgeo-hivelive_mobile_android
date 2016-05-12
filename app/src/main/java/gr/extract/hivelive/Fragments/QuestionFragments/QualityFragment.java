package gr.extract.hivelive.Fragments.QuestionFragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.CustomViews.CustomDialogFragment;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Constants;
import gr.extract.hivelive.hiveUtilities.Research;
import gr.extract.hivelive.hiveUtilities.Utils;
import im.delight.android.webview.AdvancedWebView;
import pub.devrel.easypermissions.EasyPermissions;

public class QualityFragment extends GlobalQuestionFragment implements AdvancedWebView.Listener, EasyPermissions.PermissionCallbacks{

    private View mMainView;
    private AdvancedWebView qualWebview;
    private boolean networkFlag = false, customDialogShown = false;
    private Context mContext;
    private ImageView mBlurBackImg;
    private Research researchToBeAnswered;
    private Bitmap mScreenshot;
    private CustomDialogFragment dialog;
    private int mWidth, mHeight;
    private Utils mUtils;
    private ResearchActivity researchActivity;


    private BroadcastReceiver mNetworkStateChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (intent.getExtras() != null) {
                if (isConnected) {
                    networkFlag = true;
                    if (qualWebview != null) {
                        qualWebview.setVisibility(View.VISIBLE);
                        reloadURL();
                        if (customDialogShown) {
                            researchActivity.removeErrorFragment();
                        }
                    }
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    networkFlag = false;
                    showDialog();
                    //// TODO: 29/03/16 inform activity to close connection; 
                } else {
                    networkFlag = false;
                    showDialog();
                }
            } else {
                networkFlag = false;
            }
        }

    };


    public QualityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity().getApplicationContext();
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext.registerReceiver(mNetworkStateChange, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        researchToBeAnswered = ((ResearchActivity) getActivity()).getResearchChosen();
        mUtils = new Utils(mContext);
        researchActivity = (ResearchActivity)getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_quality, container, false);
        return mMainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qualWebview = (AdvancedWebView) mMainView.findViewById(R.id.qualitative_webview);

        mMainView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mMainView.getViewTreeObserver().removeOnPreDrawListener(this);
                mWidth = mMainView.getWidth();
                mHeight = mMainView.getHeight();
                return true;
            }
        });
        qualWebview.setListener(this, this);

        setWebViewSettings();

//        reloadURL();
    }


    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        qualWebview.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        qualWebview.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        qualWebview.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        qualWebview.onActivityResult(requestCode, resultCode, intent);
    }

    private void reloadURL() {
        Toast.makeText(mContext, "Φορτώνει.. Παρακαλώ περιμένετε", Toast.LENGTH_SHORT).show();
        boolean preventCaching = true;
        String URL = Constants.URLForQualitative(researchToBeAnswered.getResearchID(), Singleton.getInstance(mContext).getCurrentUser().getToken());
        Log.e("QualitativeURL", URL);
        qualWebview.loadUrl(URL, preventCaching);
    }

    private void setWebViewSettings(){
        qualWebview.getSettings().setLoadsImagesAutomatically(true);
        qualWebview.getSettings().setJavaScriptEnabled(true);
        qualWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        qualWebview.getSettings().setSupportMultipleWindows(true);
        qualWebview.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWebView = new AdvancedWebView(mContext);
                ((FrameLayout)mMainView).addView(newWebView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                researchActivity.setNewWebViewAdded(true);

                return true;
            }

        });
    }

    public void removeWebView(){
      //  researchActivity.setNewWebViewAdded(false);
        ((FrameLayout)mMainView).removeViewAt(1);
    }


    public void showDialog() {
        qualWebview.setVisibility(View.GONE);
        customDialogShown = true;

        if (mScreenshot == null) {
            mScreenshot = getScreenViewBitmap(mMainView);
        }
        dialog = CustomDialogFragment.newInstance(mScreenshot);
//        dialog.setStyle(R.style.custom_popup, android.R.style.Theme_Translucent_NoTitleBar);

        researchActivity.addErrorFragment(dialog);
//        dialog.show(getFragmentManager(), "CustomDialogFragment");

    }


    private Bitmap getScreenViewBitmap(View v) {
        Bitmap b = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, mWidth, mHeight);
        v.draw(c);
        return b;

    }

    @Override
    public void onPageStarted(String s, Bitmap bitmap) {

    }

    @Override
    public void onPageFinished(String s) {

    }

    @Override
    public void onPageError(int i, String s, String s1) {
        showDialog();
    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        final String filename = "hive_"+mUtils.randomFileName();

        if (AdvancedWebView.handleDownload(mContext, url, filename)) {
            Toast.makeText(mContext, "Download completed.", Toast.LENGTH_SHORT).show();
        }
        else {
            // download couldn't be handled because user has disabled download manager app on the device
            Toast.makeText(mContext, "Attempt to download the file failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onExternalPageRequest(String s) {

    }

    @Override
    public void pauseWebView(){
        qualWebview.onPause();
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
