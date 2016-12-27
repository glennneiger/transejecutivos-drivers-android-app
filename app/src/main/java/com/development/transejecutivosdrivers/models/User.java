package com.development.transejecutivosdrivers.models;

import java.io.Serializable;

/**
 * Created by william.montiel on 25/09/2015.
 */
public class User implements Serializable {
    int idUser;
    String username;
    String name;
    String lastName;
    String email1;
    String email2;
    String phone1;
    String phone2;
    String apikey;
    String code;
    String token;

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail1(String email) {
        this.email1 = email;
    }

    public void setEmail2(String email) {
        this.email2 = email;
    }

    public void setPhone1(String phone) {
        this.phone1 = phone;
    }

    public void setPhone2(String phone) {
        this.phone2 = phone;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail1() {
        return email1;
    }

    public String getEmail2() {
        return email2;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getApikey() {
        return apikey;
    }

    public String getCode() {
        return code;
    }


}
