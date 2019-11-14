package com.dbvertex.company.h1bq.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.model.ResultPopUpDTO;

import java.util.List;

public class ResultPopUpADP extends RecyclerView.Adapter<ResultPopUpADP.ViewHolderProgressAdapter>{

    private Context mcontex;
    private List<ResultPopUpDTO> mprogressData;

    public ResultPopUpADP(Context mcontex, List<ResultPopUpDTO> mprogressData) {
        this.mcontex = mcontex;
        this.mprogressData = mprogressData;
    }

    @NonNull
    @Override
    public ViewHolderProgressAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mcontex);
        view = mInflater.inflate(R.layout.item_results,parent,false);
        return new ViewHolderProgressAdapter(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProgressAdapter holder, int position)
    {
        holder.optionTV.setText(mprogressData.get(position).getOptionTV());
        holder.DataTV.setText(mprogressData.get(position).getDataTV());
        holder.PercentageTV.setText("("+mprogressData.get(position).getPercentageTV()+"%)");
        holder.progressBar.setProgress(mprogressData.get(position).getPercentageTV());
    }

    @Override
    public int getItemCount() {
        return mprogressData.size();
    }

    public class ViewHolderProgressAdapter extends RecyclerView.ViewHolder{

        TextView optionTV,DataTV,PercentageTV;
        ProgressBar progressBar;
        public ViewHolderProgressAdapter(View itemView) {
            super(itemView);
            optionTV =(TextView)itemView.findViewById(R.id.optionTV);
            DataTV =(TextView)itemView.findViewById(R.id.DataTV);
            PercentageTV =(TextView)itemView.findViewById(R.id.PercentageTV);
            progressBar =(ProgressBar)itemView.findViewById(R.id.progressBar);

        }
    }
}
