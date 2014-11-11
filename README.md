POMUtils
=============

This library is for making the day easier while using git and maven. If you do almost weekly or daily releases you get in trouble with merge conflicts regarding to the parent and/or project version of your pom.xml Files. In a big project this is a real pain.

There are some tools out there which also try to accomplish this goal, but IMO they don't do it in the right way. Some are doing a search and replace of `<version>...</version>` and they don't make sure that only the parent and project version has changed. Some others are using a DOM Parser, adjusting the versions via xpath and save it. That way you get a new formatted xml file. What you also don't want.

Here you find a library which solves this problem, imo, in the right way ;-). It just adjust the parent and/or project version. Nothing else. Internally it uses classes of the versions-maven-plugin, that why it is a java program. There is only one downside. It's slow. Because git start's for every single pom merge a new VM. For ~100 pom.xml files it takes around 20 Seconds to merge them.


Installation
------------

Download the jar file or create it with `mvn clean install` and execute it with java >1.6.
It's a shaded jar, which contains all necessary dependencies.

For usage, call: `java -jar pomutils-X.X.jar --help`

```
java -jar target/pomutils-1.0.jar --help
Usage: <main class> [options] [command] [command options]
Options:
       --debug
       If you want to print debug information add this option.
       Default: false
       --help
       Used to print this information.
       Default: false
  Commands:
    merge      Used as merge driver in git. Updates the version of 'their pom.xml' with the version from 'our pom.xml' and does finally a normal 'git merge-file'
      Usage: merge [options]
        Options:
        * -b, --base
             Base Pom
        * -o, --our
             Our Pom
        * -t, --their
             Their Pom
    replace      Updates the parent version and/or the project version in the given pom.xml
      Usage: replace [options]
        Options:
        * -p, --pom
             The pom where you want to replace the parent and project version
        * -v, --version
             The new version you want to set
```

*merge* Command Usage
------------
This command is used as pom merge driver in git. To configure it you have to do the following:

1. In your git project edit the .gitattributes file (or create it if it doesn't exist) and add the following line:
    `pom.xml merge=pomMergeDriver`
2. In your .git/config you have to define how the *pomMergeDriver* should be called. Add the following lines:
	```
	[merge "pomMergeDriver"]
		name = Merge pom's using always our strategy for project and/or parent version. The rest a normal merge.
		driver = java -jar <pathToJar>/pomutils-X.X.jar merge --base=%O --our=%A --their=%B
	```

	To speed up the vm start, i use the following line:	
	
	```driver = java -jar -client -Xverify:none -Xms32m -Xmx32m  <pathToJar>/pomutils-X.X.jar merge --base=%O --our=%A --their=%B```
3. Done.

What is this merge driver doing? First, the parent/project version of the *their* pom.xml is adjusted with the one from *our* pom.xml. Then the git command "merge-file" is called, which is the default merge driver.


*replace* Command Usage
------------
This command is used to set the parent/project version of a single pom.xml file. The version-maven-plugin always adjust the child's too, sometimes you don't want that.

`java -jar <pathToJar>/pomutils-X.X.jar replace --pom=<pathToPomFile> --version=<newVersion>`
