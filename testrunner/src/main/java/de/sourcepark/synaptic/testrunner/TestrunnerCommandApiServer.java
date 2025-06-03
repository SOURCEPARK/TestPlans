package de.sourcepark.synaptic.testrunner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class TestrunnerCommandApiServer {

    private final HttpServer server;
    private static final Object sem = new Object();

    public TestrunnerCommandApiServer(String address, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(address, port), 0);
        server.createContext("/start-test", new StartTestHandler());
        server.createContext("/restart-test", new RestartTestHandler());
        server.createContext("/stop-test", new StopTestHandler());
        server.createContext("/test-status", new TestStatusHandler());
        System.out.println("Is running on " + address + ":" + port);
        server.setExecutor(null);
    }

    public void stop() throws IOException {
        server.stop(0);
    }

    public void start() throws IOException {
        server.start();
    }


}

