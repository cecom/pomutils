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

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class MainCallTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		File targetDirectory = new File("target/testresources/mainCall");
		FileUtils.deleteDirectory(targetDirectory);
		FileUtils.copyDirectory(new File("src/test/resources/mainCall"), targetDirectory);
	}

	public void testMergeCommand() throws Exception {
		String[] args = {
		        "--debug"
		        , "merge"
		        , "--our=target/testresources/mainCall/our.pom.xml"
		        , "--base=target/testresources/mainCall/base.pom.xml"
		        , "--their=target/testresources/mainCall/their.pom.xml" };

		assertEquals(0, Main.mainInternal(args));
	}

	public void testReplaceCommand() throws Exception {
		String[] args = {
		        "--debug"
		        , "replace"
		        , "--pom=target/testresources/mainCall/replace.pom.xml"
		        , "--version=\"5.0\"" };

		assertEquals(0, Main.mainInternal(args));
	}
}
