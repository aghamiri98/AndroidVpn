package com.irn.vpn.data.network.firebase;

import androidx.lifecycle.LiveData;

import com.irn.vpn.data.ContentResource;
import com.irn.vpn.data.SubscriptionStatus;

import java.util.List;

/**
 * Interface to perform the Firebase Function calls and expose the results with
 * {@link #getSubscriptions}.
 * <p>
 * Use this class by observing the {@link #getSubscriptions} LiveData.
 * Any server updates will be communicated through this LiveData.
 */
public interface ServerFunctions {
    /**
     * Live data is true when there are pending network requests.
     */
    LiveData<Boolean> getLoading();

    /**
     * The latest subscription data from the server.
     * <p>
     * Must be observed and active in order to receive updates from the server.
     */
    LiveData<List<SubscriptionStatus>> getSubscriptions();

    /**
     * The basic content URL.
     */
    LiveData<ContentResource> getBasicContent();

    /**
     * The premium content URL.
     */
    LiveData<ContentResource> getPremiumContent();
    LiveData<ContentResource> getPremiumContent3();
    LiveData<ContentResource> getPremiumContent4();

    /**
     * Fetch basic content and post results to {@link #getBasicContent()}.
     * This will fail if the user does not have a basic subscription.
     */
    void updateBasicContent();

    /**
     * Fetch premium content and post results to {@link #getPremiumContent()}.
     * This will fail if the user does not have a premium subscription.
     */
    void updatePremiumContent();

    void updatePremiumContent3();

    void updatePremiumContent4();

    /**
     * Fetches subscription data from the server and posts successful results to
     * {@link #getSubscriptions}.
     */
    void updateSubscriptionStatus();

    /**
     * Register a subscription with the server and posts successful results to
     * {@link #getSubscriptions}.
     */
    void registerSubscription(String sku, String purchaseToken);

    /**
     * Transfer subscription to this account posts successful results to
     * {@link #getSubscriptions}.
     */
    void transferSubscription(String sku, String purchaseToken);

    /**
     * Register Instance ID when the user signs in or the token is refreshed.
     */
    void registerInstanceId(String instanceId);

    /**
     * Unregister when the user signs out.
     */
    void unregisterInstanceId(String instanceId);
}
