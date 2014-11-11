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
import java.io.Writer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.maven.model.Parent;
import org.codehaus.mojo.versions.api.PomHelper;
import org.codehaus.mojo.versions.rewriting.ModifiedPomXMLEventReader;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.stax2.XMLInputFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */
public class POM {

	private final Logger logger = LoggerFactory.getLogger(POM.class);

	private File pomFile;
	private ModifiedPomXMLEventReader pom;

	/**
	 * did we changed the pom?
	 */
	private boolean changed = false;

	public POM(String pomFileAsString) {
		pomFile = new File(pomFileAsString);
		if (!pomFile.exists()) {
			throw new IllegalArgumentException("File [" + pomFile.getAbsolutePath() + "] not found.");
		}

		initialize();
	}

	private void initialize() {
		StringBuilder input;
		try {
			input = PomHelper.readXmlFile(pomFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		XMLInputFactory inputFactory = XMLInputFactory2.newInstance();
		inputFactory.setProperty(XMLInputFactory2.P_PRESERVE_LOCATION, Boolean.TRUE);

		try {
			pom = new ModifiedPomXMLEventReader(input, inputFactory);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return the version and null if the project version doesn't exist
	 */
	public String getProjectVersion() {
		try {
			return PomHelper.getProjectVersion(pom);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return the version of the parent, null if there is no parent
	 */
	public String getParentVersion() {
		try {
			Parent parent = PomHelper.getRawModel(pomFile).getParent();
			if (parent != null) {
				return parent.getVersion();
			}
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the parent version to the given one, if it exists
	 * @param newVersion
	 */
	public void setParentVersion(String newVersion) {
		try {
			if (getParentVersion() == null) {
				return;
			}
			if (getParentVersion().equals(newVersion)) {
				return;
			}

			logger.debug("Adjusting parent version from [{}] to [{}] of [{}]", getParentVersion(), newVersion, getPath());
			changed |= PomHelper.setProjectParentVersion(pom, newVersion);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Sets the project version to the given one, if it exists
	 * @param newVersion
	 */
	public void setProjectVersion(String newVersion) {
		try {
			if (getProjectVersion() == null) {
				return;
			}
			if (getProjectVersion().equals(newVersion)) {
				return;
			}

			logger.debug("Adjusting project version from [{}] to [{}] of [{}]", getProjectVersion(), newVersion, getPath());
			changed |= PomHelper.setProjectVersion(pom, newVersion);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saves the pom, if it was changed.
	 */
	public void savePom() {
		if (!changed) {
			return;
		}

		Writer writer = null;
		try {
			writer = WriterFactory.newXmlWriter(pomFile);
			IOUtil.copy(pom.asStringBuilder().toString(), writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			IOUtil.close(writer);
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
