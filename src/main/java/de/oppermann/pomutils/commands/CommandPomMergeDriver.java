package de.oppermann.pomutils.commands;

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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import de.oppermann.pomutils.rules.Ruleset;
import de.oppermann.pomutils.select.SelectionStrategy;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

@Parameters(separators = "=", commandDescription = "Used as merge driver in git.  Updates the version of 'our' pom or 'their' pom (based on the value of --select), and then does a normal 'git merge-file'")
public class CommandPomMergeDriver {

	@Parameter(names = { "-b", "--base" }, description = "Base Pom", required = true)
	private String basePom;

	@Parameter(names = { "-o", "--our" }, description = "Our Pom", required = true)
	private String ourPom;

	@Parameter(names = { "-t", "--their" }, description = "Their Pom", required = true)
	private String theirPom;

	@Parameter(names = { "-s", "--select" }, description = "Which version to select to resolve conflicts.  'our', 'their', or 'prompt'.  If 'prompt' is specified, then you will be prompted via stdout/stdin to select a version.", required = false, converter = SelectionStrategyConverter.class)
	private SelectionStrategy selectionStrategy = SelectionStrategy.OUR;

	@Parameter(names = { "-r", "--ruleset" }, description = "The ruleset to use when you merge poms. If not given only parent/project version is evaluated.")
	private File ruleSetfile;

	public String getBasePom() {
		return basePom;
	}

	public String getOurPom() {
		return ourPom;
	}

	public String getTheirPom() {
		return theirPom;
	}

	public SelectionStrategy getSelectionStrategy() {
		return selectionStrategy;
	}

	public File getRuleSetFile() {
		return ruleSetfile;
	}

	public Ruleset getRuleSet() {
		if (getRuleSetFile() == null) {
			return new Ruleset(getSelectionStrategy());
		}
		return new Ruleset(getRuleSetFile());
	}

}
