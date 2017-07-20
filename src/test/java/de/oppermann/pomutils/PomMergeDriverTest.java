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

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.codehaus.mojo.versions.api.PomHelper;
import org.codehaus.mojo.versions.rewriting.ModifiedPomXMLEventReader;

import com.ctc.wstx.stax.WstxInputFactory;

import de.oppermann.pomutils.rules.Ruleset;
import de.oppermann.pomutils.select.SelectionStrategy;
import de.oppermann.pomutils.util.POM;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class PomMergeDriverTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
	}

	public void testAutoMergeSucceded() throws Exception {
		String myTestSubFolder = "merge/autoMergeSucceded";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		Ruleset ruleset = new Ruleset(SelectionStrategy.OUR);

		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		int mergeReturnValue = pomMergeDriver.merge();

		assertTrue("merge succeeded", mergeReturnValue == 0);

		POM theirPom = new POM(theirPomFile);
		POM ourPom = new POM(ourPomFile);

		assertEquals("our", ourPom.getProjectVersion());
		assertEquals("our", theirPom.getProjectVersion());

		String theirDependecyVersoin = PomHelper.getRawModel(new File(theirPomFile)).getDependencies().get(0).getVersion();
		String ourDependencyVersion = PomHelper.getRawModel(new File(ourPomFile)).getDependencies().get(0).getVersion();

		assertEquals("dependency version change merged", theirDependecyVersoin, ourDependencyVersion);
	}

	public void testAutoMergeSucceded_their() throws Exception {
		String myTestSubFolder = "merge/autoMergeSucceded_their";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		Ruleset ruleset = new Ruleset(SelectionStrategy.THEIR);

		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		int mergeReturnValue = pomMergeDriver.merge();

		assertTrue("merge succeeded", mergeReturnValue == 0);

		POM theirPom = new POM(theirPomFile);
		POM ourPom = new POM(ourPomFile);

		assertEquals("their", ourPom.getProjectVersion());
		assertEquals("their", theirPom.getProjectVersion());

		String theirDependecyVersoin = PomHelper.getRawModel(new File(theirPomFile)).getDependencies().get(0).getVersion();
		String ourDependencyVersion = PomHelper.getRawModel(new File(ourPomFile)).getDependencies().get(0).getVersion();

		assertEquals("dependency version change merged", theirDependecyVersoin, ourDependencyVersion);
	}

	public void testAutoMergeSucceded_noConflict_our() throws Exception {
		String myTestSubFolder = "merge/autoMergeSucceded_noConflict_our";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		Ruleset ruleset = new Ruleset(SelectionStrategy.PROMPT);

		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		int mergeReturnValue = pomMergeDriver.merge();

		assertTrue("merge succeeded", mergeReturnValue == 0);

		POM theirPom = new POM(theirPomFile);
		POM ourPom = new POM(ourPomFile);

		assertEquals("our", ourPom.getProjectVersion());
		assertEquals("our", theirPom.getProjectVersion());

		String theirDependecyVersoin = PomHelper.getRawModel(new File(theirPomFile)).getDependencies().get(0).getVersion();
		String ourDependencyVersion = PomHelper.getRawModel(new File(ourPomFile)).getDependencies().get(0).getVersion();

		assertEquals("dependency version change merged", theirDependecyVersoin, ourDependencyVersion);
	}

	public void testAutoMergeSucceded_noConflict_their() throws Exception {
		String myTestSubFolder = "merge/autoMergeSucceded_noConflict_their";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		Ruleset ruleset = new Ruleset(SelectionStrategy.PROMPT);

		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		int mergeReturnValue = pomMergeDriver.merge();

		assertTrue("merge succeeded", mergeReturnValue == 0);

		POM theirPom = new POM(theirPomFile);
		POM ourPom = new POM(ourPomFile);

		assertEquals("their", ourPom.getProjectVersion());
		assertEquals("their", theirPom.getProjectVersion());

		String theirDependecyVersoin = PomHelper.getRawModel(new File(theirPomFile)).getDependencies().get(0).getVersion();
		String ourDependencyVersion = PomHelper.getRawModel(new File(ourPomFile)).getDependencies().get(0).getVersion();

		assertEquals("dependency version change merged", theirDependecyVersoin, ourDependencyVersion);
	}

	public void testAutoMergeSucceded_prompt() throws Exception {
		String myTestSubFolder = "merge/autoMergeSucceded_prompt";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		Ruleset ruleset = new Ruleset(SelectionStrategy.THEIR);

		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		int mergeReturnValue = pomMergeDriver.merge();

		assertTrue("merge succeeded", mergeReturnValue == 0);
		POM theirPom = new POM(theirPomFile);
		POM ourPom = new POM(ourPomFile);

		assertEquals("their", ourPom.getParentVersion());
		assertEquals("their", theirPom.getParentVersion());

		String theirDependecyVersoin = PomHelper.getRawModel(new File(theirPomFile)).getDependencies().get(0).getVersion();
		String ourDependencyVersion = PomHelper.getRawModel(new File(ourPomFile)).getDependencies().get(0).getVersion();

		assertEquals("dependency version change merged", theirDependecyVersoin, ourDependencyVersion);
	}

	public void testAutoMergeFailed() throws Exception {
		String myTestSubFolder = "merge/autoMergeFailed";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		Ruleset ruleset = new Ruleset(SelectionStrategy.OUR);

		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		int mergeReturnValue = pomMergeDriver.merge();

		assertTrue("merge conflict", mergeReturnValue == 1);

		POM theirPom = new POM(theirPomFile);

		StringBuilder ourPomString = new StringBuilder(FileUtils.readFileToString(new File(ourPomFile)));
		String ourProjectVersion = PomHelper.getProjectVersion(new ModifiedPomXMLEventReader(ourPomString, new WstxInputFactory()));

		assertEquals("same version now", ourProjectVersion, theirPom.getProjectVersion());
	}
	
	public void testAutoMergeWithNoBaseCommit() throws Exception {
		String myTestSubFolder = "merge/autoMergeSucceded_noConflict_their";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String basePomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml";
		String ourPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml";
		String theirPomFile = TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml";

		Ruleset ruleset = new Ruleset(SelectionStrategy.THEIR);

		PomMergeDriver pomMergeDriver = new PomMergeDriver(ruleset, basePomFile, ourPomFile, theirPomFile);
		int mergeReturnValue = pomMergeDriver.merge();

		assertTrue("merge succeeded", mergeReturnValue == 0);

		POM theirPom = new POM(theirPomFile);
		POM ourPom = new POM(ourPomFile);

		assertEquals("their", ourPom.getProjectVersion());
		assertEquals("their", theirPom.getProjectVersion());
	}

}
