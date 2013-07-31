package com.bwinparty.leapmotion.service;

import com.bwinparty.leapmotion.handler.FileHandler;
import java.io.IOException;
import java.util.Set;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.sockjs.SockJSSocket;

/**
 *
 * @author zyclonite
 */
public class VertX {

    private static VertX instance;
    private final HttpServer server;
    private final RouteMatcher routeMatcher;
    private final Vertx vertx;
    private final SockJSServer sockJsServer;

    static {
        instance = new VertX();
    }

    private VertX() {
        vertx = VertxFactory.newVertx();
        server = vertx.createHttpServer();
        routeMatcher = new RouteMatcher();
        server.requestHandler(routeMatcher);
        sockJsServer = vertx.createSockJSServer(server);
        registerNomatchHandler();
        server.listen(8080);
        //LOG.debug("VertX initialized");
    }

    public void registerGetHandler(final String path, final Handler<HttpServerRequest> handler) {
        routeMatcher.get(path, handler);
    }

    public void registerPostHandler(final String path, final Handler<HttpServerRequest> handler) {
        routeMatcher.post(path, handler);
    }

    public void registerSockJsHandler(final String path, final Handler<SockJSSocket> handler) {
        sockJsServer.installApp(new JsonObject().putString("prefix", path), handler);
    }

    public void registerWebsocketClient(final Handler<WebSocket> handler) {
        final HttpClient client = vertx.createHttpClient().setHost("127.0.0.1").setPort(6437);
        client.connectWebsocket("/v3.json", handler);
    }
    public long setTimer(final long timeout, final Handler<Long> handler) {
        return vertx.setTimer(timeout, handler);
    }
    
    public long setPeriodic(final long timeout, final Handler<Long> handler) {
        return vertx.setPeriodic(timeout, handler);
    }
    
    public void cancelTimer(final long timerid) {
        vertx.cancelTimer(timerid);
    }

    public EventBus getEventBus() {
        return vertx.eventBus();
    }
    
    public Set getConnections() {
        return vertx.sharedData().getSet("conns");
    }
    
    public void shutdown() {
        server.close();
    }

    private void registerNomatchHandler() {
        try {
            routeMatcher.noMatch(new FileHandler());
        } catch (IOException ex) {
            //LOG.error(ex);
        }
    }

    public static VertX getInstance() {
        return instance;
    }
}
