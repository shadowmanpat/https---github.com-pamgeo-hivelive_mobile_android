package gr.extract.hivelive.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import gr.extract.hivelive.Fragments.ResearchesMainFragment;
import gr.extract.hivelive.R;
import gr.extract.hivelive.Services.Events;
import gr.extract.hivelive.Services.NetworkServices;
import gr.extract.hivelive.Singletons.Singleton;
import gr.extract.hivelive.hiveUtilities.Constants;
import gr.extract.hivelive.hiveUtilities.Research;

/**
 * Created by Sophie on 31/12/15.
 */
public class ResearchListAdapter extends RecyclerView.Adapter<ResearchListAdapter.ViewHolder> {
    private ArrayList<Research> researchItems;
    private Context mContext;
    private Research mResearch;

    public ResearchListAdapter(ArrayList<Research> researchItems, Context con) {
        this.researchItems = researchItems;
        this.mContext = con;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.research_item_layout, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final boolean isResearcher = mContext.getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).getBoolean(Constants.RESEARCHER, false);

        mResearch = researchItems.get(position);
        holder.mResearchTitle.setText(mResearch.getTitle());
        holder.mDuration.setText(ResearchesMainFragment.getDuration(mResearch));
        holder.mAge.setText(mResearch.getFromAge() + " - " + mResearch.getToAge());
        holder.mGender.setText(mResearch.getSex());
        holder.mCitiz.setText(mResearch.getUrban());
        holder.mEduc.setText(mResearch.getEducation());
        holder.mOccup.setText(mResearch.getOccupation());
        holder.mSampleNum.setText(mResearch.getQuota());
        holder.mSpecialChar.setText("-");

        holder.researchButton.setTag(mResearch.getResearchID());
        holder.testButton.setTag(mResearch.getResearchID());

        if (mResearch.getType().equals("1") || !isResearcher)
            holder.testButton.setVisibility(View.GONE);


        holder.mCitiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        holder.researchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String researchID = v.getTag().toString();
                /* if the research has been answered in this session*/
                if (Singleton.getInstance(mContext).getResearchesAnswered().contains(researchID)) {
                    Toast.makeText(mContext, "Όλες οι ερωτήσεις έχουν απαντηθεί για αυτή την έρευνα.", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Research> researchHashMap = Singleton.getInstance(mContext).getResearchesArray();
                    if (researchHashMap.containsKey(researchID)) {
                        if (isResearcher) {
                            EventBus.getDefault().post(new Events.QuestionsFound(-1, researchID));
                        } else {
                            if (researchHashMap.get(researchID).getType().equals("1")) { /*ποιοτικη*/
                                EventBus.getDefault().post(new Events.QuestionsFound(100, researchID));
                            } else {
                                if (researchHashMap.get(researchID).getQuestionsForResearch().size() == 0) {
                                    NetworkServices.startgetQuestionsForResearch(mContext, researchID);
                                } else {
                                    EventBus.getDefault().post(new Events.QuestionsFound(0, researchID));

                                }
                            }
                        }
                    } else
                        NetworkServices.startgetQuestionsForResearch(mContext, researchID);
                }
            }
        });

        holder.testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String researchID = v.getTag().toString();
                /* if the research has been answered in this session*/

                HashMap<String, Research> researchHashMap = Singleton.getInstance(mContext).getResearchesArray();
                if (researchHashMap.containsKey(researchID)) {
                        if (researchHashMap.get(researchID).getType().equals("1")) { /*ποιοτικη*/
                            EventBus.getDefault().post(new Events.QuestionsFound(100, researchID));
                        } else {
                            if (researchHashMap.get(researchID).getQuestionsForResearch().size() == 0) {
                                EventBus.getDefault().post(new Events.QuestionsFound(1000, researchID));
                                NetworkServices.startgetQuestionsForResearch(mContext, researchID);
                            } else {
                                EventBus.getDefault().post(new Events.QuestionsFound(101, researchID));

                            }
                        }

                } else
                    NetworkServices.startgetQuestionsForResearch(mContext, researchID);
            }
        });
    }


    @Override
    public int getItemCount() {
        return researchItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mResearchTitle, mDuration, mGender, mAge, mCitiz, mSampleNum, mEduc, mOccup, mSpecialChar;
        Button researchButton, testButton;


        public ViewHolder(View itemView) {
            super(itemView);
            mResearchTitle = (TextView) itemView.findViewById(R.id.researchitem_title_tv);
            mDuration = (TextView) itemView.findViewById(R.id.research_duration_tv);
            mGender = (TextView) itemView.findViewById(R.id.research_gender_tv);
            mAge = (TextView) itemView.findViewById(R.id.research_ages_tv);
            mCitiz = (TextView) itemView.findViewById(R.id.research_astikotita_tv);
            mSampleNum = (TextView) itemView.findViewById(R.id.research_samples_tv);
            mEduc = (TextView) itemView.findViewById(R.id.research_education_tv);
            mOccup = (TextView) itemView.findViewById(R.id.research_occupation_tv);
            mSpecialChar = (TextView) itemView.findViewById(R.id.research_specials_tv);

            researchButton = (Button) itemView.findViewById(R.id.research_start_btn);
            testButton = (Button) itemView.findViewById(R.id.research_test_btn);
        }
    }

}
