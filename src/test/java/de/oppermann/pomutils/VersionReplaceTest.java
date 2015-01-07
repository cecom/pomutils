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
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import de.oppermann.pomutils.util.POM;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class VersionReplaceTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

		File testTargetResourceFolder = new File("target/testresources/versionReplacer");
		FileUtils.deleteDirectory(testTargetResourceFolder);
		FileUtils.copyDirectory(new File("src/test/resources/versionReplacer"), testTargetResourceFolder);
	}

	private POM adjustPomToVersion(String pomToAdjust, String newVersion) throws IOException, XMLStreamException {
		PomVersionReplacer pomVersionReplacer = new PomVersionReplacer(pomToAdjust);
		pomVersionReplacer.setVersionTo(newVersion);

		return new POM(pomToAdjust);
	}

	public void testParentAndProjectVersionChange() throws Exception {
		String newVersion = "5.0";
		POM resultPom = adjustPomToVersion("target/testresources/versionReplacer/parent.and.project.version.xml", newVersion);

		assertTrue("parent version update succeeded", newVersion.equals(resultPom.getParentVersion()));
		assertTrue("project version update succeeded", newVersion.equals(resultPom.getProjectVersion()));
	}

	public void testParentVersionChange() throws Exception {
		String newVersion = "5.0";
		POM resultPom = adjustPomToVersion("target/testresources/versionReplacer/parent.version.pom.xml", newVersion);

		assertTrue("parent version update succeeded", newVersion.equals(resultPom.getParentVersion()));
		assertNull("project version does not exist", resultPom.getProjectVersion());
	}

	public void testProjectVersionChange() throws Exception {
		String newVersion = "5.0";
		POM resultPom = adjustPomToVersion("target/testresources/versionReplacer/project.version.pom.xml", newVersion);

		assertNull("parent version does not exist", resultPom.getParentVersion());
		assertTrue("project version update succeeded", newVersion.equals(resultPom.getProjectVersion()));
	}
}