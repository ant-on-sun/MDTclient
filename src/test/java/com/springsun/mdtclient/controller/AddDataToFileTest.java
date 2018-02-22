package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;
import com.springsun.mdtclient.model.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AddDataToFileTest {
    IUser user;
    List<String> list;

    @Before
    public void setUp() throws Exception {
        user = new User();
    }

    @After
    public void tearDown() throws Exception {
        user = null;
        list = null;
    }

    @Test
    public void addDataTest1() {
        user.setLogin("Vasisualiy");
        String data = "some string";
        AddDataToFile.addData(data, user);
        list = FileAsArrayString.getContent(user);
        String lastLineInFile = list.get(list.size() - 1);
        assertEquals(data, lastLineInFile);
    }

    @Test
    public void addDataTest2() {
        user.setLogin("");
        String data = "some string";
        AddDataToFile.addData(data, user);
        list = FileAsArrayString.getContent(user);
        String lastLineInFile = list.get(list.size() - 1);
        assertEquals(data, lastLineInFile);
    }

}