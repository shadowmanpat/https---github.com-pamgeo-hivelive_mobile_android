package gr.extract.hivelive.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends Fragment {

    private View mMainView;
    private Switch offlineModeSwitch, autologinSwitch;
    private SharedPreferences mPrefs;


    public PreferencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPrefs = getActivity().getSharedPreferences(Constants.APP, Context.MODE_PRIVATE);

        mMainView = inflater.inflate(R.layout.fragment_preferences, container, false);
        offlineModeSwitch = (Switch) mMainView.findViewById(R.id.offline_mode_switch);
        autologinSwitch = (Switch) mMainView.findViewById(R.id.autologin_switch);


        offlineModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mPrefs.edit().putBoolean(Constants.OFFLINE_MODE, true).apply();
                else   mPrefs.edit().putBoolean(Constants.OFFLINE_MODE, false).apply();
            }
        });

        autologinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mPrefs.edit().putBoolean(Constants.AUTOLOGIN, true).apply();
                else   mPrefs.edit().putBoolean(Constants.AUTOLOGIN, false).apply();
            }
        });

        return mMainView;
    }

}
