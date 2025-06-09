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

public class K8sProgressIndicator implements ProgressIndicator {


    @Override
    public TestState parse(String processOutput) {
        String message = "";
        TestState ts = new TestState();

        if (processOutput.trim().startsWith("+++")) {
            // extract message
            message = processOutput.trim().substring(3);
            // calculate progress

        } else if (processOutput.trim().startsWith("###")) {
            // extract message
            message = processOutput.trim().substring(3);
            ts.status = "FAILED";
            ts.progress = 1.0;
            ts.message = message;

        }


        return null;
    }
}
