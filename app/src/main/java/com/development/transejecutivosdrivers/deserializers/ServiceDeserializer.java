package com.development.transejecutivosdrivers.deserializers;

import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.Subcompany;

import org.json.JSONException;
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
            String paxCant = validateString(JsonKeys.SERVICE_PAX_CANT, jsonObject);

            this.service.setIdService(idService);
            this.service.setReference(validateString(JsonKeys.SERVICE_REFERENCE, jsonObject));
            int pax_cant;

            if (paxCant.equals("")) {
                pax_cant = 0;
            } else {
                pax_cant = Integer.parseInt(paxCant);
            }

            this.service.setPaxCant(pax_cant);
            this.service.setPax(validateString(JsonKeys.SERVICE_PAX, jsonObject));
            this.service.setSource(validateString(JsonKeys.SERVICE_SOURCE, jsonObject));
            this.service.setDestiny(validateString(JsonKeys.SERVICE_DESTINY, jsonObject));
            this.service.setDate(validateString(JsonKeys.SERVICE_CREATE_DATE, jsonObject));
            this.service.setObservations(validateString(JsonKeys.SERVICE_OBSERVATIONS, jsonObject));
            this.service.setStatus(validateString(JsonKeys.SERVICE_STATUS, jsonObject));
            this.service.setCd(validateString(JsonKeys.SERVICE_CD, jsonObject));
            this.service.setStartDate(validateString(JsonKeys.SERVICE_START_DATE, jsonObject));
            this.service.setStartDateNice(validateString(JsonKeys.SERVICE_START_DATE_NICE, jsonObject));
            this.service.setStartTime(validateString(JsonKeys.SERVICE_START_TIME, jsonObject));
            this.service.setServiceStartTime(validateString(JsonKeys.SERVICE_SERVICE_START_TIME, jsonObject));
            this.service.setEndTime(validateString(JsonKeys.SERVICE_END_TIME, jsonObject));

            this.service.setFly(validateString(JsonKeys.SERVICE_FLY, jsonObject));
            this.service.setAeroline(validateString(JsonKeys.SERVICE_AEROLINE, jsonObject));
            this.service.setOld(validateInt(JsonKeys.SERVICE_OLD, jsonObject));
            this.service.setIdTrace(validateInt(JsonKeys.SERVICE_TRACE_ID, jsonObject));
            this.service.setB1ha(validateString(JsonKeys.SERVICE_B1HA, jsonObject));
            this.service.setBls(validateString(JsonKeys.SERVICE_BLS, jsonObject));
            this.service.setPab(validateString(JsonKeys.SERVICE_PAB, jsonObject));
            this.service.setSt(validateString(JsonKeys.SERVICE_ST, jsonObject));
            this.service.setB1haStatus(validateInt(JsonKeys.SERVICE_B1HA_STATUS, jsonObject));

            this.service.setB1haTime(validateString(JsonKeys.SERVICE_B1HA_TIME, jsonObject));
            this.service.setBlsTime(validateString(JsonKeys.SERVICE_BLS_TIME, jsonObject));
            this.service.setPabTime(validateString(JsonKeys.SERVICE_PAB_TIME, jsonObject));
            this.service.setStTime(validateString(JsonKeys.SERVICE_ST_TIME, jsonObject));
            this.service.setTraceObservations(validateString(JsonKeys.SERVICE_TRACE_OBSERVATIONS, jsonObject));
            this.service.setLicensePlate(validateString(JsonKeys.SERVICE_LICENSE_PLATE, jsonObject));

            this.service.setEvent(validateString(JsonKeys.SERVICE_EVENT, jsonObject));
            this.service.setCompany(validateString(JsonKeys.SERVICE_COMPANY, jsonObject));
            this.service.setUrlMap(validateString(JsonKeys.SERVICE_URL_MAP, jsonObject));

            try {
                JSONObject subcompanyJsonObj = jsonObject.getJSONObject(JsonKeys.SUBCOMPANY);

                Subcompany subcompany = new Subcompany();
                subcompany.setIdSubcompany(validateInt(JsonKeys.SUBCOMPANY_COMPANY_ID, subcompanyJsonObj));
                subcompany.setName(validateString(JsonKeys.SUBCOMPANY_NAME, subcompanyJsonObj));

                this.service.setSubcompany(subcompany);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Service getService() {
        return this.service;
    }
}
