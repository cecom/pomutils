POMUtils
=============

This library is for making the day easier while using git and maven.
If you do almost weekly or daily releases you get in trouble with merge
conflicts regarding to the parent and/or project version of your pom.xml files.
In a project with many child modules this is a real pain.

There are some tools out there which also try to accomplish this goal,
but IMO they don't do it in the right way. Some are doing a search and
replace of `<version>...</version>` and they don't make sure that only
the parent and project version has changed. Some others are using a DOM Parser,
adjusting the versions via xpath and save it.
That way you get a new formatted xml file. What you also don't want.

Here you find a library which solves this problem, imo, in the right way ;-).
It just adjusts the parent and/or project version. Nothing else.
Internally it uses classes of the `versions-maven-plugin`,
that why it is a java program.
There is only one downside. It's slow, because git starts a new JVM for every pom file merge.
For ~100 pom.xml files it takes around 20 seconds to merge them.


Installation
------------

Download the [jar](https://github.com/cecom/pomutils/releases/latest) file or create it with `mvn clean install` and execute it with java >1.6.
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
    merge      Used as merge driver in git.  Updates the version of 'our' pom or 'their' pom (based on the value of --select), and then does a normal 'git merge-file'
      Usage: merge [options]
        Options:
        * -b, --base
             Base Pom
        * -o, --our
             Our Pom
          -s, --select
             Which version to select to resolve conflicts.  'our', 'their', or
             'prompt'.  If 'prompt' is specified, then you will be prompted on the console
             to select a version.
             Default: our
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

1. In your git project, edit the `.gitattributes` file (or create it if it doesn't exist) and add the following line:

    ```
    pom.xml merge=pom
    ```

2. In your `.git/config`, you have to define how the `pom` merge driver should be called. Add the following lines:

	```
	[merge "pom"]
		name = Automatically resolve project and/or parent version conflicts in pom files. The rest is a normal merge.
		driver = java -jar <pathToJar>/pomutils-X.X.jar merge --base=%O --our=%A --their=%B
	```

	To speed up the vm start, I use the following line:	
	
	```driver = java -jar -client -Xverify:none -Xms32m -Xmx32m  <pathToJar>/pomutils-X.X.jar merge --base=%O --our=%A --their=%B```

3. Done.

By default, project/parent version conflicts will be resolved using *our* version.
You can change this behaviour by specifying `--select their` or  `--select prompt` on the command line.
Select `their` to always resolve version conflicts using *their* version.
Select `prompt` to prompt the user on the console to select a version at merge time.
Note that you must be performing merges from the command line for `prompt` to work.
If you use a GUI to perform merge, do not use `--select prompt`, unless the GUI provides
a way to enter input on the git merge console.

Internally, the merge driver will adjust the parent/project version
of either *their* pom.xml file or *our* pom.xml file (depending on the value of `--select`),
then run the `git merge-file` command (which is used by the default merge driver).


*replace* Command Usage
------------
This command is used to set the parent/project version of a single pom.xml file. The version-maven-plugin always adjust the child's too, sometimes you don't want that.

`java -jar <pathToJar>/pomutils-X.X.jar replace --pom=<pathToPomFile> --version=<newVersion>`
