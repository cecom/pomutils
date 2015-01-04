package de.oppermann.pomutils.util;

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

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.maven.model.Parent;
import org.codehaus.mojo.versions.api.PomHelper;
import org.codehaus.mojo.versions.rewriting.ModifiedPomXMLEventReader;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.stax2.XMLInputFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */
public class POM {

	private static final XMLInputFactory XML_INPUT_FACTORY = initializeXmlInputFactory();

	private final Logger logger = LoggerFactory.getLogger(POM.class);

	private File pomFile;
	private ModifiedPomXMLEventReader pom;

	/**
	 * did we changed the pom?
	 */
	private boolean changed = false;

	private String projectVersion;
	private String parentVersion;

	public POM(String pomFileAsString) {
		pomFile = new File(pomFileAsString);
		if (!pomFile.exists()) {
			throw new IllegalArgumentException("File [" + pomFile.getAbsolutePath() + "] not found.");
		}

		initialize();
	}

	private static XMLInputFactory initializeXmlInputFactory()
			throws FactoryConfigurationError {
		XMLInputFactory inputFactory = new WstxInputFactory();
		inputFactory.setProperty(XMLInputFactory2.P_PRESERVE_LOCATION, Boolean.TRUE);
		return inputFactory;
	}

	private void initialize() {
		try {
			StringBuilder input = new StringBuilder(FileUtils.fileRead(pomFile));
			pom = new ModifiedPomXMLEventReader(input, XML_INPUT_FACTORY);
			projectVersion = PomHelper.getProjectVersion(pom);
			
			Parent parent = PomHelper.getRawModel(pom).getParent();
			parentVersion = parent != null
					? parent.getVersion()
					: null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return the version and null if the project version doesn't exist
	 */
	public String getProjectVersion() {
		return projectVersion;
	}

	/**
	 * 
	 * @return the version of the parent, null if there is no parent
	 */
	public String getParentVersion() {
		return parentVersion;
	}

	/**
	 * Sets the parent version to the given one, if it exists
	 * @param newVersion
	 */
	public void setParentVersion(String newVersion) {
		if (this.parentVersion == null || this.parentVersion.equals(newVersion)) {
			return;
		}
		logger.debug("Adjusting parent version from [{}] to [{}] of [{}]", this.parentVersion, newVersion, getPath());
		this.parentVersion = newVersion;
		this.changed = true;
	}

	/**
	 * Sets the project version to the given one, if it exists
	 * @param newVersion
	 */
	public void setProjectVersion(String newVersion) {
		if (this.projectVersion == null || this.projectVersion.equals(newVersion)) {
			return;
		}
		logger.debug("Adjusting project version from [{}] to [{}] of [{}]", this.projectVersion, newVersion, getPath());
		this.projectVersion = newVersion;
		this.changed = true;
		
	}

	/**
	 * Saves the pom, if it was changed.
	 */
	public void savePom() {
		if (!changed) {
			return;
		}
		
		try {
			if (this.projectVersion != null) {
				changed |= PomHelper.setProjectVersion(pom, this.projectVersion);
			}
			
			if (this.parentVersion != null) {
				changed |= PomHelper.setProjectParentVersion(pom, this.parentVersion);
			}
			
			if (!changed) {
				return;
			}
			
			FileUtils.fileWrite(pomFile.getAbsolutePath(), pom.asStringBuilder().toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return the pom file path
	 */
	public String getPath() {
		return pomFile.getPath();
	}

}
