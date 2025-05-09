import xerial.sbt.Sonatype.sonatypeCentralHost
import org.typelevel.sbt.gha.JavaSpec.Distribution

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
  ),
  sonatypeCredentialHost := sonatypeCentralHost,
  githubWorkflowJavaVersions := Seq(JavaSpec(Distribution.Temurin, "21")),
  githubWorkflowScalaVersions := Seq("2.12.18"),
  githubWorkflowOSes := Seq("ubuntu-latest", "windows-latest"),

  // Only publish on version tag push (e.g. v0.3.0)
  githubWorkflowPublishTargetBranches := Seq(
    RefPredicate.StartsWith(Ref.Tag("v"))
  ),

  // GitHub Actions setup: checkout + coursier + sbt install
  githubWorkflowJobSetup := Seq(
    WorkflowStep.Use(
      ref = UseRef.Public("actions", "checkout", "v4"),
      params = Map(
        "fetch-depth" -> "0",
        "fetch-tags" -> "true"
      )
    ),
    WorkflowStep.Use(
      ref = UseRef.Public("coursier", "setup-action", "v1")
    ),
    WorkflowStep.Run(
      name = Some("Install sbt"),
      commands = List(
        "cs install sbt",
        "echo \"$HOME/.local/share/coursier/bin\" >> $GITHUB_PATH",
        "sbt sbtVersion"
      )
    )
  ),

  // Disable auto-upload step because we define our own
  githubWorkflowArtifactUpload := false,

  // Custom publish steps (download artifacts + publish)
  githubWorkflowPublish := Seq(
    WorkflowStep.Use(
      ref = UseRef.Public("actions", "download-artifact", "v4"),
      name = Some("Download target directories"),
      params = Map(
        "name" -> "target-${{ matrix.os }}-${{ matrix.java }}-${{ matrix.scala }}"
      )
    ),
    WorkflowStep.Run(
      name = Some("Inflate target directories"),
      commands = List("tar xf targets.tar", "rm targets.tar")
    ),
    WorkflowStep.Sbt(
      commands = List("ci-release"),
      env = Map(
        "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
        "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
        "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
        "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
        "CI_SNAPSHOT_RELEASE" -> "+publishSigned"
      )
    )
  )
))

// Stuff to make Central Portal happy
ThisBuild / versionScheme := Some("semver-spec")

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/gzoller/gitflow-packager"),
    "scm:git@github.com:gzoller/gitflow-packager.git"
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
git tag v0.3.0             # ⏱️ version tag must match sbt version

# 3. Push just the tag
git push origin v0.3.0     # ✅ triggers publish
 */