package com.springsun.mdtclient.controller.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckHashTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkHashTest1() {
        String s = "1:sometext";
        int h = 2333;
        assertFalse(CheckHash.checkHash(s, h));
    }

    @Test
    public void checkHashTest2() {
        String s = "1:sometext";
        int h = -1286574774;
        assertTrue(CheckHash.checkHash(s, h));
    }

}