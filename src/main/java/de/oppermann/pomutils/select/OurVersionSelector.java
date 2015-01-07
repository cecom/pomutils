package de.oppermann.pomutils.select;

import de.oppermann.pomutils.util.VersionFieldType;

/**
 * Always select 'our' version.
 */
public class OurVersionSelector implements VersionSelector {

	@Override
	public String selectVersion(String projectIdentifier, VersionFieldType versionFieldType, String ourVersion, String theirVersion) {
		return ourVersion;
	}

}
