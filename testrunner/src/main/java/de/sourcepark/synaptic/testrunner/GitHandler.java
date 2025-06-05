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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for handling Git repository operations, specifically checking out
 * a repository from a URL to a specified directory.
 */
public class GitHandler {
    private static final Logger LOGGER = LogManager.getLogger(GitHandler.class);

    /**
     * Checks out a Git repository from the specified URL to the target directory.
     * If the target directory already exists, it will be deleted and recreated.
     *
     * @param gitUrl The URL of the Git repository to check out
     * @param targetDirectory The directory where the repository should be checked out
     * @return true if checkout was successful, false otherwise
     */
    public boolean checkoutRepository(String gitUrl, String targetDirectory) {
        LOGGER.info("Checking out repository from {} to {}", gitUrl, targetDirectory);

        try {
            // Create Path object for target directory
            Path targetPath = Paths.get(targetDirectory);

            // Delete directory if it exists
            if (Files.exists(targetPath)) {
                LOGGER.info("Target directory already exists. Deleting it...");
                deleteDirectory(targetPath.toFile());
            }

            // Create parent directories if they don't exist
            Files.createDirectories(targetPath.getParent());

            // Clone the repository
            LOGGER.info("Cloning repository...");
            Git.cloneRepository()
                .setURI(gitUrl)
                .setDirectory(targetPath.toFile())
                .call();

            LOGGER.info("Repository successfully checked out");
            return true;
        } catch (GitAPIException e) {
            LOGGER.error("Git operation failed: {}", e.getMessage(), e);
            return false;
        } catch (IOException e) {
            LOGGER.error("I/O operation failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks out a Git repository with username and password authentication.
     *
     * @param gitUrl The URL of the Git repository to check out
     * @param targetDirectory The directory where the repository should be checked out
     * @param username The username for authentication
     * @param password The password for authentication
     * @return true if checkout was successful, false otherwise
     */
    public boolean checkoutRepositoryWithCredentials(String gitUrl, String targetDirectory, 
                                                    String username, String password) {
        LOGGER.info("Checking out repository from {} to {} with username/password auth", gitUrl, targetDirectory);

        try {
            // Create Path object for target directory
            Path targetPath = Paths.get(targetDirectory);

            // Delete directory if it exists
            if (Files.exists(targetPath)) {
                LOGGER.info("Target directory already exists. Deleting it...");
                deleteDirectory(targetPath.toFile());
            }

            // Create parent directories if they don't exist
            Files.createDirectories(targetPath.getParent());

            // Create credentials provider
            CredentialsProvider credentialsProvider = 
                new UsernamePasswordCredentialsProvider(username, password);

            // Clone the repository with credentials
            LOGGER.info("Cloning repository with credentials...");
            Git.cloneRepository()
                .setURI(gitUrl)
                .setDirectory(targetPath.toFile())
                .setCredentialsProvider(credentialsProvider)
                .call();

            LOGGER.info("Repository successfully checked out");
            return true;
        } catch (GitAPIException e) {
            LOGGER.error("Git operation failed: {}", e.getMessage(), e);
            return false;
        } catch (IOException e) {
            LOGGER.error("I/O operation failed: {}", e.getMessage(), e);
            return false;
        } catch (Throwable e) {
            LOGGER.error("Unexpected error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Recursively deletes a directory and all its contents.
     *
     * @param directory The directory to delete
     * @return true if deletion was successful, false otherwise
     */
    private boolean deleteDirectory(File directory) {
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File file : contents) {
                deleteDirectory(file);
            }
        }
        return directory.delete();
    }

    /**
     * Checks out a specific branch or tag of a Git repository.
     *
     * @param gitUrl The URL of the Git repository
     * @param targetDirectory The directory where the repository should be checked out
     * @param branchOrTag The branch or tag to check out
     * @return true if checkout was successful, false otherwise
     */
    public boolean checkoutRepositoryBranch(String gitUrl, String targetDirectory, String branchOrTag) {
        LOGGER.info("Checking out repository from {} (branch/tag: {}) to {}", gitUrl, branchOrTag, targetDirectory);

        try {
            // Create Path object for target directory
            Path targetPath = Paths.get(targetDirectory);

            // Delete directory if it exists
            if (Files.exists(targetPath)) {
                LOGGER.info("Target directory already exists. Deleting it...");
                deleteDirectory(targetPath.toFile());
            }

            // Create parent directories if they don't exist
            Files.createDirectories(targetPath.getParent());

            // Clone the repository with specific branch
            LOGGER.info("Cloning repository with branch/tag: {}", branchOrTag);
            Git.cloneRepository()
                .setURI(gitUrl)
                .setDirectory(targetPath.toFile())
                .setBranch(branchOrTag)
                .call();

            LOGGER.info("Repository successfully checked out");
            return true;
        } catch (GitAPIException e) {
            LOGGER.error("Git operation failed: {}", e.getMessage(), e);
            return false;
        } catch (IOException e) {
            LOGGER.error("I/O operation failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks out a specific branch or tag of a Git repository with username and password authentication.
     *
     * @param gitUrl The URL of the Git repository
     * @param targetDirectory The directory where the repository should be checked out
     * @param branchOrTag The branch or tag to check out
     * @param username The username for authentication
     * @param password The password for authentication
     * @return true if checkout was successful, false otherwise
     */
    public boolean checkoutRepositoryBranchWithCredentials(String gitUrl, String targetDirectory, 
                                                         String branchOrTag, String username, String password) {
        LOGGER.info("Checking out repository from {} (branch/tag: {}) to {} with username/password auth", 
                  gitUrl, branchOrTag, targetDirectory);

        try {
            // Create Path object for target directory
            Path targetPath = Paths.get(targetDirectory);

            // Delete directory if it exists
            if (Files.exists(targetPath)) {
                LOGGER.info("Target directory already exists. Deleting it...");
                deleteDirectory(targetPath.toFile());
            }

            // Create parent directories if they don't exist
            Files.createDirectories(targetPath.getParent());

            // Create credentials provider
            CredentialsProvider credentialsProvider = 
                new UsernamePasswordCredentialsProvider(username, password);

            // Clone the repository with specific branch and credentials
            LOGGER.info("Cloning repository with branch/tag: {} and credentials", branchOrTag);
            Git.cloneRepository()
                .setURI(gitUrl)
                .setDirectory(targetPath.toFile())
                .setBranch(branchOrTag)
                .setCredentialsProvider(credentialsProvider)
                .call();

            LOGGER.info("Repository successfully checked out");
            return true;
        } catch (GitAPIException e) {
            LOGGER.error("Git operation failed: {}", e.getMessage(), e);
            return false;
        } catch (IOException e) {
            LOGGER.error("I/O operation failed: {}", e.getMessage(), e);
            return false;
        }
    }
}
