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
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.sourcepark.synaptic.testrunner.DataBox;
import de.sourcepark.synaptic.testrunner.processing.ExecuterThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertTrue;

public class ExcuterTest {
    @Before
    public void setUp() throws Exception {
        DataBox dbx = DataBox.getInstance();
        dbx.setPassword(System.getenv("GIT_PASS"));
        dbx.setUsername(System.getenv("GIT_USER"));

        dbx.setTestRunnerIdentity("TEST_RUNNER_IDENTITY");
        dbx.setTestRunId("TEST_RUN_ID");
        dbx.setTestName("TEST_NAME");
        DataBox.getInstance().setGuiServerUrl("http://localhost:8080");
        dbx.setTestStatus("RUNNING");
        dbx.setStartTime(System.currentTimeMillis());
        dbx.setTestProgress(1);

        ExecuterThread.KUBSYNNET_COMMAND = System.getenv("KUBSYNNET_COMMAND");
    }

    @After
    public void tearDown() {
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8888);
    @Test
    public void testRunTest() {
        try {
            assertTrue("WireMock server is not running", wireMockRule.isRunning());

            int port = wireMockRule.port();
            DataBox.getInstance().setGuiServerUrl("http://localhost:" + port);

            wireMockRule.stubFor(post(urlEqualTo("/test-status"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"status\":\"success\"}")));

            ExecuterThread executerThread = new ExecuterThread("k8s", "TP00001/FSS-F-0001-RC");
            executerThread.start();
            executerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
