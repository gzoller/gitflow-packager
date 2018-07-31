package co.blocke.gfpack

import sbt._
import Keys._

//import com.typesafe.sbt.SbtNativePackager._
import scala.sys.process._
import scala.language.postfixOps

object GFPackagerPlugin extends AutoPlugin {

  override def trigger = allRequirements

  val COMMIT_SIZE = 6
  val relPat = "release/(.*)".r
  val featPat = "feature/(.*)".r

  override lazy val projectSettings = Seq(
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      val commit = "git rev-parse --verify HEAD".!! take (COMMIT_SIZE)
      "git rev-parse --abbrev-ref HEAD".!! trim match {
        case "master" => {
          val masterVer = ("git describe --always --tag".!!).trim
          artifact.name + "-" + masterVer + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        }
        case "develop"                     => artifact.name + s"-$commit-SNAPSHOT" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        case s if s.startsWith("feature/") => artifact.name + s"-$commit" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        case s if s.startsWith("hotfix/") => {
          val masterVer = ("git describe --always --tag".!!).trim
          artifact.name + s"-$masterVer-PATCH" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        }
        case relPat(r) => artifact.name + s"-$r-$commit-RC" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        case _         => artifact.name + "_unknown" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension // e.g. TravisCI builds
      }
    },
    // Need this one because newer sbt won't over-write stuff in ivy repo unless its a snapshot.
    isSnapshot := {
      "git rev-parse --abbrev-ref HEAD".!! trim match {
        case "master" => false
        case _        => true
      }
    },
    version := { // set version for built artifacts, including docker
      val commit = "git rev-parse --verify HEAD".!! take (COMMIT_SIZE)
      "git rev-parse --abbrev-ref HEAD".!! trim match {
        case "master"                     => "git describe --always --tag".!! trim
        case "develop"                    => commit + "_SNAPSHOT"
        case featPat(f)                   => f + "_" + commit
        case s if s.startsWith("hotfix/") => ("git describe --always --tag".!!).trim + "_PATCH"
        case relPat(r)                    => r + "_" + commit + "_RC"
        case _                            => commit + "_unknown" // e.g. TravisCI build
      }
    }
  )
}
