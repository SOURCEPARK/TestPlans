/**
 * Copyright SOURCEPARK GmbH 2021. Alle Rechte vorbehalten.
 * <p>
 * SOURCEPARK GmbH Gesellschaft fuer Softwareentwicklung
 * <p>
 * Hohenzollerndamm 150 Haus 7a
 * 14199 Berlin
 * <p>
 * Tel.:   +49 (0) 30 / 39 80 68 30
 * Fax:    +49 (0) 30 / 39 80 68 39
 * e-mail: kontakt@sourcepark.de
 * www:    www.sourcepark.de
 */
package de.sourcepark.synaptic.testrunner.processing;

import de.sourcepark.synaptic.testrunner.DataBox;

public class K8sProgressIndicator implements ProgressIndicator {


    @Override
    public TestState parse(String processOutput) {
        String message = "";
        TestState ts = new TestState();

        if (processOutput.trim().startsWith("+++")) {
            // extract message
            ts.message = processOutput.trim().substring(3);
            ts.status = "RUNNING";
            ts.progress = DataBox.getInstance().getTestProgress() + 1.0;
            DataBox.getInstance().setTestProgress(ts.progress);
        } else if (processOutput.trim().startsWith("###")) {
            // extract message
            message = processOutput.trim().substring(3);
            ts.status = "RUNNING";   // will be failed in the future
            ts.progress = DataBox.getInstance().getTestProgress() + 1.0;
            DataBox.getInstance().setTestProgress(ts.progress);
            ts.message = message;
        } else if (processOutput.trim().startsWith("-->")) {
            ts.progress = Double.parseDouble(processOutput.trim().substring(3));
            DataBox.getInstance().setTestProgress(ts.progress);
            ts.message = processOutput.trim().substring(3);
            ts.status = "RUNNING";
        } else {
            ts.progress = DataBox.getInstance().getTestProgress() + 0.1;
            DataBox.getInstance().setTestProgress(ts.progress);
            ts.message = processOutput.trim();
            ts.status = "RUNNING";
        }
        return ts;
    }
}
