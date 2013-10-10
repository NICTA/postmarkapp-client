import sbtrelease.ReleasePlugin.ReleaseKeys.snapshotDependencies

organization := "au.com.nicta"

name := "postmarkapp-client"

homepage := Some(url("https://github.com/NICTA/postmarkapp-client"))

licenses := Seq("BSD-style" -> url("http://opensource.org/licenses/BSD-3-Clause"))

scalaVersion := "2.10.2"

net.virtualvoid.sbt.graph.Plugin.graphSettings

seq(Revolver.settings: _*)

releaseSettings

crossScalaVersions := Seq("2.10.2")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (
  <scm>
    <url>git@github.com:NICTA/postmarkapp-client.git</url>
    <connection>scm:git:git@github.com:NICTA/postmarkapp-client.git</connection>
  </scm>
  <developers>
    <developer>
      <id>sidneyshek</id>
      <name>Sidney Shek</name>
      <url>https://github.com/sidneyshek</url>
    </developer>
  </developers>)


libraryDependencies ++= Seq(
  "net.databinder.dispatch" % "dispatch-core_2.10" % "0.11.0",
  "io.argonaut" %% "argonaut" % "6.0.1",
  "com.github.nscala-time" %% "nscala-time" % "0.4.0",
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.3" % "it, test",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "it, test",
  "org.specs2" %% "specs2" % "2.1.1" % "it, test",
  "junit" % "junit" % "4.7" % "it, test",
  "org.typelevel" %% "scalaz-specs2" % "0.1.3" % "test",
  "org.slf4j" % "slf4j-nop" % "1.7.5" % "test",
  "commons-codec" % "commons-codec" % "1.8" % "test",
  "com.github.scopt" %% "scopt" % "3.1.0" % "it"
)

testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "junitxml", "console")

scalacOptions += "-feature"