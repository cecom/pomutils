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

import javax.xml.stream.XMLStreamException;

import org.codehaus.plexus.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oppermann.pomutils.rules.Rule;
import de.oppermann.pomutils.rules.Ruleset;
import de.oppermann.pomutils.util.POM;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class PomMergeDriver {

	private final Logger logger = LoggerFactory.getLogger(PomMergeDriver.class);

	private Ruleset ruleset;
	private final String basePomFile;
	private final String ourPomFile;
	private final String theirPomFile;

	public PomMergeDriver(Ruleset ruleset, String basePomFile, String ourPomFile, String theirPomFile) {
		this.ruleset = ruleset;
		this.basePomFile = basePomFile;
		this.ourPomFile = ourPomFile;
		this.theirPomFile = theirPomFile;
	}

	public int merge() {
		try {
			POM basePom = new POM(basePomFile);
			POM ourPom = new POM(ourPomFile);
			POM theirPom = new POM(theirPomFile);

			for (Rule rule : ruleset.getRules()) {
				rule.evaluate(basePom, ourPom, theirPom);
			}

			basePom.savePom();
			theirPom.savePom();
			ourPom.savePom();
		} catch (IOException e) {
			logger.warn("Exception when attempting to merge pom versions.  Falling back to default merge.", e);
		} catch (XMLStreamException e) {
			logger.warn("Exception when attempting to merge pom versions.  Falling back to default merge.", e);
		}

		return doGitMerge();
	}

	private int doGitMerge() {
		ProcessBuilder processBuilder = new ProcessBuilder("git", "merge-file", "-L", "ours", "-L", "base", "-L", "theirs",
		        ourPomFile, basePomFile, theirPomFile);
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
