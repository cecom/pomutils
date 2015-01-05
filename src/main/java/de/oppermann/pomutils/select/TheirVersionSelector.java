package de.oppermann.pomutils.select;

/**
 * Always select 'their' version.
 */
public class TheirVersionSelector implements VersionSelector {

	@Override
	public String selectVersion(String projectIdentifier, String ourVersion, String theirVersion) {
		return theirVersion;
	}

}
