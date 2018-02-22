package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;
import com.springsun.mdtclient.model.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FileAsArrayStringTest {
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
    public void getContentTest1() {
        user.setLogin("Vasisualiy");
        ClearFileContent.clear(user);
        String dataOne = "line one";
        AddDataToFile.addData(dataOne, user);
        String dataTwo = "line two";
        AddDataToFile.addData(dataTwo, user);
        list = FileAsArrayString.getContent(user);
        assertEquals(dataOne, list.get(0));
        assertEquals(dataTwo, list.get(1));
    }
}