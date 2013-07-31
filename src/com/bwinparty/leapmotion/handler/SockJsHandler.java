package com.bwinparty.leapmotion.handler;

import com.bwinparty.leapmotion.service.VertX;
import java.util.Set;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.sockjs.SockJSSocket;

/**
 *
 * @author zyclonite
 */
public class SockJsHandler implements Handler<SockJSSocket> {

    private final Set<String> connections;
    private final VertX vertx;

    public SockJsHandler() {
        vertx = VertX.getInstance();
        connections = vertx.getConnections();
        //LOG.debug("SockJsHandler initialized");
    }

    @Override
    public void handle(final SockJSSocket sock) {
        //LOG.debug("Handle connection " + sock.writeHandlerID());
        connections.add(sock.writeHandlerID());
        sock.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(final Buffer inData) {
                //LOG.trace("got data");
            }
        });
        sock.endHandler(new VoidHandler() {
            @Override
            public void handle() {
                connections.remove(sock.writeHandlerID());
                //LOG.debug("removed connection " + sock.writeHandlerID());
            }
        });
    }
}
