package de.oppermann.pomutils.model;

import java.util.List;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;

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

public interface PomModel {

	@XBRead("/m:project/m:name")
	String getName();

	@XBRead("/m:project")
	Artifact getProjectArtifact();

	@XBRead("/m:project/m:parent")
	Artifact getParentArtifact();

	@XBRead("/m:project/m:profiles/m:profile")
	List<Profile> getProfiles();

	@XBRead("/m:project/m:profiles/m:profile[m:id='{0}']")
	Profile getProfile(String id);

	@XBRead("/m:project/m:properties/m:{0}")
	String getPropertyValue(String property);

	@XBRead("boolean(/m:project/m:properties/m:{0})")
	Boolean propertyExist(String property);

	@XBWrite("/project/properties/{0}")
	void setPropertyValue(String property, @XBValue String value);

	@XBRead("/m:project/m:dependencies/m:dependency")
	List<Artifact> getDependencies();

	public interface Artifact {
		@XBRead("./m:groupId")
		String getGroupId();

		@XBRead("./m:artifactId")
		String getArtifactId();

		@XBRead("./m:version")
		String getVersion();

		@XBWrite("./version")
		void setVersion(String version);
	}

	public interface Profile {
		@XBRead("./m:id")
		String getId();

		@XBRead("./m:properties/m:{0}")
		String getPropertyValue(String property);

		@XBRead("boolean(./m:properties/m:{0})")
		Boolean propertyExist(String property);

		@XBWrite("./properties/{0}")
		void setPropertyValue(String property, @XBValue String value);
	}

}
