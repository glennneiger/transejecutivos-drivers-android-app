package com.development.transejecutivosdrivers.deserializers;

import android.util.Log;

import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.models.Date;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class Deserializer extends DeserializerValidator {
    public JSONArray responseJsonArray = new JSONArray();
    public JSONObject responseJSONObject = new JSONObject();
    public Service service;
    public Passenger passenger;


    public JSONArray servicesJsonArray;
    public JSONArray passengersJsonArray;
    public JSONArray datesJsonArray;
    public ArrayList<ArrayList<Service>> services = new ArrayList<ArrayList<Service>>();
    ArrayList<Passenger> passengers = new ArrayList<Passenger>();
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

    public void deserializeMultiplePassengerAndService() {
        ServiceDeserializer serviceDeserializer = new ServiceDeserializer();
        serviceDeserializer.setJsonObject(this.responseJSONObject);
        serviceDeserializer.deserialize();
        this.service = serviceDeserializer.getService();

        try {
            this.passengersJsonArray = this.responseJSONObject.getJSONArray(JsonKeys.PASSENGERS);

            for (int i = 0; i < this.passengersJsonArray.length(); i++) {
                JSONObject jsonPassengerObject = (JSONObject) this.passengersJsonArray.get(i);

                PassengerDeserializer passengerDeserializer = new PassengerDeserializer();
                passengerDeserializer.setJsonObject(jsonPassengerObject);
                passengerDeserializer.deserialize();
                this.passengers.add(passengerDeserializer.getPassenger());
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
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
                //String df = DateFormat.getDateInstance(DateFormat.MEDIUM).format(d);
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

                }

                this.services.add(currentServicesArray);
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

    public ArrayList<Passenger> getPassengers() {
        return this.passengers;
    }

    public Service getService() {
        return this.service;
    }

    public Passenger getPassenger() {
        return this.passenger;
    }
}
