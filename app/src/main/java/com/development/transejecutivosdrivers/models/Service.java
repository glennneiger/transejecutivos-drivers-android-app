package com.development.transejecutivosdrivers.models;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class Service {
    public int idService;
    public String reference;
    public String date;
    public String startDate;
    public String startTime;
    public String endTime;
    public int paxCant;
    public String pax;
    public String source;
    public String destiny;
    public String status;
    public String observations;
    public String cd;
    public int old;
    String fly;
    String aeroline;
    int idTrace;
    String b1ha;
    String bls;
    String pab;
    String st;
    int b1haStatus;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getB1haStatus() {
        return b1haStatus;
    }

    public void setB1haStatus(int b1haStatus) {
        this.b1haStatus = b1haStatus;
    }

    public int getIdTrace() {
        return idTrace;
    }

    public void setIdTrace(int idTrace) {
        this.idTrace = idTrace;
    }

    public String getB1ha() {
        return b1ha;
    }

    public void setB1ha(String b1ha) {
        this.b1ha = b1ha;
    }

    public String getBls() {
        return bls;
    }

    public void setBls(String bls) {
        this.bls = bls;
    }

    public String getPab() {
        return pab;
    }

    public void setPab(String pab) {
        this.pab = pab;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public int getOld() {
        return old;
    }

    public void setOld(int old) {
        this.old = old;
    }

    public String getFly() {
        return fly;
    }

    public void setFly(String fly) {
        this.fly = fly;
    }

    public String getAeroline() {
        return aeroline;
    }

    public void setAeroline(String aeroline) {
        this.aeroline = aeroline;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getPaxCant() {
        return paxCant;
    }

    public void setPaxCant(int paxCant) {
        this.paxCant = paxCant;
    }

    public String getPax() {
        return pax;
    }

    public void setPax(String pax) {
        this.pax = pax;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservations() {
        return observations;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }


}
