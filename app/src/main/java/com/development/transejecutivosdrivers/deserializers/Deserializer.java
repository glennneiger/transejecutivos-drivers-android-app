package com.development.transejecutivosdrivers.deserializers;

import com.development.transejecutivosdrivers.models.Date;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class Deserializer extends DeserializerValidator {
    public JSONArray responseJsonArray = new JSONArray();
    public JSONObject responseJSONObject = new JSONObject();
    public Service service;
    public Passenger passenger;


    public JSONArray servicesJsonArray;
    public JSONArray datesJsonArray;
    public ArrayList<ArrayList<Service>> services = new ArrayList<ArrayList<Service>>();
    public ArrayList<Date> datesArrayList = new ArrayList<>();

    public void setResponseJsonArray(JSONArray responseJsonArray) {
        this.responseJsonArray = responseJsonArray;
    }

    public void setResponseJSONObject(JSONObject responseJSONObject) {
        this.responseJSONObject = responseJSONObject;
    }

    public void setDatesJsonArray(JSONArray dates) {
        this.datesJsonArray = dates;
    }

    public void setServicesJsonArray(JSONArray services) {
        this.servicesJsonArray = services;
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

    public void deserializeGroupedServices() {
        try {
            for (int i = 0; i < this.datesJsonArray.length(); i++) {
                String d = (String) this.datesJsonArray.get(i);
                Date date = new Date(d);
                this.datesArrayList.add(date);

                JSONArray services = this.servicesJsonArray.getJSONArray(i);

                ArrayList<Service> currentServicesArray = new ArrayList<>();

                for (int j = 0; j < services.length(); j++) {
                    JSONObject jsonServiceObject = (JSONObject) services.get(j);

                    ServiceDeserializer serviceDeserializer = new ServiceDeserializer();
                    serviceDeserializer.setJsonObject(jsonServiceObject);
                    serviceDeserializer.deserialize();
                    currentServicesArray.add(serviceDeserializer.getService());
                    this.services.add(currentServicesArray);
                }
            }
        }
        catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Service>> getServicesArray() {
        return this.services;
    }

    public ArrayList<Date> getDatesArray() {
        return datesArrayList;
    }

    public Service getService() {
        return this.service;
    }

    public Passenger getPassenger() {
        return this.passenger;
    }
}
