/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bwinparty.leapmotion;

import com.bwinparty.leapmotion.handler.LeapHandler;
import com.bwinparty.leapmotion.handler.SockJsHandler;
import com.bwinparty.leapmotion.service.VertX;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author zyclonite
 */
public class Leapmotion {
    
    private final VertX vertx;
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    private final Object sync = new Object();
    
    public Leapmotion() {
        vertx = VertX.getInstance();
        initHandlers();
        initLeapClient();
    }
    
    private void initHandlers() {
        vertx.registerSockJsHandler("/stream", new SockJsHandler());
    }
    
    private void initLeapClient() {
        vertx.registerWebsocketClient(new LeapHandler());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //JULLog4jBridge.assimilate();
        //DOMConfigurator.configureAndWatch(System.getProperty("user.dir") + "/log4j.xml", 60000);
        final Leapmotion main = new Leapmotion();
        //LOG.info("Application started");
        main.addShutdownHook();
        main.block();
    }
    
    private void block() {
        while (true) {
            try {
                stopLatch.await();
                break;
            } catch (InterruptedException e) {
                //Ignore
            }
        }
    }
    
    private void unblock() {
        stopLatch.countDown();
    }
    
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                //LOG.info("Application shutting down...");
                synchronized (sync) {
                    //do sync
                }
                vertx.shutdown();
            }
        });
    }
}
