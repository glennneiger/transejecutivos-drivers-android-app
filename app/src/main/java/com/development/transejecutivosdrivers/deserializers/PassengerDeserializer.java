package com.development.transejecutivosdrivers.deserializers;

import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.models.City;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Source;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class PassengerDeserializer extends DeserializerValidator{
    public JSONObject jsonObject = new JSONObject();
    public Passenger passenger = new Passenger();

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void deserialize() {
        int idPassenger = validateInt(JsonKeys.PASSENGER_ID, jsonObject);

        if (idPassenger != 0) {
            try {
                Source source = new Source();
                JSONObject sourceJsonObj = jsonObject.getJSONObject(JsonKeys.PASSENGER_SOURCE);
                source.setIdServiceSource(validateInt(JsonKeys.SOURCE_ID, sourceJsonObj));
                source.setLatitude(validateString(JsonKeys.SOURCE_LATITUDE, sourceJsonObj));
                source.setLongitude(validateString(JsonKeys.SOURCE_LONGITUDE, sourceJsonObj));
                source.setAddress(validateString(JsonKeys.SOURCE_ADDRESS, sourceJsonObj));
                source.setPlaceId(validateString(JsonKeys.SOURCE_PLACEID, sourceJsonObj));

                JSONObject cityJsonObj = sourceJsonObj.getJSONObject(JsonKeys.SOURCE_CITY);
                City city = new City();
                city.setIdCity(validateInt(JsonKeys.CITY_ID, cityJsonObj));
                city.setName(validateString(JsonKeys.CITY_NAME, cityJsonObj));
                source.setCity(city);

                this.passenger.setIdPassenger(idPassenger);
                this.passenger.setName(validateString(JsonKeys.PASSENGER_NAME, jsonObject));
                this.passenger.setLastname(validateString(JsonKeys.PASSENGER_LASTNAME, jsonObject));
                this.passenger.setPhone1(validateString(JsonKeys.PASSENGER_PHONE1, jsonObject));
                this.passenger.setPhone2(validateString(JsonKeys.PASSENGER_PHONE2, jsonObject));
                this.passenger.setEmail1(validateString(JsonKeys.PASSENGER_EMAIL1, jsonObject));
                this.passenger.setEmail2(validateString(JsonKeys.PASSENGER_EMAIL2, jsonObject));
                this.passenger.setSource(source);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Passenger getPassenger() {
        return this.passenger;
    }
}
