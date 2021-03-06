package com.irn.vpn.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.SessionConfig;
import com.anchorfree.hydrasdk.SessionInfo;
import com.anchorfree.hydrasdk.api.AuthMethod;
import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.api.data.ServerCredentials;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.anchorfree.hydrasdk.api.response.User;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.callbacks.CompletableCallback;
import com.anchorfree.hydrasdk.callbacks.TrafficListener;
import com.anchorfree.hydrasdk.callbacks.VpnStateListener;
import com.anchorfree.hydrasdk.compat.CredentialsCompat;
import com.anchorfree.hydrasdk.dns.DnsRule;
import com.anchorfree.hydrasdk.exceptions.ApiHydraException;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.exceptions.NetworkRelatedException;
import com.anchorfree.hydrasdk.exceptions.RequestException;
import com.anchorfree.hydrasdk.exceptions.VPNException;
import com.anchorfree.hydrasdk.vpnservice.VPNState;
import com.anchorfree.reporting.TrackingConstants;
import com.irn.vpn.Ads;
import com.irn.vpn.Fragments.FragmentVip;
import com.irn.vpn.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends UIActivity implements TrafficListener, VpnStateListener, FragmentVip.RegionChooserInterface {

    private String selectedCountry = "";
    private String selectedCountryName = "";
    private Locale locale;

    @Override
    protected void onStart() {
        super.onStart();
        loginToVpn();
        HydraSdk.addTrafficListener(this);
        HydraSdk.addVpnListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            selectedCountry = intent.getStringExtra("c");
            selectedCountryName = intent.getStringExtra("name");

        }
        if (selectedCountry != null && !VPNState.CONNECTED.equals(true)) {
            locale = new Locale("", selectedCountry);

            imgFlag.setImageResource(this.getResources().getIdentifier("drawable/" + selectedCountry.toLowerCase(), null, this.getPackageName()));
            flagName.setText(locale.getDisplayCountry());

            updateUI();
            connectToVpn();

        }

        FrameLayout adsFrameLayout = (FrameLayout) findViewById(R.id.adPlaceHolderFrameLayout);
        adsFrameLayout.setVisibility(View.VISIBLE);
        new Ads(MainActivity.this, adsFrameLayout).refreshAd(true, true);
    }



    @Override
    protected void onStop() {
        super.onStop();
        HydraSdk.removeVpnListener(this);
        HydraSdk.removeTrafficListener(this);
    }

    @Override
    public void onTrafficUpdate(long bytesTx, long bytesRx) {
        updateUI();
        updateTrafficStats(bytesTx, bytesRx);
    }

    @Override
    public void vpnStateChanged(VPNState vpnState) {
        updateUI();
    }

    @Override
    public void vpnError(HydraException e) {
        updateUI();
        handleError(e);
    }

    @Override
    protected void loginToVpn() {
        AuthMethod authMethod = AuthMethod.anonymous();
        HydraSdk.login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
            }

            @Override
            public void failure(HydraException e) {
                handleError(e);
            }
        });
    }

    @Override
    protected void isConnected(Callback<Boolean> callback) {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                callback.success(vpnState == VPNState.CONNECTED);
            }

            @Override
            public void failure(@NonNull HydraException e) {
                callback.success(false);
            }
        });
    }

    @Override
    protected void connectToVpn() {
        if (selectedCountry == null)
            selectedCountry = HydraSdk.COUNTRY_OPTIMAL;
        if (HydraSdk.isLoggedIn()) {
            showConnectProgress();
            List<String> bypassDomains = new LinkedList<>();
            bypassDomains.add("*facebook.com");
            bypassDomains.add("*wtfismyip.com");
            HydraSdk.startVPN(new SessionConfig.Builder()
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withVirtualLocation(selectedCountry)
                    .addDnsRule(DnsRule.Builder.bypass().fromDomains(bypassDomains))
                    .build(), new Callback<ServerCredentials>() {
                @Override
                public void success(ServerCredentials serverCredentials) {
                    hideConnectProgress();
                    startUIUpdateTask();
                }

                @Override
                public void failure(HydraException e) {
                    hideConnectProgress();
                    updateUI();

                    handleError(e);
                }
            });
        } else {
            loginToVpn();
            showConnectProgress();
            List<String> bypassDomains = new LinkedList<>();
            bypassDomains.add("*facebook.com");
            bypassDomains.add("*wtfismyip.com");
            HydraSdk.startVPN(new SessionConfig.Builder()
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withVirtualLocation(selectedCountry)
                    .addDnsRule(DnsRule.Builder.bypass().fromDomains(bypassDomains))
                    .build(), new Callback<ServerCredentials>() {
                @Override
                public void success(ServerCredentials serverCredentials) {
                    hideConnectProgress();
                    startUIUpdateTask();
                }

                @Override
                public void failure(HydraException e) {
                    hideConnectProgress();
                    updateUI();

                    handleError(e);
                }
            });
        }
    }

    @Override
    protected void disconnectFromVnp() {
        showConnectProgress();
        HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                hideConnectProgress();
                stopUIUpdateTask();
            }

            @Override
            public void error(HydraException e) {
                hideConnectProgress();
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void chooseServer() {

    }

    @Override
    protected void getCurrentServer(final Callback<String> callback) {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    HydraSdk.getSessionInfo(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {

                            Locale locale = new Locale("", CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                            selectedCountryName = locale.getDisplayCountry();
                            selectedCountry = CredentialsCompat.getServerCountry(sessionInfo.getCredentials());
                            setCountryName();

                            callback.success(CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                        }

                        @Override
                        public void failure(@NonNull HydraException e) {
                            callback.success(selectedCountry);
                        }
                    });

                } else {
                    callback.success(selectedCountry);
                    setCountryName();


                }
            }

            @Override
            public void failure(@NonNull HydraException e) {
                callback.failure(e);
            }
        });
    }

    private void setCountryName() {

        if (selectedCountry.equalsIgnoreCase(""))
           flagName.setText(getResources().getString(R.string.autoSelect));
        else
            flagName.setText(selectedCountryName);
    }

    @Override
    protected void checkRemainingTraffic() {
        HydraSdk.remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(HydraException e) {
                updateUI();

                handleError(e);
            }
        });
    }


    @Override
    public void onRegionSelected(Country item) {

        selectedCountry = item.getCountry();
        updateUI();

        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    showMessage("Reconnecting to VPN with " + selectedCountry);
                    HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                        @Override
                        public void complete() {
                            connectToVpn();
                        }

                        @Override
                        public void error(HydraException e) {
                            // In this case we try to reconnect
                            selectedCountry = "";
                            connectToVpn();
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
    }

    // Example of error handling
    public void handleError(Throwable e) {
        Log.w(TAG, e);
        if (e instanceof NetworkRelatedException) {
            showMessage("It Looks Like You Have not Connected With Internet");
        } else if (e instanceof VPNException) {
            switch (((VPNException) e).getCode()) {
                case VPNException.REVOKED:
                    showMessage("User revoked vpn permissions");
                    break;
                case VPNException.VPN_PERMISSION_DENIED_BY_USER:
                    showMessage("User canceled to grant vpn permissions");
                    break;
                case VPNException.HYDRA_ERROR_BROKEN:
                    showMessage("Connection with vpn service was lost");
                    break;
                case VPNException.HYDRA_DCN_BLOCKED_BW:
                    showMessage("Client traffic exceeded");
                    break;
                default:
                    showMessage("Error in VPN Service");
                    break;
            }
        } else if (e instanceof ApiHydraException) {
            switch (((ApiHydraException) e).getContent()) {
                case RequestException.CODE_NOT_AUTHORIZED:
                    /* showMessage("");*/
                    break;
                case RequestException.CODE_TRAFFIC_EXCEED:
                    /* showMessage("Server unavailable");*/
                    break;
                default:
                    /* showMessage("Other error. Check RequestException constants");*/
                    break;
            }
        }
    }




}
