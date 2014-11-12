package de.oppermann.pomutils;

import java.io.File;

import junit.framework.TestCase;
import de.oppermann.pomutils.rules.Ruleset;
import de.oppermann.pomutils.util.POM;

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

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */
public class RulesetTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
	}

	public void testProjectAndParentVersionWithOurStrategy() throws Exception {
		String myTestSubFolder = "rulesets/rulesetProjectAndParentVersion/ourStrategy";

		TestUtils.prepareTestFolder(myTestSubFolder);

		File rulesetFile = new File(TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/ruleset.yaml");
		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		String versionBeforeMerge = new POM(ourPomFile).getProjectVersion();
		Ruleset ruleset = new Ruleset(rulesetFile);

		int mergeReturnValue = doMerge(ruleset, basePomFile, ourPomFile, theirPomFile);

		assertTrue("merge succeeded", mergeReturnValue == 0);

		POM theirPom = new POM(theirPomFile);
		POM ourPom = new POM(ourPomFile);

		assertEquals("same version now", ourPom.getProjectVersion(), theirPom.getProjectVersion());
		assertEquals("our version should win", versionBeforeMerge, ourPom.getProjectVersion());
	}

	public void testProjectAndParentVersionWithTheirsStrategy() throws Exception {
		String myTestSubFolder = "rulesets/rulesetProjectAndParentVersion/theirStrategy";

		TestUtils.prepareTestFolder(myTestSubFolder);

		File rulesetFile = new File(TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/ruleset.yaml");
		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		String versionBeforeMerge = new POM(theirPomFile).getProjectVersion();
		Ruleset ruleset = new Ruleset(rulesetFile);

		int mergeReturnValue = doMerge(ruleset, basePomFile, ourPomFile, theirPomFile);

		assertTrue("merge succeeded", mergeReturnValue == 0);

		POM theirPom = new POM(theirPomFile);
		POM ourPom = new POM(ourPomFile);

		assertEquals("same version now", ourPom.getProjectVersion(), theirPom.getProjectVersion());
		assertEquals("their version should win", versionBeforeMerge, ourPom.getProjectVersion());
	}

	private int doMerge(Ruleset ruleset, String basePomFile, String ourPomFile, String theirPomFile) {
		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		return pomMergeDriver.merge();
	}
}
