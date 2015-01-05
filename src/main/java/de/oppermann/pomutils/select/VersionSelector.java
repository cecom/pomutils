package de.oppermann.pomutils.select;

/**
 * Callback used to select a version.
 */
public interface VersionSelector {

    String selectVersion(String projectIdentifier, String ourVersion, String theirVersion);
}
