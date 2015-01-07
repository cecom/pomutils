package de.oppermann.pomutils.select;

import de.oppermann.pomutils.util.VersionFieldType;

/**
 * Always select 'their' version.
 */
public class TheirVersionSelector implements VersionSelector {

	@Override
	public String selectVersion(String projectIdentifier, VersionFieldType versionFieldType, String ourVersion, String theirVersion) {
		return theirVersion;
	}

}
