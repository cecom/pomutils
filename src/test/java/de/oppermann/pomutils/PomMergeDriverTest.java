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

import de.oppermann.pomutils.select.SelectionStrategy;
import de.oppermann.pomutils.select.TheirVersionSelector;
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
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

		File testTargetResourceFolder = new File("target/testresources/merge");
		FileUtils.deleteDirectory(testTargetResourceFolder);
		FileUtils.copyDirectory(new File("src/test/resources/merge"), testTargetResourceFolder);
	}

	public void testAutoMergeSucceded() throws Exception {
		String basePomFile = "target/testresources/merge/autoMergeSucceded/base.pom.xml";
		String ourPomFile = "target/testresources/merge/autoMergeSucceded/our.pom.xml";
		String theirPomFile = "target/testresources/merge/autoMergeSucceded/their.pom.xml";

		PomMergeDriver pomMergeDriver = new PomMergeDriver(basePomFile, ourPomFile, theirPomFile, SelectionStrategy.OUR.getSelector());
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
		String basePomFile = "target/testresources/merge/autoMergeSucceded_their/base.pom.xml";
		String ourPomFile = "target/testresources/merge/autoMergeSucceded_their/our.pom.xml";
		String theirPomFile = "target/testresources/merge/autoMergeSucceded_their/their.pom.xml";

		PomMergeDriver pomMergeDriver = new PomMergeDriver(basePomFile, ourPomFile, theirPomFile, SelectionStrategy.THEIR.getSelector());
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
		String basePomFile = "target/testresources/merge/autoMergeSucceded_prompt/base.pom.xml";
		String ourPomFile = "target/testresources/merge/autoMergeSucceded_prompt/our.pom.xml";
		String theirPomFile = "target/testresources/merge/autoMergeSucceded_prompt/their.pom.xml";

		PomMergeDriver pomMergeDriver = new PomMergeDriver(basePomFile, ourPomFile, theirPomFile, new TheirVersionSelector());
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
		String basePomFile = "target/testresources/merge/autoMergeFailed/base.pom.xml";
		String ourPomFile = "target/testresources/merge/autoMergeFailed/our.pom.xml";
		String theirPomFile = "target/testresources/merge/autoMergeFailed/their.pom.xml";

		PomMergeDriver pomMergeDriver = new PomMergeDriver(basePomFile, ourPomFile, theirPomFile, SelectionStrategy.OUR.getSelector());
		int mergeReturnValue = pomMergeDriver.merge();

		assertTrue("merge conflict", mergeReturnValue == 1);

		POM theirPom = new POM(theirPomFile);
		
		StringBuilder ourPomString = new StringBuilder(FileUtils.readFileToString(new File(ourPomFile)));
		String ourProjectVersion = PomHelper.getProjectVersion(new ModifiedPomXMLEventReader(ourPomString, new WstxInputFactory()));
		
		assertEquals("same version now", ourProjectVersion, theirPom.getProjectVersion());
	}
}