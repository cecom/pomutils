package de.oppermann.pomutils.rules;

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

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oppermann.pomutils.model.PomModel;
import de.oppermann.pomutils.model.PomModel.Profile;
import de.oppermann.pomutils.select.SelectionStrategy;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class PropertyRule extends AbstractRule {

	private final Logger logger = LoggerFactory.getLogger(PropertyRule.class);

	private List<String> properties;

	public PropertyRule() {
		// for creating this instance via snakeyaml
	}

	public PropertyRule(SelectionStrategy strategy, List<String> properties) {
		super(strategy);
		this.properties = properties;
		logger.debug("Using ProjectAndParentVersionRule with strategy [{}] for properties [{}]", strategy.toString(),
		        Arrays.toString(properties.toArray()));
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	@Override
	public void evaluate(PomModel basePom, PomModel ourPom, PomModel theirPom) {
		for (String property : getProperties()) {
			logger.debug("Process property [{}]", property);

			PomModel adjustPom = null;
			PomModel withValueOfPom = null;
			switch (getStrategy()) {
				case OUR:
					adjustPom = theirPom;
					withValueOfPom = ourPom;
					break;
				case THEIR:
					adjustPom = ourPom;
					withValueOfPom = theirPom;
					break;
				default:
					throw new IllegalArgumentException("Strategy [" + getStrategy().toString() + "] not implemented.");
			}

			if (adjustPom.propertyExist(property)) {
				adjustPom.setPropertyValue(property, withValueOfPom.getPropertyValue(property));
			}

			for (Profile profile : adjustPom.getProfiles()) {
				if (!profile.propertyExist(property)) {
					continue;
				}
				String newValue = withValueOfPom.getProfile(profile.getId()).getPropertyValue(property);
				profile.setPropertyValue(property, newValue);
			}
		}
	}
}
