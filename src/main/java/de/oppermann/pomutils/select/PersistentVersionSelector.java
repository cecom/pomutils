package de.oppermann.pomutils.select;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.codehaus.plexus.util.IOUtil;

/**
 * Persists a selected version from a {@link #delegate} version selector to disk,
 * and returns any valid previously selected version on future invocations.
 * 
 * This is used to store user selection between process invocations.
 * The user's selection will be valid for 2 mins.
 */
public class PersistentVersionSelector implements VersionSelector {
	
	/**
	 * Default file that is used to persist selection.
	 * Found in the working directory.
	 */
	private static final File DEFAULT_PERSISTENT_FILE = new File(".pomVersionSelection");
	
	/**
	 * Timeout in milliseconds after which the selection is considered timed out.
	 * A user will have to make a new selection after this time has elapsed.
	 * 
	 * Since pomutils is invoked for every file, and there's no indication
	 * of the first or last invocation, we rely on a timeout to clear
	 * state between merge invocations.
	 */
	private static final long TIMEOUT = 2 * 60 * 1000; // 2 mins
	
	/**
	 * File that is used to persist selection state.
	 */
	private final File persistentFile;

	/**
	 * Used to select the version if the persistent state does not exist or is no longer valid.
	 */
	private final VersionSelector delegate;
	
	/**
	 * Represents the user's selection for a given conflict.
	 */
	private static class SelectionState {
		private static final String PROPERTY_OUR_VERSION = "ourVersion";
		private static final String PROPERTY_THEIR_VERSION = "theirVersion";
		private static final String PROPERTY_SELECTED_VERSION = "selectedVersion";

		private final String ourVersion;
		private final String theirVersion;
		/**
		 * Version that the user selected, or null if the user chose to skip resolution.
		 */
		private final String selectedVersion;
		
		private SelectionState(
				String ourVersion,
				String theirVersion,
				String selectedVersion) {
			
			super();
			this.ourVersion = ourVersion;
			this.theirVersion = theirVersion;
			this.selectedVersion = selectedVersion;
		}
		
		public static SelectionState fromProperties(Properties properties) {
			String ourVersion = properties.getProperty(PROPERTY_OUR_VERSION);
			String theirVersion = properties.getProperty(PROPERTY_THEIR_VERSION);
			String selectedVersion = properties.getProperty(PROPERTY_SELECTED_VERSION);
			
			if (ourVersion == null || theirVersion == null) {
				return null;
			}
			return new SelectionState(ourVersion, theirVersion, selectedVersion);
		}
		
		public Properties toProperties() {
			Properties properties = new Properties();
			properties.setProperty(PROPERTY_OUR_VERSION, ourVersion);
			properties.setProperty(PROPERTY_THEIR_VERSION, theirVersion);
			if (selectedVersion != null) {
				properties.setProperty(PROPERTY_SELECTED_VERSION, selectedVersion);
			}
			return properties;
		}
		
		public String getSelectedVersion() {
			return selectedVersion;
		}
		
		public boolean isValidFor(String ourVersion, String theirVersion) {
			return this.ourVersion.equals(ourVersion)
					&& this.theirVersion.equals(theirVersion);
		}
	}
	
	public PersistentVersionSelector(VersionSelector delegate) {
		this(delegate, DEFAULT_PERSISTENT_FILE);
	}

	public PersistentVersionSelector(VersionSelector delegate, File persistentFile) {
		super();
		this.delegate = delegate;
		this.persistentFile = persistentFile;
	}

	@Override
	public String selectVersion(String projectIdentifier, String ourVersion, String theirVersion) {
		try {
			SelectionState selectionState = readState();
			
			String selectedVersion;
			if (selectionState != null && selectionState.isValidFor(ourVersion, theirVersion)) {
				selectedVersion = selectionState.getSelectedVersion();
			} else {
				selectedVersion = delegate.selectVersion(projectIdentifier, ourVersion, theirVersion);
				writeState(new SelectionState(ourVersion, theirVersion, selectedVersion));
			}
			return selectedVersion;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Reads the selection state from {@link #persistentFile}.
	 */
	private SelectionState readState() throws IOException {
		if (!persistentFile.canRead()) {
			return null;
		}
		
		if (persistentFile.lastModified() < System.currentTimeMillis() - TIMEOUT) {
			return null;
		}
		
		FileReader reader = null; 
		
		try {
			reader = new FileReader(persistentFile);
			Properties properties = new Properties();
			properties.load(reader);
			
			return SelectionState.fromProperties(properties);
		} finally {
			IOUtil.close(reader);
		}
	}
	
	/**
	 * Writes the selection state to {@link #persistentFile}.
	 */
	private void writeState(SelectionState selectionState) throws IOException {
		FileWriter writer = null; 
		
		try {
			writer = new FileWriter(persistentFile);
			Properties properties = selectionState.toProperties();
			properties.store(
					writer,
					String.format("Used by the 'pomutils' merge driver to store version selections%n"
						+ "within the same invocation of git merge%n"
						+ "(to avoid repeatedly prompting the user to select a version).%n"
						+ "Selected versions will only be reused for 2 minutes.%n"
						+ "It is safe to delete this file between git merge invocations%n"
						+ "(which will cause the user to be prompted again).%n"));
		} finally {
			IOUtil.close(writer);
		}
	}
	
}
