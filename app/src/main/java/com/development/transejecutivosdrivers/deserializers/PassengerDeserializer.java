package com.development.transejecutivosdrivers.deserializers;

import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.models.Passenger;
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
            String code = validateString(JsonKeys.PASSENGER_CODE, jsonObject);
            String name = validateString(JsonKeys.PASSENGER_NAME, jsonObject);
            String lastName = validateString(JsonKeys.PASSENGER_LASTNAME, jsonObject);
            String phone = validateString(JsonKeys.PASSENGER_PHONE, jsonObject);
            String email = validateString(JsonKeys.PASSENGER_EMAIL, jsonObject);

            this.passenger.setIdPassenger(idPassenger);
            this.passenger.setCode(code);
            this.passenger.setName(name);
            this.passenger.setLastName(lastName);
            this.passenger.setPhone(phone);
            this.passenger.setEmail(email);
        }
    }

    public Passenger getPassenger() {
        return this.passenger;
    }
}
