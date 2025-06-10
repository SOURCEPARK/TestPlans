/**
 * Copyright SOURCEPARK GmbH 2021. Alle Rechte vorbehalten.
 *
 * SOURCEPARK GmbH Gesellschaft fuer Softwareentwicklung
 *
 * Hohenzollerndamm 150 Haus 7a
 * 14199 Berlin
 *
 * Tel.:   +49 (0) 30 / 39 80 68 30
 * Fax:    +49 (0) 30 / 39 80 68 39
 * e-mail: kontakt@sourcepark.de
 * www:    www.sourcepark.de
 */
package de.sourcepark.synaptic.testrunner;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class TestRunner {

    private static int heartbeatInterval = 60;
    private static String identity;
    private static String guiServerUrl = "http://localhost:8080";
    private static String bindAddress = "0.0.0.0";
    private static int bindPort = 8000;
    private static String dnsServerName = "localhost";
    private static List<String> platforms = List.of("k8s");
    private final static Logger LOG = LogManager.getLogger(TestRunner.class);

    private void registerTestRunner() {

    }


    public static void main(String[] args) throws ParseException {
        TestrunnerCommandApiServer server=null;
        MonitoringApiClient apiClient = null;
        HeartBeater heartBeater = null;

        Options options = new Options();
        options.addOption("b", "heartbeat-interval", true,
                "Heartbeat interval in seconds (default = 60).");
        options.addOption("i", "identity", true,
                "Sets test runner identity.");
        options.addOption("h", "help", false, "Shows this help.");
        options.addRequiredOption("u", "gui-server", true,
                "URL of the GUI server (default = http://localhost:8080).");
        options.addOption("s", "apiserver-name", true,
                "DNS resolvable command server name. (default = http://localhost:8000)\"");
        options.addOption("b", "bind-address", true,
                "Address the command server is binding to (default = 0.0.0.0).");
        options.addOption("p", "bind-port", true,
                "Port the command server is binding to (default = 8000).");
        options.addOption("f", "platforms", true,
                "Komma separated list of platforms supported by the testrunner (no spaces allowed).");
        options.addOption("c", "credentials", true,
                "Credentials for the git repository access. Username and Password separated by colon " +
                        "(no spaces allowed).");
        options.addOption("g", "git-checkout-folder", true,
                "Folder where the Testplans will be checked out. " +
                        "(no spaces allowed).");


        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("b")) {
            heartbeatInterval = Integer.parseInt(cmd.getOptionValue("b"));
        }
        if (cmd.hasOption("i")) {
            identity = cmd.getOptionValue("i");
        }
        else {
            identity = UUID.randomUUID().toString();
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
        if (cmd.hasOption("f")) {
            platforms = List.of(cmd.getOptionValue("f").split(","));
        }

        if (cmd.hasOption("c")) {
            String[] parts = cmd.getOptionValue("c").split(":");
            DataBox.getInstance().setUsername(parts[0]);
            DataBox.getInstance().setPassword(parts[1]);
        }
        if (cmd.hasOption("g")) {
            DataBox.getInstance().setGitCheckoutFolder(cmd.getOptionValue("g"));
        }


            try {
            DataBox.getInstance().setTestRunnerIdentity(identity)
                    .setTestStatus("IDLE")
                    .setHeartbeatSequence(0)
                    .setHeartbeatInterval(heartbeatInterval)
                    .setGuiServerUrl(guiServerUrl);

            server = new TestrunnerCommandApiServer(bindAddress, bindPort);
            server.start();
            apiClient = new MonitoringApiClient(guiServerUrl);
            if (!apiClient.registerRunner(identity, "http://" + dnsServerName + ":" + bindPort, platforms)) {
                LOG.error("Failed to register testrunner at "+ guiServerUrl);
                return;
            }
            apiClient.join();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (server != null) {
                try {
                    server.stop();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (apiClient != null) {
                apiClient.stop();
            }
        }
    }
}
