package gr.extract.hivelive.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import gr.extract.hivelive.Fragments.QuestionFragments.GridRadiobuttonFragment;
import gr.extract.hivelive.R;
import gr.extract.hivelive.hiveUtilities.Answer;

public class GridRadioAnswersAdapter extends RecyclerView.Adapter<GridRadioAnswersAdapter.GridAnswersViewHolder> {

    private ArrayList<Answer> data;
    private Context mContext;
    private int dataRows = 0, dataCols = 0, mSelectedItem = 0;
    private boolean gridImage = false;
    private ArrayList<Answer> columnAnswers, rowAnswers;
    private GridRadiobuttonFragment fragment;
    LinearLayout.LayoutParams paramForCols = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
    RadioGroup.LayoutParams paramForRows = new RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.MATCH_PARENT,
            RadioGroup.LayoutParams.WRAP_CONTENT, 1.0f);


    public GridRadioAnswersAdapter(Context con, ArrayList<Answer> dataGiven, ArrayList<Answer> rowAns, ArrayList<Answer> columnAns, GridRadiobuttonFragment fr, boolean gridWithImgs) {
        this.data = dataGiven;
        this.mContext = con;
        this.fragment = fr;
        this.columnAnswers = columnAns;
        this.rowAnswers = rowAns;
        this.gridImage = gridWithImgs;


        dataCols = columnAnswers.size();
        dataRows = dataGiven.size() - dataCols;

    }


    @Override
    public GridAnswersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_grid_multiple, parent, false);
        return new GridAnswersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GridAnswersViewHolder holder, final int position) {
        Answer answer = rowAnswers.get(position);


        if (position == 0) {
            TextView columnTextView;
            holder.columnValues_ll.setVisibility(View.VISIBLE);

            for (int i = 0; i < dataCols; i++) {
                if (gridImage){

                    View draweelayout = LayoutInflater.from(mContext).inflate(R.layout.custom_gridbutton_images, null);


                    SimpleDraweeView dr = (SimpleDraweeView) draweelayout.findViewById(R.id.drawee_iv);
                    TextView txt = (TextView) draweelayout.findViewById(R.id.drawee_txt);

                    dr.setImageURI(Uri.parse(columnAnswers.get(i).getImageurl()));
//                    Resources r = mContext.getResources();
//                    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
//                    paramForCols.setMargins((int) px, 0, 0, (int) px);
//                    dr.setLayoutParams(paramForCols);
                    txt.setText(columnAnswers.get(i).getText());
                    holder.columns_container.addView(draweelayout);

                }else{
                    columnTextView = new TextView(mContext);
                    columnTextView.setText(columnAnswers.get(i).getText());
                    columnTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
                    columnTextView.setTextSize(14.0f);
                    Resources r = mContext.getResources();
                    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
                    paramForCols.setMargins((int) px, 0, 0, (int) px);
                    columnTextView.setLayoutParams(paramForCols);
                    holder.columns_container.addView(columnTextView);
                }

            }
        }

        if (rowAnswers.contains(answer)) {
            holder.answer.setText(answer.getText());
            holder.answer.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
        }


        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSelectedItem = position;

//                Toast.makeText(mContext, position + ","+v.getTag().toString()+" clicked!", Toast.LENGTH_SHORT).show();
                addAnswer(position, v.getTag().toString());
            }
        };

        int counter = 0;
        RadioButton rd;

//        paramForRows.gravity = Gravity.CENTER_HORIZONTAL;

        while (counter < dataCols) {
            rd = new RadioButton(mContext);
            rd.setTag(counter);
            paramForRows.gravity = Gravity.CENTER_HORIZONTAL;
            rd.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.abc_btn_radio_material));
            rd.setOnClickListener(clickListener);
            rd.setLayoutParams(paramForRows);
            holder.radiogroupOfAnswers.addView(rd);
            counter++;
        }

//        ((RadioButton)holder.radiogroupOfAnswers.getChildAt(0)).setChecked(true);

    }

    @Override
    public int getItemCount() {
        return dataRows;
    }

    private void addAnswer(int position, String radioButtonTag) {
        fragment.addNewAnswer(String.valueOf(position), radioButtonTag);
    }


    public static class GridAnswersViewHolder extends RecyclerView.ViewHolder {
        //@Bind(R.id.grid_answer_tv)
        TextView answer;
        //@Bind(R.id.grid_answers_group)
        RadioGroup radiogroupOfAnswers;

        LinearLayout columnValues_ll;
        LinearLayout rows_layout;
        //@Bind(R.id.columns_container)
        LinearLayout columns_container;


        public GridAnswersViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(itemView);
            answer = (TextView) itemView.findViewById(R.id.grid_answer_tv);
            radiogroupOfAnswers = (RadioGroup) itemView.findViewById(R.id.grid_answers_group);
            columnValues_ll = (LinearLayout) itemView.findViewById(R.id.values_columns);
            columns_container = (LinearLayout) itemView.findViewById(R.id.columns_container);
            rows_layout = (LinearLayout) itemView.findViewById(R.id.rows_layout);

        }

    }
}
