package de.oppermann.pomutils;


import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

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
 * Tests tab expand feature:
 *
 * <pre>
 * --expandtab={amount-of-space}
 * </pre>
 *
 * @author Boris Brodski <brodsky_boris@yahoo.com>
 *
 */
public class TabExpanderTest extends TestCase {
	private static File dir;

	public void setUp() {
		dir = new File(TestUtils.resourceBaseTestFolder + "/" + "tabExpander");
		dir.mkdirs();
	}

	public void testExpandTab1() throws Exception {
		String result = expand("", 4);
		assertEquals("", result);
	}

	public void testExpandTab2() throws Exception {
		String result = expand("\t", 4);
		assertEquals("    ", result);
	}

	public void testExpandTab3() throws Exception {
		String result = expand("line1\n\t\tText:\ttest\n\t\t\n", 4);
		assertEquals("line1\n        Text:\ttest\n        \n", result);
	}

	public void testExpandTab4() throws Exception {
		String result = expand("line1\n\t\tText:\ttest\n\t\t\n", 1);
		assertEquals("line1\n  Text:\ttest\n  \n", result);
	}

	public void testIntegrationWithoutExpandTab() throws Exception {
		String myTestSubFolder = "tabExpander/withoutExpandTab";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String[] args = {
				"merge"
				, "--our=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml"
				, "--base=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml"
				, "--their=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml" };

		assertEquals(1, Main.mainInternal(args));
	}

	public void testIntegrationWithExpandTab() throws Exception {
		String myTestSubFolder = "tabExpander/withExpandTab";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String[] args = {
				"merge"
				, "--our=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml"
				, "--base=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml"
				, "--their=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml"
				, "--expandtab=4" };

		assertEquals(0, Main.mainInternal(args));
	}

	private String expand(String from, int spaces) throws Exception {
		File file = new File(dir, "test.txt");
		FileUtils.write(file, from);
		new TabExpander(file.getAbsolutePath(), spaces).expand();

		return FileUtils.readFileToString(file);
	}

}
