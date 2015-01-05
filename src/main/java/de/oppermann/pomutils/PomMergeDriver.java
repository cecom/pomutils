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

	public PomMergeDriver(String basePomFile, String ourPomFile, String theirPomFile, VersionSelector versionSelector) {
		basePom = new POM(basePomFile);
		ourPom = new POM(ourPomFile);
		theirPom = new POM(theirPomFile);
		this.versionSelector = versionSelector;
	}

	public int merge() {
		
		String ourVersion = ourPom.getProjectVersion();
		String theirVersion = theirPom.getProjectVersion();
		
		if (ourVersion == null) {
			ourVersion = ourPom.getParentVersion();
			theirVersion = theirPom.getParentVersion();
		}
		
		if (ourVersion != null && theirVersion != null && !ourVersion.equals(theirVersion)) {
			String newVersion = versionSelector.selectVersion(ourPom.getProjectIdentifier(), ourVersion, theirVersion);
					
			if (newVersion != null) {
						
				POM pomToAdjust = newVersion.equals(ourVersion)
						? theirPom
						: ourPom;
				
				adjustPomVersion(pomToAdjust, newVersion);
			}
		}
		return doGitMerge();
	}

	private void adjustPomVersion(POM pomToAdjust, String newVersion) {
		pomToAdjust.setProjectVersion(newVersion);
		pomToAdjust.setParentVersion(newVersion);
		pomToAdjust.savePom();
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
