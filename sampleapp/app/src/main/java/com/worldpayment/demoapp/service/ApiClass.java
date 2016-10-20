package com.worldpayment.demoapp.service;

import org.apache.http.NameValuePair;

import java.util.ArrayList;


public class ApiClass {

    public static String masterAPI = "http:www.google.com";
    ArrayList<NameValuePair> postParameters;
    String res;

    //Transaction Details
    public String getTransactionDetails(int params) {
        try {
            res = CustomHttpClient.executeHttpGet(masterAPI + "Method Name here..." + params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
