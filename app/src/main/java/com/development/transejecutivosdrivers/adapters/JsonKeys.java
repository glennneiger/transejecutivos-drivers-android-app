package com.development.transejecutivosdrivers.adapters;

/**
 * Created by william.montiel on 07/03/2016.
 */
public class JsonKeys {
    /**
     * User Json Keys
     */
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_LASTNAME = "lastname";
    public static final String USER_LASTNAMEP = "lastName";
    public static final String USER_USERNAME = "username";
    public static final String USER_EMAIL1 = "email1";
    public static final String USER_EMAIL2 = "email2";
    public static final String USER_PHONE1 = "phone1";
    public static final String USER_PHONE2 = "phone2";
    public static final String USER_APIKEY = "api_key";
    public static final String USER_CODE = "code";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USER_TOKEN = "gcm_token";
    public static final String USER_FIRSTIME = "first_time";
    public static final String USER_NOTIFICATIONS = "notifications";


    /**
     * General Json Keys
     */
    public static final String MESSAGE = "message";
    public static final String ERROR = "error";
    public static final String DATE = "date";
    public static final String APP_VERSION = "version";
    public static final String RESPONSE = "response";

    /**
     * Services Json Keys
     */
    public static final String SERVICES = "services";
    public static final String SERVICE = "service";
    public static final String DATES = "dates";
    public static final String SERVICE_ID = "service_id";
    public static final String SERVICE_REFERENCE = "ref";
    public static final String SERVICE_CREATE_DATE = "date";
    public static final String SERVICE_START_DATE = "start_date";
    public static final String SERVICE_START_TIME = "start_time";
    public static final String SERVICE_END_TIME = "end_time";
    public static final String SERVICE_PAX_CANT = "pax_cant";
    public static final String SERVICE_PAX = "pax";
    public static final String SERVICE_SOURCE = "source";
    public static final String SERVICE_DESTINY = "destiny";
    public static final String SERVICE_STATUS = "status";
    public static final String SERVICE_OBSERVATIONS = "observations";
    public static final String SERVICE_CD = "cd";
    public static final String SERVICE_FLY = "fly";
    public static final String SERVICE_AEROLINE = "aeroline";
    public static final String SERVICE_OLD = "old";
    public static final String SERVICE_TRACE_ID = "trace_id";
    public static final String SERVICE_B1HA = "b1ha";
    public static final String SERVICE_BLS = "bls";
    public static final String SERVICE_PAB = "pab";
    public static final String SERVICE_ST = "st";
    public static final String SERVICE_B1HA_STATUS = "b1haStatus";

    /**
     * Passenger Json Keys
     */
    public static final String PASSENGER_ID = "passenger_id";
    public static final String PASSENGER_CODE = "passenger_code";
    public static final String PASSENGER_NAME = "passenger_name";
    public static final String PASSENGER_LASTNAME = "passenger_lastname";
    public static final String PASSENGER_COMPANY = "company";
    public static final String PASSENGER_PHONE = "phone";
    public static final String PASSENGER_EMAIL = "email";

    /**
     * Date Json Keys
     */
    public static final String DATE_DATA = "date";

    /**
     * Tracing Json Keys
     */
    public static final String TRACING_START = "start";
    public static final String TRACING_END = "end";
    public static final String TRACING_OBSERVATIONS = "observations";
    public static final String TRACING_IMAGE = "image";

    /**
     * Location
     */
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String LOCATION = "location";
    public static final String PRELOCATION = "prelocation";
    public static final String ONSERVICE = "location";

    /**
     * Cache stored service data
     */
    public static final String SERVICE_PREF = "idServiceTransEjecutivosDriversPref";
    public static final String SERVICE_KEY = "idServiceLoaded";

    /**
     * Cache stored general data
     */
    public static final String TAKING_PHOTO_PREF = "takingPhotoTransEjecutivosDriversPref";
    public static final String TAKING_PHOTO_KEY = "isPhotoTaked";
    public static final String TAKING_PHOTO= "photo";

    /**
     * Notifications push
     */
    final static public String NOTIFICATION_DESCRIPTION = "message";
    final static public String NOTIFICATION_DATE        = "createdon";
    final static public String NOTIFICACION_PUTEXTRA    = "notificacionpush";
    final static public String TOKEN_GCM = "tokengcm";
    public static final String HEADER_KEY = "Authorization";
}
