package de.oppermann.pomutils.select;

/**
 * Strategy to use to select a version to resolve conflicts.
 */
public enum SelectionStrategy {
	
	/**
	 * Always select 'our' version (the default). 
	 */
	OUR,
	/**
	 * Always select 'their' version. 
	 */
	THEIR,
	/**
	 * Prompt the user on the console for them to select the version. 
	 */
	PROMPT;
	
	/*
	 * Overridden so that the proper string appears in --help output. 
	 */
	public String toString() {
		return name().toLowerCase();
	}
}
