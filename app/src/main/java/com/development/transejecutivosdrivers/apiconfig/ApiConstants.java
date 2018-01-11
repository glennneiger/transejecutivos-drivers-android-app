package com.development.transejecutivosdrivers.apiconfig;

public class ApiConstants {
    public static final String URL_BASE = "http://app.transportesejecutivos.com";
    public static final String API = "api/driverapp";
    public static final String API_VERSION = "v1";
    public static final String LOGIN = "session/apilogin";
    public static final String SERVICES_GROUPED = "servicesgrouped";
    public static final String RECOVERPASS = "session/apirecoverpassword";
    public static final String UPDATE_PROFILE = "updateprofile";
    public static final String SEARCH_PENDING_SERVICE = "searchpendingservice";
    public static final String GET_SERVICE = "getservice";
    public static final String ACCEPT_SERVICE = "acceptordeclineservice";
    public static final String TRACE_SERVICE = "traceservice";
    public static final String CONFIRM_SERVICE = "confirmservice";
    public static final String SET_ON_SOURCE = "setonsource";
    public static final String START_SERVICE = "startservice";
    public static final String SET_LOCATION = "setlocation";
    public static final String FINISH_SERVICE = "finishservice";
    public static final String SET_PRELOCATION = "setprelocation";
    public static final String RESET_PASSWORD = "session/apiresetpassword";
    public static final String SEARCH_SERVICE = "searchservice";
    public static final String RESET_SERVICE = "resetservice";
    public static final String SUPPORT_PHONE = "getsupportphones";
    public static final String APPLICATION_VERSION = "appversion";
    public static final String MAP = "maps";
    public static final String CHANGE_TIME = "changetime";

    public static final String URL_LOGIN = URL_BASE + "/" + LOGIN;
    public static final String URL_SERVICES_GROUPED = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SERVICES_GROUPED;
    public static final String URL_RECOVER_PASSWORD = URL_BASE + "/" + RECOVERPASS;
    public static final String URL_UPDATE_PROFILE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + UPDATE_PROFILE;
    public static final String URL_SEARCH_PENDING_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SEARCH_PENDING_SERVICE;
    public static final String URL_CHANGE_TIME = URL_BASE + "/" + API + "/" + API_VERSION + "/" + CHANGE_TIME;
    public static final String URL_GET_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + GET_SERVICE;
    public static final String URL_ACCEPT_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + ACCEPT_SERVICE;
    public static final String URL_TRACE_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + TRACE_SERVICE;
    public static final String URL_CONFIRM_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + CONFIRM_SERVICE;
    public static final String URL_SET_ON_SOURCE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SET_ON_SOURCE;
    public static final String URL_START_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + START_SERVICE;
    public static final String URL_SET_LOCATION = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SET_LOCATION;
    public static final String URL_FINISH_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + FINISH_SERVICE;
    public static final String URL_SET_PRELOCATION = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SET_PRELOCATION;
    public static final String URL_RESET_PASSWORD = URL_BASE  + "/" + RESET_PASSWORD;
    public static final String URL_SEARCH_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SEARCH_SERVICE;
    public static final String URL_RESET_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + RESET_SERVICE;
    public static final String URL_SUPPORT_PHONE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SUPPORT_PHONE;
    public static final String URL_APP_VERSION= URL_BASE + "/" + API + "/" + API_VERSION + "/" + APPLICATION_VERSION;

    public static final String URL_TEST = URL_BASE + "/" + API + "/" + API_VERSION + "/lala";

    public static final String URL_IMAGE_MAP = URL_BASE + "/" + MAP + "/";
}
