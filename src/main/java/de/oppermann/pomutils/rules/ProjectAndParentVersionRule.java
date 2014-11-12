package de.oppermann.pomutils.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oppermann.pomutils.select.SelectionStrategy;
import de.oppermann.pomutils.util.POM;
import de.oppermann.pomutils.util.VersionFieldType;

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
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class ProjectAndParentVersionRule implements Rule {

	private final Logger logger = LoggerFactory.getLogger(ProjectAndParentVersionRule.class);

	private SelectionStrategy strategy;

	public ProjectAndParentVersionRule() {
		// for creating this instance via snakeyaml
	}

	public ProjectAndParentVersionRule(SelectionStrategy strategy) {
		this.strategy = strategy;
		logger.debug("Using ProjectAndParentVersionRule with strategy [{}]", strategy.toString());
	}

	public SelectionStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(SelectionStrategy strategy) {
		this.strategy = strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = SelectionStrategy.valueOf(strategy.toUpperCase());
	}

	@Override
	public void evaluate(POM basePom, POM ourPom, POM theirPom) {
		try {
			List<POM> adjustedPoms = new ArrayList<POM>();

			addIfNotNull(adjustedPoms, adjustVersion(basePom, ourPom, theirPom, VersionFieldType.PROJECT));
			addIfNotNull(adjustedPoms, adjustVersion(basePom, ourPom, theirPom, VersionFieldType.PARENT));

			for (POM adjustedPom : adjustedPoms) {
				adjustedPom.savePom();
			}
		} catch (IOException e) {
			logger.warn("Exception when attempting to merge pom versions.  Falling back to default merge.", e);
		} catch (XMLStreamException e) {
			logger.warn("Exception when attempting to merge pom versions.  Falling back to default merge.", e);
		}
	}

	private void addIfNotNull(List<POM> adjustedPoms, POM adjustedPom) {
		if (adjustedPom != null) {
			adjustedPoms.add(adjustedPom);
		}
	}

	private POM adjustVersion(POM basePom, POM ourPom, POM theirPom, VersionFieldType versionFieldType) {
		String baseVersion = versionFieldType.get(basePom);
		String ourVersion = versionFieldType.get(ourPom);
		String theirVersion = versionFieldType.get(theirPom);
		if (baseVersion != null && ourVersion != null && theirVersion != null && !ourVersion.equals(theirVersion)) {
			String newVersion;
			if (baseVersion.equals(ourVersion)) {
				/*
				 * Our version hasn't changed, so no conflict. Just use theirVersion.
				 */
				newVersion = theirVersion;
			} else if (baseVersion.equals(theirVersion)) {
				/*
				 * Their version hasn't changed, so no conflict. Just use ourVersion.
				 */
				newVersion = ourVersion;
			} else {
				/*
				 * Both our version and their version have changed from the base, so conflict.
				 */
				newVersion = strategy.getSelector().selectVersion(
				        ourPom.getProjectIdentifier(),
				        versionFieldType,
				        ourVersion,
				        theirVersion);
			}

			if (newVersion != null) {
				/*
				 * newVersion can be null if the user wants to skip resolution.
				 */

				POM pomToAdjust = newVersion.equals(ourVersion)
				        ? theirPom
				        : ourPom;

				versionFieldType.set(pomToAdjust, newVersion);
				return pomToAdjust;
			}
		}
		return null;
	}
}
