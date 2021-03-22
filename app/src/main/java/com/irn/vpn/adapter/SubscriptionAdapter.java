package com.irn.vpn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.irn.vpn.R;
import com.irn.vpn.utils.SubscriptionBean;

import java.util.ArrayList;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {
    onCLick onc;
    Context mContext;
    ArrayList<SubscriptionBean> subArray;
    ArrayList<Boolean> flags;

    public SubscriptionAdapter(Context mContext, ArrayList<SubscriptionBean> subArray) {
        this.mContext = mContext;
        this.subArray = subArray;
        flags = new ArrayList<>();
        for (int i = 0; i < subArray.size(); i++) {
            flags.add(false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_subscr, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tv_sub_name.setText(subArray.get(position).getSubName());
        holder.tv_sub_price.setText(subArray.get(position).getPrice());

        if (flags.get(position))
            holder.sub_radio.setChecked(true);
        else
            holder.sub_radio.setChecked(false);

        holder.subscr_main_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onc != null) {
                    onc.onClick(position);
                    for (int i = 0; i < flags.size(); i++) {
                        flags.set(i, false);
                    }
                    flags.set(position, true);

                    notifyItemRangeChanged(0, flags.size());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return subArray.size();
    }


    public interface onCLick {
        public void onClick(int pos);
    }

    public void onClickListener(onCLick oncl) {
        this.onc = oncl;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_sub_name, tv_sub_price;
        LinearLayout ll_sub_plan;
        RadioButton sub_radio;
        CardView subscr_main_card;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_sub_name = itemView.findViewById(R.id.tv_sub_name);
            tv_sub_price = itemView.findViewById(R.id.tv_sub_price);
            ll_sub_plan = itemView.findViewById(R.id.ll_sub_plan);
            sub_radio = itemView.findViewById(R.id.sub_radio);
            subscr_main_card = itemView.findViewById(R.id.subscr_main_card);

        }
    }
}
