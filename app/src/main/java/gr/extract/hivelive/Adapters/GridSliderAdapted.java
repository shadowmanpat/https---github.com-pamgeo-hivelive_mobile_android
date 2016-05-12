package gr.extract.hivelive.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import gr.extract.hivelive.Activities.ResearchActivity;
import gr.extract.hivelive.Fragments.QuestionFragments.GridSliderFragment;
import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.Answer;

public class GridSliderAdapted extends RecyclerView.Adapter<GridSliderAdapted.ViewHolder> {

    private ArrayList<Answer> rowAnswers, colAnswers;
    private GridSliderFragment mFragment;
    private Context mContext;
    private int dataCols, dataRows;
    LinearLayout.LayoutParams paramForCols = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
    LinearLayout.LayoutParams paramForRows, paramForAnswer;


    public GridSliderAdapted(Context con, ArrayList<Answer> rowAnswers, ArrayList<Answer> colAnswers, GridSliderFragment fr, int dataSize) {
        this.rowAnswers = rowAnswers;
        this.colAnswers = colAnswers;
        this.mContext = con;
        this.mFragment = fr;

        dataCols = colAnswers.size();
        dataRows = dataSize - dataCols;

//        paramForAnswer = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//
//        paramForRows = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT, (float) dataCols);
//        paramForRows.setMargins(10, 0, 10, 0);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_slider_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Answer answer = rowAnswers.get(position);
        if (position == 0) {
            TextView columnTextView;
            holder.columnsRow_ll.setVisibility(View.VISIBLE);

            for (int i = 0; i < dataCols; i++) {
                columnTextView = new TextView(mContext);
                columnTextView.setText(colAnswers.get(i).getText());
                columnTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
                columnTextView.setTextSize(14.0f);
                Resources r = mContext.getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
                paramForCols.setMargins((int) px, 0, 0, (int) px);
                columnTextView.setLayoutParams(paramForCols);
                holder.columnsValues.addView(columnTextView);
            }
        }

        if (rowAnswers.contains(answer)) {
            holder.answer.setText(answer.getText());
            holder.answer.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
//            holder.answer.setLayoutParams(paramForAnswer);
        }

        holder.answerRangeBar.setTickStart(1);
//        holder.answerRangeBar.setLayoutParams(paramForRows);
//        if (dataCols > 1)
//            holder.answerRangeBar.setSeekPinByIndex(1);
//        else
            holder.answerRangeBar.setSeekPinByIndex(1);
        holder.answerRangeBar.setTickEnd(dataCols);
        holder.answerRangeBar.setTickInterval(1);

        holder.answerRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
//                rightPinValue = getAnswerByPosition(rightPinValue).getText();
                mFragment.addNewAnswer(position, Integer.valueOf(rightPinValue));

//                ((ResearchActivity)getActivity()).answerSelection(getAnswerByPosition(rightPinValue));
            }
        });


    }

    @Override
    public int getItemCount() {
        return rowAnswers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.grid_answer_tv)
        TextView answer;
        @Bind(R.id.columns_container)
        LinearLayout columnsRow_ll;
        @Bind(R.id.values_columns)
        LinearLayout columnsValues;
        @Bind(R.id.rows_layout)
        LinearLayout rows_layout;
        @Bind(R.id.horizontal_rangebar)
        RangeBar answerRangeBar;


        public ViewHolder(View itemview) {
            super(itemview);
            ButterKnife.bind(itemView);
            answer = (TextView) itemview.findViewById(R.id.grid_answer_tv);
            columnsRow_ll = (LinearLayout) itemview.findViewById(R.id.values_columns);
            columnsValues = (LinearLayout) itemview.findViewById(R.id.columns_container);
            rows_layout = (LinearLayout) itemview.findViewById(R.id.rows_layout);
            answerRangeBar = (RangeBar) itemview.findViewById(R.id.horizontal_rangebar);
        }
    }
}
