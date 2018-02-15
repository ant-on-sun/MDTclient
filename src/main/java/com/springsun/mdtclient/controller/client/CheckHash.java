package com.springsun.mdtclient.controller.client;

public class CheckHash {

    public static boolean checkHash(String s, int hash){
        int i = s.hashCode();
        if (i == hash) {
            return true;
        }
        return false;
    }
}
