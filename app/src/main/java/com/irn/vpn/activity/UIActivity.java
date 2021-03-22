package com.irn.vpn.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.vpnservice.VPNState;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.irn.vpn.Constants;
import com.irn.vpn.Fragments.DialFrag;
import com.irn.vpn.R;
import com.irn.vpn.SubApp;
import com.irn.vpn.billing.BillingClientLifecycle;
import com.irn.vpn.newAct.BillingViewModel;
import com.irn.vpn.newAct.FirebaseUserViewModel;
import com.irn.vpn.newAct.SubscriptionStatusViewModel;
import com.irn.vpn.speed.Speed;
import com.irn.vpn.speed.TotalTraffic;
import com.irn.vpn.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public abstract class UIActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TAG = MainActivity.class.getSimpleName();
    private InterstitialAd mInterstitialAd;
    private int adCount = 0;
    VPNState state;
    int progressBarValue = 0;
    Handler handler = new Handler();
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    LinearLayout ll_ads;

    /*@BindView(R.id.toolbar)
    protected Toolbar toolbar;*/

    /*@BindView(R.id.tv_connect_message)
    TextView connectedMessage;*/


    public static final int HOME_PAGER_INDEX = 0;
    public static final int PREMIUM_PAGER_INDEX = 1;
    public static final int SETTINGS_PAGER_INDEX = 2;

    private static final int RC_SIGN_IN = 0;
    private static final int COUNT = 3;


    private BillingClientLifecycle billingClientLifecycle;

    private FirebaseUserViewModel authenticationViewModel;
    BillingViewModel billingViewModel;
    SubscriptionStatusViewModel subscriptionViewModel;


    @BindView(R.id.tv_timer)
    TextView timerTextView;
    @BindView(R.id.tvDownloadSpeed)
    TextView tvDownloadSpeed;
    @BindView(R.id.tvUploadSpeed)
    TextView tvUploadSpeed;



    @BindView(R.id.flag_image)
    ImageView imgFlag;

    @BindView(R.id.connect_btn)
    ImageView connectBtnTextView;
    @BindView(R.id.imgRateUs)
    ImageView imgRateUs;
    @BindView(R.id.imagepurc)
    ImageView imagepurc;
    @BindView(R.id.imgShare)
    ImageView imgShare;

    @BindView(R.id.connection_state)
    TextView connectionStateTextView;

    @BindView(R.id.tvCounty)
    TextView flagName;

    @BindView(R.id.laySelectServer)
    LinearLayout laySelectServer;

    @BindView(R.id.connection_progress)
    ProgressBar connectionProgressBar;


    private AdView mAdView;
    //    com.facebook.ads.AdView fbAdView;
    LinearLayout adContainer;

//    private InterstitialAd interstitialAd;
//    com.facebook.ads.InterstitialAd fbInterstitialAd;

    int key = 0;

    private BroadcastReceiver trafficReceiver;
    public static final String TRAFFIC_ACTION = "traffic_action";

    Handler handlerTrafic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);

        mAdView = (AdView) findViewById(R.id.adViewmain);
