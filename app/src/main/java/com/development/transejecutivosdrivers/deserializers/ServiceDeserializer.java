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
            String startTime = validateString(JsonKeys.SERVICE_START_TIME, jsonObject);
            String endTime = validateString(JsonKeys.SERVICE_END_TIME, jsonObject);

            String paxCant = validateString(JsonKeys.SERVICE_PAX_CANT, jsonObject);
            String pax = validateString(JsonKeys.SERVICE_PAX, jsonObject);
            String source = validateString(JsonKeys.SERVICE_SOURCE, jsonObject);
            String destiny = validateString(JsonKeys.SERVICE_DESTINY, jsonObject);
            String observations = validateString(JsonKeys.SERVICE_OBSERVATIONS, jsonObject);
            String status = validateString(JsonKeys.SERVICE_STATUS, jsonObject);
            String cd = validateString(JsonKeys.SERVICE_CD, jsonObject);
            int old = validateInt(JsonKeys.SERVICE_OLD, jsonObject);
            String fly = validateString(JsonKeys.SERVICE_FLY, jsonObject);
            String aeroline = validateString(JsonKeys.SERVICE_AEROLINE, jsonObject);
            int idTrace = validateInt(JsonKeys.SERVICE_TRACE_ID, jsonObject);
            String b1ha = validateString(JsonKeys.SERVICE_B1HA, jsonObject);
            String bls = validateString(JsonKeys.SERVICE_BLS, jsonObject);
            String pab = validateString(JsonKeys.SERVICE_PAB, jsonObject);
            String st = validateString(JsonKeys.SERVICE_ST, jsonObject);

            String b1haTime = validateString(JsonKeys.SERVICE_B1HA_TIME, jsonObject);
            String blsTime = validateString(JsonKeys.SERVICE_BLS_TIME, jsonObject);
            String pabTime = validateString(JsonKeys.SERVICE_PAB_TIME, jsonObject);
            String stTime = validateString(JsonKeys.SERVICE_ST_TIME, jsonObject);

            int b1haStatus = validateInt(JsonKeys.SERVICE_B1HA_STATUS, jsonObject);

            this.service.setIdService(idService);
            this.service.setReference(reference);
            int pax_cant;

            if (paxCant.equals("")) {
                pax_cant = 0;
            } else {
                pax_cant = Integer.parseInt(paxCant);
            }

            this.service.setPaxCant(pax_cant);
            this.service.setPax(pax);
            this.service.setSource(source);
            this.service.setDestiny(destiny);
            this.service.setDate(createDate);
            this.service.setObservations(observations);
            this.service.setStatus(status);
            this.service.setCd(cd);
            this.service.setStartDate(startDate);

            this.service.setStartTime(startTime);
            this.service.setEndTime(endTime);

            this.service.setFly(fly);
            this.service.setAeroline(aeroline);
            this.service.setOld(old);
            this.service.setIdTrace(idTrace);
            this.service.setB1ha(b1ha);
            this.service.setBls(bls);
            this.service.setPab(pab);
            this.service.setSt(st);
            this.service.setB1haStatus(b1haStatus);

            this.service.setB1haTime(b1haTime);
            this.service.setBlsTime(blsTime);
            this.service.setPabTime(pabTime);
            this.service.setStTime(stTime);
        }
    }

    public Service getService() {
        return this.service;
    }
}
