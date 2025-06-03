package de.sourcepark.synaptic.testrunner;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.List;

public class TestRunner {

    private static int heartbeatInterval = 60;
    private static String identity;
    private static String guiServerUrl = "http://localhost:8080";
    private static String bindAddress = "0.0.0.0";
    private static int bindPort = 8000;
    private static String dnsServerName = "localhost";


    private void registerTestRunner() {

    }


    public static void main(String[] args) throws ParseException {
        TestrunnerCommandApiServer server;

        Options options = new Options();
        options.addOption("b", "heartbeat-interval", true,
                "Heartbeat interval in seconds (default = 60).");
        options.addOption("i", "identity", true,
                "Sets test runner identity.");
        options.addOption("h", "help", false, "Shows this help.");
        options.addOption("u", "gui-server", true,
                "URL of the GUI server (default = http://localhost:8080).");
        options.addOption("s", "apiserver-name", true,
                "DNS resolvable command server name. (default = http://localhost:8000)\"");
        options.addOption("b", "bind-address", true,
                "Address the command server is binding to (default = 0.0.0.0).");
        options.addOption("p", "bind-port", true,
                "Port the command server is binding to (default = 8000).");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("b")) {
            heartbeatInterval = Integer.parseInt(cmd.getOptionValue("b"));
        }
        if (cmd.hasOption("i")) {
            identity = cmd.getOptionValue("i");
        }
        if (cmd.hasOption("u")) {
            guiServerUrl = cmd.getOptionValue("u");
        }
        if (cmd.hasOption("b")) {
            bindAddress = cmd.getOptionValue("b");
        }
        if (cmd.hasOption("p")) {
            bindPort = Integer.parseInt(cmd.getOptionValue("p"));
        }
        if (cmd.hasOption("s")) {
            dnsServerName = cmd.getOptionValue("s");
        }

        try {
            DataBox.getInstance().setTestRunnerIdentity(identity)
                    .setTestStatus("IDLE")
                    .setHeartbeatSequence(0)
                    .setHeartbeatInterval(heartbeatInterval)
                    .setGuiServerUrl(guiServerUrl);

            server = new TestrunnerCommandApiServer(bindAddress, bindPort);
            server.start();
            MonitoringApiClient apiClient = new MonitoringApiClient(guiServerUrl);
            apiClient.registerRunner(identity, "http://" + dnsServerName + ":" + bindPort, List.of("k8s", "docker"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
