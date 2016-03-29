package com.development.transejecutivosdrivers.deserializers;

import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.models.Service;

import org.json.JSONObject;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class ServiceDeserializer extends DeserializerValidator{
    public JSONObject jsonObject = new JSONObject();
    public Service service = new Service();

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public void deserialize() {
        int idService = validateInt(JsonKeys.SERVICE_ID, jsonObject);

        if (idService != 0) {
            String reference = validateString(JsonKeys.SERVICE_REFERENCE, jsonObject);
            String createDate = validateString(JsonKeys.SERVICE_CREATE_DATE, jsonObject);
            String startDate = validateString(JsonKeys.SERVICE_START_DATE, jsonObject);
            String paxCant = validateString(JsonKeys.SERVICE_PAX_CANT, jsonObject);
            String pax = validateString(JsonKeys.SERVICE_PAX, jsonObject);
            String source = validateString(JsonKeys.SERVICE_SOURCE, jsonObject);
            String destiny = validateString(JsonKeys.SERVICE_DESTINY, jsonObject);
            String observations = validateString(JsonKeys.SERVICE_OBSERVATIONS, jsonObject);
            String status = validateString(JsonKeys.SERVICE_STATUS, jsonObject);
            String cd = validateString(JsonKeys.SERVICE_CD, jsonObject);

            this.service.setIdService(idService);
            this.service.setReference(reference);
            int pax_cant = Integer.parseInt(paxCant);
            this.service.setPaxCant(pax_cant);
            this.service.setPax(pax);
            this.service.setSource(source);
            this.service.setDestiny(destiny);
            this.service.setDate(createDate);
            this.service.setObservations(observations);
            this.service.setStatus(status);
            this.service.setCd(cd);
            this.service.setStartDate(startDate);
        }
    }

    public Service getService() {
        return this.service;
    }
}
