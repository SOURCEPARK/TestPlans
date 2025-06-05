package de.sourcepark.synaptic.testrunner;

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

    public final String KUBSYNNET_COMMAND = "/usr/share/synaptic-kubsynnet/kubsynnet";

    private final static Logger LOG = LogManager.getLogger(ExecuterThread.class);
    private final String testDataFolder;
    private final String platform;

    public ExecuterThread(String platform, String testDataFolder) {
        super("ExecuterThread");
        this.testDataFolder = testDataFolder;
        this.platform = platform;
    }

    private String runk8sCommand() {
        //TODO: renew licence pack if needed
        //TODO: Start kubsynnet
        //TODO: parsing and translating kubsynnet output to testrunner progress data
        return null;
    }

    private String runProcess(List<String> commandLine, boolean hasBinaryOutput) throws IOException, InterruptedException {

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
        }
        else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

            while ((line = reader.readLine()) != null) {
                tempResult.append(line);
                tempResult.append("\n");
            }
            result = tempResult.toString();
        }

        boolean isProcessRunning = true;
        int maxRetries = 60;
        int exitCode;

        LOG.info("Waiting [" + maxRetries + "] seconds for termination.");
        do {
            try {
                exitCode = process.exitValue();
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
            LOG.error("Process does not terminate. Updater will try to kill the process now.");
            process.destroy();
        }
        LOG.info("Process completed: " + tempResult.toString());
        return result;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