//        adContainer = (LinearLayout) findViewById(R.id.banner_container);

        adMobBanner();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.Admob_intertesial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startActivity(new Intent(UIActivity.this, Servers.class));

            }
        });
        ButterKnife.bind(this);
      /*  setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/


        laySelectServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    startActivity(new Intent(UIActivity.this, Servers.class));
            }
        });


        imgRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rateUs();
            }
        });

        imagepurc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDial();
            }
        });
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Deo vpn provide all server free https://play.google.com/store/apps/details?id=" + getPackageName());
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }


            }
        });


        authenticationViewModel = ViewModelProviders.of(this).get(FirebaseUserViewModel.class);
        billingViewModel = ViewModelProviders.of(this).get(BillingViewModel.class);
        subscriptionViewModel = ViewModelProviders.of(this).get(SubscriptionStatusViewModel.class);

        billingClientLifecycle = ((SubApp) getApplication()).getBillingClientLifecycle();
        getLifecycle().addObserver(billingClientLifecycle);

        // Register purchases when they change.
        billingClientLifecycle.purchaseUpdateEvent.observe(this, new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                if (purchases != null) {
                    registerPurchases(purchases);
                }
            }
        });

        // Launch billing flow when user clicks button to buy something.
        billingViewModel.buyEvent.observe(this, new Observer<BillingFlowParams>() {
            @Override
            public void onChanged(BillingFlowParams billingFlowParams) {
                if (billingFlowParams != null) {
                    billingClientLifecycle
                            .launchBillingFlow(UIActivity.this, billingFlowParams);
                }
            }
        });

        // Open the Play Store when event is triggered.
        billingViewModel.openPlayStoreSubscriptionsEvent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String sku) {
                Log.i(TAG, "Viewing subscriptions on the Google Play Store");
                String url;
                if (sku == null) {
                    // If the SKU is not specified, just open the Google Play subscriptions URL.
                    url = Constants.PLAY_STORE_SUBSCRIPTION_URL;
                } else {
                    // If the SKU is specified, open the deeplink for this SKU on Google Play.
                    url = String.format(Constants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL,
                            sku, getApplicationContext().getPackageName());
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // Update authentication UI.
        final Observer<FirebaseUser> fireaseUserObserver = new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable final FirebaseUser firebaseUser) {
                invalidateOptionsMenu();
//                if (firebaseUser == null) {
//                    triggerSignIn();
//                } else {
//                    Log.d(TAG, "Current user: "
//                            + firebaseUser.getEmail() + " " + firebaseUser.getDisplayName());
//                }
            }
        };
        authenticationViewModel.firebaseUser.observe(this, fireaseUserObserver);

        // Update subscription information when user changes.
        authenticationViewModel.userChangeEvent.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                subscriptionViewModel.userChanged();
                List<Purchase> purchases = billingClientLifecycle.purchaseUpdateEvent.getValue();
                if (purchases != null) {
                    registerPurchases(purchases);
                }
            }
        });

       /* startService(getServiceIntent(UIActivity.this));


        trafficReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiveTraffic(context, intent);
            }
        };

        registerReceiver(trafficReceiver, new IntentFilter(TRAFFIC_ACTION));*/
        handlerTrafic = new Handler();
        handleTraficData();

    }

    /**
     * Register SKUs and purchase tokens with the server.
     */
    private void registerPurchases(List<Purchase> purchaseList) {
        for (Purchase purchase : purchaseList) {
            String sku = purchase.getSku();
            String purchaseToken = purchase.getPurchaseToken();
            Log.d(TAG, "Register purchase with sku: " + sku + ", token: " + purchaseToken);
            subscriptionViewModel.registerSubscription(sku, purchaseToken);
        }
    }

    private void triggerSignIn() {
        Log.d(TAG, "Attempting SIGN-IN!");
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        // Configure the different methods users can sign in
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
        providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If sign-in is successful, update ViewModel.
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Sign-in SUCCESS!");
                authenticationViewModel.updateFirebaseUser();
            } else {
                Log.d(TAG, "Sign-in FAILED!");
            }
        } else {
            Log.e(TAG, "Unrecognized request code: " + requestCode);
        }
    }


    private long mLastRxBytes = 0;
    private long mLastTxBytes = 0;
    private long mLastTime = 0;

    private Speed mSpeed;


    private void handleTraficData() {

        if (handlerTrafic == null)
            return;


        handlerTrafic.postDelayed(new Runnable() {
            @Override
            public void run() {

                setTraficData();
            }
        }, 1000);


    }

    private void setTraficData() {
        long currentRxBytes = TrafficStats.getTotalRxBytes();

        long currentTxBytes = TrafficStats.getTotalTxBytes();

        long usedRxBytes = currentRxBytes - mLastRxBytes;
        long usedTxBytes = currentTxBytes - mLastTxBytes;
        long currentTime = System.currentTimeMillis();
        long usedTime = currentTime - mLastTime;

        mLastRxBytes = currentRxBytes;
        mLastTxBytes = currentTxBytes;
        mLastTime = currentTime;

        mSpeed = new Speed(this);
        mSpeed.calcSpeed(usedTime, usedRxBytes, usedTxBytes);

//            mIndicatorNotification.updateNotification(mSpeed);
        Log.e("speed-->>", "down-->>" + mSpeed.down.speedValue + "    upload-->>" + mSpeed.up.speedValue);


        if (mSpeed != null && mSpeed.up != null && mSpeed.down != null) {


            tvDownloadSpeed.setText(mSpeed.down.speedValue + " " + mSpeed.down.speedUnit);
            tvUploadSpeed.setText(mSpeed.up.speedValue + " " + mSpeed.up.speedUnit);

//            sendBroadcast(traffic);
        }

        handleTraficData();
    }


    private void receiveTraffic(Context context, Intent intent) {

        String in = "";
        String out = "";

        in = String.format(getResources().getString(R.string.traffic_in),
                intent.getStringExtra(TotalTraffic.DOWNLOAD_ALL));
        out = String.format(getResources().getString(R.string.traffic_out),
                intent.getStringExtra(TotalTraffic.UPLOAD_ALL));

        tvDownloadSpeed.setText(in);
        tvUploadSpeed.setText(out);


    }


    @Override
    public void onBackPressed() {
      /*  DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {*/
        super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_glob) {

            key = 1;

            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                startActivity(new Intent(this, Servers.class));
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_upgrade) {

            key = 1;

            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                startActivity(new Intent(this, Servers.class));
            }

        } else if (id == R.id.nav_helpus) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"infinityvideoreward@courseunity.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Improve Comments");
            intent.putExtra(Intent.EXTRA_TEXT, "message body");

            try {
                startActivity(Intent.createChooser(intent, "send mail"));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "No mail app found!!!", Toast.LENGTH_SHORT);
            } catch (Exception ex) {
                Toast.makeText(this, "Unexpected Error!!!", Toast.LENGTH_SHORT);
            }
        } else if (id == R.id.nav_rate) {
            rateUs();
        } else if (id == R.id.nav_share) {

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Deo vpn provide all server free https://play.google.com/store/apps/details?id=com.inapp.vpn");
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, Settings.class));
        } else if (id == R.id.nav_faq) {

            key = 2;
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

    protected abstract void loginToVpn();

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    disconnectAlert();
                    //disconnectFromVnp();
                } else {
                    updateUI();
                    connectToVpn();
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                state = vpnState;
                switch (vpnState) {
                    case IDLE: {
                        loadIcon();
                        connectBtnTextView.setEnabled(true);
                        connectionStateTextView.setText(R.string.disconnected);
                        timerTextView.setVisibility(View.GONE);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        loadIcon();
                        connectBtnTextView.setEnabled(true);
                        connectionStateTextView.setText(R.string.connected);
                        timer();
//                        timerTextView.setVisibility(View.VISIBLE);//-w7shal
                        hideConnectProgress();
                        loadAd();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        loadIcon();
                        connectionStateTextView.setText(R.string.connecting);
                        connectBtnTextView.setEnabled(false);
                        timerTextView.setVisibility(View.GONE);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
                        // connectBtnTextView.setBackgroundResource(R.drawable.icon_connect);
                        connectBtnTextView.setBackgroundResource(R.drawable.pause);
                        connectionStateTextView.setText(R.string.paused);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void failure(@NonNull HydraException e) {
               /* currentServerBtn.setText(R.string.optimal_server);
                selectedServerTextView.setText("UNKNOWN");*/
            }
        });
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        //trafficStats.setText(getResources().getString(R.string.traffic_stats, outString, inString));
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
            //trafficLimitTextView.setText("UNLIMITED available");
        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

            //trafficLimitTextView.setText(getResources().getString(R.string.traffic_limit, trafficUsed, trafficLimit));
        }
    }

    protected void showConnectProgress() {
        // connectionProgressBar.setProgress(10);
        connectionProgressBar.setVisibility(View.VISIBLE);
        //connectionStateTextView.setVisibility(View.GONE);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
                    progressBarValue++;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            connectionProgressBar.setProgress(progressBarValue);
                            //  ShowText.setText(progressBarValue +"/"+Progressbar.getMax());

                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void hideConnectProgress() {
        connectionProgressBar.setVisibility(View.GONE);
//        connectionStateTextView.setVisibility(View.VISIBLE); //----------w7shal
    }

    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flag to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    protected void timer() {
        if (adCount == 0) {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            timeSwapBuff += timeInMilliseconds;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerTrafic = null;
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerTextView.setText(String.format("%02d", hrs) + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };

    protected void loadAd() {

        if (adCount == 0) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                adCount++;
            }
        }
    }

    protected void loadIcon() {
        if (state == VPNState.IDLE) {

            connectBtnTextView.setImageResource(R.drawable.play);
            // Glide.with(this).load(R.drawable.ic_connect).into(connectBtnTextView);

            //imgGif.setRotationY(180);
//            Glide.with(this).load(R.drawable.bull_test).into(imgGif);
//            connectedMessage.setVisibility(View.INVISIBLE);

        } else if (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {

//            imgGif.setRotationY(0);
//            imgGif.setVisibility(View.VISIBLE);
//            Glide.with(this).asGif().load(R.drawable.bull).into(imgGif);
//            connectBtnTextView.setVisibility(View.INVISIBLE);
//            connectedMessage.setVisibility(View.INVISIBLE);

        } else if (state == VPNState.CONNECTED) {

            Glide.with(this).load(R.drawable.pause).into(connectBtnTextView);
//            Glide.with(this).load(R.drawable.ic_connect_done).into(imgGif);
            connectBtnTextView.setVisibility(View.VISIBLE);
//            connectedMessage.setVisibility(View.VISIBLE);
        }
    }

    protected void disconnectAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to disconnect?");
        builder.setPositiveButton("Disconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        disconnectFromVnp();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    /*private void fbBannerShow() {

        fbAdView = new com.facebook.ads.AdView(UIActivity.this, getString(R.string.fb_banner_id), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        adContainer.addView(fbAdView);
        fbAdView.loadAd();

        fbAdView.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
//                adMobBanner();
                Log.d("Tag", "onError: <--" + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
    }*/

    private void adMobBanner() {

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
                Log.e("Tag", "onAdFailedToLoad: errorCode: " + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {

            }
        });
    }

    boolean dialOnce = false;

    public void ShowDial() {
        if (!dialOnce) {
            FragmentManager fm = getSupportFragmentManager();
            DialFrag DialRec = DialFrag.newInstance();
            DialRec.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
            DialRec.show(fm, "");
            DialRec.doClick(new DialFrag.Infc() {
                @Override
                public void passdata(int pos) {
                    switch (pos) {
                        case 0:
                            billingViewModel.buyBasic();
                            break;

                        case 1:
                            billingViewModel.buyPremium();
                            break;

                        case 2:
                            billingViewModel.buyPremium3();
                            break;

                        case 3:
                            billingViewModel.buyPremium4();
                            break;

                        case 5:

                            break;
                    }
                    dialOnce = false;

                }
            });


        }
        dialOnce = true;

    }
}
