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
  val headPat = "refs/tags/(.*)".r

  private def getLatestTag = "git describe --abbrev=0 --tag"

  private def getEnvVersion: Option[String] = {
    val envVersion = System.getenv("BUILD_VERSION")
    if (envVersion == null || envVersion == "")
      None
    else
      Some(envVersion)
  }

  val isGitHubAction = {
    val isAction = System.getenv("GITHUB_ACTIONS")
    if (isAction != "true")
      false
    else
      true
  }

  override lazy val projectSettings = Seq(
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      val commit = "git rev-parse --verify HEAD".!! take (COMMIT_SIZE)
      "git rev-parse --abbrev-ref HEAD".!! trim match {
        case "master" | "main" => {
          val masterVer = (getLatestTag.!!).trim
          artifact.name + "-" + masterVer + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        }
        case "develop" => artifact.name + s"-$commit-SNAPSHOT" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        case s if s.startsWith("feature/") => artifact.name + s"-$commit" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        case s if s.startsWith("hotfix/") => {
          val masterVer = (getLatestTag.!!).trim
          artifact.name + s"-$masterVer-PATCH" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension
        }
        case relPat(r) => artifact.name + s"-$r-$commit-RC" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension

        // This happens for github actions where the version is given in an Env variable
        case _ if getEnvVersion.isDefined => artifact.name + "-" + getEnvVersion.get + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension

        // GitHub workflow (actions) -- normal case with detached HEAD
        case "HEAD" if isGitHubAction && System.getenv("GITHUB_REF").startsWith("refs/tags/") => 
          val headPat(masterVer) = System.getenv("GITHUB_REF")
          artifact.name + "-" + masterVer + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension

        case branch => 
          println(">>> Unknown gitflow branch: "+branch)
          artifact.name + "_unknown" + artifact.classifier.map(c => s"-$c").getOrElse("") + "." + artifact.extension // e.g. TravisCI builds
      }
    },
    // Need this one because newer sbt won't over-write stuff in ivy repo unless its a snapshot.
    isSnapshot := {
      "git rev-parse --abbrev-ref HEAD".!! trim match {
        case "master" | "main" => false
        case "HEAD" if isGitHubAction && System.getenv("GITHUB_REF").startsWith("refs/tags/") => false
        case _ => true
      }
    },
    version := { // set version for built artifacts, including docker
      val commit = "git rev-parse --verify HEAD".!! take (COMMIT_SIZE)
      "git rev-parse --abbrev-ref HEAD".!! trim match {
        case "master" | "main" => getLatestTag.!! trim
        case "develop" => commit + "_SNAPSHOT"
        case featPat(f) => f + "_" + commit
        case s if s.startsWith("hotfix/") => (getLatestTag.!!).trim + "_PATCH"
        case relPat(r) => r + "_" + commit + "_RC"

        // This happens for github actions where the version is given in an Env variable
        case _ if getEnvVersion.isDefined => getEnvVersion.get

        // GitHub release (detached HEAD)
        case "HEAD" if isGitHubAction && System.getenv("GITHUB_REF").startsWith("refs/tags/") => 
          val headPat(masterVer) = System.getenv("GITHUB_REF")
          masterVer

        case _ => commit + "_unknown" // e.g. TravisCI build
      }
    })
}
