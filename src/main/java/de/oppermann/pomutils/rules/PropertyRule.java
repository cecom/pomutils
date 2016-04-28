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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

	private List<String> propertiesRegex;

	public PropertyRule() {
		// for creating this instance via snakeyaml
	}

	public PropertyRule(SelectionStrategy strategy, List<String> properties) {
		super(strategy);
		this.properties = properties;
		logger.debug("Using ProjectAndParentVersionRule with strategy [{}] for properties [{}]", strategy.toString(), Arrays.toString(properties.toArray()));
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public List<String> getPropertiesRegex() {
		return propertiesRegex;
	}

	public void setPropertiesRegex(List<String> propertiesRegex) {
		this.propertiesRegex = propertiesRegex;
	}

	@Override
	public void evaluate(POM basePom, POM ourPom, POM theirPom) throws IOException, XMLStreamException {
		POM adjustPom;
		POM withValueOfPom;

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

		if (getProperties() != null) {
			for (String property : getProperties()) {
				logger.debug("Process property [{}]", property);
				resolvePropertyValue(property, adjustPom, withValueOfPom);
			}
		}

		if (getPropertiesRegex() != null) {
			for (String propertyRegex : getPropertiesRegex()) {
				logger.debug("Process property regex [{}]", propertyRegex);
				Pattern regex = compilePropertyRegex(propertyRegex);
				for (String property : adjustPom.getMatchingProperties(regex)) {
					resolvePropertyValue(property, adjustPom, withValueOfPom);
				}
			}
		}
	}

	private void resolvePropertyValue(String property, POM adjustPom, POM withValueOfPom) throws XMLStreamException, IOException {
		adjustPom.setPropertyToValue(property, withValueOfPom.getProperties().getProperty(property));
		for (Profile profile : adjustPom.getProfiles()) {
            adjustPom.setPropertyToValue(profile.getId(), property, withValueOfPom.getProfileProperty(profile.getId(), property));
        }
	}

	private Pattern compilePropertyRegex(String propertyRegex) {
		Pattern regex;
		try {
            regex = Pattern.compile(propertyRegex);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex expression for property regex [" + propertyRegex + "] ");
        }
		return regex;
	}

}
