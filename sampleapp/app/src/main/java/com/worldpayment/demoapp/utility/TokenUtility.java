package com.worldpayment.demoapp.utility;

import android.content.Context;
import android.preference.PreferenceManager;

import com.worldpay.library.domain.Card;
import com.worldpay.library.enums.CardSourceType;
import com.worldpay.library.webservices.services.ServiceRequest;
import com.worldpayment.demoapp.BuildConfig;

import java.util.HashMap;
import java.util.Map;

import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;

public class TokenUtility {

    public static void populateRequestHeaderFields(ServiceRequest request, Context context) {
        String authToken = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_AUTH_TOKEN, null);
        request.setAuthToken(authToken);
        request.setMerchantId(BuildConfig.MERCHANT_ID);
        request.setMerchantKey(BuildConfig.MERCHANT_KEY);
        request.setDeveloperId(BuildConfig.DEVELOPER_ID);
        request.setApplicationVersion(BuildConfig.VERSION_NAME);
    }

    public static Card getTestCard() {
        Card card = new Card();
        card.setNumber("4111111111111111");
        card.setExpirationMonth(12);
        card.setExpirationYear(2020);
        card.setFirstName("Visa");
        card.setLastName("Test");
        card.setCvv("999");
        card.setSourceType(CardSourceType.CREDIT_MANUAL);
        return card;
    }

    public HashMap<String, String> getStates() {

        HashMap<String, String> hashMap = new HashMap<String, String>() {
            {
                put("AL", "Alabama");
                put("AK", "Alaska");
                put("AZ", "Arizona");
                put("AR", "Arkansas");
                put("CA", "California");
                put("CO", "Colorado");
                put("CT", "Connecticut");
                put("DE", "Delaware");
                put("FL", "Florida");
                put("GA", "Georgia");
                put("HI", "Hawaii");
                put("ID", "Idaho");
                put("IL", "Illinois");
                put("IN", "Indiana");
                put("IA", "Iowa");
                put("KS", "Kansas");
                put("KY", "Kentucky");
                put("LA", "Louisiana");
                put("ME", "Maine");
                put("MD", "Maryland");
                put("MA", "Massachusetts");
                put("MI", "Michigan");
                put("MN", "Minnesota");
                put("MS", "Mississippi");
                put("MO", "Missouri");
                put("MT", "Montana");
                put("NE", "Nebraska");
                put("NV", "Nevada");
                put("NH", "New Hampshire");
                put("NJ", "New Jersey");
                put("NM", "New Mexico");
                put("NY", "New York");
                put("NC", "North Carolina");
                put("ND", "North Dakota");
                put("OH", "Ohio");
                put("OK", "Oklahoma");
                put("OR", "Oregon");
                put("PA", "Pennsylvania");
                put("RI", "Rhode Island");
                put("SC", "South Carolina");
                put("SD", "South Dakota");
                put("TN", "Tennessee");
                put("TX", "Texas");
                put("UT", "Utah");
                put("VT", "Vermont");
                put("VA", "Virginia");
                put("WA", "Washington");
                put("WV", "West Virginia");
                put("WI", "Wisconsin");
                put("WY", "Wyoming");
            }
        };
        return hashMap;
    }

    public <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
