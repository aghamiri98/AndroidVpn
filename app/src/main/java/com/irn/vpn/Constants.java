/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.irn.vpn;

public class Constants {
    // Use the fake local server data or real remote server.
    public static boolean USE_FAKE_SERVER = false;


    public static final String n1 = "MONTHLY";
    public static final String n2 = "EVERY 3 MONTHS";
    public static final String n3 = "EVERY 6 MONTHS";
    public static final String n4 = "YEARLY";

    public static final String n1_price = "\u20B9 1000";
    public static final String n2_price = "\u20B9 1500";
    public static final String n3_price = "\u20B9 2000";
    public static final String n4_price = "\u20B9 2200";


    //TODO 20200314
    public static final String BASIC_SKU = "indrakvpn";
    public static final String PREMIUM_SKU = "indrakvpn2";
    public static final String PREMIUM_SKU3 = "indrakvpn3";
    public static final String PREMIUM_SKU4 = "indrakvpn4";
    public static final String PLAY_STORE_SUBSCRIPTION_URL
            = "https://play.google.com/store/account/subscriptions";
    public static final String PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL
            = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s";

//    public static final String BASIC_SKU = "basic_subscription";
//    public static final String PREMIUM_SKU = "premium_subscription";
//    public static final String PLAY_STORE_SUBSCRIPTION_URL
//            = "https://play.google.com/store/account/subscriptions";
//    public static final String PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL
//            = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s";
}
