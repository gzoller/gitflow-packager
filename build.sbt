lazy val unusedWarnings = Seq("-Ywarn-unused-import", "-Ywarn-unused")

lazy val commonSettings: Seq[Setting[_]] = Seq(
	version in ThisBuild := "0.1.5",
    organization in ThisBuild := "co.blocke",
    homepage in ThisBuild := Some(url(s"https://github.com/gzoller/${name.value}/Readme.md")),
    licenses in ThisBuild := Seq("MIT" -> url(s"https://github.com/gzoller/${name.value}/LICENSE")),
    description in ThisBuild := "name jars and Docker according to gitflow branching scheme",
    scmInfo in ThisBuild := Some(ScmInfo(url(s"https://github.com/gzoller/${name.value}"), s"git@github.com:gzoller/${name.value}.git")),
    scalaVersion := (crossScalaVersions in ThisBuild).value.last,
    scalacOptions ++= Seq(Opts.compile.deprecation, "-Xlint", "-feature"),
    scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
      case Some((2, v)) if v >= 11 => unusedWarnings
    }.toList.flatten,
    publishArtifact in Test := false,
    bintrayRepository := "releases",
    bintrayOrganization := Some("blocke"),
    bintrayPackageLabels := Seq("sbt", "sbt-plugin", "git-flow")
    // bintrayOrganization := Some("sbt"),
    // bintrayPackage := "gitflow-packager"
  ) ++ Seq(Compile, Test).flatMap(c =>
    scalacOptions in (c, console) --= unusedWarnings
  )

resolvers += Resolver.sonatypeRepo("releases")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")

lazy val root = (project in file("."))
  .settings(
  	commonSettings,
    sbtPlugin := true,
    name := "gitflow-packager",
    publishMavenStyle := false
    /*
    crossSbtVersions := List("0.13.15", "1.0.0-RC2"),
    scalaVersion := (CrossVersion partialVersion sbtCrossVersion.value match {
      case Some((0, 13)) => "2.10.6"
      case Some((1, _))  => "2.12.2"
      case _             => sys error s"Unhandled sbt version ${sbtCrossVersion.value}"
    }),
    */
    // resolvers += Resolver.sonatypeRepo("releases"),
    // libraryDependencies ++= Seq("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")
    /*
    scriptedSettings,
    scriptedBufferLog := true,
    scriptedLaunchOpts ++= Seq(
      "-Xmx1024M",
      "-XX:MaxPermSize=256M",
      "-Dbintray.user=username",
      "-Dbintray.pass=password",
      "-Dplugin.version=" + version.value
    )
    */
  )

/*
sbtPlugin := true

version := "0.1.5"

name := "gitflow-packager"

organization := "co.blocke"

scalacOptions in Compile ++= Seq("-deprecation", "-target:jvm-1.6")

publishMavenStyle := true

resolvers += Resolver.url("dl.bintray-repo", new java.net.URL("https://dl.bintray.com/sbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")

bintrayOrganization := Some("blocke")

bintrayReleaseOnPublish in ThisBuild := false

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayRepository := "releases"

bintrayPackageLabels := Seq("sbt", "sbt-plugin", "git-flow")
*/
