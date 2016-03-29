package com.development.transejecutivosdrivers.deserializers;

import org.json.JSONException;
import org.json.JSONObject;

public class DeserializerValidator {
    public String validateString(String key, JSONObject jsonObject) {
        try {
            String value = jsonObject.getString(key).trim();
            if (!value.isEmpty() && value != null && value != "null" && value != "" && value != "null") {
                return value;
            }
            return "";
        }
        catch(JSONException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public double validateDouble(String key, JSONObject jsonObject) {
        try {
            String value = jsonObject.getString(key).trim();
            if (!value.isEmpty() && value != null && value != "null" && value != "" && value != "null") {
                return Double.parseDouble(value);
            }
            return 0;
        }
        catch(JSONException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int validateInt(String key, JSONObject jsonObject) {
        try {
            String value = jsonObject.getString(key).trim();
            if (!value.isEmpty() && value != null && value != "null" && value != "" && value != "null") {
                return Integer.parseInt(value);
            }
            return 0;
        }
        catch(JSONException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public String validateDate(String key1, String key2,JSONObject jsonObject) {
        try {
            String end = jsonObject.getString(key1).trim();
            String start = jsonObject.getString(key2).trim();
            if (!end.isEmpty() && end != null && end != "null" && end != "" && end != "null") {
                return start + " - " + end;
            }
            else {
                return start;
            }
        }
        catch(JSONException ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
