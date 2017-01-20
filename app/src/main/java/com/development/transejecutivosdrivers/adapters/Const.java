package com.development.transejecutivosdrivers.adapters;

/**
 * Created by kevin.ramirez on 25/07/2016.
 */
public class Const {
    //GCM
    final static public String CONST_GCM_DEFAULTSENDERID = "442225472160";
    final static public String CONST_GCM_REGISTER_SUCCES = "RegistrationSuccess";
    final static public String CONST_GCM_REGISTER_ERROR = "RegistrationError";
    final static public String CONST_GMC_MESSAGE_REGISTER_ERROR = "Hubo un error en el registro GCM";
    final static public String CONST_GOOGLE_PLAY_NOT_INSTALL = "Google play services no se encuentra instalado en el dipositivo";
    final static public String CONST_GOOGLE_PLAY_NOT_SUPPORT = "El dispositivo no tiene soporte para Google Play Services";

    //NOTIFICATION PUSH
    final static public String CONST_NOTIFICATION_PUSH_BODY = "message";
    final static public String CONST_NOTIFICATION_PUSH_TITLE = "msgtitle";
    final static public String CONST_NOTIFICACION_PUSH_CRITERIA = "criteria";
    //error
    final static public String CONST_MSG_ERROR_SERVER = "Ocurrió un error en el servidor, por favor intentar más tarde.";

    //Exit Aplication
    final static public String CONST_MSG_EXIT_APP = "";

    //Call Phone
    final static public String CONST_MSG_CALL_PHONE = "Ocurrió un error, numero telefonico no valido";

    //URL
    final static public String CONST_MSG_URL_ERROR = "Ocurrió un error, url no valida";
    final static public String error404 = "Recurso no encontrado";

    public interface ACTION {
        public static String MAIN_ACTION = "action.main";
        public static String STARTFOREGROUND_ACTION = "action.startforeground";
        public static String STOPFOREGROUND_ACTION = "action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
