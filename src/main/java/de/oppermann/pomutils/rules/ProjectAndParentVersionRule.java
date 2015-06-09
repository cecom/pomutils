package de.oppermann.pomutils.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oppermann.pomutils.model.PomModel;
import de.oppermann.pomutils.select.SelectionStrategy;
import de.oppermann.pomutils.util.PomUtils;
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

public class ProjectAndParentVersionRule extends AbstractRule {

	private final Logger logger = LoggerFactory.getLogger(ProjectAndParentVersionRule.class);

	public ProjectAndParentVersionRule() {
		// for creating this instance via snakeyaml
	}

	public ProjectAndParentVersionRule(SelectionStrategy strategy) {
		super(strategy);
		logger.debug("Using ProjectAndParentVersionRule with strategy [{}]", strategy.toString());
	}

	@Override
	public void evaluate(PomModel basePom, PomModel ourPom, PomModel theirPom) {
		adjustVersion(basePom, ourPom, theirPom, VersionFieldType.PROJECT);
		adjustVersion(basePom, ourPom, theirPom, VersionFieldType.PARENT);
	}

	private void adjustVersion(PomModel basePom, PomModel ourPom, PomModel theirPom, VersionFieldType versionFieldType) {
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
				newVersion = getStrategy().getSelector().selectVersion(
				        PomUtils.calculateProjectIdentifier(ourPom),
				        versionFieldType,
				        ourVersion,
				        theirVersion);
			}

			if (newVersion != null) {
				/*
				 * newVersion can be null if the user wants to skip resolution.
				 */

				PomModel pomToAdjust = newVersion.equals(ourVersion)
				        ? theirPom
				        : ourPom;

				versionFieldType.set(pomToAdjust, newVersion);
			}
		}
	}
}
