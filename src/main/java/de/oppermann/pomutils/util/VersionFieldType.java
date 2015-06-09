package de.oppermann.pomutils.util;

import de.oppermann.pomutils.model.PomModel;

/**
 * Type of version field.
 */
public enum VersionFieldType {
	PROJECT {
		@Override
		public String get(PomModel pom) {
			if (pom.getProjectArtifact() == null) {
				return null;
			}
			return pom.getProjectArtifact().getVersion();
		}

		@Override
		public void set(PomModel pom, String newVersion) {
			pom.getProjectArtifact().setVersion(newVersion);
		}

	},
	PARENT {
		@Override
		public String get(PomModel pom) {
			if (pom.getParentArtifact() == null) {
				return null;
			}
			return pom.getParentArtifact().getVersion();
		}

		@Override
		public void set(PomModel pom, String newVersion) {
			pom.getParentArtifact().setVersion(newVersion);
		}

	};

	public abstract String get(PomModel pom);

	public abstract void set(PomModel pom, String newVersion);
}