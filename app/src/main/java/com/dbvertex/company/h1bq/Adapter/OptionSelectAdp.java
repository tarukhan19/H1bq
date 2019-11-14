package com.dbvertex.company.h1bq.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.model.Poll_OptionData;

import java.util.ArrayList;
import java.util.List;

public class OptionSelectAdp extends RecyclerView.Adapter<OptionSelectAdp.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<Poll_OptionData> mpollData;
    private int mSelectedItemPosition = -1;
    String optionselectType;
    ArrayList<String> ansIdlist;
    ArrayList<String> selectAnsId;

    public OptionSelectAdp(Context mcontex, List<Poll_OptionData> mpollData, String optionselectType, ArrayList<String> ansIdlist, ArrayList<String> selectAnsId) {
        this.mcontex = mcontex;
        this.mpollData = mpollData;
        this.optionselectType = optionselectType;
        this.ansIdlist = ansIdlist;
        this.selectAnsId = selectAnsId;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mcontex);
        view = mInflater.inflate(R.layout.item_poll_option, parent, false);
        return new ViewHolderPollAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

        if (optionselectType.equalsIgnoreCase(Constants.SINGLE_SELECTION)) {

            holder.option1TV.setText(mpollData.get(position).getOptionTV());
            if (mpollData.get(position).isChecked()) {
                holder.imgradio.setImageResource(R.drawable.radio_active);
                mSelectedItemPosition = position;
            } else {
                holder.imgradio.setImageResource(R.drawable.radio_inactive);
            }
            if (selectAnsId.size() == 0) {
                holder.optionLLO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ansIdlist.clear();
                        int previousSelectState = mSelectedItemPosition;
                        mSelectedItemPosition = holder.getAdapterPosition();
                        notifyItemChanged(previousSelectState);
                        //notify new selected Item
                        notifyItemChanged(mSelectedItemPosition);
                        notifyDataSetChanged();
                        ansIdlist.add(mpollData.get(position).getOptionId());

                        // //Log.e("listSentSingle",ansIdlist+"");

                    }
                });
            } else {
                holder.optionLLO.setOnClickListener(null);
            }


            holder.bindDataWithViewHolder(mpollData.get(position), position);

        } else {
            final Poll_OptionData mdata = mpollData.get(position);
            //    //Log.e("checked",mpollData.get(position).isSelected()+"");

            holder.imgradio.setImageResource(mpollData.get(position).isSelected() ? R.drawable.radio_active : R.drawable.radio_inactive);
            holder.option1TV.setText(mpollData.get(position).getOptionTV());

            if (selectAnsId.size() == 0) {
                holder.optionLLO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mdata.setSelected(!mdata.isSelected());
                        holder.imgradio.setImageResource(mdata.isSelected() ? R.drawable.radio_active : R.drawable.radio_inactive);

                        if (mdata.isSelected() == true) {
                            ansIdlist.add(mdata.getOptionId());

                        } else {
                            ansIdlist.remove(mdata.getOptionId());
                        }
                        //       //Log.e("listSentMulti", ansIdlist + "");

                        notifyDataSetChanged();
                    }
                });
            } else {
                holder.optionLLO.setOnClickListener(null);
            }


        }


    }

    @Override
    public int getItemCount() {
        return mpollData != null ? mpollData.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

        TextView option1TV;
        LinearLayout optionLLO, circleA;
        ImageView imgradio;

        private Poll_OptionData mmdata;


        public ViewHolderPollAdapter(View itemView) {
            super(itemView);

            circleA = (LinearLayout) itemView.findViewById(R.id.circleA);
            optionLLO = (LinearLayout) itemView.findViewById(R.id.optionLLO);
            option1TV = (TextView) itemView.findViewById(R.id.option1TV);
            imgradio = (ImageView) itemView.findViewById(R.id.imgradio);

        }

        public void bindDataWithViewHolder(Poll_OptionData mpollData, int position) {

            this.mmdata = mpollData;
            //  //Log.e("checked1",mpollData.isChecked()+"");

            //  imgradio.setImageResource(mpollData.isChecked() ? R.drawable.radio_active : R.drawable.radio_inactive);

            if (position == mSelectedItemPosition) {
                imgradio.setImageResource(R.drawable.radio_active);
            } else {
                imgradio.setImageResource(R.drawable.radio_inactive);
            }

        }
    }


}
