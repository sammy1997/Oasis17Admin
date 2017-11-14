package com.example.sammy.oasisadminapp17;

/**
 * Created by sammy on 19/10/17.
 */

public class URLS {
//    public static String hostname="https://bits-oasis.org/2017/api/";
    public static String hostname="http://192.168.43.141:8000/api/";
    public static String tokenUrl=hostname+"api_token";
    public static String tokenRefresh=hostname+"api_token_refresh";
    public static String fetch_events=hostname+"get_events_cd";
    public static String getProfile=hostname+"get_profile";
    public static String registerTeam=hostname+"register_team";
    public static String profshows=hostname+"profshows/";
    public static String validateProf=hostname+"validate_prof_show";
    public static String addProf=hostname+"add_prof_show/";
    public static String notify="https://fcm.googleapis.com/fcm/send";
}
