package gr.extract.hivelive.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import gr.extract.hivelive.Adapters.ResearchListAdapter;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Research;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResearchesMainFragment extends Fragment {

    private View mMainView;
    private RecyclerView.LayoutManager recyclerviewManager;
    private RecyclerView mResearchesRecyclerview;
    private ResearchListAdapter mAdapter;
    private Context mContext ;


    public ResearchesMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        //JodaTimeAndroid.init(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_researches_main, container, false);
        mResearchesRecyclerview = (RecyclerView) mMainView.findViewById(R.id.researches_list);
        recyclerviewManager = new LinearLayoutManager(getActivity());
        mResearchesRecyclerview.setLayoutManager(recyclerviewManager);

        ArrayList<Research> researchesArray = new ArrayList<>();
        HashMap<String , Research> IdsToResearches = Singleton.getInstance(mContext).getResearchesArray();

        for (String researchID : IdsToResearches.keySet()){
            researchesArray.add(IdsToResearches.get(researchID));
        }

        mAdapter = new ResearchListAdapter(researchesArray, mContext);
        mResearchesRecyclerview.setAdapter(mAdapter);


        return mMainView;
    }

    public static String getDuration(Research res){
        return "20\'";
    }

}
