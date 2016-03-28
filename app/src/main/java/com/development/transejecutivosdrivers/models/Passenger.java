package com.development.transejecutivosdrivers.models;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class Passenger {
    int idPassenger;
    String code;
    String name;
    String lastName;
    String phone;
    String email;
    String fly;
    String aeroline;

    public int getIdPassenger() {
        return idPassenger;
    }

    public void setIdPassenger(int idPassenger) {
        this.idPassenger = idPassenger;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
