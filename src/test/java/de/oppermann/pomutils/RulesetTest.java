package de.oppermann.pomutils;

import java.io.File;

import junit.framework.TestCase;

import org.xmlbeam.XBProjector;

import de.oppermann.pomutils.model.PomModel;
import de.oppermann.pomutils.rules.Ruleset;

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

	private XBProjector xbProjector;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");

		xbProjector = TestUtils.createXBProjector();
	}

	public void testProjectAndParentVersionWithOurStrategy() throws Exception {
		String myTestSubFolder = "rulesets/rulesetProjectAndParentVersion/ourStrategy";

		TestUtils.prepareTestFolder(myTestSubFolder);

		File rulesetFile = new File(TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/ruleset.yaml");
		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		String versionBeforeMerge = xbProjector.io().file(ourPomFile).read(PomModel.class).getProjectArtifact()
		        .getVersion();
		Ruleset ruleset = new Ruleset(rulesetFile);

		int mergeReturnValue = doMerge(ruleset, basePomFile, ourPomFile, theirPomFile);

		assertTrue("merge succeeded", mergeReturnValue == 0);

		PomModel theirPom = xbProjector.io().file(theirPomFile).read(PomModel.class);
		PomModel ourPom = xbProjector.io().file(ourPomFile).read(PomModel.class);

		assertEquals("same version now", ourPom.getProjectArtifact().getVersion(), theirPom.getProjectArtifact()
		        .getVersion());
		assertEquals("our version should win", versionBeforeMerge, ourPom.getProjectArtifact().getVersion());
	}

	public void testProjectAndParentVersionWithTheirsStrategy() throws Exception {
		String myTestSubFolder = "rulesets/rulesetProjectAndParentVersion/theirStrategy";

		TestUtils.prepareTestFolder(myTestSubFolder);

		File rulesetFile = new File(TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/ruleset.yaml");
		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		String versionBeforeMerge = xbProjector.io().file(theirPomFile).read(PomModel.class).getProjectArtifact()
		        .getVersion();
		Ruleset ruleset = new Ruleset(rulesetFile);

		int mergeReturnValue = doMerge(ruleset, basePomFile, ourPomFile, theirPomFile);

		assertTrue("merge succeeded", mergeReturnValue == 0);

		PomModel theirPom = xbProjector.io().file(theirPomFile).read(PomModel.class);
		PomModel ourPom = xbProjector.io().file(ourPomFile).read(PomModel.class);

		assertEquals("same version now", ourPom.getProjectArtifact().getVersion(), theirPom.getProjectArtifact()
		        .getVersion());
		assertEquals("their version should win", versionBeforeMerge, ourPom.getProjectArtifact().getVersion());
	}

	private int doMerge(Ruleset ruleset, String basePomFile, String ourPomFile, String theirPomFile) {
		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		return pomMergeDriver.merge();
	}

	public void testPropertyRuleWithOurStrategy() throws Exception {
		String myTestSubFolder = "rulesets/rulesetPropertyRule/ourStrategy";

		TestUtils.prepareTestFolder(myTestSubFolder);

		File rulesetFile = new File(TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/ruleset.yaml");
		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		PomModel ourPomBeforeMerge = xbProjector.io().file(ourPomFile).read(PomModel.class);

		String foobarPropertyExpectedResult = ourPomBeforeMerge.getPropertyValue("foobar");
		String jdbcBaseUrlExpectedResult = ourPomBeforeMerge.getPropertyValue("jdbc.base.url");

		Ruleset ruleset = new Ruleset(rulesetFile);

		int mergeReturnValue = doMerge(ruleset, basePomFile, ourPomFile, theirPomFile);

		assertTrue("merge succeeded", mergeReturnValue == 0);

		PomModel theirPom = xbProjector.io().file(theirPomFile).read(PomModel.class);
		PomModel ourPom = xbProjector.io().file(ourPomFile).read(PomModel.class);

		assertEquals("<foobar> property same content now", ourPom.getPropertyValue("foobar"), theirPom
		        .getPropertyValue("foobar"));
		assertEquals("<jdbc.base.url> property same content now", ourPom.getPropertyValue("jdbc.base.url"),
		        theirPom.getPropertyValue("jdbc.base.url"));
		assertEquals("our version of <foobar> should win", foobarPropertyExpectedResult,
		        ourPom.getPropertyValue("foobar"));
		assertEquals("our version of <jdbc.base.url> should win", jdbcBaseUrlExpectedResult,
		        ourPom.getPropertyValue("jdbc.base.url"));

		assertNull("property <foobar> in profile <develop> should not exist", ourPom.getProfile("develop")
		        .getPropertyValue("foobar"));
		assertEquals("property <foobar> in profile <delivery> should be empty", "",
		        ourPom.getProfile("delivery").getPropertyValue("foobar"));

		assertEquals("our version of <jdbc.base.url> in profile <develop> should win", jdbcBaseUrlExpectedResult,
		        ourPom.getProfile("develop").getPropertyValue("jdbc.base.url"));
		assertEquals("<jdbc.base.url> of profile <delivery> should be empty", "",
		        ourPom.getProfile("delivery").getPropertyValue("jdbc.base.url"));
	}

	public void testPropertyRuleWithTheirStrategy() throws Exception {
		String myTestSubFolder = "rulesets/rulesetPropertyRule/theirStrategy";

		TestUtils.prepareTestFolder(myTestSubFolder);

		File rulesetFile = new File(TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/ruleset.yaml");
		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		PomModel theirPomBeforeMerge = xbProjector.io().file(theirPomFile).read(PomModel.class);

		String foobarPropertyExpectedResult = theirPomBeforeMerge.getPropertyValue("foobar");
		String jdbcBaseUrlExpectedResult = theirPomBeforeMerge.getPropertyValue("jdbc.base.url");
		Ruleset ruleset = new Ruleset(rulesetFile);

		int mergeReturnValue = doMerge(ruleset, basePomFile, ourPomFile, theirPomFile);

		assertTrue("merge succeeded", mergeReturnValue == 0);

		PomModel theirPom = xbProjector.io().file(theirPomFile).read(PomModel.class);
		PomModel ourPom = xbProjector.io().file(ourPomFile).read(PomModel.class);

		assertEquals("<foobar> property same content now", theirPom.getPropertyValue("foobar"),
		        ourPom.getPropertyValue("foobar"));
		assertEquals("<jdbc.base.url> property same content now",
		        theirPom.getPropertyValue("jdbc.base.url"),
		        ourPom.getPropertyValue("jdbc.base.url"));
		assertEquals("their version of <foobar> should win", foobarPropertyExpectedResult,
		        ourPom.getPropertyValue("foobar"));
		assertEquals("their version of <jdbc.base.url> should win", jdbcBaseUrlExpectedResult,
		        ourPom.getPropertyValue("jdbc.base.url"));

		assertNull("property <foobar> in profile <develop> should not exist", ourPom.getProfile("develop")
		        .getPropertyValue("foobar"));
		assertEquals("property <foobar> in profile <delivery> should be empty", "",
		        ourPom.getProfile("delivery").getPropertyValue("foobar"));

		assertEquals("their version of <jdbc.base.url> in profile <develop> should win", jdbcBaseUrlExpectedResult,
		        ourPom.getProfile("develop").getPropertyValue("jdbc.base.url"));
		assertEquals("<jdbc.base.url> of profile <delivery> should be empty", "",
		        ourPom.getProfile("delivery").getPropertyValue("jdbc.base.url"));
	}

}
