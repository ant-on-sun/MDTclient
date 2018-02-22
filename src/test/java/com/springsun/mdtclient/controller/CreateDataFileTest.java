package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;
import com.springsun.mdtclient.model.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class CreateDataFileTest {
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
    public void createFileTest1() {
        user.setLogin("Vasisualiy");
        CreateDataFile.createFile(user);
        String pathToFile = CreateDataFile.getPathAsString();
        assertTrue(new File(pathToFile).exists());
    }
}