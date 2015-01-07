package de.oppermann.pomutils.select;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A {@link VersionSelector} that prompts the user
 * via an output stream and reads the response via an input stream.
 */
public class StreamVersionSelector implements VersionSelector {

	private final PrintStream out;
	private final BufferedReader in;
	
	/**
	 * Uses {@link System#out} and {@link System#in}. 
	 */
	public StreamVersionSelector() {
		this(System.out, System.in);
	}
	
	public StreamVersionSelector(OutputStream out, InputStream in) {
		this.out = (out instanceof PrintStream) ? (PrintStream) out : new PrintStream(out);
		this.in = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public String selectVersion(String projectIdentifier, String ourVersion, String theirVersion) {
		
		out.printf("%nVersion conflict found in pom file for %s.%n%n", projectIdentifier);
		
		out.println("Select the version to use to resolve the conflict:");
		out.println();
		
		out.printf("    1) %s (ours)%n", ourVersion);
		out.printf("    2) %s (theirs)%n", theirVersion);
		out.println("    s) Skip and resolve later");
		out.println();
		
		out.println("If the same conflict is found in other pom files in this merge");
		out.println("or any future merges that occur in the next 2 minutes),");
		out.println("your selection will be used for them as well.");
		out.println();
		
		try {
			do {
				out.printf("Preferred version? [1/2/s]: ");
				out.flush();
				
				String selection = in.readLine();
				
				if (selection == null || selection.equalsIgnoreCase("s")) {
					out.println();
					return null;
				} else if (selection.trim().equalsIgnoreCase("1") || selection.trim().equals(ourVersion)) {
					return ourVersion;
				} else if (selection.trim().equalsIgnoreCase("2") || selection.trim().equals(theirVersion)) {
					return theirVersion;
				}
				
			} while (true);
		} catch (IOException e) {
			throw new RuntimeException();
		} 
	}

}
