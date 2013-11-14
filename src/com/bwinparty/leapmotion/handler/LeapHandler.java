/*
 * leapmotionx - Leap Motion Prototype
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.leapmotion.handler;

import com.bwinparty.leapmotion.service.VertX;
import java.util.Set;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.WebSocket;

/**
 *
 * @author Lukas Prettenthaler
 */
public class LeapHandler implements Handler<WebSocket> {

    private final Set<String> connections;
    private final VertX vertx;

    public LeapHandler() {
        vertx = VertX.getInstance();
        connections = vertx.getConnections();
        //LOG.debug("LeapHandler initialized");
    }

    @Override
    public void handle(final WebSocket sock) {
        sock.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(final Buffer inData) {
                //LOG.trace("got data");
                for (String actorID : connections) {
                    vertx.getEventBus().publish(actorID, inData);
                }
            }
        });
    }
}
