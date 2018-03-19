package com.ashish.adit_portal.helper;

/**
 * Created by Ashish on 9/29/2016.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashish.adit_portal.R;

import java.util.ArrayList;


public class OtherDataAdapter extends RecyclerView.Adapter<OtherDataAdapter.ViewHolder> {
    private ArrayList<Notice> countries;
    private Listener listener;
    public interface Listener {
        void onClick(int position);
    }

    public OtherDataAdapter(ArrayList<Notice> countries) {
        this.countries = countries;

    }

    @Override
    public OtherDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        CardView view = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_card_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OtherDataAdapter.ViewHolder viewHolder, int i) {
        Notice notice = countries.get(viewHolder.getAdapterPosition());
        CardView cardView = (CardView) viewHolder.itemView;
        viewHolder.tv_country.setText(notice.getTitle());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(viewHolder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return null != countries ? countries.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_country;
        public ViewHolder(CardView view) {
            super(view);
            tv_country = (TextView) view.findViewById(R.id.other_textbox);
        }

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}