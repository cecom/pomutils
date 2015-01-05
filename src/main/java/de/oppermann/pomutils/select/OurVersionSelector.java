package de.oppermann.pomutils.select;

/**
 * Always select 'our' version.
 */
public class OurVersionSelector implements VersionSelector {

	@Override
	public String selectVersion(String projectIdentifier, String ourVersion, String theirVersion) {
		return ourVersion;
	}

}
