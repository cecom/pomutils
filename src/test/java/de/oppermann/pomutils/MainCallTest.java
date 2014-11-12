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

import junit.framework.TestCase;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class MainCallTest extends TestCase {

	public void testMergeCommandWithoutRuleset() throws Exception {
		String myTestSubFolder = "mainCall/withoutRuleset";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String[] args = {
		        "merge"
		        , "--our=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml"
		        , "--base=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml"
		        , "--their=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml" };

		assertEquals(0, Main.mainInternal(args));
	}

	public void testMergeCommandWithRuleset() throws Exception {
		String myTestSubFolder = "mainCall/withRuleset";

		TestUtils.prepareTestFolder(myTestSubFolder);

		String[] args = {
		        "merge"
		        , "--ruleset=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/ruleset.yaml"
		        , "--our=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/our.pom.xml"
		        , "--base=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/base.pom.xml"
		        , "--their=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/their.pom.xml" };

		assertEquals(0, Main.mainInternal(args));
	}

	public void testReplaceCommand() throws Exception {
		String myTestSubFolder = "mainCall/versionReplace";

		TestUtils.prepareTestFolder(myTestSubFolder);
		String[] args = {
		        "replace"
		        , "--pom=" + TestUtils.resourceBaseTestFolder + "/" + myTestSubFolder + "/replace.pom.xml"
		        , "--version=\"5.0\"" };

		assertEquals(0, Main.mainInternal(args));
	}
}
