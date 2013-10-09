import sbt._
import Keys._
import Tests._

object B extends Build
{
  lazy val CustomIntegrationTest = config("it") extend(Test)
  lazy val root =
    Project("root", file("."))
      .configs( CustomIntegrationTest )
      .settings( inConfig(CustomIntegrationTest)(Defaults.testSettings) : _*)
}