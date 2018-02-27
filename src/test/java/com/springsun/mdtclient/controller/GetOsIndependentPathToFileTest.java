package com.springsun.mdtclient.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetOsIndependentPathToFileTest {
    String s;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        s = "";
    }

    //in Windows
    @Test
    public void getPathTest1(){
        s = "file:/C:\\Users";
        assertEquals("C:\\Users", GetOsIndependentPathToFile.getPath(s));
    }

    //in Windows
    @Test
    public void getPathTest2(){
        s = "file:/E:\\Users";
        assertEquals("E:\\Users", GetOsIndependentPathToFile.getPath(s));
    }

    //in Windows
    @Test (expected = StringIndexOutOfBoundsException.class)
    public void getPathTest3(){
        s = ":\\Users";
        GetOsIndependentPathToFile.getPath(s);
    }

    //in Unix
    @Test (expected = StringIndexOutOfBoundsException.class)
    public void getPathTest4(){
        s = "Users/someDirectory";
        GetOsIndependentPathToFile.getPath(s);
    }

    //in Windows
    @Test
    public void getPathTest5(){
        s = "file:/E:\\Users\\some Directory";
        assertEquals("E:\\Users\\some Directory", GetOsIndependentPathToFile.getPath(s));
    }
}