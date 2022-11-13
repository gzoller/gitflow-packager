inThisBuild(List(
  organization := "co.blocke",
  homepage := Some(url("https://github.com/gzoller/gitflow-packager")),
  licenses := List("MIT" -> url("https://opensource.org/licenses/MIT")),
  developers := List(
    Developer(
      "gzoller",
      "Greg Zoller",
      "gzoller@outlook.com",
      url("http://www.blocke.co")
    )
  )
))

lazy val commonSettings: Seq[Setting[_]] = Seq(
  scalaVersion := "2.12.13",
  scalacOptions ++= Seq(Opts.compile.deprecation, "-Xlint", "-feature")
)

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    commonSettings,
    sbtPlugin := true,
    name := "gitflow-packager",
    moduleName := "gitflow-packager"
  )
