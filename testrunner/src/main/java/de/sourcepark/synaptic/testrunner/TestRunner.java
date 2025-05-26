package de.sourcepark.synaptic.testrunner;

import java.io.IOException;

public class TestRunner {

    public static void main(String[] args) {
        TestrunnerCommandApiServer server;
        try {
            server = new TestrunnerCommandApiServer();
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
