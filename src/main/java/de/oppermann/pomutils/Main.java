package de.oppermann.pomutils;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import de.oppermann.pomutils.commands.CommandMain;
import de.oppermann.pomutils.commands.CommandPomMergeDriver;
import de.oppermann.pomutils.commands.CommandPomVersionReplacer;
import de.oppermann.pomutils.util.ManifestUtils;

/**
 * Main Class. Provides an entry Point to all Commands.
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class Main {

	private static Logger logger = null;

	public static void main(String... args) {
		int resultValue = mainInternal(args);
		System.exit(resultValue);
	}

	protected static int mainInternal(String... args) {
		CommandMain mainCommand = new CommandMain();
		CommandPomMergeDriver mergeCommand = new CommandPomMergeDriver();
		CommandPomVersionReplacer versionReplacerCommand = new CommandPomVersionReplacer();

		JCommander jc = new JCommander(mainCommand);
		jc.addCommand("merge", mergeCommand);
		jc.addCommand("replace", versionReplacerCommand);

		jc.parse(args);

		String logLevel = mainCommand.isDebug() ? "debug" : "error";
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", logLevel);

		logger = LoggerFactory.getLogger(Main.class);

		logger.info("PomUtils {}", ManifestUtils.getManifestVersion());

		if ("merge".equals(jc.getParsedCommand())) {
			return executePomMergeDriver(mergeCommand);
		} else if ("replace".equals(jc.getParsedCommand())) {
			executePomVersionReplacer(versionReplacerCommand);
			return 0;
		}
		jc.usage();
		return 1;
	}

	private static int executePomMergeDriver(CommandPomMergeDriver mergeCommand) {
		PomMergeDriver pomMergeDriver = new PomMergeDriver(mergeCommand.getBasePom(), mergeCommand.getOurPom(),
		        mergeCommand.getTheirPom());
		pomMergeDriver.adjustTheirPomVersion();
		return pomMergeDriver.doGitMerge();
	}

	private static void executePomVersionReplacer(CommandPomVersionReplacer versionReplacerCommand) {
		PomVersionReplacer pomVersionReplacer = new PomVersionReplacer(versionReplacerCommand.getPom());
		pomVersionReplacer.setVersionTo(versionReplacerCommand.getVersion());
	}

}
