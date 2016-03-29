package com.development.transejecutivosdrivers.apiconfig;

public class ApiConstants {
    public static final String URL_BASE = "http://www.transportesejecutivos.com";
    public static final String API = "api";
    public static final String API_VERSION_1 = "v1";
    public static final String API_VERSION = "v2";
    public static final String LOGIN = "login";
    public static final String SERVICE = "service";
    public static final String SERVICES = "services";
    public static final String SERVICES_GROUPED = "servicesgrouped";
    public static final String RECOVERPASS = "recoverpassword";
    public static final String UPDATE_PROFILE = "updateprofile";
    public static final String SEARCH_PENDING_SERVICE = "searchpendingservice";
    public static final String GET_PENDING_SERVICE = "getpendingservice";
    public static final String UPDATE_STATUS_SERVICE = "updatestatusservice";

    public static final String URL_LOGIN = URL_BASE + "/" + API + "/" + API_VERSION + "/" + LOGIN;
    public static final String URL_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SERVICE;
    public static final String URL_SERVICES = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SERVICES;
    public static final String URL_SERVICES_GROUPED = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SERVICES_GROUPED;
    public static final String URL_RECOVER_PASSWORD = URL_BASE + "/" + API + "/" + API_VERSION_1 + "/" + RECOVERPASS;
    public static final String URL_UPDATE_PROFILE = URL_BASE + "/" + API + "/" + API_VERSION_1 + "/" + UPDATE_PROFILE;
    public static final String URL_SEARCH_PENDING_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SEARCH_PENDING_SERVICE;
    public static final String URL_GET_PENDING_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + GET_PENDING_SERVICE;
    public static final String URL_UPDATE_STATUS_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + UPDATE_STATUS_SERVICE;
}
