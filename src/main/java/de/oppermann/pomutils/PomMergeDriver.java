package de.oppermann.pomutils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oppermann.pomutils.select.VersionSelector;
import de.oppermann.pomutils.util.POM;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class PomMergeDriver {

	private final Logger logger = LoggerFactory.getLogger(PomMergeDriver.class);
	
	private final POM basePom;
	private final POM ourPom;
	private final POM theirPom;
	
	/**
	 * The version selector to use resolve version conflicts.
	 */
	private final VersionSelector versionSelector;
	
	private static enum VersionField {
		PROJECT {
			@Override
			public String get(POM pom) {
				return pom.getProjectVersion();
			}
			@Override
			public void set(POM pom, String newVersion) {
				pom.setProjectVersion(newVersion);
			}
			
		},
		PARENT {
			@Override
			public String get(POM pom) {
				return pom.getParentVersion();
			}
			@Override
			public void set(POM pom, String newVersion) {
				pom.setParentVersion(newVersion);
			}
			
		};
		
		public abstract String get(POM pom);
		public abstract void set(POM pom, String newVersion);
	}

	public PomMergeDriver(String basePomFile, String ourPomFile, String theirPomFile, VersionSelector versionSelector) {
		basePom = new POM(basePomFile);
		ourPom = new POM(ourPomFile);
		theirPom = new POM(theirPomFile);
		this.versionSelector = versionSelector;
	}

	public int merge() {
		
		List<POM> adjustedPoms = new ArrayList<POM>();
		
		addIfNotNull(adjustedPoms, adjustVersion(VersionField.PROJECT));
		addIfNotNull(adjustedPoms, adjustVersion(VersionField.PARENT));
		
		for (POM adjustedPom : adjustedPoms) {
			adjustedPom.savePom();
		}
		
		return doGitMerge();
	}

	private void addIfNotNull(List<POM> adjustedPoms, POM adjustedPom) {
		if (adjustedPom != null) {
			adjustedPoms.add(adjustedPom);
		}
	}

	private POM adjustVersion(VersionField versionField) {
		String ourVersion = versionField.get(ourPom);
		String theirVersion = versionField.get(theirPom);
		if (ourVersion != null && !ourVersion.equals(theirVersion)) {
			String newVersion = versionSelector.selectVersion(ourPom.getProjectIdentifier(), ourVersion, theirVersion);
					
			if (newVersion != null) {
						
				POM pomToAdjust = newVersion.equals(ourVersion)
						? theirPom
						: ourPom;
				
				versionField.set(pomToAdjust, newVersion);
				return pomToAdjust;
			}
		}
		return null;
	}

	private int doGitMerge() {
		ProcessBuilder processBuilder = new ProcessBuilder("git", "merge-file", "-L", "ours", "-L", "base", "-L", "theirs", ourPom.getPath(),
		        basePom.getPath(), theirPom.getPath());
		processBuilder.redirectErrorStream(true);
		try {
			final Process p = processBuilder.start();

			consumeGitOutput(p);

			return p.waitFor();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void consumeGitOutput(final Process p) throws IOException {
		
		String output = IOUtil.toString(new BufferedInputStream(p.getInputStream(), 256));
		
		logger.debug("Git merge output:\n{}", output);
	}

}
