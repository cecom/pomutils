package de.oppermann.pomutils.select;

import de.oppermann.pomutils.util.VersionFieldType;

/**
 * Callback used to select a version.
 */
public interface VersionSelector {

	/**
	 * Selects the version to use to resolve a version conflict.
	 * 
	 * @param projectIdentifier Human-readable string used to identify the pom project.
	 * @param versionFieldType type of version field where the conflict was found.
	 * @param ourVersion version of 'our' pom file
	 * @param theirVersion version of 'their' pom file
	 * @return the version to use to resolve the conflict. (either ourVersion, theirVersion, or null)
	 *         if null, then the conflict will not be resolved. 
	 */
	String selectVersion(String projectIdentifier, VersionFieldType versionFieldType, String ourVersion, String theirVersion);
}
