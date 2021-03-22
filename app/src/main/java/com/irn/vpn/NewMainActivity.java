package com.irn.vpn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.irn.vpn.speed.TotalTraffic;


public class NewMainActivity extends AppCompatActivity {


    private BroadcastReceiver trafficReceiver;

    public static final String TRAFFIC_ACTION = "traffic_action";


    TextView tvDownloadSpeed, tvUploadSpeed , tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);
        tvDownloadSpeed = (TextView) findViewById(R.id.tvDownloadSpeed);
        tvUploadSpeed = (TextView) findViewById(R.id.tvUploadSpeed);
//        tvTotal = (TextView) findViewById(R.id.tvTotal);


    }


    private void receiveTraffic(Context context, Intent intent) {

        String in = "";
        String out = "";
        String total = "";

        in = String.format(getResources().getString(R.string.traffic_in),
                intent.getStringExtra(TotalTraffic.DOWNLOAD_ALL));
        out = String.format(getResources().getString(R.string.traffic_out),
                intent.getStringExtra(TotalTraffic.UPLOAD_ALL));

        total = String.format(getResources().getString(R.string.traffic_out),
                intent.getStringExtra(TotalTraffic.TOTAL));
        String data;
        Log.i("SERVER", "" + intent.getStringExtra(TotalTraffic.DOWNLOAD_SESSION));
        /*if (intent.getStringExtra(TotalTraffic.DOWNLOAD_SESSION).contains("MB")) {
            data = intent.getStringExtra(TotalTraffic.DOWNLOAD_SESSION).replace("MB", "");
            Log.i("SERVER", "data  " + data);
            float use = Float.parseFloat(data);
            Log.i("SERVER", "use  " + use);
                    *//*if(use>500){
                        if(Constantdata.getRateCount(ServerActivity.this)==0) {
                            ShowRating();
                        }
                    }*//*
        }*/

        tvDownloadSpeed.setText(in);
        tvUploadSpeed.setText(out);
        tvTotal.setText(total);


    }


}
