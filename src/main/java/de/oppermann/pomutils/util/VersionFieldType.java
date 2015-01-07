package de.oppermann.pomutils.util;


/**
 * Type of version field.
 */
public enum VersionFieldType {
	PROJECT {
		@Override
		public String get(POM pom) {
			return pom.getProjectVersion();
		}
		@Override
		public void set(POM pom, String newVersion) {
			pom.setProjectVersion(newVersion);
		}
		
	},
	PARENT {
		@Override
		public String get(POM pom) {
			return pom.getParentVersion();
		}
		@Override
		public void set(POM pom, String newVersion) {
			pom.setParentVersion(newVersion);
		}
		
	};
	
	public abstract String get(POM pom);
	public abstract void set(POM pom, String newVersion);
}