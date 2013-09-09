import AssemblyKeys._

import sbtrelease.ReleasePlugin.ReleaseKeys.snapshotDependencies

organization := "au.com.nicta"

name := "postmarkapp-client"

scalaVersion := "2.10.0"

net.virtualvoid.sbt.graph.Plugin.graphSettings

seq(Revolver.settings: _*)

releaseSettings

// Ignore snapshot dependencies for release process, mainly for machines
// which is relatively stable but is not on maven central.
snapshotDependencies := Seq()

assemblySettings

assemblyCacheOutput in assembly := true

mergeStrategy in assembly := {
  case "reference.conf" =>
    MergeStrategy.concat
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}

artifact in (Compile, assembly) ~= { art =>
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)

libraryDependencies ++= Seq(
  "net.databinder.dispatch" % "dispatch-core_2.10" % "0.11.0",
  "io.argonaut" %% "argonaut-unfiltered" % "6.0",
  "com.github.nscala-time" %% "nscala-time" % "0.4.0",
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.0" % "it, test",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "it, test",
  "org.specs2" %% "specs2" % "2.1.1" % "it, test",
  "junit" % "junit" % "4.7" % "it, test",
  "org.typelevel" %% "scalaz-specs2" % "0.1.3" % "test"
)

testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "junitxml", "console")

scalacOptions += "-feature"