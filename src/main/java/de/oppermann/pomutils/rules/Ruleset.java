package de.oppermann.pomutils.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import de.oppermann.pomutils.model.PomModel;
import de.oppermann.pomutils.select.SelectionStrategy;

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

public class Ruleset {

	private List<Rule> rules = new ArrayList<Rule>();

	/**
	 * Creates the default Ruleset with the ProjectAndParentVersionRule.
	 */
	public Ruleset(SelectionStrategy strategy) {
		rules.add(new ProjectAndParentVersionRule(strategy));
	}

	public Ruleset(File rulesetFile) {
		if (!rulesetFile.exists()) {
			throw new IllegalArgumentException("File [" + rulesetFile.getAbsolutePath() + "] does not exist");
		}

		Yaml yaml = new Yaml();

		FileInputStream is = null;
		try {
			is = new FileInputStream(rulesetFile);
			for (Object data : yaml.loadAll(is)) {
				rules.add((Rule) data);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void evaluate(PomModel basePom, PomModel ourPom, PomModel theirPom) {
		for (Rule rule : rules) {
			rule.evaluate(basePom, ourPom, theirPom);
		}
	}

}
