package com.irn.vpn;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.anchorfree.hydrasdk.HydraSDKConfig;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.ClientInfo;
import com.anchorfree.hydrasdk.vpnservice.connectivity.NotificationConfig;
import com.irn.vpn.billing.BillingClientLifecycle;
import com.irn.vpn.data.DataRepository;
import com.irn.vpn.data.disk.AppDatabase;
import com.irn.vpn.data.disk.LocalDataSource;
import com.irn.vpn.data.network.WebDataSource;
import com.irn.vpn.data.network.firebase.FakeServerFunctions;
import com.irn.vpn.data.network.firebase.ServerFunctions;
import com.irn.vpn.data.network.firebase.ServerFunctionsImpl;

public class SubApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initHydraSdk();
    }

    public void initHydraSdk() {
        SharedPreferences prefs = getPrefs();
        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl("https://backend.northghost.com")
                .carrierId("1212_idemo")
                .build();

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .build();

        HydraSdk.setLoggingLevel(Log.VERBOSE);

        HydraSDKConfig config = HydraSDKConfig.newBuilder()
                .observeNetworkChanges(true) //sdk will handle network changes and start/stop vpn
                .captivePortal(true) //sdk will handle if user is behind captive portal wifi
                .moveToIdleOnPause(false)//sdk will report PAUSED state
                .build();
        HydraSdk.init(this, clientInfo, notificationConfig, config);
    }

    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
        SharedPreferences prefs = getPrefs();
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(BuildConfig.STORED_HOST_URL_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_HOST_URL_KEY, hostUrl).apply();
        }

        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(BuildConfig.STORED_CARRIER_ID_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_CARRIER_ID_KEY, carrierId).apply();
        }
        initHydraSdk();
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private final AppExecutors executors = new AppExecutors();

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public LocalDataSource getLocalDataSource() {
        return LocalDataSource.getInstance(executors, getDatabase());
    }

    public ServerFunctions getServerFunctions() {
        if (Constants.USE_FAKE_SERVER) {
            return FakeServerFunctions.getInstance();
        } else {
            return ServerFunctionsImpl.getInstance();
        }
    }

    public WebDataSource getWebDataSource() {
        return WebDataSource.getInstance(executors, getServerFunctions());
    }

    public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository
                .getInstance(getLocalDataSource(), getWebDataSource(), getBillingClientLifecycle());
    }
}
