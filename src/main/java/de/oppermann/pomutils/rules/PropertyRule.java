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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.maven.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oppermann.pomutils.select.SelectionStrategy;
import de.oppermann.pomutils.util.POM;

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
		logger.debug("Using ProjectAndParentVersionRule with strategy [{}] for properies []", strategy.toString(), Arrays.toString(properties.toArray()));
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	@Override
	public void evaluate(POM basePom, POM ourPom, POM theirPom) throws IOException, XMLStreamException {
		for (String property : getProperties()) {
			logger.debug("Process property [{}]", property);

			POM adjustPom = null;
			POM withValueOfPom = null;
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

			adjustPom.setPropertyToValue(property, withValueOfPom.getProperties().getProperty(property));
			for (Profile profile : adjustPom.getProfiles()) {
				adjustPom.setPropertyToValue(profile.getId(), property, withValueOfPom.getProfileProperty(profile.getId(), property));
			}
		}

		theirPom.savePom();
		ourPom.savePom();
	}
}
