package co.blocke.gfpack

import sbt._
import Keys._

import com.typesafe.sbt.SbtNativePackager._

object GFPackagerPlugin extends AutoPlugin {

  override def trigger = allRequirements

  val COMMIT_SIZE = 6
  val relPat = "release/(.*)".r
  val featPat = "feature/(.*)".r

  override lazy val projectSettings = Seq(
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      val commit = "git rev-parse --verify HEAD".!! take(COMMIT_SIZE)
      "git rev-parse --abbrev-ref HEAD".!! trim match {
        case "master"  => {
          val masterVer = ("git describe --always --tag".!!).trim
          artifact.name + "-" + masterVer + "." + artifact.extension
        }
        case "develop" => artifact.name + s"-$commit-SNAPSHOT." + artifact.extension
        case s if s.startsWith("feature/") => artifact.name + s"-$commit." + artifact.extension
        case s if s.startsWith("hotfix/") => {
          val masterVer = ("git describe --always --tag".!!).trim
          artifact.name + s"-$masterVer-PATCH." + artifact.extension
        }
        case relPat(r) => artifact.name + s"-$r-$commit-RC." + artifact.extension
      }
    },
    version := {  // set version for built artifacts, including docker
      val commit = "git rev-parse --verify HEAD".!! take(COMMIT_SIZE)
      "git rev-parse --abbrev-ref HEAD".!! trim match {
        case "master"  => "git describe --always --tag".!! trim
        case "develop" => commit+"_SNAPSHOT"
        case featPat(f) => f+"_"+commit
        case s if s.startsWith("hotfix/") => ("git describe --always --tag".!!).trim + "_PATCH"
        case relPat(r) => r+"_"+commit+"_RC"
    }}
  )
}
