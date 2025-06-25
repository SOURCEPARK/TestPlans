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
package de.sourcepark.synaptic.testrunner.processing;

import de.sourcepark.synaptic.testrunner.DataBox;
import de.sourcepark.synaptic.testrunner.Tools;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class ExecuterThread extends Thread {

    public static String KUBSYNNET_COMMAND = "/usr/bin/kubsynnet";
    private static boolean PROCESS_RUNNING = true;

    private static String REPORT_DUMMY="## Testbericht für [TP-00003-FSS-F-00001-RCD]\n" +
            "\n" +
            "### Testlauf vom: 25.06.2025 10:32 - 11:19\n" +
            "\n" +
            "**Ergebnis:** Der Test wurde erfolgreich beendet es konnten keine Abweichungen festgestellt werden.";

    private final static Logger LOG = LogManager.getLogger(ExecuterThread.class);
    private final String testDataFolder;
    private final String platform;
    private final ProgressIndicator progressIndicator;
    private final boolean creationMode;

    public ExecuterThread(String platform, String testDataFolder, boolean creationMode) {
        super("ExecuterThread");
        this.testDataFolder = testDataFolder;
        this.platform = platform;
        this.creationMode = creationMode;
        switch(platform) {
            case "k8s":
                this.progressIndicator = new K8sProgressIndicator();
                break;
            default:
                this.progressIndicator = null;
        }
    }

    public static void setProcessRunning(boolean processRunning) {
        PROCESS_RUNNING = processRunning;
    }

    private List<String> createk8sCommand(boolean creationMode) {
        //TODO: renew licence pack if needed

        //TODO: Start kubsynnet
        String dataFolder = DataBox.getInstance().getGitCheckoutFolder()+testDataFolder;
        if (creationMode) {
            String licenceKey = System.getenv("LICENCE_KEY");
            if (licenceKey == null) {
                licenceKey = "NOT_SET";
            }
            return List.of(KUBSYNNET_COMMAND, "create", "-t", dataFolder, "-H", "localhost", "-x", licenceKey);
        } else {
            return List.of(KUBSYNNET_COMMAND, "purge", "-t", dataFolder);
        }
    }

    private void sendProgressPostRequest(String message) throws IOException {
        int elapsedSeconds = (int) ((System.currentTimeMillis() - DataBox.getInstance().getStartTime()) / 1000);
        Map<String, Object> params = Map.of(
                "runnerId", DataBox.getInstance().getTestRunnerIdentity(),
                "testRunId", DataBox.getInstance().getTestRunId(),
                "testName", DataBox.getInstance().getTestName(),
                "status", DataBox.getInstance().getTestStatus(),
                "startTime", DataBox.getInstance().getStartTime(),
                "elapsedSeconds", String.valueOf(elapsedSeconds),
                "message", message,
                "progress", DataBox.getInstance().getTestProgress()
        );
        DataBox.getInstance().setElapsedSeconds(elapsedSeconds);
        DataBox.getInstance().setTestMessage(message);

//        Tools.sendPostRequest("/test-status", params);
    }

    private void sendProgress(String processOutput) throws IOException {

        //TODO: parsing and translating kubsynnet output to testrunner progress data
        TestState ts = progressIndicator.parse(processOutput);
        if (ts != null && ts.progress <= 1.0 ) {
            sendProgressPostRequest(ts.message);
        }
    }


    private String runProcess(List<String> commandLine, boolean hasBinaryOutput) throws IOException, InterruptedException {
        PROCESS_RUNNING = true;
        StringBuilder cl = new StringBuilder();
        StringBuilder tempResult = new StringBuilder();
        String result;

        for (String i : commandLine) {
            cl.append(i);
            cl.append(" ");
        }

        LOG.info("Executing command line: [" + cl.toString() + "]");
        ProcessBuilder builder = new ProcessBuilder(commandLine);
        builder.redirectErrorStream(true);


        Map<String, String> env = builder.environment();
        env.put("PATH", "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin");
        env.put("PYTHONUNBUFFERED", "1");  // Wichtig: Deaktiviert die Python-Ausgabepufferung

        Process process = builder.start();
        String line;

        InputStream stdout = process.getInputStream();


        if (hasBinaryOutput) {

            byte buffer[];
            do {
                buffer = IOUtils.toByteArray(stdout);
                if (buffer.length > 0) {
                    tempResult.append(Base64.getEncoder().encodeToString(buffer));
                }
            } while (buffer.length > 0);
            result = tempResult.toString();
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

            while ((line = reader.readLine()) != null && PROCESS_RUNNING) {
                tempResult.append(line);
                System.out.println("  --- " + line);
                tempResult.append("\n");
                sendProgress(line);   //FIXME: fairly inefficient
            }
            result = tempResult.toString();
        }

        if (!PROCESS_RUNNING) {
            process.destroy();
        }

        boolean isProcessRunning = true;
        int maxRetries = 60;
        int exitCode;

        LOG.info("Waiting [" + maxRetries + "] seconds for termination.");
        do {
            try {
                exitCode = process.exitValue();
                if (exitCode != 0) {
                    DataBox.getInstance().setTestStatus("FAILED");
                    DataBox.getInstance().setTestRunnerStatus("IDLE");
                    LOG.error("K8s process terminated with exit code: " + exitCode);
                } else {
                    DataBox.getInstance().setTestStatus("COMPLETED");
                    DataBox.getInstance().setTestRunnerStatus("IDLE");
                }
                isProcessRunning = false;
            } catch (IllegalThreadStateException ex) {
                LOG.info("Process not terminated. Waiting ...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException iex) {
                    LOG.trace("getExitCode call interrupted.");
                }
                maxRetries--;
            }
        } while (isProcessRunning && maxRetries > 0);

        if (maxRetries == 0 && isProcessRunning) {
            LOG.error("Process does not terminate. Will try to kill the process now.");
            process.destroy();
        }
        Map<String, Object> params = Map.of("report", REPORT_DUMMY);
        Tools.sendPostRequest("/test-runner/"+DataBox.getInstance().getTestRunnerIdentity()+"/complete", params);

        LOG.info("Process completed: " + tempResult.toString());
        return result;
    }


    @Override
    public void run() {
        String message ="Test executor thread completed.";
        try {
            if (creationMode) {
                List<String> commandLine = createk8sCommand(true);
                runProcess(commandLine, false);
            } else {
                List<String> commandLine = createk8sCommand(false);
                runProcess(commandLine, false);
            }

        } catch (InterruptedException e) {
            LOG.error("Thread interrupted.", e);
            message = "Thread interrupted.";
            DataBox.getInstance().setTestStatus("FAILED");
        } catch (Throwable e) {
            message = "Unexpected exception: " + e.getMessage();
            LOG.error("Unexpected exception.", e);
            DataBox.getInstance().setTestStatus("FAILED");
        } finally {
            LOG.info("Test executor thread completed. Result is: " + message);
            DataBox.getInstance().setTestProgress(1.0);
            try {
                sendProgressPostRequest(message);
            } catch (IOException e) {
                LOG.error("Failed to send progress.", e);
            }
        }
    }
}
