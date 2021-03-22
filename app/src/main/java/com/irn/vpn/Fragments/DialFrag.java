package com.irn.vpn.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.irn.vpn.Constants;
import com.irn.vpn.R;
import com.irn.vpn.adapter.SubscriptionAdapter;
import com.irn.vpn.utils.SubscriptionBean;

import java.util.ArrayList;


public class DialFrag extends DialogFragment {

    View tv_close;
    Infc interfac;
    RecyclerView rec_subscriptions;
    SubscriptionAdapter subSAdp;
    ArrayList<SubscriptionBean> SubArray;

    String[] Name = {Constants.n1, Constants.n2, Constants.n3, Constants.n4};
    String[] SKU = {Constants.BASIC_SKU, Constants.PREMIUM_SKU, Constants.PREMIUM_SKU3, Constants.PREMIUM_SKU4};
    String[] Price = {Constants.n1_price, Constants.n2_price, Constants.n3_price, Constants.n4_price};
    Button btn_buy;
    int positions;


    public interface Infc {
        public void passdata(int pos);
    }

    public void doClick(Infc in) {
        interfac = in;
    }

    public DialFrag() {
        // Required empty public constructor
    }


    public static DialFrag newInstance() {
        DialFrag fragment = new DialFrag();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.SubscriptionDialog;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return v;
    }

    @Override

    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SubArray = new ArrayList<>();
        for (int i = 0; i < Name.length; i++) {
            SubscriptionBean sB = new SubscriptionBean();
            sB.setSubName(Name[i]);
            sB.setPrice(Price[i]);
            sB.setSKU(SKU[i]);
            SubArray.add(sB);
        }

        rec_subscriptions = view.findViewById(R.id.rec_subscriptions);
        rec_subscriptions.setLayoutManager(new LinearLayoutManager(view.getContext()));
        tv_close = view.findViewById(R.id.tv_close);
        btn_buy = view.findViewById(R.id.btn_buy);
        tv_close.setVisibility(View.VISIBLE);
        subSAdp = new SubscriptionAdapter(view.getContext(), SubArray);
        subSAdp.onClickListener(new SubscriptionAdapter.onCLick() {
            @Override
            public void onClick(int pos) {
                positions = pos;
            }
        });
        rec_subscriptions.setAdapter(subSAdp);

        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dismiss();
//                MainActivity.pip = 0;
                if (interfac != null) {
                    dismiss();
                    interfac.passdata(5);
                }
            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dismiss();
//                MainActivity.pip = 0;
                if (interfac != null) {
                    dismiss();
                    interfac.passdata(positions);
                }
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }


}
