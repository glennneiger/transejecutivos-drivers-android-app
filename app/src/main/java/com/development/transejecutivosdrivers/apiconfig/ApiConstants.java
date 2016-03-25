package com.development.transejecutivosdrivers.apiconfig;

public class ApiConstants {
    public static final String URL_BASE = "http://www.transportesejecutivos.com";
    public static final String API = "api";
    public static final String API_VERSION = "v1";
    public static final String LOGIN = "login";
    public static final String SERVICE = "service";
    public static final String SERVICES = "services";
    public static final String SERVICES_GROUPED = "servicesgrouped";
    public static final String RECOVERPASS = "recoverpassword";
    public static final String UPDATE_PROFILE = "updateprofile";
    public static final String DRIVER_PHOTO = "app/conductores/logos/redimensionar_logo.php?imagen=";
    public static final String CAR_PHOTO = "app/conductores/logos/redimensionar_logo.php?imagen=";

    public static final String URL_LOGIN = URL_BASE + "/" + API + "/" + API_VERSION + "/" + LOGIN;
    public static final String URL_SERVICE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SERVICE;
    public static final String URL_SERVICES = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SERVICES;
    public static final String URL_SERVICES_GROUPED = URL_BASE + "/" + API + "/" + API_VERSION + "/" + SERVICES_GROUPED;
    public static final String URL_RECOVER_PASSWORD = URL_BASE + "/" + API + "/" + API_VERSION + "/" + RECOVERPASS;
    public static final String URL_UPDATE_PROFILE = URL_BASE + "/" + API + "/" + API_VERSION + "/" + UPDATE_PROFILE;
    public static final String URL_DRIVER_PHOTO = URL_BASE + "/" + DRIVER_PHOTO;
    public static final String URL_CAR_PHOTO = URL_BASE + "/" + CAR_PHOTO;
}
