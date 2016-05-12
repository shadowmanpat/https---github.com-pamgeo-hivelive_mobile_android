package gr.extract.hivelive.CustomViews;


import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import gr.extract.hivelive.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomDialogFragment extends Fragment {

    private View dialog;
    private ImageView mBlurBackImg;
    private static Bitmap mScreenShot;


    public CustomDialogFragment() {
        // Required empty public constructor
    }

    public static CustomDialogFragment newInstance(Bitmap bt) {
        mScreenShot = bt;
        CustomDialogFragment fragment = new CustomDialogFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialog = inflater.inflate(R.layout.custom_dialog, container, false);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBlurBackImg = (ImageView) dialog.findViewById(R.id.blur_back_imageview);
        mBlurBackImg.setImageBitmap(mScreenShot);
    }
}
