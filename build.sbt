sbtPlugin := true

scalaVersion in Global := "2.10.5"

version := "0.1.0"

name := "gitflow-packager"

organization := "co.blocke"

scalacOptions in Compile ++= Seq("-deprecation", "-target:jvm-1.6")

publishMavenStyle := false

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.5-M3")

bintrayOrganization := Some("blocke")

bintrayReleaseOnPublish in ThisBuild := false

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayRepository := "releases"

bintrayPackageLabels := Seq("sbt", "sbt-plugin", "git-flow")
