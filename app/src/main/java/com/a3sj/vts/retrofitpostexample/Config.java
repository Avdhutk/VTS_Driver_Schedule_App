package com.a3sj.vts.retrofitpostexample;

/**
 * Created by Avdhut K on 09-03-2017.
 */
public class Config {

    public static final String LOGIN_URL = "http://vts.comeze.com/TrackAppData/DriverLogin.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_FAILURE = "failure";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";
    public static final String RUNNING_PREF_NAME = "isRunning";
    //This would be used to store the email of current logged in user
    public static  String EMAIL_SHARED_PREF = "email";

    public static  String NAME_SHARED_PREF = "name";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static  String LOGGEDIN_SHARED_PREF = "loggedin";
    public static  String JOURNEYSTARTED_SHARED_PREF = "journeystarted";

    public static final String DATA_URL = "http://vts.comeze.com/TrackAppData/getDailySchedule.php";
    public static final String KEY_NAME = "Driver_name";
    public static final String KEY_ROUTE = "Route";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "Time";
    public static final String KEY_BUS = "Bus_Number";


    public static final String KEY_SELECTEDTIME = "departuretime";
    public static final String KEY_SELECTEDDATE = "date";

}
