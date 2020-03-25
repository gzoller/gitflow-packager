lazy val unusedWarnings = Seq("-Ywarn-unused-import", "-Ywarn-unused")

lazy val crossVersions = crossScalaVersions := Seq("2.12.9", "2.13.1")

lazy val commonSettings: Seq[Setting[_]] = Seq(
  version in ThisBuild := "0.1.9",
  organization in ThisBuild := "co.blocke",
  homepage in ThisBuild := Some(
    url(s"https://github.com/gzoller/${name.value}/Readme.md")
  ),
  licenses in ThisBuild := Seq(
    "MIT" -> url(s"https://github.com/gzoller/${name.value}/LICENSE")
  ),
  description in ThisBuild := "name jars and Docker according to gitflow branching scheme",
  scmInfo in ThisBuild := Some(
    ScmInfo(
      url(s"https://github.com/gzoller/${name.value}"),
      s"git@github.com:gzoller/${name.value}.git"
    )
  ),
  scalaVersion := "2.12.9",
  scalacOptions ++= Seq(Opts.compile.deprecation, "-Xlint", "-feature"),
  scalacOptions ++= PartialFunction
    .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
      case Some((2, v)) if v >= 11 => unusedWarnings
    }
    .toList
    .flatten,
  publishArtifact in Test := false,
  bintrayRepository := "releases",
  bintrayOrganization := Some("blocke"),
  bintrayPackageLabels := Seq("sbt", "sbt-plugin", "git-flow")
  // bintrayOrganization := Some("sbt"),
  // bintrayPackage := "gitflow-packager"
) ++ Seq(Compile, Test).flatMap(
  c => scalacOptions in (c, console) --= unusedWarnings
)

resolvers += Resolver.sonatypeRepo("releases")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.6")

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    sbtPlugin := true,
    name := "gitflow-packager",
    publishMavenStyle := false
  )
