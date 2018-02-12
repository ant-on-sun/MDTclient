package com.springsun.mdtclient.model;

public interface IUser {
    void setLogin(String userLogin);
    void setPassword(String userPassword);
    void setDistanceTraveled(int distanceTraveled);
    void setCurrentLatitude(float currentLatitude);
    void setCurrentLongitude(float currentLongitude);
    String getLogin();
    String getPassword();
    int getDistanceTraveled();
    float getCurrentLatitude();
    float getCurrentLongitude();
}
