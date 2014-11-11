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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oppermann.pomutils.util.POM;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class PomMergeDriver {

	private final Logger logger = LoggerFactory.getLogger(PomMergeDriver.class);

	private POM basePom;
	private POM ourPom;
	private POM theirPom;

	public PomMergeDriver(String basePomFile, String ourPomFile, String theirPomFile) {
		basePom = new POM(basePomFile);
		ourPom = new POM(ourPomFile);
		theirPom = new POM(theirPomFile);
	}

	public void adjustTheirPomVersion() {
		adjustParentVersion();
		adjustProjectVersion();
		theirPom.savePom();
	}

	public int doGitMerge() {
		ProcessBuilder processBuilder = new ProcessBuilder("git", "merge-file", "-p", "-L", "our", "-L", "base", "-L", "theirs", ourPom.getPath(),
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

	private void adjustParentVersion() {
		String ourParentVersion = ourPom.getParentVersion();
		String theirParentVersion = theirPom.getParentVersion();

		if (ourParentVersion != null && !ourParentVersion.equals(theirParentVersion)) {
			theirPom.setParentVersion(ourParentVersion);
		}
	}

	private void adjustProjectVersion() {
		String ourVersion = ourPom.getProjectVersion();
		String theirVersion = theirPom.getProjectVersion();

		if (ourVersion != null && !ourVersion.equals(theirVersion)) {
			theirPom.setProjectVersion(ourVersion);
		}
	}

	private void consumeGitOutput(final Process p) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		logger.debug("Git merge output:");
		logger.debug("=====================================");
		String line;
		while ((line = reader.readLine()) != null) {
			logger.debug(line);
		}
		reader.close();
		logger.debug("=====================================");
	}

}
