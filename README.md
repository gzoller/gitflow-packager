# gitflow-packager
Align sbt-built artifacts with appropriate branch using git-flow branching strategy.

The goal of this plugin is so to correlate as-built artifacts (jars, dockers, etc.) back to the branch and commit that created it.  This is a great thing for moving code between environments and to know precisely what code is running or being tested.

For a sample project 'futon' the following named artifacts will be generated according to the appropriate git-flow branch:

| git-flow Branch  | jar Product | Docker Image Tag |
| :------------ |:---------- | :--------------- |
| feature/something      | futon-28cb50.jar | something_28cb50
| develop      | futon-28cb50-SNAPSHOT.jar | 28cb50
| release/1.2 | futon-1.2-28cb50-RC.jar | 1.2_28cb50_RC
| master | futon-1.2.jar | 1.2
| hotfix/urgent | futon-1.2-PATCH.jar | 1.2_PATCH

For master and hotfix branches the '1.2' version designation comes from the git tag, which is set by the git-flow process (hubflow tools in my case).  For the release branch the version comes from the branch name, and there is no release designation for earlier branches.

If you are building Docker images with sbt-native-packager then the Docker images created will follow a similar convention, as shown.

## Use

Include it in your projects by adding the following to your project's plugins.sbt file:

    resolvers += Resolver.url("co.blocke ivy resolver", url("http://dl.bintray.com/blocke/releases/"))(Resolver.ivyStylePatterns)
    addSbtPlugin("co.blocke" % "gitflow-packager" % "0.1.0")

Remember *not* to define the version setting in your project!  This plugin will do that for you.  When you want to cut a labeled release, do that with git-flow (or hubflow) and the plugin will key off that version:

    $ git hf release start 1.2

