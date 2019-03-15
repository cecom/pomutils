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
    merge       Used as merge driver in git.  Updates the version of 'our' pom or 'their' pom (based on the value of --select), and then does a normal 'git merge-file'
      Usage: merge [options]
        Options:
        * -b, --base
             Base Pom
        * -o, --our
             Our Pom
        * -t, --their
             Their Pom
          -s, --select
             Which version to select to resolve conflicts.  'our', 'their', or
             'prompt'.  If 'prompt' is specified, then you will be prompted via
             stdout/stdin to select a version.
             Default: our
          -r, --ruleset
             The ruleset to use when you merge poms. If you don't specify a ruleset,
             a default ruleset will be used. Default is ProjectAndParentVersionRule with our strategy.

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
Select `prompt` to prompt the user via stdout/stdin to select a version at merge time.
Note that you must be performing merges from the command line for `prompt` to work.
If you use a GUI to perform merge, do not use `--select prompt`, unless the GUI provides
a way to enter input on the git merge console.

Internally, the merge driver will adjust the parent/project version
of either *their* pom.xml file or *our* pom.xml file (depending on the value of `--select`),
then run the `git merge-file` command (which is used by the default merge driver).

*merge* Command *ADVANCED* Usage
------------

It is also possible to write your own *Rule* for conflict resolution. Currently there are three implemented rules:

  - de.oppermann.pomutils.rules.ProjectAndParentVersionRule
  - de.oppermann.pomutils.rules.PropertyRule
  - de.oppermann.pomutils.rules.ScmTagRule

If you don't specify `--ruleset` on command line the *de.oppermann.pomutils.rules.ProjectAndParentVersionRule*
rule is used. But if you want e.g. to resolve conflicts on maven properties too, or want
to evaluate your own rule, you have to create a ruleset configuration file. This file looks like (yaml format):

    --- !!de.oppermann.pomutils.rules.ProjectAndParentVersionRule
    strategy: OUR                     # possible values: OUR|THEIR|PROMPT
                                      # resolves all parent and project version conflicts, using the *OUR* strategy
    --- !!de.oppermann.pomutils.rules.PropertyRule
    strategy  : OUR                   # possible values: OUR|THEIR
    properties:                       # resolves the two given properties in the global and profile property section, using the *OUR* strategy
         - jdbc.user.name
         - foobar.version
    propertiesRegex:                  # resolves properties in the global and profile property section matching regex expression, using the *OUR* strategy
         - .+\.version
		 
	--- !!de.oppermann.pomutils.rules.ScmTagRule
    strategy: OUR                     # possible values: OUR|THEIR
	                                  # resolves the  <scm><tag>...</tag></scm> element conflicts, using the *OUR* strategy

Basically you define the rules which should be used by adding the implementation class of the rule with *--- !!*,
followed by the configuration. If you want to write your own rule, have a look at the implemented ones and perhaps send
a pull request to merge it ;-)

After you wrote your ruleset configuration file, add it to your git project *somewhere* and change the `driver` entry in your `.git/config`, e.g.:

```
[merge "pom"]
     name = Automatically resolve project and/or parent version and properties in pom files. The rest is a normal merge.
     driver = java -jar <pathToJar>/pomutils-X.X.jar merge --base=%O --our=%A --their=%B --ruleset=.pomutilsMergeRuleset
```

*replace* Command Usage
------------
This command is used to set the parent/project version of a single pom.xml file. The version-maven-plugin always adjust the child's too, sometimes you don't want that.

`java -jar <pathToJar>/pomutils-X.X.jar replace --pom=<pathToPomFile> --version=<newVersion>`


HINTS
------------
In my project's I ship the pomutils.jar within the project. My .git/config entry looks like:

```
[merge "pom"]
     driver = java -jar ./buildmgr/pomutils.jar merge --base=%O --our=%A --their=%B --ruleset=./buildmgr/ruleset
```

Now all developers can use the merge driver without downloading first the jar. They only have to add the .git/config entry.

