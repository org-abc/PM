package com.kondie.pocketmechanic;

/**
 * Created by kondie on 2018/02/11.
 */

public class Constants {

    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    public static final String PACKAGE_NAME = "com.kondie.pocketmechanic";
    public static final String GOOGLE_API_KEY = "AIzaSyBwBtDsNntA7GG8yc_A0flfWvqtSCXABQw";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String PM_HOSTING_WEBSITE = "https://pocket-mechanic-268506.appspot.com/";
    public static final String BUCKET_NAME = "pocket_mechanic_bucket2";
    public static final String WRONG_PART = BUCKET_NAME + ".storage.googleapis.com";
    public static final String CORRECT_PART = "storage.googleapis.com/" + BUCKET_NAME;
    public static final String[] serviceOptions = {"Towing", "Flat tire", "Engine problems", "Flat/dead battery", "Faulty brakes", "Electronics", "Lights", "Other"};
}
