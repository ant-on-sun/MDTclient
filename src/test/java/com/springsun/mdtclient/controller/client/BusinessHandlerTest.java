package com.springsun.mdtclient.controller.client;

import com.springsun.mdtclient.controller.IClient;
import com.springsun.mdtclient.model.DispetchingData;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({DispetchingData.class})
public class BusinessHandlerTest extends ApplicationTest {

//    @BeforeClass
//    public static void setUpSpec() throws Exception {
//        if (Boolean.getBoolean("headless")){
//            System.setProperty("testfx.robot", "glass");
//            System.setProperty("testfx.headless", "true");
//            System.setProperty("prism.order", "sw");
//            System.setProperty("prism.text", "t2k");
//            System.setProperty("java.awt.headless", "true");
//        }
//    }

    private BusinessHandler bHandler;
    private ChannelHandlerContext ctxmock;
    private Object msg;
    private DispetchingData dispetchingData;
    private final String approved = "approved";
    private Stage stage;
    private IClient clientmock;
    private Throwable causemock;

    @Before
    public void setUp() throws Exception {
        dispetchingData = new DispetchingData();
        bHandler = new BusinessHandler(dispetchingData);
        ctxmock = mock(ChannelHandlerContext.class);
        clientmock = mock(Client.class);
        causemock = mock(Throwable.class);
        DispetchingData.setClient(clientmock);

//        FxToolkit.registerPrimaryStage();
//        FxToolkit.setupApplication(AppThreadForTests.class);
//        stage = AppThreadForTests.getPrimaryStage();

//        PowerMockito.mockStatic(DispetchingData.class);
//        PowerMockito.doNothing().when(DispetchingData.class);

        msg = "1:" + approved;
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
    }

    @After
    public void tearDown() throws Exception {
        dispetchingData = null;
        bHandler = null;
        msg = "";
    }

    @Test
    public void channelReadTest1a() throws InterruptedException {
        msg = "1:" + approved;
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String s = dispetchingData.messageModelProperty().get();
        assertTrue(bHandler.getAppPasswordChecked());
        assertEquals("", s);
    }

    @Test
    public void channelReadTest1b() throws InterruptedException {
        msg = "1:" + approved + "qwer";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        boolean connected = dispetchingData.connectedProperty().get();
        verify(ctxmock, times(1)).close();
        assertFalse(connected);
    }

    @Test
    public void channelReadTest2a() throws InterruptedException {
        msg = "2:incorrect password";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        boolean checked = dispetchingData.checkedProperty().get();
        assertFalse(checked);
    }

    @Test
    public void channelReadTest3a() throws InterruptedException {
        msg = "3:login and password are correct";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        boolean checked = dispetchingData.checkedProperty().get();
        assertTrue(checked);
    }

    @Test
    public void channelReadTest4a() throws InterruptedException {
        msg = "4:112";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String result = dispetchingData.resultProperty().get();
        assertEquals("112", result);
    }

    @Test
    public void channelReadTest5a() throws InterruptedException {
        msg = "5:login exist";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        boolean checked = dispetchingData.checkedProperty().get();
        boolean loginExist = dispetchingData.loginAlredyExistProperty().get();
        assertTrue(checked);
        assertTrue(loginExist);
    }

    @Test
    public void channelReadTest6a() throws InterruptedException {
        msg = "6:no such login";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        boolean checked = dispetchingData.checkedProperty().get();
        boolean loginExist = dispetchingData.loginAlredyExistProperty().get();
        assertFalse(checked);
        assertFalse(loginExist);
    }

    @Test
    public void channelReadTest7a() throws InterruptedException {
        msg = "7:new user successfully created";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        boolean created = dispetchingData.userCreatedProperty().get();
        assertTrue(created);
    }

    @Test
    public void channelReadTest8a() throws InterruptedException {
        msg = "8:couldn't create new user";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        boolean created = dispetchingData.userCreatedProperty().get();
        assertFalse(created);
    }

    @Test
    public void channelReadTest9a() throws InterruptedException {
        msg = "9:couldn't reset result to zero";
        prepareMessage();
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String created = dispetchingData.resultProperty().get();
        assertEquals("couldn't reset result to zero", created);
    }

    @Test
    public void channelReadTest10a() throws InterruptedException {
        msg = "10:some text";
        prepareMessage();
        bHandler.setErrorCounter(1);
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String message = dispetchingData.messageModelProperty().get();
        assertEquals("some text", message);
        verify(ctxmock, times(0)).close();
        verify(clientmock, times(1)).writeToChannel();
        assertEquals(2, bHandler.getErrorCounter());
    }

    @Test
    public void channelReadTest10b() throws InterruptedException {
        msg = "10:some new text";
        prepareMessage();
        bHandler.setErrorCounter(2000);
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String message = dispetchingData.messageModelProperty().get();
        assertEquals("some new text", message);
        verify(ctxmock, times(1)).close();
        verify(clientmock, times(0)).writeToChannel();
        assertEquals(2000, bHandler.getErrorCounter());
    }

    @Test
    public void channelReadTest11a() throws InterruptedException {
        msg = "11:invalid key protocol was received on server";
        prepareMessage();
        when(clientmock.getMessage()).thenReturn("3:test");
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String message = dispetchingData.messageModelProperty().get();
        assertEquals("invalid key protocol was received on server", message);
        verify(ctxmock, times(0)).close();
        verify(clientmock, times(1)).writeToChannel();
    }

    @Test
    public void channelReadTest11b() throws InterruptedException {
        msg = "11:invalid key protocol was received on server";
        prepareMessage();
        when(clientmock.getMessage()).thenReturn("0:test");
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String message = dispetchingData.messageModelProperty().get();
        assertEquals("invalid key protocol was received on server", message);
        verify(ctxmock, times(1)).close();
        verify(clientmock, times(0)).writeToChannel();
    }

    @Test
    public void channelReadTestDefaultA() throws InterruptedException {
        msg = "300:some text";
        prepareMessage();
        bHandler.setErrorCounter(1);
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String message = dispetchingData.messageModelProperty().get();
        assertEquals("some text", message);
        verify(clientmock, times(1)).writeToChannel(anyString());
        verify(ctxmock, times(0)).close();
        assertEquals(2, bHandler.getErrorCounter());
    }

    @Test
    public void channelReadTestDefaultB() throws InterruptedException {
        msg = "300:some text";
        prepareMessage();
        bHandler.setErrorCounter(300);
        bHandler.channelRead(ctxmock, msg);
        Thread.sleep(200);
        String message = dispetchingData.messageModelProperty().get();
        assertEquals("some text", message);
        verify(clientmock, times(0)).writeToChannel(anyString());
        verify(ctxmock, times(1)).close();
        assertEquals(300, bHandler.getErrorCounter());
    }

    @Test
    public void exceptionCaughtTest1() throws InterruptedException {
        bHandler.setErrorCounter(3);
        bHandler.exceptionCaught(ctxmock, causemock);
        verify(ctxmock, times(0)).close();
        verify(clientmock, times(1)).writeToChannel(anyString());
        assertEquals(4, bHandler.getErrorCounter());
    }

    @Test
    public void exceptionCaughtTest2() throws InterruptedException {
        bHandler.setErrorCounter(300);
        bHandler.exceptionCaught(ctxmock, causemock);
        verify(ctxmock, times(1)).close();
        verify(clientmock, times(0)).writeToChannel(anyString());
        assertEquals(300, bHandler.getErrorCounter());
    }


    private void prepareMessage(){
        int h = msg.hashCode();
        msg = (Object)(msg + ":" + h);
    }
}