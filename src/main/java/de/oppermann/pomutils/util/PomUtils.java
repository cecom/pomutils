package de.oppermann.pomutils.util;

import de.oppermann.pomutils.model.PomModel;

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

public class PomUtils {

	public static String calculateProjectIdentifier(PomModel pom) {
		String groupId = pom.getProjectArtifact().getGroupId();
		String parentGroupId = pom.getParentArtifact() != null ? pom.getParentArtifact().getGroupId() : null;
		String artifactId = pom.getProjectArtifact().getArtifactId();
		String projectName = pom.getName();

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

}
