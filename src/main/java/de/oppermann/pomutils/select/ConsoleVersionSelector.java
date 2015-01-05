package de.oppermann.pomutils.select;

import java.io.Console;

/**
 * A {@link VersionSelector} that prompts the user to select the version
 * via the system console.
 */
public class ConsoleVersionSelector implements VersionSelector {

	@Override
	public String selectVersion(String projectIdentifier, String ourVersion, String theirVersion) {
		Console console = System.console();
		
		console.printf("%nVersion conflict found in pom file for %s.%n%n", projectIdentifier);
		
		console.printf("Select the version to use to resolve the conflict:%n%n");
		
		console.printf("    1) %s (ours)%n", ourVersion);
		console.printf("    2) %s (theirs)%n", theirVersion);
		console.printf("    s) Skip and resolve later%n%n");
		
		console.printf("If the same conflict is found in other pom files in this merge%n");
		console.printf("or any future merges that occur in the next 2 minutes),%n");
		console.printf("your selection will be used for them as well.%n%n");
		
		do {
			String selection = console.readLine("Preferred version? [1/2/s]: ");
			
			if (selection == null || selection.trim().equalsIgnoreCase("s")) {
				console.printf("%n");
				return null;
			} else if (selection.trim().equalsIgnoreCase("1") || selection.trim().equals(ourVersion)) {
				return ourVersion;
			} else if (selection.trim().equalsIgnoreCase("2") || selection.trim().equals(theirVersion)) {
				return theirVersion;
			}
			
		} while (true); 
	}

}
