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
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;

import de.oppermann.pomutils.model.PomModel;
import de.oppermann.pomutils.rules.Ruleset;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class PomMergeDriver {

	private final Logger logger = LoggerFactory.getLogger(PomMergeDriver.class);

	private Ruleset ruleset;
	private final File basePomFile;
	private final File ourPomFile;
	private final File theirPomFile;
	private final XBProjector xbProjector;

	public PomMergeDriver(Ruleset ruleset, String basePom, String ourPom, String theirPom) {
		this.ruleset = ruleset;
		this.basePomFile = new File(basePom);
		this.ourPomFile = new File(ourPom);
		this.theirPomFile = new File(theirPom);

		xbProjector = new XBProjector();
		xbProjector.config().as(DefaultXMLFactoriesConfig.class).setNamespacePhilosophy(NamespacePhilosophy.NIHILISTIC);
	}

	public int merge() {
		try {
			logger.debug("Doing merge [our={}] [base={}] [their={}]", ourPomFile.getAbsolutePath(),
			        basePomFile.getAbsolutePath(), theirPomFile.getAbsolutePath());

			PomModel basePom = xbProjector.io().file(basePomFile).read(PomModel.class);
			PomModel ourPom = xbProjector.io().file(ourPomFile).read(PomModel.class);
			PomModel theirPom = xbProjector.io().file(theirPomFile).read(PomModel.class);

			System.setProperty("line.separator", "\n");

			// FileUtils.copyFile(basePomFile, new File("c:/allegro/tmp/testUtils/base.original.xml"));
			// FileUtils.copyFile(ourPomFile, new File("c:/allegro/tmp/testUtils/our.original.xml"));
			// FileUtils.copyFile(theirPomFile, new File("c:/allegro/tmp/testUtils/their.original.xml"));

			ruleset.evaluate(basePom, ourPom, theirPom);

			// TODO: check obs nötig ist
			// xbProjector.io().file(basePomFile).write(basePom);
			xbProjector.io().file(ourPomFile).write(ourPom);
			xbProjector.io().file(theirPomFile).write(theirPom);

			// FileUtils.copyFile(basePomFile, new File("c:/allegro/tmp/testUtils/base.xmlbeam.xml"));
			// FileUtils.copyFile(ourPomFile, new File("c:/allegro/tmp/testUtils/our.xmlbeam.xml"));
			// FileUtils.copyFile(theirPomFile, new File("c:/allegro/tmp/testUtils/their.xmlbeam.xml"));
		} catch (Throwable e) {
			logger.warn("Exception when attempting to merge pom versions.  Falling back to default merge.", e);
		}

		return doGitMerge();
	}

	private int doGitMerge() {
		ProcessBuilder processBuilder = new ProcessBuilder("git", "merge-file", "-q", "-L", "ours", "-L", "base", "-L",
		        "theirs",
		        ourPomFile.getAbsolutePath(), basePomFile.getAbsolutePath(), theirPomFile.getAbsolutePath());
		processBuilder.redirectErrorStream(true);
		try {
			final Process p = processBuilder.start();

			// consumeGitOutput(p);

			return p.waitFor();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void consumeGitOutput(final Process p) throws IOException {

		String output = IOUtils.toString(new BufferedInputStream(p.getInputStream(), 256));

		logger.debug("Git merge output:\n{}", output);
	}

}
