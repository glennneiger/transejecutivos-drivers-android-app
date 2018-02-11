package com.development.transejecutivosdrivers.models;

/**
 * Created by will on 2/10/2018.
 */

public class Source {
    int idServiceSource;
    String latitude;
    String longitude;
    String placeId;
    String address;
    City city;

    public int getIdServiceSource() {
        return idServiceSource;
    }

    public void setIdServiceSource(int idServiceSource) {
        this.idServiceSource = idServiceSource;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
