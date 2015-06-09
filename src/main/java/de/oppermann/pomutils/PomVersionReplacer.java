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

import java.io.IOException;

import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;

import de.oppermann.pomutils.model.PomModel;

/**
 * 
 * @author Sven Oppermann <sven.oppermann@gmail.com>
 * 
 */

public class PomVersionReplacer {

	private final String pomFile;
	private final XBProjector xbProjector;

	public PomVersionReplacer(String pomFile) {
		this.pomFile = pomFile;
		xbProjector = new XBProjector();
		xbProjector.config().as(DefaultXMLFactoriesConfig.class).setNamespacePhilosophy(NamespacePhilosophy.NIHILISTIC);
	}

	public void setVersionTo(String newVersion) {
		try {
			PomModel pom = xbProjector.io().file(pomFile).read(PomModel.class);
			if (pom.getParentArtifact() != null && pom.getParentArtifact().getVersion() != null) {
				pom.getParentArtifact().setVersion(newVersion);
			}
			if (pom.getProjectArtifact().getVersion() != null) {
				pom.getProjectArtifact().setVersion(newVersion);
			}

			// TODO: check obs nötig ist
			xbProjector.io().file(pomFile).write(pom);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
