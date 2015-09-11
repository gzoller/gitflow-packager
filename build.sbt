sbtPlugin := true

scalaVersion in Global := "2.10.5"

name := "gitflow-packager"

organization := "co.blocke"

scalacOptions in Compile ++= Seq("-deprecation", "-target:jvm-1.6")

publishMavenStyle := true

version := "1.0.0"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.5-M1")
