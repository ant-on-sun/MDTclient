package com.springsun.mdtclient.model.user;

import com.springsun.mdtclient.model.IUser;

public class User implements IUser{
    private String userLogin;
    private String userPassword;
    private int distanceTraveled;
    private float currentLatitude = 1000;
    private float currentLongitude = 1000;

    @Override
    public void setCurrentLatitude(float currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    @Override
    public void setCurrentLongitude(float currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    @Override
    public float getCurrentLatitude() {
        return currentLatitude;
    }
    @Override
    public float getCurrentLongitude() {
        return currentLongitude;
    }

    @Override
    public void setLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @Override
    public void setPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public void setDistanceTraveled(int distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    @Override
    public String getLogin() {
        return userLogin;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public int getDistanceTraveled() {
        return distanceTraveled;
    }
}
