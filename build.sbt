import xerial.sbt.Sonatype.sonatypeCentralHost
import org.typelevel.sbt.gha.JavaSpec.Distribution.Zulu

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

ThisBuild / sonatypeCredentialHost := sonatypeCentralHost
ThisBuild / githubWorkflowScalaVersions := Seq("3.5.2")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec(Zulu, "21")) // ← uncommented and fixed
ThisBuild / githubWorkflowOSes := Seq("ubuntu-latest", "windows-latest")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.StartsWith(Ref.Tag("v"))                        // <-- enables tag-based publishing
)
ThisBuild / version := {
  val tag = sys.process.Process("git describe --tags --exact-match").!!.trim
  if (tag.startsWith("v"))
    tag.drop(1)
  else
    tag
}
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.StartsWith(Ref.Tag("v"))
)

ThisBuild / githubWorkflowJobSetup := Seq(
  WorkflowStep.Use(
    UseRef.Public("actions", "checkout", "v4")
  ),
  WorkflowStep.Use(
    UseRef.Public("coursier", "setup-action", "v1")
  ),
  WorkflowStep.Run(
    name = Some("Install sbt"),
    commands = List(
      "cs install sbt",
      "echo \"$HOME/.local/share/coursier/bin\" >> $GITHUB_PATH",
      "sbt sbtVersion"
    )
  )
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
      "CI_SNAPSHOT_RELEASE" -> "+publishSigned"
    )
  )
)

lazy val commonSettings: Seq[Setting[_]] = Seq(
  scalaVersion := "2.12.18",
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

/*
# 1. Push code to master first
git checkout master
git pull
git push origin master     # 🔁 code is on master, no publish yet

# 2. Create tag from master commit (not from a feature branch!)
git tag v0.3.0           # ⏱️ version tag must match sbt version

# 3. Push just the tag
git push origin v0.3.0   # ✅ triggers publish
 */