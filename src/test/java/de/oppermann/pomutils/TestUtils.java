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

import org.apache.commons.io.FileUtils;
import org.xmlbeam.XBProjector;

import de.oppermann.pomutils.util.SaxonXPathFactoriesConfig;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */
public class TestUtils {

	public static final String resourceBaseTestFolder = "target/testresources";

	public static void prepareTestFolder(String subFolder) throws IOException {
		String targetFolder = resourceBaseTestFolder + "/" + subFolder;
		File targetFolderFile = new File(targetFolder);
		FileUtils.deleteDirectory(targetFolderFile);
		FileUtils.copyDirectory(new File("src/test/resources/" + subFolder), targetFolderFile);
	}

	public static XBProjector createXBProjector() {
		XBProjector xbProjector = new XBProjector(new SaxonXPathFactoriesConfig());

		// xbProjector.config().as(DefaultXMLFactoriesConfig.class).setNamespacePhilosophy(NamespacePhilosophy.NIHILISTIC);

		return xbProjector;
	}

}
