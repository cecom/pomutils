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
import java.util.List;
import java.util.Properties;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
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
 *         this class is a wrapper class to the version-maven-plugin
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
	private String projectIdentifier;
	private Model rawModel;

	public POM(String pomFileAsString) throws IOException, XMLStreamException {
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

	private void initialize() throws IOException, XMLStreamException {
		StringBuilder input = new StringBuilder(FileUtils.fileRead(pomFile));
		pom = new ModifiedPomXMLEventReader(input, XML_INPUT_FACTORY);
		rawModel = PomHelper.getRawModel(pom);
		projectIdentifier = calculateProjectIdentifier();
		projectVersion = rawModel.getVersion();
		parentVersion = rawModel.getParent() != null
		        ? rawModel.getParent().getVersion()
		        : null;
	}

	private String calculateProjectIdentifier() {
		String groupId = rawModel.getGroupId();
		String parentGroupId = rawModel.getParent() != null ? rawModel.getParent().getGroupId() : null;
		String artifactId = rawModel.getArtifactId();
		String projectName = rawModel.getName();

		StringBuilder identifier = new StringBuilder(64);

		if (projectName != null) {
			identifier.append(projectName);
			identifier.append(" (");
		}

		if (groupId != null) {
			identifier.append(groupId);
			identifier.append(":");
		} else if (parentGroupId != null) {
			identifier.append(parentGroupId);
			identifier.append(":");
		}

		if (artifactId != null) {
			identifier.append(artifactId);
		}

		if (projectName != null) {
			identifier.append(")");
		}

		return identifier.toString();
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
	 * Gets an identifier that can be used for logging/prompting.
	 */
	public String getProjectIdentifier() {
		return projectIdentifier;
	}

	/**
	 * Sets the parent version to the given one, if it exists
	 * @param newVersion
	 */
	public void setParentVersion(String newVersion) {
		if (this.parentVersion == null || this.parentVersion.equals(newVersion)) {
			return;
		}
		logger.debug("Adjusting parent version from [{}] to [{}] of [{}] for [{}]", this.parentVersion, newVersion, getPath(), this.projectIdentifier);
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
		logger.debug("Adjusting project version from [{}] to [{}] of [{}] for [{}]", this.projectVersion, newVersion, getPath(), this.projectIdentifier);
		this.projectVersion = newVersion;
		this.changed = true;

	}

	/**
	 * Saves the pom, if it was changed.
	 */
	public void savePom() throws IOException, XMLStreamException {
		if (!changed) {
			return;
		}

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
	}

	/**
	 * 
	 * @return the pom file path
	 */
	public String getPath() {
		return pomFile.getPath();
	}

	public String getProfileProperty(String profileId, String property) {
		Properties properties = getProfileProperties(profileId);
		return properties.getProperty(property);
	}

	public Properties getProfileProperties(String profileId) {
		if (profileId == null) {
			throw new IllegalArgumentException("profileId is null");
		}
		for (Profile profile : getRawModel().getProfiles()) {
			if (profileId.equals(profile.getId())) {
				return profile.getProperties();
			}
		}
		return new Properties();
	}

	public Properties getProperties() {
		return getRawModel().getProperties();
	}

	public void setPropertyToValue(String property, String newPropertyValue) throws XMLStreamException, IOException {
		setPropertyToValue(null, property, newPropertyValue);
	}

	public void setPropertyToValue(String profileId, String property, String newPropertyValue) throws XMLStreamException, IOException {
		if (property == null) {
			logger.debug("Property is null, nothing to do.");
			return;
		}
		if (newPropertyValue == null) {
			logger.debug("newPropertyValue of property [{}] is null, nothing to do.", property);
			return;
		}

		if (profileId == null && newPropertyValue.equals(getProperties().getProperty(property))) {
			return;
		}

		if (profileId != null && newPropertyValue.equals(getProfileProperties(profileId).getProperty(property))) {
			return;
		}

		if (profileId == null) {
			logger.debug("Adjusting property  [{}] from [{}] to [{}] of [{}]", property, getProperties().getProperty(property), newPropertyValue, getPath());

		} else {
			logger.debug("Adjusting property [{}] from [{}] to [{}] of profile [{}] of [{}]", property, getProperties().getProperty(property),
			        newPropertyValue,
			        profileId, getPath());
		}
		boolean propertyChanged = PomHelper.setPropertyVersion(pom, profileId, property, newPropertyValue);
		if (propertyChanged) {
			changed = true;
			rawModel = PomHelper.getRawModel(pom);
		}
	}

	public List<Profile> getProfiles() {
		return getRawModel().getProfiles();
	}

	private Model getRawModel() {
		return rawModel;
	}

}
