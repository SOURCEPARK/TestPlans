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
package de.sourcepark.synaptic.testrunner;

public class DataBox {

    private static DataBox dataBox;
    private String testRunnerIdentity;
    private String testRunnerUrl;

    private String testStatus = "IDLE";
    private String testRunId = "NOT_YET_SET";
    private String testName = "NOT_YET_SET";
    private long testStartTime = 0;
    private double testProgress = 0.0;
    private String testErrorcode;
    private String testErrortext;
    private String gitCheckoutFolder="/tmp";


    private String guiServerUrl;
    private String password;
    private String username;

    private String runningTestPlan;

    private long startTime;
    private long heartbeatTime;
    private long heartbeatSequence;
    private long heartbeatInterval;


    private DataBox() {
    }

    public static DataBox getInstance() {
        if (dataBox == null) {
            dataBox = new DataBox();
        }
        return dataBox;
    }

    public DataBox setTestRunnerIdentity(String testRunnerIdentity) {
        this.testRunnerIdentity = testRunnerIdentity;
        return this;
    }

    public DataBox setTestRunnerUrl(String testRunnerUrl) {
        this.testRunnerUrl = testRunnerUrl;
        return this;
    }

    public DataBox setTestStatus(String testStatus) {
        this.testStatus = testStatus;
        return this;
    }

    public DataBox setTestRunId(String testRunId) {
        this.testRunId = testRunId;
        return this;
    }

    public DataBox setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public DataBox setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
        return this;
    }

    public DataBox setHeartbeatSequence(long heartbeatSequence) {
        this.heartbeatSequence = heartbeatSequence;
        return this;
    }

    public DataBox setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public DataBox setGuiServerUrl(String guiServerUrl) {
        this.guiServerUrl = guiServerUrl;
        return this;
    }

    public DataBox setTestName(String testName) {
        this.testName = testName;
        return this;
    }

    public DataBox setTestStartTime(long testStartTime) {
        this.testStartTime = testStartTime;
        return this;
    }

    public DataBox setTestProgress(int testProgress) {
        this.testProgress = testProgress;
        return this;
    }

    public DataBox setTestErrorcode(String testErrorcode) {
        this.testErrorcode = testErrorcode;
        return this;
    }

    public DataBox setTestErrortext(String testErrortext) {
        this.testErrortext = testErrortext;
        return this;
    }

    public DataBox setPassword(String password) {
        this.password = password;
        return this;
    }

    public DataBox setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTestRunnerIdentity() {
        return testRunnerIdentity;
    }

    public String getTestRunnerUrl() {
        return testRunnerUrl;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public String getTestRunId() {
        return testRunId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public long getHeartbeatSequence() {
        return heartbeatSequence;
    }

    public long getHeartbeatInterval() {
        return heartbeatInterval;

    }

    public String getGuiServerUrl() {
        return guiServerUrl;
    }


    public String getTestName() {
        return testName;
    }

    public long getTestStartTime() {
        return testStartTime;
    }

    public double getTestProgress() {
        return testProgress;
    }

    public String getTestErrorcode() {
        return testErrorcode;
    }

    public String getTestErrortext() {
        return testErrortext;
    }

    public String getGitCheckoutFolder() {
        return gitCheckoutFolder;
    }

    public void setGitCheckoutFolder(String gitCheckoutFolder) {
        this.gitCheckoutFolder = gitCheckoutFolder;
    }
}
