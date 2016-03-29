package com.development.transejecutivosdrivers.deserializers;

import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class Deserializer extends DeserializerValidator {
    public JSONArray responseJsonArray = new JSONArray();
    public JSONObject responseJSONObject = new JSONObject();
    public Service service;
    public Passenger passenger;

    public void setResponseJsonArray(JSONArray responseJsonArray) {
        this.responseJsonArray = responseJsonArray;
    }

    public void setResponseJSONObject(JSONObject responseJSONObject) {
        this.responseJSONObject = responseJSONObject;
    }

    public void deserializeOnePassengerAndService() {
        ServiceDeserializer serviceDeserializer = new ServiceDeserializer();
        serviceDeserializer.setJsonObject(this.responseJSONObject);
        serviceDeserializer.deserialize();
        this.service = serviceDeserializer.getService();

        PassengerDeserializer passengerDeserializer = new PassengerDeserializer();
        passengerDeserializer.setJsonObject(this.responseJSONObject);
        passengerDeserializer.deserialize();
        this.passenger = passengerDeserializer.getPassenger();
    }

    public Service getService() {
        return this.service;
    }

    public Passenger getPassenger() {
        return this.passenger;
    }
}
